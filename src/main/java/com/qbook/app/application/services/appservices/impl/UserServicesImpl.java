package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.InvalidClientException;
import com.qbook.app.application.configuration.exception.NotAuthorisedException;
import com.qbook.app.application.configuration.exception.PasswordMismatchException;
import com.qbook.app.application.models.EmailForgotPasswordModel;
import com.qbook.app.application.models.ResetPasswordModel;
import com.qbook.app.application.models.UserForgotPasswordModel;
import com.qbook.app.application.models.scheduleModels.ScheduleUserAcceptedTermsModel;
import com.qbook.app.application.models.scheduleModels.ScheduleUserHasAcceptedTermsModel;
import com.qbook.app.application.models.webPlatformModels.LoggedInUserModel;
import com.qbook.app.application.models.webPlatformModels.LoginUserModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.UserServices;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.AuthTokenRepository;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.LoginSessionRepository;
import com.qbook.app.utilities.Constants;
import com.qbook.app.utilities.factory.ClientFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log
@Service
@AllArgsConstructor
public class UserServicesImpl implements UserServices {
	private final ClientRepository clientRepository;
	private final EmployeeRepository employeeRepository;
	private final EmailService emailService;
	private final AuthTokenServices authTokenServices;
	private final AuthTokenRepository authTokenRepository;
	private final LoginSessionRepository loginSessionRepository;
	private final ClientFactory clientFactory;

	@Override
	public LoggedInUserModel loginPlatformUser(LoginUserModel loginUserModel) {
	    log.info("Started service call loginPlatformUser() at " + System.currentTimeMillis());
		Optional<Client> clientOptional = clientRepository.findByUsername(loginUserModel.getUsername());

		Optional<Employee> employeeOptional = employeeRepository.findByUsername(loginUserModel.getUsername());
		LoginSessions loginSessions = new LoginSessions();

		if (clientOptional.isPresent()) {
			loginSessions.setClient(clientOptional.get());
            log.info("Ended service call loginPlatformUser() at " + System.currentTimeMillis());
			return loginSpecificUser(clientOptional.get(), loginUserModel.getPassword(), loginSessions);
		} else if (employeeOptional.isPresent()) {
            log.info("Ended service call loginPlatformUser() at " + System.currentTimeMillis());
		    loginSessions.setEmployee(employeeOptional.get());
			return loginSpecificUser(employeeOptional.get(), loginUserModel.getPassword(), loginSessions);
		} else {
            log.info("Ended service call loginPlatformUser() at " + System.currentTimeMillis());
			throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL_DE);
		}
	}

	@Override
	public UserForgotPasswordModel userForgotPassword(String userEmailAddress) {
        log.info("Started service call userForgotPassword() at " + System.currentTimeMillis());
		Optional<Client> clientOptional = clientRepository.findByUsername(userEmailAddress);
		Optional<Employee> employeeOptional = employeeRepository.findByUsername(userEmailAddress);

		if (clientOptional.isPresent()) {
            log.info("Ended service call userForgotPassword() at " + System.currentTimeMillis());
			sendEmailToUser(clientOptional.get().getContactDetails().getEmailAddress(), clientOptional.get().getFirstName() + " " + clientOptional.get().getLastName(), this.authTokenServices.generateAuthToken(clientOptional.get().getId().toString()));
			return new UserForgotPasswordModel("We have sent an email to your account. Please click on the link and follow the instructions.");
		} else if (employeeOptional.isPresent()) {
            log.info("Ended service call userForgotPassword() at " + System.currentTimeMillis());
			sendEmailToUser(employeeOptional.get().getContactDetails().getEmailAddress(), employeeOptional.get().getFirstName() + " " + employeeOptional.get().getLastName(), this.authTokenServices.generateAuthToken(employeeOptional.get().getId().toString()));
			return new UserForgotPasswordModel("We have sent an email to your account. Please click on the link and follow the instructions.");
		} else {
            log.info("Ended service call userForgotPassword() at " + System.currentTimeMillis());
			throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL_DE);
		}

	}

	private void sendEmailToUser(String emailAddress, String fullName, String token) {
		EmailForgotPasswordModel emailForgotPasswordModel = new EmailForgotPasswordModel();
		emailForgotPasswordModel.setEmailAddress(emailAddress);
		emailForgotPasswordModel.setFullName(fullName);
		emailForgotPasswordModel.setToken(token);
		emailService.sendUserForgotPasswordEmail(emailForgotPasswordModel);
	}

	@Override
	public UserForgotPasswordModel userResetPassword(String userId, ResetPasswordModel resetPasswordModel) {
        log.info("Started service call userResetPassword() at " + System.currentTimeMillis());
		Optional<Client> clientOptional = clientRepository.findById(new ObjectId(userId));
		Optional<Employee> employeeOptional = employeeRepository.findById(new ObjectId(userId));

		if (clientOptional.isPresent()) {

			if(resetPasswordModel.getConfirmPassword().equals(resetPasswordModel.getPassword())) {
				StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
				clientOptional.get().setPassword(spe.encryptPassword(resetPasswordModel.getPassword()));
				clientRepository.save(clientOptional.get());
                log.info("Ended service call userResetPassword() at " + System.currentTimeMillis());
				return new UserForgotPasswordModel("You password has been successfully reset.");
			} else {
                log.info("Ended service call userResetPassword() at " + System.currentTimeMillis());
				throw new PasswordMismatchException("Please ensure the passwords provided match.");
			}
		} else if (employeeOptional.isPresent()) {
            log.info("Ended service call userResetPassword() at " + System.currentTimeMillis());
			if(resetPasswordModel.getConfirmPassword().equals(resetPasswordModel.getPassword())) {
				StrongPasswordEncryptor spe = new StrongPasswordEncryptor();
				employeeOptional.get().setPassword(spe.encryptPassword(resetPasswordModel.getPassword()));
				employeeRepository.save(employeeOptional.get());

				return new UserForgotPasswordModel("You password has been successfully reset.");
			} else {
                log.info("Ended service call userResetPassword() at " + System.currentTimeMillis());
				throw new PasswordMismatchException("Please ensure the passwords provided match.");
			}
		} else {
            log.info("Ended service call userResetPassword() at " + System.currentTimeMillis());
			throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL_DE);
		}
	}

    @Override
    public ScheduleUserAcceptedTermsModel checkIfUserHasAcceptedTerms(String authToken) {
        log.info("Started service call checkIfUserHasAcceptedTerms() at " + System.currentTimeMillis());

        Optional<Client> clientOptional = clientRepository.findById(new ObjectId(authTokenServices.extractUserId(authToken.substring(7))));

        if(clientOptional.isPresent()) {
            log.info("Ended service call checkIfUserHasAcceptedTerms() at " + System.currentTimeMillis());
            return new ScheduleUserAcceptedTermsModel(clientOptional.get().isHasAcceptedTermsAndAgreements());
        } else {
            log.severe("Ended service call checkIfUserHasAcceptedTerms() at " + System.currentTimeMillis());
            throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL_DE);
        }
    }

    @Override
    public ScheduleUserHasAcceptedTermsModel userHasAcceptedTerms(String authToken) {
        log.info("Started service call userHasAcceptedTerms() at " + System.currentTimeMillis());

		Optional<Client> clientOptional = clientRepository.findById(new ObjectId(authTokenServices.extractUserId(authToken.substring(7))));

        if(clientOptional.isPresent()) {

			clientOptional.get().setHasAcceptedTermsAndAgreements(true);
            clientRepository.save(clientOptional.get());

            log.info("Ended service call userHasAcceptedTerms() at " + System.currentTimeMillis());
            return new ScheduleUserHasAcceptedTermsModel("Thanks for accepting our terms and agreement, we hope you enjoy your experience with us.");
        } else {
            log.info("Ended service call userHasAcceptedTerms() at " + System.currentTimeMillis());
            throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL_DE);
        }
    }

    private LoggedInUserModel loginSpecificUser(User user, String password, LoginSessions loginSessions) {
		if (user != null) {
			if(!user.isActive()) {
				throw new NotAuthorisedException("Your account has been disabled. Please contact the company to find out why.");
			} else {
				StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();

				if(strongPasswordEncryptor.checkPassword(password, user.getPassword())) {

					String token = authTokenServices.generateAuthToken(user.getId().toString());

					authTokenRepository.save(new AuthToken(token));
					loginSessions.setLoginDateTime(DateTime.now().toLocalDate().toDate());
					loginSessions.setPlatformUsed(PlatformUsed.WEB);
					loginSessions.setAuthToken(token);

					loginSessionRepository.save(loginSessions);

					LoggedInUserModel loggedInUserModel = new LoggedInUserModel();
					loggedInUserModel.setFullName(user.getFirstName() + " " + user.getLastName());
					loggedInUserModel.setToken(token);
					loggedInUserModel.setRole(user.getRole());

					if(user.getUserPermissionList() == null) {
						loggedInUserModel.setUserPermissionList(clientFactory.setupUserPermissionsFromModel());
					} else {
						loggedInUserModel.setUserPermissionList(user.getUserPermissionList());
					}

					return loggedInUserModel;
				} else {
					throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL);
				}
			}
		} else {
			throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL);
		}
	}
}




