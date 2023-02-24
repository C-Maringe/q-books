package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.DuplicateUserException;
import com.qbook.app.application.configuration.exception.InvalidDayException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.configurationModels.ViewOperationTimesModel;
import com.qbook.app.application.models.employeeModels.*;
import com.qbook.app.application.models.scheduleModels.ScheduleNewBlockoutTimeForWorkingDayModel;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import com.qbook.app.application.services.appservices.BlockOutDayService;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.SuperUserServices;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.*;
import com.qbook.app.utilities.factory.Factory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class SuperUserServicesImpl implements SuperUserServices {

	private final BookingRepository bookingRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final EmployeeTypeRepository employeeTypeRepository;
	private final EmailService emailService;
	private final PermissionRepository permissionRepository;
	private final ModelMapper modelMapper;
	private final ApplicationConfigurationsServices applicationConfigurationsServices;
	private final BlockOutDayService blockOutDayService;
	private final ApplicationProperties applicationProperties;

    @Override
    public EmployeeRegisteredModel registerNewEmployee(NewEmployeeModel newEmployeeModel) {

        Employee employee = Factory.buildNewEmployee(newEmployeeModel);

	    validateAccountNotSetupAlready(employee.getUsername());

		Optional<EmployeeType> employeeTypeOptional = employeeTypeRepository.findByEmployeeType(newEmployeeModel.getEmployeeType());

	    if (!employeeTypeOptional.isPresent()){
		    EmployeeType employeeType = new EmployeeType();
		    employeeType.setEmployeeType(newEmployeeModel.getEmployeeType());

			employeeTypeRepository.save(employeeType);
		    employee.setEmployeeType(employeeType);
	    }
	    else {
		    employee.setEmployeeType(employeeTypeOptional.get());
	    }

	    employeeRepository.save(employee);

	    emailService.sendNewEmployeeEmail(newEmployeeModel);

	    EmployeeRegisteredModel employeeRegisteredModel = new EmployeeRegisteredModel();
	    employeeRegisteredModel.setMessage("Registration of the new employee was successful. A notification email will be sent to them with there credentials");
	    employeeRegisteredModel.setSuccess(true);

        return employeeRegisteredModel;
    }

	@Override
	public List<UserPermission> viewAllSystemPermissions() {
		return permissionRepository
				.findAll()
				.stream()
				.map(permission -> {
					UserPermission userPermission = new UserPermission();
					userPermission.setPermissionFeature(permission.getPermissionFeature().getName());
					userPermission.setCanRead(false);
					userPermission.setCanWrite(false);

					return userPermission;
				})
				.collect(Collectors.toList());
	}

	@Override
	// TODO: Fix This
	public CreatedEmployeeWorkingDayModel createEmployeeWorkingDayModel(NewEmployeeWorkingDayModel newEmployeeWorkingDayModel) {
		System.out.println("toAddWorkingDays");
		// ensure there are no duplicates for same day and all info needed is provided.
		Optional<Employee> toAddWorkingDays = employeeRepository.findById(new ObjectId(newEmployeeWorkingDayModel.getEmployeeWorkingId()));


		if (!toAddWorkingDays.isPresent()){
			throw new ResourceNotFoundException("The employees account was not found");
		} else {
			// validate values
			Optional<EmployeeWorkingDay> employeeWorkingDayOptional = toAddWorkingDays
					.get()
					.getEmployeeWorkingDays()
					.stream()
					.filter(employeeWorkingDay -> {
					    DateTime toAllocate = new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchStartTime());

						return new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).getYear() == toAllocate.getYear() &&
								new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).getMonthOfYear() == toAllocate.getMonthOfYear() &&
								new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).getDayOfMonth() == toAllocate.getDayOfMonth();
					})
					.findFirst();
			if(employeeWorkingDayOptional.isPresent()) {
				throw new InvalidDayException("The selected day already has a working start and end times allocated try remove it and try again.");
			} else {
			    // first check if they are stating that this needs to be used for the entire week or not
                if(newEmployeeWorkingDayModel.isApplyToWholeWeek()) {
                    // we need to know which day we are on and which days are part of our working week
					// if start day of the week is Sunday we need to make sure its in the new week
                    DateTime startingTimeForDay = new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayStartTime());
                    int weekToAdd = 0;
                    if (startingTimeForDay.dayOfWeek().getAsText().equals("Sunday")) {
                    	weekToAdd = 1;
					}

                    DateTime endingTimeForDay = new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayEndTime());
                    DateTime lunchTimeForDay = new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchStartTime());

                    // 1 = Monday
					// 7 = Sunday
                    int startDayOfWeek = startingTimeForDay.plusWeeks(weekToAdd).dayOfWeek().withMinimumValue().getDayOfWeek();
                    int lastDayOfWeek = startingTimeForDay.plusWeeks(weekToAdd).dayOfWeek().withMaximumValue().getDayOfWeek();

					System.out.println(startingTimeForDay);
					System.out.println(lunchTimeForDay);
					System.out.println(endingTimeForDay);

                    // figure out where to start
                    for(int i = startDayOfWeek; i <= lastDayOfWeek; i++) {
						System.out.println(i);
						validateTimes(newEmployeeWorkingDayModel);
						DateTime dailyScheduleStartTime = startingTimeForDay.plusWeeks(weekToAdd).dayOfWeek().withMinimumValue().plusDays(i - 1);

						if (isWorkingDay(dailyScheduleStartTime.getMillis())) { // we need to check every day is a working day
                            DateTime dailyScheduleEndTime = endingTimeForDay.plusWeeks(weekToAdd).dayOfWeek().withMinimumValue().plusDays(i - 1);
                            DateTime dailyLunchTime =  lunchTimeForDay.plusWeeks(weekToAdd).dayOfWeek().withMinimumValue().plusDays(i - 1);

                            EmployeeWorkingDay employeeWorkingDay = modelMapper.map(newEmployeeWorkingDayModel, EmployeeWorkingDay.class);

                            employeeWorkingDay.setEmployeeWorkingDayId(new ObjectId().toString());
                            employeeWorkingDay.setEmployeeWorkingDayName(dailyScheduleStartTime.dayOfWeek().getAsText());
                            employeeWorkingDay.setEmployeeWorkingDayStartTime(dailyScheduleStartTime.getMillis());
                            employeeWorkingDay.setEmployeeWorkingDayEndTime(dailyScheduleEndTime.getMillis());
                            employeeWorkingDay.setEmployeeWorkingDayLunchStartTime(dailyLunchTime.getMillis());

                            toAddWorkingDays.get().getEmployeeWorkingDays().add(
									employeeWorkingDay
                            );
                        }
					}

                    employeeRepository.save(toAddWorkingDays.get());

					generateBlockoutTimesForSchedule(toAddWorkingDays.get().getEmployeeWorkingDays(), toAddWorkingDays.get().getId().toString());

                    return new CreatedEmployeeWorkingDayModel("The employee working day was successfully allocated.",true);
                } else {

					validateTimes(newEmployeeWorkingDayModel);

                    if (isWorkingDay(newEmployeeWorkingDayModel.getEmployeeWorkingDayStartTime())) {
                        toAddWorkingDays.get().getEmployeeWorkingDays().add(
                                addNewEmployeeWorkingDay(newEmployeeWorkingDayModel)
                        );
                    }

					employeeRepository.save(toAddWorkingDays.get());

					generateBlockoutTimesForSchedule(toAddWorkingDays.get().getEmployeeWorkingDays(), toAddWorkingDays.get().getId().toString());

                    return new CreatedEmployeeWorkingDayModel("The employee working day was successfully allocated.",true);
                }
			}
		}
	}

	// TODO: Fix This
	private void generateBlockoutTimesForSchedule(List<EmployeeWorkingDay> employeeWorkingDays, String employeeId) {
		ViewOperationTimesModel viewOperationTimesModel = applicationConfigurationsServices.viewOperatingTimes();

		employeeWorkingDays
				.forEach(employeeWorkingDay -> {
//					LocalTime companyStartTime = LocalTime.parse(viewOperationTimesModel.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));
//
//					LocalTime provided = new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).toLocalTime();
//
//					ScheduleNewBlockoutTimeForWorkingDayModel shiftBlockedTime = new ScheduleNewBlockoutTimeForWorkingDayModel();
//
//					if(provided.isBefore(companyStartTime) || provided.isEqual(companyStartTime)) { // early shift
//						shiftBlockedTime.setBlockoutTimeTitle("Worked Early Shift");
//
//						LocalTime workingDayEndTime = new LocalTime(employeeWorkingDay.getEmployeeWorkingDayEndTime());
//						LocalTime companyClosingTime = LocalTime.parse(viewOperationTimesModel.getWorkEndTime(), DateTimeFormat.forPattern("HH:mm"));
//
//						// get difference in minutes
//						int differenceInMinutes = Minutes.minutesBetween(workingDayEndTime, companyClosingTime).getMinutes();
//
//						shiftBlockedTime.setStartDateTime(applicationProperties.getLongDateTimeFormatter().print(employeeWorkingDay.getEmployeeWorkingDayEndTime()));
//						shiftBlockedTime.setEndDateTime(applicationProperties.getLongDateTimeFormatter().print(new DateTime(employeeWorkingDay.getEmployeeWorkingDayEndTime()).plusMinutes(differenceInMinutes)));
//					} else {
//						shiftBlockedTime.setBlockoutTimeTitle("Working Late Shift");
//
//						LocalTime workingDayStartTime = new LocalTime(employeeWorkingDay.getEmployeeWorkingDayStartTime());
//						LocalTime companyOpeningTime = LocalTime.parse(viewOperationTimesModel.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));
//
//						// get difference in minutes
//						int differenceInMinutes = Minutes.minutesBetween(companyOpeningTime, workingDayStartTime).getMinutes();
//
//						shiftBlockedTime.setStartDateTime(applicationProperties.getLongDateTimeFormatter().print(new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).minusMinutes(differenceInMinutes)));
//						shiftBlockedTime.setEndDateTime(applicationProperties.getLongDateTimeFormatter().print(new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime())));
//					}
//
//					shiftBlockedTime.setEmployeesId(employeeId);
//					// need to calculate the block out time
//
//					shiftBlockedTime.setWorkingDayId(employeeWorkingDay.getEmployeeWorkingDayId());
//
//					blockOutDayService.blockoutScheduleTimeForEmployeeHours(shiftBlockedTime);
//
//					ScheduleNewBlockoutTimeForWorkingDayModel scheduleNewBlockoutTimeForWorkingDayModel = new ScheduleNewBlockoutTimeForWorkingDayModel();
//					scheduleNewBlockoutTimeForWorkingDayModel.setBlockoutTimeTitle("Lunch");
//					scheduleNewBlockoutTimeForWorkingDayModel.setEmployeesId(employeeId);
//					scheduleNewBlockoutTimeForWorkingDayModel.setStartDateTime(applicationProperties.getLongDateTimeFormatter().print(employeeWorkingDay.getEmployeeWorkingDayLunchStartTime()));
//					scheduleNewBlockoutTimeForWorkingDayModel.setEndDateTime(applicationProperties.getLongDateTimeFormatter().print(new DateTime(employeeWorkingDay.getEmployeeWorkingDayLunchStartTime()).plusMinutes(employeeWorkingDay.getEmployeeWorkingDayLunchDuration())));
//					scheduleNewBlockoutTimeForWorkingDayModel.setWorkingDayId(employeeWorkingDay.getEmployeeWorkingDayId());
//
//					blockOutDayService.blockoutScheduleTimeForEmployeeHours(scheduleNewBlockoutTimeForWorkingDayModel);
				});
	}

	public EmployeeWorkingDay addNewEmployeeWorkingDay(NewEmployeeWorkingDayModel newEmployeeWorkingDayModel) {
		EmployeeWorkingDay employeeWorkingDay = modelMapper.map(newEmployeeWorkingDayModel, EmployeeWorkingDay.class);

		System.out.println(employeeWorkingDay);

		employeeWorkingDay.setEmployeeWorkingDayId(new ObjectId().toString());
		System.out.println(employeeWorkingDay);
		employeeWorkingDay.setEmployeeWorkingDayName(new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayStartTime()).dayOfWeek().getAsText());
		System.out.println(employeeWorkingDay);
		return employeeWorkingDay;
	}

 	private void validateTimes(NewEmployeeWorkingDayModel newEmployeeWorkingDayModel) {
		// validate times allocated are within operating times
		notBeforeCurrentTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayStartTime());
		notBeforeCurrentTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayEndTime());
		notBeforeCurrentTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchStartTime());
//		new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchStartTime()).plusMinutes(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchDuration()).getMillis();
//		System.out.println("5");

		isWithinOperatingHours(newEmployeeWorkingDayModel.getEmployeeWorkingDayStartTime());
		isWithinOperatingHours(newEmployeeWorkingDayModel.getEmployeeWorkingDayEndTime());
		isWithinOperatingHours(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchStartTime());
		isWithinOperatingHours(new DateTime(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchStartTime()).plusMinutes(newEmployeeWorkingDayModel.getEmployeeWorkingDayLunchDuration()).getMillis());
	}

	private void notBeforeCurrentTime(long timestamp) {
		if(new DateTime(timestamp).isBeforeNow()) {
			throw new InvalidDayException("Please ensure the time selected is not an old date.");
		}
	}

	// TODO: Fix This
	private void isWithinOperatingHours(long timestamp) {
		ViewOperationTimesModel viewOperationTimesModel = applicationConfigurationsServices.viewOperatingTimes();
//		System.out.println(viewOperationTimesModel);

//		LocalTime companyEndTime = LocalTime.parse(viewOperationTimesModel.getWorkEndTime(), DateTimeFormat.forPattern("HH:mm"));
//		LocalTime companyStartTime = LocalTime.parse(viewOperationTimesModel.getWorkStartTime(), DateTimeFormat.forPattern("HH:mm"));
//
//		LocalTime provided = new DateTime(timestamp).toLocalTime();
//		if (provided.isAfter(companyEndTime)) {
//			throw new InvalidDayException("Please ensure the time selected is with in the company operating hours which is " + companyStartTime.toString(DateTimeFormat.forPattern("HH:mm")) + " - " + companyEndTime.toString(DateTimeFormat.forPattern("HH:mm")));
//		}
//
//		if (provided.isBefore(companyStartTime)) {
//			throw new InvalidDayException("Please ensure the time selected is with in the company operating hours which is " + companyStartTime.toString(DateTimeFormat.forPattern("HH:mm")) + " - " + companyEndTime.toString(DateTimeFormat.forPattern("HH:mm")));
//		}
	}

	// TODO: Fix This
	private boolean isWorkingDay(long timestamp) {
        DateTime dayToTest = new DateTime(timestamp);

        ViewOperationTimesModel viewOperationTimesModel = applicationConfigurationsServices.viewOperatingTimes();

//        Optional<String> workingDayOptional = viewOperationTimesModel
//                .getDaysAvailable()
//                .stream()
//                .filter(s -> s.equals(dayToTest.dayOfWeek().getAsText()))
//                .findFirst();

//        return workingDayOptional.isPresent();
		return false;
    }

	@Override
	public List<EmployeeWorkingDayModel> viewAllEmployeeWorkingDays(String employeeId, int month, int year) {
		Optional<Employee> toViewWorkingDays = employeeRepository.findById(new ObjectId(employeeId));
		if ( !toViewWorkingDays.isPresent() ){
			throw new ResourceNotFoundException("The employees account was not found");
		} else {
			return toViewWorkingDays
					.get()
					.getEmployeeWorkingDays()
					.stream()
					.filter(employeeWorkingDay -> new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).getMonthOfYear() == month &&
							new DateTime(employeeWorkingDay.getEmployeeWorkingDayStartTime()).getYear() == year)
					.map(employeeWorkingDay -> modelMapper.map(employeeWorkingDay, EmployeeWorkingDayModel.class))
					.collect(Collectors.toList());

		}
	}

	@Override
	public RemovedEmployeeWorkingDayModel removeEmployeeWorkingDay(String employeeId, String employeeWorkingDayId) {
		Optional<Employee> toViewWorkingDays = employeeRepository.findById(new ObjectId(employeeId));
		if ( !toViewWorkingDays.isPresent() ){
			throw new ResourceNotFoundException("The employees account was not found");
		} else {
			List<EmployeeWorkingDay> tempList = toViewWorkingDays.get().getEmployeeWorkingDays();

			String employeeWorkingDayIdToRemove = "";
			for(EmployeeWorkingDay employeeWorkingDay: tempList) {
				if(employeeWorkingDay.getEmployeeWorkingDayId().equals(employeeWorkingDayId)) {
					employeeWorkingDayIdToRemove = employeeWorkingDay.getEmployeeWorkingDayId();
					tempList.remove(employeeWorkingDay);
					break;
				}
			}

			toViewWorkingDays.get().setEmployeeWorkingDays(tempList);

            employeeRepository.save(toViewWorkingDays.get());

            // remove the working day related to the schedule blockout time
			if(!employeeWorkingDayIdToRemove.equals("")) {
				List<Booking> blockedOutScheduleSlots = bookingRepository.findAllByWorkingDayId(employeeWorkingDayIdToRemove);

				if(blockedOutScheduleSlots.size() > 0) {
					for(Booking blockedOutSlot: blockedOutScheduleSlots) {
						blockedOutSlot.setBookingStatus("Cancelled");

						bookingRepository.save(blockedOutSlot);
					}
				} else {
					log.warning("No scheduled blockout day found for working day id -> " + employeeWorkingDayIdToRemove);
				}
			}
			return new RemovedEmployeeWorkingDayModel("The employee working day was successfully removed.", true);
		}
	}

	@Override
    public EmployeeUpdatedModel disableAccount(String employeeId) {
        Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(employeeId));
        if ( !employeeOptional.isPresent() ){
            throw new ResourceNotFoundException("The employees account was not disabled. The account was not found");
        } else {
        	Employee toBeUpdated = employeeOptional.get();

            toBeUpdated.setActive(false);
            employeeRepository.save(toBeUpdated);

	        EmployeeUpdatedModel employeeUpdatedModel = new EmployeeUpdatedModel();
	        employeeUpdatedModel.setMessage("The employees account was successfully disabled");
	        employeeUpdatedModel.setSuccess(true);

	        return employeeUpdatedModel;
        }
    }

    @Override
    public EmployeeUpdatedModel enableAccount(String employeeId) {
		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(employeeId));
		if ( !employeeOptional.isPresent() ){
			throw new ResourceNotFoundException("The employees account was not disabled. The account was not found");
		} else {
			Employee toBeUpdated = employeeOptional.get();
            toBeUpdated.setActive(true);
			employeeRepository.save(toBeUpdated);

	        EmployeeUpdatedModel employeeUpdatedModel = new EmployeeUpdatedModel();
	        employeeUpdatedModel.setMessage("The employees account was successfully enabled.");
	        employeeUpdatedModel.setSuccess(true);

	        return employeeUpdatedModel;
        }
    }

    @Override
    public List<ViewEmployeeType> getAllEmployeeTypes() {
	    return employeeTypeRepository
				.findAll()
			    .stream()
			    .map(employeeType -> {
				    ViewEmployeeType viewEmployeeTypeModel = new ViewEmployeeType();
				    viewEmployeeTypeModel.setEmployeeType(employeeType.getEmployeeType());
				    viewEmployeeTypeModel.setId(employeeType.getId().toString());
				    return viewEmployeeTypeModel;
			    })
			    .collect(Collectors.toList());
    }

    @Override
    public List<ViewEmployeeModel> getAllEmployees() {
        return employeeRepository
				.findAll()
		        .stream()
		        .map(employee -> {
		        	ViewEmployeeModel viewEmployeeModel = new ViewEmployeeModel();
			        viewEmployeeModel.setUserId(employee.getId().toString());
		        	viewEmployeeModel.setFirstName(employee.getFirstName());
		        	viewEmployeeModel.setLastName(employee.getLastName());
		        	viewEmployeeModel.setEmailAddress(employee.getContactDetails().getEmailAddress());
			        viewEmployeeModel.setActive(employee.isActive());
					viewEmployeeModel.setEmployeeLevel(employee.getEmployeeLevel());
					viewEmployeeModel.setEmployeeType(employee.getEmployeeType().getEmployeeType());
					viewEmployeeModel.setContactDetails(employee.getContactDetails().getMobileNumber());
		        	return viewEmployeeModel;
		        })
		        .collect(Collectors.toList());
    }


    @Override
    public ViewFullEmployeeModel getEmployee(String employeeId) {
		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(employeeId));
		if ( !employeeOptional.isPresent() )
			throw new ResourceNotFoundException("The employees account was not disabled. The account was not found");

		Employee employee = employeeOptional.get();

        ViewFullEmployeeModel viewFullEmployeeModel = this.modelMapper.map(employee, ViewFullEmployeeModel.class);
        viewFullEmployeeModel.setUserId(employee.getId().toString());

        if(viewFullEmployeeModel.getUserPermissionList().size() == 0) {
        	viewFullEmployeeModel.setUserPermissionList(
			        permissionRepository
							.findAll()
					        .stream()
					        .map(permission -> {
						        UserPermission userPermission = new UserPermission();
						        userPermission.setPermissionFeature(permission.getPermissionFeature().getName());
						        userPermission.setCanRead(false);
						        userPermission.setCanWrite(false);

						        return userPermission;
					        })
					        .collect(Collectors.toList())
	        );
        }

        return viewFullEmployeeModel;
    }

    @Override
    public EmployeeUpdatedModel updateEmployee(UpdateEmployeeModel updateEmployeeModel) {
		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(updateEmployeeModel.getUserId()));
		if ( !employeeOptional.isPresent() )
			throw new ResourceNotFoundException("The employees account was not disabled. The account was not found");

        Optional<EmployeeType> employeeTypeOptional = employeeTypeRepository.findByEmployeeType(updateEmployeeModel.getEmployeeType());

		EmployeeType employeeType;
        if(!employeeTypeOptional.isPresent()) {
            employeeType = new EmployeeType();
            employeeType.setEmployeeType(updateEmployeeModel.getEmployeeType());
            employeeTypeRepository.save(employeeType);
        } else {
        	employeeType = employeeTypeOptional.get();
		}

        Employee employeeToUpdate = employeeOptional.get();

        employeeToUpdate.setEmployeeType(employeeType);
        employeeToUpdate.setFirstName(updateEmployeeModel.getFirstName());
        employeeToUpdate.setLastName(updateEmployeeModel.getLastName());
        employeeToUpdate.setEmployeeLevel(updateEmployeeModel.getEmployeeLevel());
        employeeToUpdate.setMustBookConsultationFirstTime(updateEmployeeModel.isMustBookConsultationFirstTime());
        employeeToUpdate.setContactDetails(updateEmployeeModel.getContactDetails());
	    employeeToUpdate.setUserPermissionList(Factory.setupUserPermissionsFromModel(updateEmployeeModel.getUserPermissions()));

        employeeRepository.save(employeeToUpdate);

	    EmployeeUpdatedModel employeeUpdatedModel = new EmployeeUpdatedModel();
	    employeeUpdatedModel.setMessage("Update of the employee details was successful.");
	    employeeUpdatedModel.setSuccess(true);

	    return employeeUpdatedModel;
    }

    private void validateAccountNotSetupAlready(String emailAddress) {
	    Optional<Employee> empl = employeeRepository.findByUsername(emailAddress.trim());
	    Optional<Client> client = clientRepository.findByUsername(emailAddress.trim());
	    if(empl.isPresent()){
		    if(empl.get().isActive()){
			    throw new DuplicateUserException("Error registering employee,the email provided is already used.");
		    }
	    }else if(client.isPresent()){
		    throw new DuplicateUserException("Error registering employee,the email provided is already used.");
	    }
    }
}
