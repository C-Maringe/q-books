package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.reportingExceptions.MissingUsernameException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.reportingModels.*;
import com.qbook.app.application.services.appservices.ReportsServices;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.BookingRepository;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.DailyCashupRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.LocalDateTime;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class ReportsServicesImpl implements ReportsServices {
	private final BookingRepository bookingRepository;
	private final ClientRepository clientRepository;
	private final DailyCashupRepository dailyCashupRepository;
	private final ModelMapper modelMapper;
	private final ApplicationProperties applicationProperties;

	@Override
	public ReportBookingOverviewModel getAllBookingsDuring(Long from, Long to, String filter, String userFilter, String employeeFilter) {

		LocalDateTime bookingStartDateTime = new LocalDateTime(from);
		LocalDateTime february2020 = new LocalDateTime(2020, 2, 29, 0, 0);
		if(bookingStartDateTime.isBefore(february2020)) {
			return buildGeneralOldReport(from, to, filter, userFilter, employeeFilter);
		} else {
			return returnBookingsPostNewFinancialYear(from, to, userFilter, employeeFilter);
		}
	}

	private ReportBookingOverviewModel buildGeneralOldReport(Long from, Long to, String filter, String userFilter, String employeeFilter) {
		List<ReportBookingModel> reportBookingModels = returnBookingsForReport(
				from,
				to,
				filter,
				userFilter,
				employeeFilter
		)
				.stream()
				.map(booking -> {
					LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());
					LocalDateTime bookingEndDateTime = new LocalDateTime(booking.getEndDateTime());

					String bookingSlot = bookingStartDateTime.toString(applicationProperties.getLongDateTimeFormatter()) + " - " + bookingEndDateTime.toString(applicationProperties.getLongDateTimeFormatter());
					String stringifiedTreatments = stringifyTreatments(booking);
					String employeeFullName = booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName();
					String clientFullName = booking.getClient().getFirstName() + " " + booking.getClient().getLastName();
					String clientEmail = booking.getClient().getContactDetails().getEmailAddress();

					ReportBookingModel reportBookingModel = new ReportBookingModel();
					reportBookingModel.setClientId(booking.getClient().getId().toString());
					reportBookingModel.setClientFullName(clientFullName);
					reportBookingModel.setClientEmail(clientEmail);
					reportBookingModel.setBookingSlot(bookingSlot);
					reportBookingModel.setTreatments(stringifiedTreatments);
					reportBookingModel.setEmployeeFullName(employeeFullName);

					int totalTimeSpentOnBooking = 0;
					double totalRevenueFromBooking = 0.0;

					for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
						if(bookingListItem.getSpecials() != null) {
							totalTimeSpentOnBooking += bookingListItem.getSpecials().getDuration();
							totalRevenueFromBooking += (bookingListItem.getSpecials().getSeniorPrice() * bookingListItem.getSpecialQuantity());
						} else {
							totalTimeSpentOnBooking += bookingListItem.getTreatment().getDuration();
							if(!bookingListItem.getTreatment().isSpecial()) {
								if(bookingListItem.getTreatment().isDoneByJunior()) {
									totalRevenueFromBooking += (bookingListItem.getTreatment().getJuniorPrice() * bookingListItem.getTreatmentQuantity());
								} else {
									totalRevenueFromBooking += (bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity());
								}
							} else {
								totalRevenueFromBooking += (bookingListItem.getTreatment().getSpecialPrice() * bookingListItem.getTreatmentQuantity());
							}
						}
					}
					reportBookingModel.setTotalRevenueForBooking(totalRevenueFromBooking);
					reportBookingModel.setTotalRevenueForBookingIncludingVAT(Double.parseDouble(applicationProperties.getDecimalFormat().format(totalRevenueFromBooking)));
					reportBookingModel.setTotalTimeSpentOnBooking(totalTimeSpentOnBooking);

					return reportBookingModel;
				})
				.collect(Collectors.toList());

		ReportBookingOverviewModel reportBookingOverviewModel = new ReportBookingOverviewModel();
		reportBookingOverviewModel.setReportBookingModels(reportBookingModels);

		int totalTimeSpentOnAllBookings = 0;
		double totalRevenueFromAllBookings = 0.0;

		for(ReportBookingModel reportBookingModel: reportBookingModels) {
			totalTimeSpentOnAllBookings += reportBookingModel.getTotalTimeSpentOnBooking();
			totalRevenueFromAllBookings += reportBookingModel.getTotalRevenueForBooking();
		}

		reportBookingOverviewModel.setTotalTimeWorked(totalTimeSpentOnAllBookings);
		reportBookingOverviewModel.setTotalRevenue(totalRevenueFromAllBookings);
		reportBookingOverviewModel.setTotalRevenueInclVAT(Double.parseDouble(applicationProperties.getDecimalFormat().format(totalRevenueFromAllBookings)));
		return reportBookingOverviewModel;
	}

	private List<Booking> returnBookingsForReport(Long from, Long to,String filter, String userFilter, String employeeFilter) {
		List<Booking> allByFilter;
		//query all bookings where booking start time is >= from and end time is <= to and status is equal to active or cancelled
		if(filter.equals("Active") || filter.equals("Cancelled")){
			allByFilter = bookingRepository.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOut(
					from, to, filter, false
			);
		} else {
			allByFilter = bookingRepository.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndDayToBlockOut(
					from, to, false
			);
		}

		List<Booking> allByUsers = new ArrayList<>();
		//filter by user
		if(!userFilter.equals("All")){
			for(Booking b: allByFilter){
				if(!b.isDayToBlockOut()) {
					if (b.getClient().getContactDetails().getEmailAddress().equals(userFilter)) {
						allByUsers.add(b);
					}
				}
			}
		}else{
			allByUsers = allByFilter;
		}

		List<Booking> allByEmployee = new ArrayList<>();
		//filter by user
		if(!employeeFilter.equals("All")){
			for(Booking b: allByUsers){
				if(b.getEmployee().getContactDetails().getEmailAddress().equals(employeeFilter)){
					allByEmployee.add(b);
				}
			}
		} else{
			allByEmployee = allByUsers;
		}
		return allByEmployee;
	}

	private ReportBookingOverviewModel returnBookingsPostNewFinancialYear(Long from, Long to, String userFilter, String employeeFilter) {
		List<DailyCashup> dailyCashups = dailyCashupRepository.findAllByDateCashingUpBetweenAndCompletedOrderByDateTimeCompletedDesc(
				from,
				to,
				true
		);

		List<ReportExtendedBookingModel> reportBookingModels = new ArrayList<>();

		for(DailyCashup dailyCashup: dailyCashups) {
			reportBookingModels.addAll(dailyCashup
					.getSales()
					.stream()
					.filter(sale -> {
						if(userFilter.equals("All")) {
							return true;
						} else {
							return sale.getBooking().getClient().getContactDetails().getEmailAddress().equals(userFilter);
						}
					})
					.filter(sale -> {
						if(employeeFilter.equals("All")) {
							return true;
						} else {
							return sale.getAssistedBy().getContactDetails().getEmailAddress().equals(employeeFilter);
						}
					})
					.filter(sale -> sale.getBooking() != null)
					.map(sale -> {
						LocalDateTime bookingStartDateTime = new LocalDateTime(sale.getBooking().getStartDateTime());
						LocalDateTime bookingEndDateTime = new LocalDateTime(sale.getBooking().getEndDateTime());

						String bookingSlot = bookingStartDateTime.toString(applicationProperties.getLongDateTimeFormatter()) + " - " + bookingEndDateTime.toString(applicationProperties.getLongDateTimeFormatter());
						String stringifiedTreatments = stringifyTreatments(sale.getBooking());
						String stringifiedProducts = stringifyProducts(sale);
						String employeeFullName = sale.getAssistedBy().getFirstName() + " " + sale.getAssistedBy().getLastName();
						String clientFullName = sale.getSaleTo().getFirstName() + " " + sale.getSaleTo().getLastName();
						String clientEmail = sale.getSaleTo().getContactDetails().getEmailAddress();
						ReportExtendedBookingModel reportBookingModel = new ReportExtendedBookingModel();
						reportBookingModel.setClientId(sale.getSaleTo().getId().toString());
						reportBookingModel.setClientFullName(clientFullName);
						reportBookingModel.setClientEmail(clientEmail);
						reportBookingModel.setBookingSlot(bookingSlot);
						if(!stringifiedProducts.equals("")) {
							reportBookingModel.setTreatments(stringifiedTreatments + "<br> <b>Products</b><br>" + stringifiedProducts);
						} else {
							reportBookingModel.setTreatments(stringifiedTreatments);
						}
						reportBookingModel.setEmployeeFullName(employeeFullName);

						double amountExVat = sale.getTotalSalePrice() - (sale.getTotalSalePrice() * 0.15);
						reportBookingModel.setTotalRevenueForBooking(BigDecimal.valueOf(amountExVat).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						reportBookingModel.setTotalRevenueForBookingIncludingVAT(BigDecimal.valueOf(sale.getTotalSalePrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
						reportBookingModel.setCardPaymentTotal(sale.getTotalCardPaid());
						reportBookingModel.setCashTotal(sale.getTotalCashPaid());
						reportBookingModel.setEftTotal(sale.getTotalEFTPaid());
						reportBookingModel.setOtherTotal(sale.getTotalVoucherPaid());
						if(sale.getDepositPaid() > 0.0) {
							reportBookingModel.setDepositPaid(true);
							reportBookingModel.setDepositAmount(sale.getDepositPaid());
						}
						if(sale.getBooking().getBookingList() != null && sale.getBooking().getBookingList().getBookingListItems().size() > 0) {
							reportBookingModel.setTotalTimeSpentOnBooking(
									sale
											.getBooking()
											.getBookingList()
											.getBookingListItems()
											.stream()
											.map(bookingListItem -> {
												if (bookingListItem.getTreatment() != null) {
													return bookingListItem.getTreatment().getDuration();
												} else {
													return bookingListItem.getSpecials().getDuration();
												}
											})
											.reduce((Integer::sum))
											.get()
							);
						}
						return reportBookingModel;
					})
					.collect(Collectors.toList()));
		}

		ReportBookingExtendedOverviewModel reportBookingExtendedOverviewModel = new ReportBookingExtendedOverviewModel();
		reportBookingExtendedOverviewModel.setReportExtendedBookingModels(reportBookingModels);

		int totalTimeSpentOnAllBookings = 0;
		double totalRevenueFromAllBookings = 0.0;
		double totalRevenueFromAllBookingsPlusVAT = 0.0;

		for(ReportExtendedBookingModel reportBookingModel: reportBookingModels) {
			totalTimeSpentOnAllBookings += reportBookingModel.getTotalTimeSpentOnBooking();
			totalRevenueFromAllBookings += reportBookingModel.getTotalRevenueForBooking();
			totalRevenueFromAllBookingsPlusVAT += reportBookingModel.getTotalRevenueForBookingIncludingVAT();
		}
		reportBookingExtendedOverviewModel.setTotalTimeWorked(totalTimeSpentOnAllBookings);
		reportBookingExtendedOverviewModel.setTotalRevenue(BigDecimal.valueOf(totalRevenueFromAllBookings).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		reportBookingExtendedOverviewModel.setTotalRevenueInclVAT(BigDecimal.valueOf(totalRevenueFromAllBookingsPlusVAT).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		return reportBookingExtendedOverviewModel;
	}

	@Override
	public ReportBookingOverviewModel getAllBookingsDuringForClientInvoice(Long from, Long to, String userFilter, String employeeFilter) {
		List<ReportBookingModel> reportBookingModels = returnBookingsForReport(
				from,
				to,
				"Active",
				userFilter,
				employeeFilter
		)
				.stream()
				.map(booking -> {
					LocalDateTime bookingStartDateTime = new LocalDateTime(booking.getStartDateTime());
					LocalDateTime bookingEndDateTime = new LocalDateTime(booking.getEndDateTime());

					String bookingSlot = bookingStartDateTime.toString(applicationProperties.getLongDateTimeFormatter()) + " - " + bookingEndDateTime.toString(applicationProperties.getLongDateTimeFormatter());
					String stringifiedTreatments = stringifyTreatmentsForEmail(booking);
					String employeeFullName = booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName();
					String clientFullName = booking.getClient().getFirstName() + " " + booking.getClient().getLastName();
					String clientEmail = booking.getClient().getContactDetails().getEmailAddress();

					ReportBookingModel reportBookingModel = new ReportBookingModel();
					reportBookingModel.setClientId(booking.getClient().getId().toString());
					reportBookingModel.setClientFullName(clientFullName);
					reportBookingModel.setClientEmail(clientEmail);
					reportBookingModel.setBookingSlot(bookingSlot);
					reportBookingModel.setTreatments(stringifiedTreatments);
					reportBookingModel.setEmployeeFullName(employeeFullName);
					if(booking.isDepositPaid()) {
						reportBookingModel.setDepositPaid(true);
						reportBookingModel.setDepositAmount(booking.getTransaction().getAmount());
					}
					int totalTimeSpentOnBooking = 0;
					double totalRevenueFromBooking = 0.0;

					for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
						if(bookingListItem.getSpecials() != null) {
							totalTimeSpentOnBooking += bookingListItem.getSpecials().getDuration();
							totalRevenueFromBooking += (bookingListItem.getSpecials().getSeniorPrice() * bookingListItem.getSpecialQuantity());
						} else {

							totalTimeSpentOnBooking += bookingListItem.getTreatment().getDuration();
							if(!bookingListItem.getTreatment().isSpecial()) {
								totalRevenueFromBooking += (bookingListItem.getTreatment().getSeniorPrice() * bookingListItem.getTreatmentQuantity());
							} else {
								totalRevenueFromBooking += (bookingListItem.getTreatment().getSpecialPrice() * bookingListItem.getTreatmentQuantity());
							}
						}
					}

					reportBookingModel.setTotalRevenueForBooking(totalRevenueFromBooking);
					reportBookingModel.setTotalRevenueForBookingIncludingVAT(Double.parseDouble(applicationProperties.getDecimalFormat().format(totalRevenueFromBooking)));
					reportBookingModel.setTotalTimeSpentOnBooking(totalTimeSpentOnBooking);

					return reportBookingModel;
				})
				.collect(Collectors.toList());

		ReportBookingOverviewModel reportBookingOverviewModel = new ReportBookingOverviewModel();
		reportBookingOverviewModel.setReportBookingModels(reportBookingModels);

		int totalTimeSpentOnAllBookings = 0;
		double totalRevenueFromAllBookings = 0.0;

		for(ReportBookingModel reportBookingModel: reportBookingModels) {
			totalTimeSpentOnAllBookings += reportBookingModel.getTotalTimeSpentOnBooking();
			totalRevenueFromAllBookings += reportBookingModel.getTotalRevenueForBooking();
		}

		reportBookingOverviewModel.setTotalTimeWorked(totalTimeSpentOnAllBookings);
		reportBookingOverviewModel.setTotalRevenue(totalRevenueFromAllBookings);
		return reportBookingOverviewModel;
	}

	private String stringifyProducts(Sale sale) {
		StringBuilder stringyFideItems = new StringBuilder();

		for(CashupItem cashupItem: sale.getCashupItems()) {
			if(cashupItem.getCashupItemType().equals(CashupItemType.PRODUCT)) {
				stringyFideItems.append(cashupItem.getQuantity()).append(" x ").append(cashupItem.getItemName()).append("<br/>");
			}
		}

		return stringyFideItems.toString();
	}

	private String stringifyTreatments(Booking booking) {
		StringBuilder stringyFideItems = new StringBuilder();

		for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
			if(bookingListItem.getSpecials() != null) {
				stringyFideItems.append(bookingListItem.getSpecialQuantity()).append(" x ").append(bookingListItem.getSpecials().getSpecialName()).append("<br/>");
			} else {
				stringyFideItems.append(bookingListItem.getTreatmentQuantity()).append(" x ").append(bookingListItem.getTreatment().getTreatmentName()).append("<br/>");
			}
		}

		return stringyFideItems.toString();
	}

	private String stringifyTreatmentsForEmail(Booking booking) {
		StringBuilder stringyFideItems = new StringBuilder();

		for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
			if(bookingListItem.getSpecials() != null) {
				stringyFideItems.append(bookingListItem.getSpecialQuantity()).append(" x ").append(bookingListItem.getSpecials().getSpecialName()).append("\n\n");
			} else {
				stringyFideItems.append(bookingListItem.getTreatmentQuantity()).append(" x ").append(bookingListItem.getTreatment().getTreatmentName()).append("\n\n");
			}
		}

		return stringyFideItems.toString();
	}

	@Override
	public ReportClientsOverviewModel getAllSignUpsDuring(Long from, Long to) {
		List<Client> allClientSignUpsForRange = clientRepository.findAllByDateRegisteredBetween(from, to);

		List<ReportClientSignUpModel> reportClientSignUpModels = allClientSignUpsForRange
				.stream()
				.map(client -> {
					List<Booking> listOfClientsBookings =  getAllBookingsForClient(client.getUsername());

					ReportClientSignUpModel reportClientSignUpModel = modelMapper.map(client, ReportClientSignUpModel.class);
					reportClientSignUpModel.setTotalBookingsMade(listOfClientsBookings.size());

					return reportClientSignUpModel;
				})
				.collect(Collectors.toList());

		ReportClientsOverviewModel reportClientsOverviewModel = new ReportClientsOverviewModel();
		reportClientsOverviewModel.setReportClientSignUpModels(reportClientSignUpModels);

		int totalActiveClients = 0;
		int totalClientsWithBookings = 0;

		for(ReportClientSignUpModel reportClientSignUpModel: reportClientSignUpModels) {
			if(reportClientSignUpModel.isActive()) {
				totalActiveClients += 1;
			}

			totalClientsWithBookings += reportClientSignUpModel.getTotalBookingsMade();
		}

		reportClientsOverviewModel.setTotalActiveClients(totalActiveClients);
		reportClientsOverviewModel.setTotalClientsWithBookings(totalClientsWithBookings);
		return reportClientsOverviewModel;

	}

	@Override
	public List<ReportClientSearchModel> findClientByName(String clientName) {
		return clientRepository
				.findAllByIsActiveOrderByFirstName(true)
				.stream()
				.filter(client -> (client.getFirstName().toLowerCase().contains(clientName) || client.getLastName().toLowerCase().contains(clientName)))
				.map(client -> modelMapper.map(client, ReportClientSearchModel.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<ReportClientInsightsModel> topServiceItemsPerClient(String clientUserName) {
		if(clientUserName.equals("")){
			throw new MissingUsernameException("Please ensure a valid user is provided.");
		}

		List<Booking> listOfClientsBookings =  getAllBookingsForClient(clientUserName);

		if(listOfClientsBookings.size() == 0){
			return new ArrayList<>();
		}

		HashMap<String, AtomicInteger> listOfTopTreatment = new HashMap<>();

		for(Booking clientBooking : listOfClientsBookings){
			for(BookingListItem bookingItemsPerBooking : clientBooking.getBookingList().getBookingListItems()){

				if (bookingItemsPerBooking.getTreatmentQuantity() > 0) {
					if (listOfTopTreatment.containsKey(bookingItemsPerBooking.getTreatment().getTreatmentName())) {
						listOfTopTreatment.get(bookingItemsPerBooking.getTreatment().getTreatmentName()).getAndAdd(1);
					} else {
						//new value so just initialise count
						listOfTopTreatment.put(bookingItemsPerBooking.getTreatment().getTreatmentName(), new AtomicInteger(1));
					}
				}
			}
		}

		List<ReportClientInsightsModel> reportClientInsightsModels = new ArrayList<>();
		listOfTopTreatment
				.forEach((treatmentName, total) -> reportClientInsightsModels.add(new ReportClientInsightsModel(treatmentName, total.get())));

		return reportClientInsightsModels
				.stream()
				.sorted(Comparator.comparing(ReportClientInsightsModel::getTotalBooked).reversed())
				.collect(Collectors.toList());
	}

	@Override
	public List<ReportTopClientModel> topClientsBetweenStartAndEnd(Long start, Long end) {
		//get all bookings that were successful
		List<Booking> listOfBookings = bookingRepository.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOut(
				start, end, "Active", false
		);

		HashMap<String, AtomicInteger> listOfTopClients = new HashMap<>();

		for(Booking booking : listOfBookings){
			//iterate over the topClientList to see if the user is already in the list
			if (listOfTopClients.containsKey(booking.getClient().getFirstName() + " " + booking.getClient().getLastName() + "(" + booking.getClient().getUsername() + ")"))
				listOfTopClients.get(booking.getClient().getFirstName() + " " + booking.getClient().getLastName() + "(" + booking.getClient().getUsername() + ")").getAndAdd(1);
			else
				listOfTopClients.put(booking.getClient().getFirstName() + " " + booking.getClient().getLastName() + "(" + booking.getClient().getUsername() + ")", new AtomicInteger(1));
		}

		return listOfTopClients
				.entrySet()
				.stream()
				.map(stringAtomicIntegerEntry -> new ReportTopClientModel(stringAtomicIntegerEntry.getKey(), stringAtomicIntegerEntry.getValue().intValue()))
				.sorted(new ReportClientSorter())
				.collect(Collectors.toList());
	}

	@Override
	public List<ReportTopTreatmentModel> topTreatmentsBetweenStartAndEnd(Long start, Long end) {
		List<Booking> listOfBookings = bookingRepository.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndBookingStatusAndDayToBlockOut(
				start, end, "Active", false
		);

		HashMap<String, AtomicInteger> listOfTopTreatment = new HashMap<>();

		for (Booking booking : listOfBookings) {
			BookingList bookingList = booking.getBookingList();
			for (BookingListItem bookingListItem :bookingList.getBookingListItems()) {
				if (bookingListItem.getTreatmentQuantity() > 0) {
					if (listOfTopTreatment.containsKey(bookingListItem.getTreatment().getTreatmentName())) {
						listOfTopTreatment.get(bookingListItem.getTreatment().getTreatmentName()).getAndAdd(1);
					} else {
						//new value so just initialise count
						listOfTopTreatment.put(bookingListItem.getTreatment().getTreatmentName(), new AtomicInteger(1));
					}
				}
			}
		}

		return listOfTopTreatment
				.entrySet()
				.stream()
				.map(stringAtomicIntegerEntry -> new ReportTopTreatmentModel(stringAtomicIntegerEntry.getKey(), stringAtomicIntegerEntry.getValue().intValue()))
				.sorted(new ReportTreatmentSorter())
				.collect(Collectors.toList());
	}

	private List<Booking> getAllBookingsForClient(String username) {
		Optional<Client> found = clientRepository.findByUsername(username);

		if(!found.isPresent())
			return new ArrayList<>();

		Client client = found.get();

		return bookingRepository.findAllByBookingStatusAndDayToBlockOutAndClient("Active", false, client);
	}

	private static class ReportTreatmentSorter implements Comparator<ReportTopTreatmentModel> {

		@Override
		public int compare(ReportTopTreatmentModel o1, ReportTopTreatmentModel o2) {
			return Integer.compare(o2.getTotalBookings(), o1.getTotalBookings());
		}
	}

	private static class ReportClientSorter implements Comparator<ReportTopClientModel> {

		@Override
		public int compare(ReportTopClientModel o1, ReportTopClientModel o2) {
			return Integer.compare(o2.getTotalBookings(), o1.getTotalBookings());
		}
	}
}
