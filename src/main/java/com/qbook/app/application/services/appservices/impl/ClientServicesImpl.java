package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.DuplicateUserException;
import com.qbook.app.application.configuration.exception.InvalidClientException;
import com.qbook.app.application.configuration.exception.NotAuthorisedException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.*;
import com.qbook.app.application.models.clientModels.ClientDisabledModel;
import com.qbook.app.application.models.clientModels.ClientEnabledModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.ClientServices;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.application.services.appservices.LoyaltyPointsService;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.AuthTokenRepository;
import com.qbook.app.domain.repository.ClientRepository;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.LoginSessionRepository;
import com.qbook.app.utilities.Constants;
import com.qbook.app.utilities.factory.ClientFactory;
import com.qbook.app.utilities.factory.NoteFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class ClientServicesImpl implements ClientServices {
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final AuthTokenServices authTokenServices;
    private final AuthTokenRepository authTokenRepository;
    private final LoginSessionRepository loginSessionRepository;
    private final LoyaltyPointsService loyaltyPointsService;
    private final ApplicationProperties applicationProperties;
    private final ClientFactory clientFactory;

    @Override
    public RegisteredClientModel registerClient(NewClientModel newClientModel, PlatformUsed platformUsed) {
        Optional<Client> clientOptional = clientRepository.findByUsername(newClientModel.getEmailAddress());

        Optional<Employee> employeeOptional = employeeRepository.findByUsername(newClientModel.getEmailAddress());

        if (clientOptional.isPresent() || employeeOptional.isPresent())
            throw new DuplicateUserException("The email address provided is already registered on the system. If you forgot your password, please try resetting it with the forgot password feature.");

        Client newClient = clientFactory.buildClientModel(newClientModel);
        newClient.setPlatformUsed(platformUsed);

        Client saved = clientRepository.save(newClient);

        emailService.sendClientRegistrationEmail(newClient.getContactDetails().getEmailAddress(), "Registration Confirmation ", newClient);

        String token = authTokenServices.generateAuthToken(saved.getId().toString());

        authTokenRepository.save(new AuthToken(token));

        return new RegisteredClientModel(
                token,
                newClient.getFirstName(),
                newClient.getLastName()
        );
    }

    @Override
    public LoggedInClientModel login(LoginClientModel loginClientModel) {
        Optional<Client> clientOptional = clientRepository.findByUsername(loginClientModel.getUsername());

        Optional<Employee> employeeOptional = employeeRepository.findByUsername(loginClientModel.getUsername());

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            if (!client.isActive()) {
                throw new NotAuthorisedException("Your account has been disabled. Please contact the company to find out why.");
            } else {
                StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();

                if (encryptor.checkPassword(loginClientModel.getPassword(), client.getPassword())) {

                    String token = authTokenServices.generateAuthToken(client.getId().toString());

                    authTokenRepository.save(new AuthToken(token));

                    LoginSessions loginSessions = new LoginSessions();
                    loginSessions.setClient(client);
                    loginSessions.setLoginDateTime(DateTime.now().toLocalDate().toDate());
                    loginSessions.setPlatformUsed(PlatformUsed.MOBILE);
                    loginSessions.setAuthToken(token);

                    loginSessionRepository.save(loginSessions);

                    return new LoggedInClientModel(
                            token,
                            client.getFirstName(),
                            client.getLastName()
                    );
                } else {
                    throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL);
                }
            }
        } else if (employeeOptional.isPresent()) {
            throw new NotAuthorisedException("Your account does not have access to the application. Please register as a client to proceed.");
        } else {
            throw new InvalidClientException(Constants.ERROR_LOGIN_EMAIL);
        }
    }

    @Override
    public ViewClientModel editProfile(String id, UpdateClientModel updateClientModel) {
        Client toEdit = getClientToEdit(id);

        toEdit.setFirstName(updateClientModel.getFirstName());
        toEdit.setLastName(updateClientModel.getLastName());

        ContactDetails contactDetails = new ContactDetails(
                toEdit.getContactDetails().getEmailAddress(),
                updateClientModel.getMobileNumber()
        );

        toEdit.setContactDetails(contactDetails);

        clientRepository.save(toEdit);

        ViewClientModel viewClientModel = clientFactory.buildViewClientModel(toEdit);
        viewClientModel.setLoyaltyPoints(loyaltyPointsService.viewClientLoyaltyPoints(toEdit));
        viewClientModel.setVouchers(loyaltyPointsService.viewClientVouchers(toEdit));
        return viewClientModel;
    }

    @Override
    public ViewClientModel editClientProfile(String id, UpdateClientProfileModel updateClientProfileModel) {
        Client toEdit = getClientToEdit(id);

        toEdit.setFirstName(updateClientProfileModel.getFirstName());
        toEdit.setLastName(updateClientProfileModel.getLastName());

        ContactDetails contactDetails = new ContactDetails(
                toEdit.getContactDetails().getEmailAddress(),
                updateClientProfileModel.getMobileNumber()
        );

        toEdit.setContactDetails(contactDetails);

        if (!updateClientProfileModel.getDateOfBirth().equals("")) {
            toEdit.setDateOfBirth(applicationProperties.getLongDateTimeFormatter().parseDateTime(updateClientProfileModel.getDateOfBirth()).toDate().getTime());
        }

        clientRepository.save(toEdit);

        ViewClientModel viewClientModel = clientFactory.buildViewClientModel(toEdit);
        viewClientModel.setLoyaltyPoints(loyaltyPointsService.viewClientLoyaltyPoints(toEdit));
        viewClientModel.setVouchers(loyaltyPointsService.viewClientVouchers(toEdit));
        return viewClientModel;
    }

    @Override
    public ViewClientModel addNoteToClientProfile(String authorisation, String clientId, ClientNewNoteModel clientNewNoteModel) {

        User user = authTokenServices.extractUser(authorisation.substring(7));

        Client toEdit = getClientToEdit(clientId);

        toEdit.getNotes().add(NoteFactory.buildNote(clientNewNoteModel, user));

        clientRepository.save(toEdit);

        ViewClientModel viewClientModel = clientFactory.buildViewClientModel(toEdit);
        viewClientModel.setLoyaltyPoints(loyaltyPointsService.viewClientLoyaltyPoints(toEdit));
        viewClientModel.setVouchers(loyaltyPointsService.viewClientVouchers(toEdit));
        return viewClientModel;
    }

    @Override
    public ClientDisabledModel disableAccount(String clientId) {
        Client toEdit = getClientToEdit(clientId);
        toEdit.setActive(false);
        clientRepository.save(toEdit);
        return new ClientDisabledModel("The clients account was successfully disabled.");
    }

    @Override
    public ClientEnabledModel enableAccount(String clientId) {
        Client toEdit = getClientToEdit(clientId);
        toEdit.setActive(true);
        clientRepository.save(toEdit);
        return new ClientEnabledModel("The clients account was successfully enabled.");
    }

    @Override
    public List<ViewClientModel> getAllClients() {
        return clientRepository
                .findAllByIsActiveOrderByFirstName(true)
                .stream()
                .map(client -> {
                    ViewClientModel viewClientModel = clientFactory.buildViewClientModel(client);
                    viewClientModel.setLoyaltyPoints(loyaltyPointsService.viewClientLoyaltyPoints(client));
                    viewClientModel.setVouchers(loyaltyPointsService.viewClientVouchers(client));
                    return viewClientModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ViewFullClientModel> getAllClientsToEdit() {
        List<Client> clients = clientRepository.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")));

        return clients
                .stream()
                .map(client -> {
                    ViewFullClientModel viewClientModel = clientFactory.buildViewFullClientModel(client);
                    viewClientModel.setLoyaltyPoints(loyaltyPointsService.viewClientLoyaltyPoints(client));
                    viewClientModel.setVouchers(loyaltyPointsService.viewClientVouchers(client));
                    return viewClientModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ViewClientModel viewClientProfile(String id) {

        Client client = getClientToEdit(id);

        ViewClientModel viewClientModel = clientFactory.buildViewClientModel(client);
        viewClientModel.setLoyaltyPoints(loyaltyPointsService.viewClientLoyaltyPoints(client));
        viewClientModel.setVouchers(loyaltyPointsService.viewClientVouchers(client));
        return viewClientModel;
    }

    @Override
    public ViewClientReceiveMarketingEmailModel activateMarketingEmailsRetrieval(final String clientId, final ReceiveMarketingEmailModel receiveMarketingEmail) {
        Client toEdit = getClientToEdit(clientId);

        toEdit.setReceiveMarketingEmails(receiveMarketingEmail.isReceiveEmails());
        clientRepository.save(toEdit);

        return clientFactory.buildReceiveMarketingEmailModel(toEdit);
    }

    @Override
    public ReceiveMarketingEmailModel findReceiveMarketingEmailsPermissionOfUser(final String clientId) {//NOPMD

        Optional<Client> clientOptional = clientRepository.findById(new ObjectId(clientId));

        if (!clientOptional.isPresent()) {
            throw new ResourceNotFoundException("We could not find the requested client to disable. Please contact the administrator.");
        }
        return clientFactory.buildRetrievalModel(clientOptional.get());
    }

    private Client getClientToEdit(String clientId) {
        if (clientId == null) {
            throw new ResourceNotFoundException("We could not find the requested client to disable. Please contact the administrator.");
        }
        Optional<Client> toBeUpdated = clientRepository.findById(new ObjectId(clientId));

        if (!toBeUpdated.isPresent()) {
            throw new ResourceNotFoundException("We could not find the requested client to disable. Please contact the administrator.");
        }

        return toBeUpdated.get();
    }
}
