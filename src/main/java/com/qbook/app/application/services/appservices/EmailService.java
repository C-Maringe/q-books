package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.BookingCancellationNotificationMessage;
import com.qbook.app.application.models.EmailForgotPasswordModel;
import com.qbook.app.application.models.EmployeeGoalProgress;
import com.qbook.app.application.models.employeeModels.NewEmployeeModel;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Employee;

import javax.mail.Address;

public interface EmailService {

	void sendBookingEmail(Booking booking);

	void sendBookingEmailToEmployee(Booking booking);

	void sendClientRegistrationEmail(String to,String subject, Client client);

	void sendBookingCancellationQueueClientNotification(BookingCancellationNotificationMessage bookingCancellationNotificationMessage);

    void sendBookingCancellationClientNotification(Client client, String startDateTime);

    void sendBookingCancellationEmployeeNotification(Employee employee, Client client, String startDateTime);

    void sendClientReminderEmail(Booking booking);

	void sendEmailPromotionalEmail(String subject, String body, Address[] recipients);
	
	void sendNewEmployeeEmail(NewEmployeeModel newEmployeeModel);

	void sendUserForgotPasswordEmail(EmailForgotPasswordModel emailForgotPasswordModel);

	void sendClientVoucherReceivedEmail(Client client);

	void sendClientFeedbackEmail(Client client);

	void sendEmployeeGoalProgressEmail(EmployeeGoalProgress employeeGoalProgress);
}
