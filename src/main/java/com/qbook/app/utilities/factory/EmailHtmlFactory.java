package com.qbook.app.utilities.factory;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingCancellationNotificationMessage;
import com.qbook.app.application.models.EmailForgotPasswordModel;
import com.qbook.app.application.models.EmployeeGoalProgress;
import com.qbook.app.application.models.employeeModels.NewEmployeeModel;
import com.qbook.app.application.models.reportingModels.ReportBookingOverviewModel;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.BookingListItem;
import com.qbook.app.domain.models.Client;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@AllArgsConstructor
public class EmailHtmlFactory {
    private final TemplateEngine templateEngine;
    private final ApplicationProperties applicationProperties;
    
    public String clientRegistration(Client client) {
        Context context = new Context();
        context.setVariable("fullName", client.getFirstName() + " " + client.getLastName());
        return templateEngine.process("clientRegistration", context);
    }

    public String clientBookingConfirmation(Booking booking) {
        double total = 0.0;
        for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
            if(bookingListItem.getTreatment() != null) {
	            if(bookingListItem.getTreatment().isSpecial()) {
		            total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSpecialPrice());
	            } else {
		            if(bookingListItem.getTreatment().isDoneByJunior()) {
                        total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getJuniorPrice());
                    } else {
                        total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSeniorPrice());
                    }
	            }
            } else {
                total += (bookingListItem.getSpecialQuantity() * bookingListItem.getSpecials().getSeniorPrice());
            }
        }

        Context context = new Context();
        context.setVariable("fullName", booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
        context.setVariable("therapist", booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
        context.setVariable("bookingListItems", booking.getBookingList().getBookingListItems());
        context.setVariable("bookingDate", applicationProperties.getLongDateTimeFormatter().print(new DateTime(booking.getStartDateTime())));
        context.setVariable("bookingTotal", Double.parseDouble(
                applicationProperties.getDecimalFormat().format(total * applicationProperties.getVatAmount())
        ));
        return templateEngine.process("clientBooking", context);
    }

    public String clientBookingNotification(Booking booking) {
        double total = 0.0;
        for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
            if(bookingListItem.getTreatment() != null) {
                if(bookingListItem.getTreatment().isSpecial()) {
                    total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSpecialPrice());
                } else {
                    if(bookingListItem.getTreatment().isDoneByJunior()) {
                        total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getJuniorPrice());
                    } else {
                        total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSeniorPrice());
                    }
                }
            } else {
                total += (bookingListItem.getSpecialQuantity() * bookingListItem.getSpecials().getSeniorPrice());
            }
        }
        Context context = new Context();
        context.setVariable("employeeFullName", booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
        context.setVariable("fullName", booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
        context.setVariable("bookingListItems", booking.getBookingList().getBookingListItems());
        context.setVariable("bookingDate", applicationProperties.getLongDateTimeFormatter().print(new DateTime(booking.getStartDateTime())));
        context.setVariable("bookingTotal", Double.parseDouble(
                applicationProperties.getDecimalFormat().format(total * applicationProperties.getVatAmount())
        ));
        return templateEngine.process("employeeBookingNotification", context);
    }

    public String clientBookingCancellationQueueNotification(BookingCancellationNotificationMessage bookingCancellationNotificationMessage) {
        Context context = new Context();
        context.setVariable("fullName", bookingCancellationNotificationMessage.getFullName());
        context.setVariable("dateTimeOpened", bookingCancellationNotificationMessage.getStartDateTime());
        context.setVariable("employeeAvailable", bookingCancellationNotificationMessage.getEmployeeAvailable());
        return templateEngine.process("clientBookingCancellationQueueNotification", context);
    }

    public String clientBookingCancellationNotification(String fullName, String dateTimeCancelled) {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("dateTimeOpened", dateTimeCancelled);
        return templateEngine.process("clientBookingCancellationNotification", context);
    }

    public String employeeBookingCancellationNotification(String fullName, String dateTimeCancelled) {
        Context context = new Context();
        context.setVariable("fullName", fullName);
        context.setVariable("dateTimeOpened", dateTimeCancelled);
        return templateEngine.process("employeeBookingCancellationNotification", context);
    }

    public String clientBookingReminder(Booking booking) {
        double total = 0.0;
        for(BookingListItem bookingListItem: booking.getBookingList().getBookingListItems()) {
            if(bookingListItem.getTreatment() != null) {
                if(bookingListItem.getTreatment().isSpecial()) {
                    total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSpecialPrice());
                } else {
                    if(bookingListItem.getTreatment().isDoneByJunior()) {
                        total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getJuniorPrice());
                    } else {
                        total += (bookingListItem.getTreatmentQuantity() * bookingListItem.getTreatment().getSeniorPrice());
                    }
                }
            } else {
                total += (bookingListItem.getSpecialQuantity() * bookingListItem.getSpecials().getSeniorPrice());
            }
        }

        Context context = new Context();
        context.setVariable("name", booking.getClient().getFirstName());
        context.setVariable("bookingTime", applicationProperties.getLongDateTimeFormatter().print(new DateTime(booking.getStartDateTime())));
        context.setVariable("bookingTotal", Double.parseDouble(
                applicationProperties.getDecimalFormat().format(total * applicationProperties.getVatAmount())
        ));
        context.setVariable("bookingListItems", booking.getBookingList().getBookingListItems());

        return templateEngine.process("clientBookingReminder", context);
    }

    public String employeeRegisteredNotification(NewEmployeeModel newEmployeeModel) {
        Context context = new Context();
        context.setVariable("fullName", newEmployeeModel.getFirstName() + " " + newEmployeeModel.getLastName());
	    context.setVariable("username", newEmployeeModel.getContactDetails().getEmailAddress());
	    context.setVariable("password", newEmployeeModel.getPassword());
        return templateEngine.process("employeeRegistration", context);
    }

	public String userForgotPasswordNotification(EmailForgotPasswordModel emailForgotPasswordModel) {
		String forgotPasswordUrl = applicationProperties.getBaseUrl() + "/views/forgot_password/reset_password.html?tkid=" + emailForgotPasswordModel.getToken();

		Context context = new Context();
		context.setVariable("fullName", emailForgotPasswordModel.getFullName());
		context.setVariable("forgotPasswordLink", forgotPasswordUrl);
		return templateEngine.process("userForgotPasswordEmail", context);
	}

	public String clientQuickEmail(Client client, String subject, String body) {
		Context context = new Context();
		context.setVariable("fullName", client.getFirstName() + " " + client.getLastName());
		context.setVariable("subject", subject);
		context.setVariable("body", body);
		return templateEngine.process("clientQuickEmail", context);
	}

	public String clientInvoiceEmail(String fullName,String fromDate, String toDate, ReportBookingOverviewModel reportBookingOverviewModel) {
		Context context = new Context();
		context.setVariable("fullName", fullName);
		context.setVariable("fromDate", fromDate);
		context.setVariable("toDate", toDate);
		context.setVariable("reportBookingModels", reportBookingOverviewModel.getReportBookingModels());
		context.setVariable("bookingTotal", reportBookingOverviewModel.getTotalRevenue());
		return templateEngine.process("clientInvoiceBookingNotification", context);
	}

    public String platformEmail(String subject, String body) {
        Context context = new Context();
        context.setVariable("subject", subject);
        context.setVariable("body", body);
        return templateEngine.process("platformEmails", context);
    }

    public String clientVoucherReceivedEmail(Client client) {
        Context context = new Context();
        context.setVariable("fullName", client.getFirstName() + " " + client.getLastName());
        return templateEngine.process("clientVoucherReceivedEmail", context);
    }

    public String clientFeedbackEmail(Client client) {
        Context context = new Context();
        context.setVariable("fullName", client.getFirstName() + " " + client.getLastName());
        return templateEngine.process("clientFeedbackRequest", context);
    }

    public String employeeGoalProgressEmail(EmployeeGoalProgress employeeGoalProgress) {
        Context context = new Context();
        context.setVariable("fullName", employeeGoalProgress.getFullName());
        context.setVariable("goal", employeeGoalProgress.getGoal());
        context.setVariable("goalProgress", employeeGoalProgress.getGoalProgress());
        context.setVariable("measureDate", employeeGoalProgress.getMeasureDate());
        context.setVariable("daysToMeasureDate", employeeGoalProgress.getDaysToMeasureDate());
        return templateEngine.process("employeeGoalProgressNotification", context);
    }
}
