package com.qbook.app.utilities.factory;


import com.qbook.app.application.configuration.exception.PasswordMismatchException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.*;
import com.qbook.app.domain.models.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ClientFactory {
    private final ApplicationProperties applicationProperties;

    public Client buildClientModel(NewClientModel newClientModel) {
        if (!newClientModel.getPassword().equals(newClientModel.getConfirmPassword())) {
            throw new PasswordMismatchException("Please ensure the passwords provided match.");
        }

        StrongPasswordEncryptor strongPasswordEncryptor = new StrongPasswordEncryptor();
        ContactDetails cd = new ContactDetails(newClientModel.getEmailAddress(), newClientModel.getMobileNumber());

        Client client = new Client();
        client.setContactDetails(cd);
        client.setUsername(newClientModel.getEmailAddress().trim());
        client.setFirstName(newClientModel.getFirstName().trim());
        client.setLastName(newClientModel.getLastName().trim());
        client.setPassword(strongPasswordEncryptor.encryptPassword(newClientModel.getPassword()));
        client.setDateRegistered(new DateTime().getMillis());
        client.setRole(UserType.CLIENT.getName());
        client.setActive(true);
        client.setUserPermissionList(setupUserPermissionsFromModel());

        return client;
    }

    public List<UserPermission> setupUserPermissionsFromModel() {
        List<UserPermission> userPermissionList = new ArrayList<>();

        UserPermission permission = new UserPermission();
        permission.setPermissionFeature(PermissionFeature.ANALYTICS.getName());
        permission.setCanRead(false);
        permission.setCanWrite(false);
        userPermissionList.add(permission);

        UserPermission bookings = new UserPermission();
        bookings.setPermissionFeature(PermissionFeature.BOOKINGS.getName());
        bookings.setCanRead(false);
        bookings.setCanWrite(false);
        userPermissionList.add(bookings);

        UserPermission configurations = new UserPermission();
        configurations.setPermissionFeature(PermissionFeature.CONFIGURATIONS.getName());
        configurations.setCanRead(false);
        configurations.setCanWrite(false);
        userPermissionList.add(configurations);

        UserPermission employees = new UserPermission();
        employees.setPermissionFeature(PermissionFeature.EMPLOYEES.getName());
        employees.setCanRead(false);
        employees.setCanWrite(false);
        userPermissionList.add(employees);

        UserPermission marketing = new UserPermission();
        marketing.setPermissionFeature(PermissionFeature.MARKETING.getName());
        marketing.setCanRead(false);
        marketing.setCanWrite(false);
        userPermissionList.add(marketing);

        UserPermission reporting = new UserPermission();
        reporting.setPermissionFeature(PermissionFeature.REPORTING.getName());
        reporting.setCanRead(false);
        reporting.setCanWrite(false);
        userPermissionList.add(reporting);

        UserPermission schedule = new UserPermission();
        schedule.setPermissionFeature(PermissionFeature.SCHEDULE.getName());
        schedule.setCanRead(true);
        schedule.setCanWrite(true);
        userPermissionList.add(schedule);

        UserPermission sales = new UserPermission();
        sales.setPermissionFeature(PermissionFeature.SALES.getName());
        sales.setCanRead(false);
        sales.setCanWrite(false);
        userPermissionList.add(sales);

        UserPermission treatments = new UserPermission();
        treatments.setPermissionFeature(PermissionFeature.TREATMENTS.getName());
        treatments.setCanRead(false);
        treatments.setCanWrite(false);
        userPermissionList.add(treatments);

        UserPermission clients = new UserPermission();
        clients.setPermissionFeature(PermissionFeature.CLIENT_MANAGEMENT.getName());
        clients.setCanRead(false);
        clients.setCanWrite(false);
        userPermissionList.add(clients);

        UserPermission goals = new UserPermission();
        goals.setPermissionFeature(PermissionFeature.GOALS.getName());
        goals.setCanRead(false);
        goals.setCanWrite(false);
        userPermissionList.add(goals);

        return userPermissionList;
    }

    public ViewClientModel buildViewClientModel(Client client) {
        ModelMapper modelMapper = new ModelMapper();

        ViewClientModel viewClientModel = modelMapper.map(client, ViewClientModel.class);
        viewClientModel.setDateOfBirth(applicationProperties.getLongDateTimeFormatter().print(client.getDateOfBirth()));
        viewClientModel.setEmailAddress(client.getContactDetails().getEmailAddress());
        viewClientModel.setMobileNumber(client.getContactDetails().getMobileNumber());

        return viewClientModel;
    }

    public ViewFullClientModel buildViewFullClientModel(Client client) {
        ModelMapper modelMapper = new ModelMapper();

        ViewFullClientModel viewClientModel = modelMapper.map(client, ViewFullClientModel.class);
        viewClientModel.setDateOfBirth(applicationProperties.getLongDateTimeFormatter().print(client.getDateOfBirth()));
        viewClientModel.setEmailAddress(client.getContactDetails().getEmailAddress());
        viewClientModel.setMobileNumber(client.getContactDetails().getMobileNumber());

        return viewClientModel;
    }

    public ViewClientReceiveMarketingEmailModel buildReceiveMarketingEmailModel(Client client) {
        ModelMapper modelMapper = new ModelMapper();
        ViewClientReceiveMarketingEmailModel clientModel = modelMapper.map(client, ViewClientReceiveMarketingEmailModel.class);
        clientModel.setReceiveEmails(client.isReceiveMarketingEmails());

        return clientModel;
    }

    public ReceiveMarketingEmailModel buildRetrievalModel(final Client client) {
        final ReceiveMarketingEmailModel model = new ReceiveMarketingEmailModel();
        model.setReceiveEmails(client.isReceiveMarketingEmails());
        return model;
    }
}
