package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.ErrorDetails;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingCancellationNotificationMessage;
import com.qbook.app.application.models.EmailForgotPasswordModel;
import com.qbook.app.application.models.EmployeeGoalProgress;
import com.qbook.app.application.models.employeeModels.NewEmployeeModel;
import com.qbook.app.application.models.reportingModels.ReportBookingOverviewModel;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.SystemEmail;
import com.qbook.app.domain.repository.SystemEmailRepository;
import com.qbook.app.utilities.factory.EmailHtmlFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Log
@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
	private final EmailHtmlFactory emailHtmlFactory;
	private final SystemEmailRepository systemEmailRepository;
    private final JavaMailSender emailSender;
    private final ApplicationProperties applicationProperties;

    @Override
    public void sendBookingEmail(Booking booking) {
        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();
            try {
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Booking Confirmation");
                systemEmail.setRecipient(booking.getClient().getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientBookingConfirmation(booking));
                
                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendBookingEmail: Sent message successfully");
            }catch(Exception e){
                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending booking confirmation ",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);

                log.log(Level.SEVERE, "sendBookingEmail: Sending email failed." , e);
            }
        });
    }

    @Override
    public void sendBookingEmailToEmployee(Booking booking) {
        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();
            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Client Booking");
                systemEmail.setRecipient(booking.getEmployee().getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientBookingNotification(booking));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendBookingEmailToEmployee: Sent message successfully");
            } catch(Exception e){
                log.log(Level.SEVERE, "sendBookingEmailToEmployee: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending booking reminder ",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendClientRegistrationEmail(String to, String subject, Client client) {
        

        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();

            try{

                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject(subject);
                systemEmail.setRecipient(to);
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientRegistration(client));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendClientRegistrationEmail: Sent message successfully");
            }catch(Exception e){
                log.log(Level.SEVERE, "sendClientRegistrationEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending client registration.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendBookingCancellationQueueClientNotification(BookingCancellationNotificationMessage bookingCancellationNotificationMessage) {
	    log.log(Level.INFO, "sendBookingCancellationQueueClientNotification("+bookingCancellationNotificationMessage.toString()+")");

        

        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();

            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Space Has Opened Up!");
                systemEmail.setRecipient(bookingCancellationNotificationMessage.getEmail());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientBookingCancellationQueueNotification(bookingCancellationNotificationMessage));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendBookingCancellationQueueClientNotification: Sent message successfully");
            }catch(Exception e){
                log.log(Level.SEVERE, "sendBookingCancellationQueueClientNotification: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending cancellation queue email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendBookingCancellationClientNotification(Client client, String startDateTime) {
        

        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();

            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Booking Cancellation");
                systemEmail.setRecipient(client.getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientBookingCancellationNotification(client.getFirstName()+ " " + client.getLastName(), startDateTime));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendBookingCancellationClientNotification: Sent message successfully");
            }catch(Exception e){
                log.log(Level.SEVERE, "sendBookingCancellationClientNotification: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending client cancellation queue email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendBookingCancellationEmployeeNotification(Employee employee, Client client, String startDateTime) {
        

        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();

            try{

                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Booking Cancellation");
                systemEmail.setRecipient(employee.getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.employeeBookingCancellationNotification(client.getFirstName()+ " " + client.getLastName(), startDateTime));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendBookingCancellationEmployeeNotification: Sent message successfully");
            }catch(Exception e){
                log.log(Level.SEVERE, "sendBookingCancellationEmployeeNotification: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending employee cancellation notification email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendClientReminderEmail(Booking booking) {
        executorService.submit(() -> {

            SystemEmail systemEmail = new SystemEmail();

            try{

                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Booking Reminder");
                systemEmail.setRecipient(booking.getClient().getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientBookingReminder(booking));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendClientNotificationEmail: Sent message successfully");
            }catch(Exception e){
                log.log(Level.SEVERE, "sendClientNotificationEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending client booking reminder email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendEmailPromotionalEmail(String subject, String body, Address[] recipients) {
        executorService.submit(() -> {

            SystemEmail systemEmail = new SystemEmail();

            try{

                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject(subject);
                StringBuilder recipientsString = new StringBuilder();

                for (Address recipient : recipients) {
                    recipientsString.append(recipient);
                }

                systemEmail.setRecipient(recipientsString.toString());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.platformEmail(subject, body));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setBcc((InternetAddress[]) recipients);
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);


                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendEmailPromotionalEmail: Sent message successfully");
            } catch(Exception e){
                log.log(Level.SEVERE, "sendEmailPromotionalEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending promotional email",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendNewEmployeeEmail(NewEmployeeModel newEmployeeModel) {
        executorService.submit(() -> {
            SystemEmail systemEmail = new SystemEmail();
            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Employee Registered");
                systemEmail.setRecipient(newEmployeeModel.getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.employeeRegisteredNotification(newEmployeeModel));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendNewEmployeeEmail: Sent message successfully");
            }catch(Exception e){
                log.log(Level.SEVERE, "sendNewEmployeeEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending employee registered email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendUserForgotPasswordEmail(EmailForgotPasswordModel emailForgotPasswordModel) {
        executorService.submit(() -> {

            SystemEmail systemEmail = new SystemEmail();

            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Password Reset Verification");
                systemEmail.setRecipient(emailForgotPasswordModel.getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.userForgotPasswordNotification(emailForgotPasswordModel));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendUserForgotPasswordEmail: Sent message successfully");
            } catch(Exception e) {
                log.log(Level.SEVERE, "sendUserForgotPasswordEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending forgot password email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendClientVoucherReceivedEmail(Client client) {
        executorService.submit(() -> {

            SystemEmail systemEmail = new SystemEmail();

            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Congratulations!! You have received a voucher.");
                systemEmail.setRecipient(client.getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientVoucherReceivedEmail(client));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendClientVoucherReceivedEmail: Sent message successfully");
            } catch(Exception e) {
                log.log(Level.SEVERE, "sendClientVoucherReceivedEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending voucher received email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendClientFeedbackEmail(Client client) {
        executorService.submit(() -> {

            SystemEmail systemEmail = new SystemEmail();

            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Thank you for your booking.");
                systemEmail.setRecipient(client.getContactDetails().getEmailAddress());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.clientFeedbackEmail(client));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendClientFeedbackEmail: Sent message successfully");
            } catch(Exception e) {
                log.log(Level.SEVERE, "sendClientFeedbackEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending feedback email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }

    @Override
    public void sendEmployeeGoalProgressEmail(EmployeeGoalProgress employeeGoalProgress) {
        executorService.submit(() -> {

            SystemEmail systemEmail = new SystemEmail();

            try{
                systemEmail.setDateTimeStart(System.currentTimeMillis());
                systemEmail.setSubject("Goal Progress.");
                systemEmail.setRecipient(employeeGoalProgress.getEmployeeEmail());
                systemEmail.setSender(applicationProperties.getFromAddress());
                systemEmail.setBody(emailHtmlFactory.employeeGoalProgressEmail(employeeGoalProgress));

                final MimeMessage message = emailSender.createMimeMessage();

                final MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(systemEmail.getRecipient());
                helper.setFrom(applicationProperties.getFromAddress());
                helper.setSubject(systemEmail.getSubject());
                helper.setText(systemEmail.getBody(), true);
                emailSender.send(message);

                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmail.setSent(true);
                systemEmailRepository.save(systemEmail);

                log.log(Level.INFO, "sendEmployeeGoalProgressEmail: Sent message successfully");
            } catch(Exception e) {
                log.log(Level.SEVERE, "sendEmployeeGoalProgressEmail: Sending email failed." , e);

                systemEmail.setSent(false);
                systemEmail.setErrorDetails(new ErrorDetails(
                        "Error sending voucher received email.",
                        400,
                        e.getMessage(),
                        DateTime.now().getMillis(),
                        e.getClass().getCanonicalName()
                ));
                systemEmail.setDateTimeEnd(System.currentTimeMillis());
                systemEmail.setDateTimeSent(System.currentTimeMillis());
                systemEmailRepository.save(systemEmail);
            }
        });
    }
}
