package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.*;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.NewBookingItemModel;
import com.qbook.app.application.models.NewBookingModel;
import com.qbook.app.application.models.productModels.ProductItemModel;
import com.qbook.app.application.models.productModels.ProductItemToCaptureModel;
import com.qbook.app.application.models.salesModels.*;
import com.qbook.app.application.services.appservices.*;
import com.qbook.app.application.services.specifications.BookingValidationService;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.*;
import com.qbook.app.utilities.Constants;
import com.qbook.app.utilities.factory.SalesFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Log
@Service
@AllArgsConstructor
public class SalesServicesImpl implements SalesServices {
	private final BookingRepository bookingRepository;
	private final EmployeeRepository employeeRepository;
	private final TreatmentRepository treatmentRepository;
	private final SpecialPackageRepository specialPackageRepository;
	private final DailyCashupRepository dailyCashupRepository;
	private final BookingListItemRepository bookingListItemRepository;
	private final BookingListRepository bookingListRepository;
	private final AuthTokenServices authTokenServices;
	private final BookingValidationService bookingValidationService;
	private final SaleRepository saleRepository;
	private final ProductRepository productRepository;
	private final LoyaltyPointsService loyaltyPointsService;
	private final ClientRepository clientRepository;
	private final ApplicationProperties applicationProperties;
	private final SalesFactory salesFactory;
	private final ScheduleServices scheduleServices;
	private final EmailService emailService;

	@Override
	public BookingCancellationModel cancelBooking(ObjectId bookingId, ObjectId userId) {

		Optional<Employee> employeeOptional = employeeRepository.findById(userId);

		if(!employeeOptional.isPresent()) {
			throw new NotAuthorisedException("Your are not allowed to cancel this booking. Please contact the administrator.");
		}

		Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);

		if(!bookingOptional.isPresent()) {
			throw new ResourceNotFoundException("The booking was not found. Please contact the administrator.");
		}

		Booking toBeCancelled = bookingOptional.get();
		toBeCancelled.setBookingStatus("Cancelled");
		bookingRepository.save(toBeCancelled);

		return new BookingCancellationModel(
				true,
				Constants.BOOKING_CANCELLED
		);
	}

	@Override
	public SaleSpecificBookingModel viewSpecificSaleToUpdate(String id) {
		if(!ObjectId.isValid(id)) {
			throw new InvalidBookingException("We cant seem to find the booking, please contact the administrator.");
		}

		Optional<Booking> bookingOptional = bookingRepository.findById(new ObjectId(id));

		if(!bookingOptional.isPresent()) {
			throw new ResourceNotFoundException("The booking was not found. Please contact the administrator.");
		}

		Booking booking = bookingOptional.get();

		SaleSpecificBookingModel saleSpecificBookingModel = salesFactory.buildSaleSpecificBookingModel(booking);

		getAllTimeSlotsAndBookingsForEdit(booking, saleSpecificBookingModel);
		// get first one to be used during cash up that was not redeemed yet
		booking.getClient().getVouchers()
				.stream()
				.filter(voucher -> !voucher.isRedeemed())
				.filter(voucher -> DateTime.now().isBefore(voucher.getExpiryDate()))
				.findFirst()
				.ifPresent(saleSpecificBookingModel::setVoucher);

		if(booking.isDepositPaid()) {
			saleSpecificBookingModel.setDepositPaid(booking.isDepositPaid());
			saleSpecificBookingModel.setDepositAmount(booking.getTransaction().getAmount());
		}

		double totalAmountToPay = 0.0;
		for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
			if(bookingListItem.getSpecials() != null) { // Legacy Specials Tool
				if(bookingListItem.getSpecials().isDoneByJunior()) {
					totalAmountToPay += (bookingListItem.getSpecialQuantity() * bookingListItem.getSpecials().getJuniorPrice());
				} else {
					totalAmountToPay += (bookingListItem.getSpecialQuantity() * bookingListItem.getSpecials().getSeniorPrice());
				}
			} else {
				if(bookingListItem.getTreatment().isSpecial()) {
					totalAmountToPay += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSpecialPrice());
				} else {
					if(bookingListItem.getTreatment().isDoneByJunior()) {
						totalAmountToPay += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getJuniorPrice());
					} else {
						totalAmountToPay += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSeniorPrice());
					}
				}
			}
		}

		Optional<Sale> optionalSale = saleRepository.findByBooking(booking);
		// add products to booking and to sale

		double productTotalToPay = 0.0;
		if(optionalSale.isPresent()) {
			for(CashupItem cashupItem: optionalSale.get().getCashupItems()) {

				if(cashupItem.getCashupItemType().equals(CashupItemType.PRODUCT)) {
					productTotalToPay += cashupItem.getQuantity() * cashupItem.getItemPrice();
				}
			}
		}

		// remove discount and remove deposit paid
		if(saleSpecificBookingModel.getVoucher() != null) {
			double amountOff = (totalAmountToPay) * (applicationProperties.getVoucherDiscountAmount() / 100);
			saleSpecificBookingModel.setTotalToPay(
					BigDecimal.valueOf(
							(
									(productTotalToPay) +
									(totalAmountToPay) - saleSpecificBookingModel.getDepositAmount()
							) - amountOff)
							.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
			);
		} else {
			saleSpecificBookingModel.setTotalToPay(
					BigDecimal.valueOf
							(
									(productTotalToPay) +
									(totalAmountToPay) - saleSpecificBookingModel.getDepositAmount()
							)
							.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
			);
		}
		return saleSpecificBookingModel;
	}

	private SalesBookingOverviewModel getAllBookingsForDateCashup(LocalDateTime localDateTime) {

		AtomicInteger totalCaptured = new AtomicInteger();

		List<SalesBookingModel> salesBookingModels =  bookingRepository
				.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOut
						(localDateTime.toDateTime().withTimeAtStartOfDay().getMillis(), localDateTime.toDateTime().plusDays(1).withTimeAtStartOfDay().getMillis(), "Active", false)
				.stream()
				.map(booking -> {
					LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());
					LocalDateTime bookingEndDateTime = new LocalDateTime(booking.getEndDateTime());

					Optional<Sale> optionalSale = saleRepository.findByBooking(booking);

					if(optionalSale.isPresent() && optionalSale.get().isCaptured()) {
						totalCaptured.getAndIncrement();
					}

					// if there is products add it to the list
					List<ProductModel> productModels = new ArrayList<>();

					optionalSale.ifPresent(sale -> {
						if(sale.getCashupItems() != null) {
							productModels.addAll(
									sale.getCashupItems()
									.stream()
									.filter(cashupItem -> cashupItem.getCashupItemType().equals(CashupItemType.PRODUCT))
									.map(salesFactory::buildProductModel)
									.collect(Collectors.toList())
							);

						}
					});

					return new SalesBookingModel(
							booking.getClient().getFirstName() + " " + booking.getClient().getLastName(),
							booking.getClient().getContactDetails().getEmailAddress(),
							bookingStartDateTime.toString(applicationProperties.getLongDateTimeFormatter()) + " - " + bookingEndDateTime.toString(applicationProperties.getLongDateTimeFormatter()),
							stringifyTreatments(booking),
							booking.getId().toString(),
							Double.valueOf(applicationProperties.getDecimalFormat().format(getBookingItemTotal(booking.getBookingList())).replace(",",".")),
							optionalSale.isPresent() && optionalSale.get().isCaptured(),
							productModels
					);
				})
				.collect(Collectors.toList());

		SalesBookingOverviewModel salesBookingOverviewModel = new SalesBookingOverviewModel();
		salesBookingOverviewModel.setSalesBookingModelList(salesBookingModels);
		salesBookingOverviewModel.setOverviewTotal(salesBookingModels.stream().mapToDouble(SalesBookingModel::getBookingTotal).sum());

		salesBookingOverviewModel.setCashupCanBeCompleted(totalCaptured.get() == salesBookingOverviewModel.getSalesBookingModelList().size());

		return salesBookingOverviewModel;
	}

	private void getAllTimeSlotsAndBookingsForEdit(Booking booking, SaleSpecificBookingModel saleSpecificBookingModel) {
		List<SaleTimeSlotModel> saleTimeSlotModels = scheduleServices
				.viewAllTimeSlotsForBlockout("Tuesday")
				.stream()
				.map(timeSlotModel -> {
					SaleTimeSlotModel saleTimeSlotModel = new SaleTimeSlotModel();
					saleTimeSlotModel.setTime(timeSlotModel.getTime());
					saleTimeSlotModel.setBookingStartTime(timeSlotModel.getTime().equals(applicationProperties.getSimpleTimeFormat().format(new DateTime(booking.getStartDateTime()).toDate())));

					return saleTimeSlotModel;
				})
				.collect(Collectors.toList());

		saleSpecificBookingModel.setSaleTimeSlotModels(saleTimeSlotModels);

		final HashMap<String, Integer> treatmentIds = new HashMap<>();

		for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
			treatmentIds.put(bookingListItem.getTreatment().getId().toString(), bookingListItem.getTreatmentQuantity());
		}

		List<SalesBookingItemModel> salesBookingItemModels =
				treatmentRepository
						.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "treatmentName")))
						.stream()
						.filter(Treatment::isActive)
						.map(treatment -> {
							if(treatmentIds.containsKey(treatment.getId().toString())) {
								if(treatment.isSpecial()) {
									return new SalesBookingItemModel(
											treatment.getId().toString(),
											treatment.getTreatmentName(),
											treatment.getTreatmentDescription(),
											Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice())),
											treatment.getDuration(),
											treatmentIds.get(treatment.getId().toString()),
											treatment.isSpecial(),
											Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice()))
									);
								} else {
									return new SalesBookingItemModel(
											treatment.getId().toString(),
											treatment.getTreatmentName(),
											treatment.getTreatmentDescription(),
											Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice())),
											treatment.getDuration(),
											treatmentIds.get(treatment.getId().toString()),
											false,
											0.0
									);
								}
							} else {
								if(treatment.isSpecial()) {
									return new SalesBookingItemModel(
											treatment.getId().toString(),
											treatment.getTreatmentName(),
											treatment.getTreatmentDescription(),
											Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice())),
											treatment.getDuration(),
											0,
											treatment.isSpecial(),
											Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice()))
									);
								} else {
									return new SalesBookingItemModel(
											treatment.getId().toString(),
											treatment.getTreatmentName(),
											treatment.getTreatmentDescription(),
											Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice())),
											treatment.getDuration(),
											0,
											false,
											0.0
									);
								}

							}
						})
						.collect(Collectors.toList());

		saleSpecificBookingModel.setSalesBookingItemModels(salesBookingItemModels);
	}
	@Override
	public SalesBookingOverviewModel startCashingUpSpecificDay(String employeeId, String dayCashingUp) {
		if(employeeId == null || dayCashingUp == null) {
			throw new CashUpException("Please ensure all the needed values are provided.");
		}

		LocalDateTime dayBeingCashedUp = LocalDateTime.parse(dayCashingUp, DateTimeFormat.forPattern("yy-MM-dd"));

		// we can only cash up the current day if the day has already ended
//		validateDayReadyForCashup(applicationConfigurationModel, dayBeingCashedUp);

		Optional<DailyCashup> dailyCashups = dailyCashupRepository
				.findByDateCashingUp(dayBeingCashedUp.toDateTime().getMillis());

		SalesBookingOverviewModel salesBookingOverviewModel = getAllBookingsForDateCashup(dayBeingCashedUp);

		if(!dailyCashups.isPresent()) {
			Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(employeeId));
			if(employeeOptional.isPresent()) {
				DailyCashup dailyCashup = new DailyCashup();
				dailyCashup.setId(new ObjectId());
				dailyCashup.setDateCashingUp(dayBeingCashedUp.toDateTime().withTimeAtStartOfDay().getMillis());
				dailyCashup.setDateTimeStarted(DateTime.now().getMillis());
				dailyCashup.setStartedBy(employeeOptional.get());

				dailyCashup.setStarted(true);

				salesBookingOverviewModel.setCashupAlreadyStarted(false);
				salesBookingOverviewModel.setCashupId(dailyCashup.getId().toString());
				dailyCashupRepository.save(dailyCashup);
			}
		} else {
			if(dailyCashups.get().isCompleted()) {
				throw new DayAlreadyCashedUpException("This day has already been cashed up and cannot be performed again.");
			}

			salesBookingOverviewModel.setCashupAlreadyStarted(true);
			salesBookingOverviewModel.setCashupId(dailyCashups.get().getId().toString());

		}

		return salesBookingOverviewModel;
	}

	@Override
	public SalesEditedBookingModel editBookingAndComplete(SalesEditBookingModel salesEditBookingModel) {

		if(salesEditBookingModel.getBookingId() == null) {
			throw new InvalidIdException("Please ensure a valid booking is provided.");
		}

		if(!ObjectId.isValid(salesEditBookingModel.getBookingId())) {
			throw new InvalidIdException("Please ensure a valid booking is provided.");
		}

		Optional<Booking> bookingOptional = bookingRepository.findById(new ObjectId(salesEditBookingModel.getBookingId()));

		if(!bookingOptional.isPresent()) {
			throw new ResourceNotFoundException("Please ensure a valid booking is provided.");
		}

		Booking booking = bookingOptional.get();
		Optional<Integer> bookingDuration =
				stream(salesEditBookingModel.getSaleEditBookingItemModels())
						.map(saleEditBookingItemModel -> {
							if(!saleEditBookingItemModel.isSpecialOffer()) {
								return treatmentRepository.findById(new ObjectId(saleEditBookingItemModel.getId())).get().getDuration();
							} else {
								return specialPackageRepository.findById(new ObjectId(saleEditBookingItemModel.getId())).get().getDuration();
							}
						})
						.reduce(Integer::sum);

		if(bookingDuration.isPresent()) {

			for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
				bookingListItemRepository.delete(bookingListItem);
			}

			bookingListRepository.delete(booking.getBookingList());
			DateTime proposedStartDateTime = applicationProperties.getLongDateTimeFormatter().parseDateTime(salesEditBookingModel.getStartDateTime());
			DateTime proposedEndDateTime = proposedStartDateTime.plusMinutes(bookingDuration.get());

			// remove the booking treatment list items and add new one
			BookingList bookingList = new BookingList();
			bookingList.setId(new ObjectId());

			booking.setBookingList(bookingList);

			List<BookingListItem> bookingListItems = Arrays
					.stream(salesEditBookingModel.getSaleEditBookingItemModels())
					.map(saleEditBookingItemModel -> {
						BookingListItem bookingListItem = new BookingListItem();
						bookingListItem.setId(new ObjectId());

						if(!saleEditBookingItemModel.isSpecialOffer()) {
							Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(saleEditBookingItemModel.getId()));
							if(treatmentOptional.isPresent()) {
								bookingListItem.setTreatmentQuantity(saleEditBookingItemModel.getQuantity());
								bookingListItem.setTreatment(treatmentOptional.get());
							}
						} else {
							Optional<SpecialPackage> specialPackageOptional = specialPackageRepository.findById(new ObjectId(saleEditBookingItemModel.getId()));
							if(specialPackageOptional.isPresent()) {
								bookingListItem.setSpecialQuantity(saleEditBookingItemModel.getQuantity());
								bookingListItem.setSpecials(specialPackageOptional.get());
							}
						}

						bookingListItemRepository.save(bookingListItem);

						return bookingListItem;
					})
					.collect(Collectors.toList());


			bookingList.setBookingListItems(bookingListItems);

			bookingListRepository.save(bookingList);

			booking.setBookingList(bookingList);
			booking.setDuration(bookingDuration.get());
			booking.setStartDateTime(proposedStartDateTime.getMillis());
			booking.setEndDateTime(proposedEndDateTime.getMillis());

			bookingRepository.save(booking);
		}
		return new SalesEditedBookingModel("The booking was updated and completed successfully.");
	}

	@Override
	public SalesItemCaptured captureSalesItem(SalesItemToCaptureModel salesItemToCaptureModel) {
		Optional<Booking> bookingOptional = bookingRepository.findById(new ObjectId(salesItemToCaptureModel.getBookingId()));

		if(!bookingOptional.isPresent()) {
			throw new InvalidIdException("Please ensure a valid booking is provided.");
		}

		Booking booking = bookingOptional.get();

		Optional<Sale> optionalSale = saleRepository.findByBooking(booking);

		// if the booking already has a sale attached to it then use that
		Sale sale;
		if(optionalSale.isPresent()) {
			sale = optionalSale.get();
		} else {
			sale = new Sale();
			sale.setAssistedBy(booking.getEmployee());
			sale.setSaleTo(booking.getClient());
			sale.setDateTimeOfSale(booking.getStartDateTime());
			sale.setBooking(booking);
		}

		double saleTotalPrice = 0.0;
		double bookingTotalPrice = 0.0;
		List<CashupItem> cashupItems = new ArrayList<>();
		for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
			CashupItem cashupItem = new CashupItem();
			cashupItem.setCashupItemType(CashupItemType.BOOKING);

			if(bookingListItem.getTreatment() != null) {
				cashupItem.setDuration(bookingListItem.getTreatment().getDuration());
				cashupItem.setItemName(bookingListItem.getTreatment().getTreatmentName());
				cashupItem.setQuantity(bookingListItem.getTreatmentQuantity());
				if(!bookingListItem.getTreatment().isSpecial()) {
					if(bookingListItem.getTreatment().isDoneByJunior()) {
						cashupItem.setItemPrice(bookingListItem.getTreatment().getJuniorPrice());
						cashupItem.setTotalPrice(
								Double.parseDouble(applicationProperties.getDecimalFormat()
										.format((bookingListItem.getTreatment().getJuniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."))
						);
					} else {
						cashupItem.setItemPrice(bookingListItem.getTreatment().getSeniorPrice());
						cashupItem.setTotalPrice(
								Double.parseDouble(applicationProperties.getDecimalFormat()
										.format((bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."))
						);
					}
				} else {
					cashupItem.setItemPrice(bookingListItem.getTreatment().getSpecialPrice());
					cashupItem.setTotalPrice(
							Double.parseDouble(applicationProperties.getDecimalFormat()
									.format((bookingListItem.getTreatment().getSpecialPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."))
					);
				}
				cashupItem.setServiceItemId(bookingListItem.getTreatment().getId());
				cashupItem.setSpecial(false);
				saleTotalPrice += cashupItem.getTotalPrice();
				bookingTotalPrice += cashupItem.getTotalPrice();
			} else {
				cashupItem.setDuration(bookingListItem.getSpecials().getDuration());
				cashupItem.setItemName(bookingListItem.getSpecials().getSpecialName());
				cashupItem.setQuantity(bookingListItem.getSpecialQuantity());
				if(bookingListItem.getSpecials().isDoneByJunior()) {
					cashupItem.setItemPrice(bookingListItem.getSpecials().getJuniorPrice());
					cashupItem.setTotalPrice(
							Double.parseDouble(applicationProperties.getDecimalFormat()
									.format((bookingListItem.getSpecials().getJuniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."))
					);
				} else {
					cashupItem.setItemPrice(bookingListItem.getSpecials().getSeniorPrice());
					cashupItem.setTotalPrice(
							Double.parseDouble(applicationProperties.getDecimalFormat()
									.format((bookingListItem.getSpecials().getSeniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."))
					);
				}
				cashupItem.setServiceItemId(bookingListItem.getSpecials().getId());
				cashupItem.setSpecial(true);
				saleTotalPrice += cashupItem.getTotalPrice();
				bookingTotalPrice += cashupItem.getTotalPrice();
			}

			cashupItems.add(cashupItem);
		}

		double productTotalPrice = 0.0;
		if(optionalSale.isPresent()) { // Adds product totals to booking total incase of products purchased
			if(optionalSale.get().getCashupItems() != null && optionalSale.get().getCashupItems().size() > 0) {
				productTotalPrice += optionalSale
						.get()
						.getCashupItems()
						.stream()
						.filter(cashupItem -> cashupItem.getCashupItemType().equals(CashupItemType.PRODUCT))
						.reduce(
								0.00,
								(partialResult, cashupItem2) ->
										partialResult + Double.parseDouble(applicationProperties.getDecimalFormat().format((cashupItem2.getItemPrice() * cashupItem2.getQuantity()))
										.replace(",",".")),
								Double::sum
						);

				saleTotalPrice += productTotalPrice;
			}
		}

		sale.setTotalSalePrice(saleTotalPrice);

		if(optionalSale.isPresent()) {
			sale.getCashupItems().addAll(cashupItems);
		} else {
			sale.setCashupItems(cashupItems);
		}

		sale.setDiscounted(salesItemToCaptureModel.isDiscounted());
		if(salesItemToCaptureModel.isDiscounted()) {
			sale.setDiscountPercentage(salesItemToCaptureModel.getDiscountPercentage());
		}

		if(!salesItemToCaptureModel.getVoucherNumber().equals("")) {
			sale.setVoucherNumber(salesItemToCaptureModel.getVoucherNumber());
		}

		sale.setTotalCardPaid(salesItemToCaptureModel.getTotalCardPaid());
		sale.setTotalCashPaid(salesItemToCaptureModel.getTotalCashPaid());
		sale.setTotalEFTPaid(salesItemToCaptureModel.getTotalEFTPaid());
		double totalPaymentsMade = sale.getTotalCardPaid() + sale.getTotalCashPaid() + sale.getTotalEFTPaid() + sale.getTotalVoucherPaid();

		if(booking.isDepositPaid()) {
			sale.setDepositPaid(booking.getTransaction().getAmount());

			saleTotalPrice -= sale.getDepositPaid(); // remove amount paid in deposit
		}

		if(salesItemToCaptureModel.isDiscounted()) {

			// if there is a voucher used then its 10% of booking not products
			if(sale.getVoucherNumber() != null && !sale.getVoucherNumber().equals("")) {
				// get voucher number ensure it is still valid
				if(loyaltyPointsService.isVoucherRedeemed(sale.getSaleTo(), sale.getVoucherNumber())) {
					throw new CashUpException("The following voucher has already been redeemed. Please use another voucher number.");
				}


				double amountOff = bookingTotalPrice * (applicationProperties.getVoucherDiscountAmount() / 100);
				sale.setTotalSalePrice(
						BigDecimal.valueOf(
								(
										(productTotalPrice) +
										(bookingTotalPrice) - sale.getDepositPaid()
								) - amountOff)
								.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
				);

				BigDecimal totalPaymentMadeValue = new BigDecimal(totalPaymentsMade);
				BigDecimal totalPriceValue = new BigDecimal(sale.getTotalSalePrice());
				if(totalPaymentMadeValue.setScale(2, RoundingMode.HALF_UP).compareTo(totalPriceValue.setScale(2, RoundingMode.HALF_UP)) != 0) {
					throw new CashUpException("Please ensure the total payments match up to the total booking price including the discount.");
				}
			} else {
				double amountOff = saleTotalPrice * (salesItemToCaptureModel.getDiscountPercentage() / 100);

				sale.setTotalSalePrice(saleTotalPrice - amountOff);

				BigDecimal totalPaymentMadeValue = new BigDecimal(totalPaymentsMade);
				BigDecimal totalPriceValue = new BigDecimal(sale.getTotalSalePrice());
				if(totalPaymentMadeValue.setScale(2, RoundingMode.HALF_UP).compareTo(totalPriceValue.setScale(2, RoundingMode.HALF_UP)) != 0) {
					throw new CashUpException("Please ensure the total payments match up to the total booking price including the discount.");
				}
			}
		} else {
			BigDecimal totalPaymentMadeValue = new BigDecimal(totalPaymentsMade);
			BigDecimal totalPriceValue = new BigDecimal(sale.getTotalSalePrice());

			if(totalPaymentMadeValue.setScale(2, RoundingMode.HALF_UP).compareTo(totalPriceValue.setScale(2, RoundingMode.HALF_UP)) != 0) {
				throw new CashUpException("Please ensure the total payments match up to the total booking price.");
			}
		}

		sale.setCaptured(true);
		saleRepository.save(sale);

		Optional<DailyCashup> dailyCashupOptional = dailyCashupRepository.findById(new ObjectId(salesItemToCaptureModel.getCashUpId()));

		if(!dailyCashupOptional.isPresent()) {
			throw new InvalidIdException("Please ensure a valid cash up is provided.");
		}

		DailyCashup dailyCashup = dailyCashupOptional.get();
		dailyCashup.getSales().add(sale);
		dailyCashup.setDateTimeUpdated(DateTime.now().getMillis());

		dailyCashupRepository.save(dailyCashup);

		// if voucher number used then changed to redeemed
		if(sale.getVoucherNumber() != null && !sale.getVoucherNumber().equals("")) {
			Client client = sale.getSaleTo();
			client.getVouchers()
					.stream()
					.filter(voucher -> voucher.getVoucherNumber().equals(sale.getVoucherNumber()))
					.findFirst()
					.ifPresent(voucher -> {
						voucher.setRedeemed(true);
						voucher.setValid(false);
						voucher.setRedeemedDate(DateTime.now().toDate().getTime());
					});

			clientRepository.save(client);
		}
		captureLoyaltyPoints(sale);

		return new SalesItemCaptured("We have successfully captured the sales item.");
	}

	private void captureLoyaltyPoints(Sale sale) {
		int pointsToAdd = 0;
		double totalBookingValue = sale
				.getCashupItems()
				.stream()
				.filter(cashupItem -> cashupItem.getCashupItemType().equals(CashupItemType.BOOKING))
				.reduce(0.00, (partialResult, cashupItem2) -> partialResult + Double.parseDouble(applicationProperties.getDecimalFormat().format((cashupItem2.getItemPrice() * cashupItem2.getQuantity())).replace(",",".")),
						Double::sum);
		if(totalBookingValue > 2001) {
			pointsToAdd = 10;
		} else if(totalBookingValue > 1501 && totalBookingValue <= 2000) {
			pointsToAdd = 9;
		} else if(totalBookingValue > 1001 && totalBookingValue <= 1500) {
			pointsToAdd = 8;
		} else if(totalBookingValue > 851 && totalBookingValue <= 1000) {
			pointsToAdd = 7;
		} else if(totalBookingValue > 701 && totalBookingValue <= 850) {
			pointsToAdd = 6;
		} else if(totalBookingValue > 551 && totalBookingValue <= 700) {
			pointsToAdd = 5;
		} else if(totalBookingValue > 401 && totalBookingValue <= 550) {
			pointsToAdd = 4;
		} else if(totalBookingValue > 251 && totalBookingValue <= 400) {
			pointsToAdd = 3;
		} else if(totalBookingValue > 101 && totalBookingValue <= 250) {
			pointsToAdd = 2;
		} else if(totalBookingValue > 50 && totalBookingValue <= 100) {
			pointsToAdd = 1;
		}

		loyaltyPointsService.addPointsToClientAccount(pointsToAdd, sale.getSaleTo());
	}

	@Override
	public SalesItemCaptured captureSalesProductItem(ProductItemToCaptureModel productItemToCaptureModel) {
		Optional<Booking> bookingOptional = bookingRepository.findById(new ObjectId(productItemToCaptureModel.getBookingId()));

		if(!bookingOptional.isPresent()) {
			throw new InvalidIdException("Please ensure a valid booking is provided.");
		}

		Booking booking = bookingOptional.get();
		Optional<Sale> optionalSale = saleRepository.findByBooking(booking);
		// add products to booking and to sale
		Sale sale;
		if(optionalSale.isPresent()) {
			sale = optionalSale.get();
		} else {
			sale = new Sale();
			sale.setAssistedBy(booking.getEmployee());
			sale.setSaleTo(booking.getClient());
			sale.setDateTimeOfSale(booking.getStartDateTime());
			sale.setCaptured(false);
			sale.setBooking(booking);
		}

		List<CashupItem> cashupItems = new ArrayList<>();
		for(ProductItemModel productItemModel: productItemToCaptureModel.getProducts()) {
			productRepository.findById(new ObjectId(productItemModel.getId()))
					.ifPresent(product -> {
						CashupItem cashupItem = new CashupItem();
						cashupItem.setCashupItemType(CashupItemType.PRODUCT);
						cashupItem.setItemName(product.getProductName());
						cashupItem.setQuantity(productItemModel.getQuantity());
						cashupItem.setItemPrice(product.getPrice());
						cashupItem.setTotalPrice(
								BigDecimal.valueOf(productItemModel.getQuantity() * product.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
						);
						cashupItem.setServiceItemId(new ObjectId(productItemModel.getId()));
						cashupItems.add(cashupItem);
					});
		}

		sale.setCashupItems(cashupItems);

		saleRepository.save(sale);

		return new SalesItemCaptured("We have successfully captured the sales item.");
	}

	@Override
	public SalesCashupCompletedModel completeCashup(String authorization, SalesCashupCompleteModel salesCashupCompleteModel) {
		if(salesCashupCompleteModel.getCashUpId() == null) {
			throw new InvalidIdException("Please ensure a valid cash up is provided.");
		}

		if(!ObjectId.isValid(salesCashupCompleteModel.getCashUpId())) {
			throw new InvalidIdException("Please ensure a valid cash up is provided.");
		}

		Optional<DailyCashup> dailyCashupOptional = dailyCashupRepository.findById(new ObjectId(salesCashupCompleteModel.getCashUpId()));

		if(!dailyCashupOptional.isPresent()) {
			throw new InvalidIdException("Please ensure a valid cash up is provided.");
		}

		DailyCashup dailyCashup = dailyCashupOptional.get();

		List<Client> clientsToEmail = new ArrayList<>();
		List<Sale> sales = bookingRepository
				.findAllByBookingStatus("Active")
				.stream()
				.filter(booking -> booking.getBlockedDayTitle() == null)
				.filter(booking -> {

					LocalDateTime localDateTime = new LocalDateTime(dailyCashup.getDateCashingUp());

					LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());

					return localDateTime.toDateTime().withTimeAtStartOfDay().isEqual(bookingStartDateTime.toDateTime().withTimeAtStartOfDay());
				})
				.map(booking -> {
					Sale sale = new Sale();
					sale.setAssistedBy(booking.getEmployee());
					sale.setSaleTo(booking.getClient());
					sale.setDateTimeOfSale(booking.getStartDateTime());

					double saleTotalPrice = 0.0;
					for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
						double bookingTotalPrice;
						if(!bookingListItem.getTreatment().isSpecial()) {
							if(bookingListItem.getTreatment().isDoneByJunior()) {
								bookingTotalPrice = Double.parseDouble(applicationProperties.getDecimalFormat()
										.format((bookingListItem.getTreatment().getJuniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."));
							} else {
								bookingTotalPrice =
										Double.parseDouble(applicationProperties.getDecimalFormat()
												.format((bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."));
							}
						} else {
							bookingTotalPrice =
									Double.parseDouble(applicationProperties.getDecimalFormat()
											.format((bookingListItem.getTreatment().getSpecialPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."));
						}

						saleTotalPrice += bookingTotalPrice;
					}

					sale.setTotalSalePrice(saleTotalPrice);

					clientsToEmail.add(sale.getSaleTo());
					return sale;
				})
				.collect(Collectors.toList());

		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(
				this.authTokenServices.extractUserId(authorization)
		));

		if(!employeeOptional.isPresent()) {
			throw new InvalidIdException("Please ensure a valid employee is provided.");
		}

		dailyCashup.setDateTimeCompleted(DateTime.now().getMillis());
		dailyCashup.setCompleted(true);
		dailyCashup.setCashedUpBy(employeeOptional.get());

		Optional<Double> daysTotal =
				sales.
				stream()
				.map(Sale::getTotalSalePrice)
				.reduce(Double::sum);

		daysTotal
				.ifPresent(dailyCashup::setDaysTakings);

		dailyCashupRepository.save(dailyCashup);

		sendFeedbackEmails(clientsToEmail);

		return new SalesCashupCompletedModel("Your cash up was completed successfully.");
	}

	private void sendFeedbackEmails(List<Client> clients) {
		clients
				.forEach(emailService::sendClientFeedbackEmail);
	}
	@Override
	public BookingCreatedModel createBookingForClientDuringCashUp(SaleNewBookingModel saleNewBookingModel) {
		NewBookingModel newBookingModel = new NewBookingModel();
		newBookingModel.setEmployeeId(saleNewBookingModel.getEmployeeId());
		newBookingModel.setStartDateTime(saleNewBookingModel.getStartDateTime());

		ModelMapper modelMapper = new ModelMapper();

		NewBookingItemModel[] newBookingItemModel = new NewBookingItemModel[saleNewBookingModel.getSaleNewBookingItemModels().length];

		for(int i = 0; i < saleNewBookingModel.getSaleNewBookingItemModels().length; i++) {
			newBookingItemModel[i] = modelMapper.map(saleNewBookingModel.getSaleNewBookingItemModels()[i], NewBookingItemModel.class);
		}

		newBookingModel.setNewBookingItemModel(newBookingItemModel);

		this.bookingValidationService.skipValidateAndCreate(saleNewBookingModel.getClientId(), newBookingModel, BookingCreatedBy.EMPLOYEE);

		BookingCreatedModel bookingCreatedModel = new BookingCreatedModel();
		bookingCreatedModel.setMessage("You're booking was made successfully as part of the cash up process.");
		return bookingCreatedModel;
	}

	private Double getBookingItemTotal(BookingList bookingList) {
		double bookingItemPrice = 0.0;
		for (BookingListItem bookingListItem : bookingList.getBookingListItems()) {
			if(!bookingListItem.getTreatment().isSpecial()) {
				if(bookingListItem.getTreatment().isDoneByJunior()) {
					bookingItemPrice += Double.parseDouble(applicationProperties.getDecimalFormat()
							.format((bookingListItem.getTreatment().getJuniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."));
				} else {
					bookingItemPrice +=
							Double.parseDouble(applicationProperties.getDecimalFormat()
									.format((bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."));
				}
			} else {
				bookingItemPrice +=
						Double.parseDouble(applicationProperties.getDecimalFormat()
								.format((bookingListItem.getTreatment().getSpecialPrice() * bookingListItem.getTreatmentQuantity())).replace(",","."));
			}
		}

		return bookingItemPrice;
	}

	private String stringifyTreatments(Booking booking) {
		StringBuilder stringBuilder = new StringBuilder();

		for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
			if(bookingListItem.getSpecials() != null) { // Legacy Specials Tool
				if(bookingListItem.getSpecials().isDoneByJunior()) {
					stringBuilder
							.append(bookingListItem.getSpecialQuantity())
							.append(" x ")
							.append(bookingListItem.getSpecials().getSpecialName())
							.append(" = R ")
							.append(bookingListItem.getSpecialQuantity() * Double.parseDouble(applicationProperties.getDecimalFormat().format(bookingListItem.getSpecials().getJuniorPrice()))).append("<br/>");
				} else {
					stringBuilder
							.append(bookingListItem.getSpecialQuantity())
							.append(" x ")
							.append(bookingListItem.getSpecials().getSpecialName())
							.append(" = R ")
							.append(bookingListItem.getSpecialQuantity() * Double.parseDouble(applicationProperties.getDecimalFormat().format(bookingListItem.getSpecials().getSeniorPrice()))).append("<br/>");
				}
			} else {
				if(bookingListItem.getTreatment().isSpecial()) {
					stringBuilder
							.append(bookingListItem.getTreatmentQuantity())
							.append(" x ")
							.append(bookingListItem.getTreatment().getTreatmentName())
							.append(" = R ")
							.append(bookingListItem.getTreatmentQuantity() * Double.parseDouble(applicationProperties.getDecimalFormat().format(bookingListItem.getTreatment().getSpecialPrice())))
							.append("<br/>");
				} else {
					if(bookingListItem.getTreatment().isDoneByJunior()) {
						stringBuilder
								.append(bookingListItem.getTreatmentQuantity())
								.append(" x ")
								.append(bookingListItem.getTreatment().getTreatmentName())
								.append(" = R ")
								.append(bookingListItem.getTreatmentQuantity() * Double.parseDouble(applicationProperties.getDecimalFormat().format(bookingListItem.getTreatment().getJuniorPrice())))
								.append("<br/>");
					} else {
						stringBuilder
								.append(bookingListItem.getTreatmentQuantity())
								.append(" x ")
								.append(bookingListItem.getTreatment().getTreatmentName())
								.append(" = R ")
								.append(bookingListItem.getTreatmentQuantity() * Double.parseDouble(applicationProperties.getDecimalFormat().format(bookingListItem.getTreatment().getSeniorPrice())))
								.append("<br/>");
					}
				}
			}
		}

		return stringBuilder.toString();
	}
}
