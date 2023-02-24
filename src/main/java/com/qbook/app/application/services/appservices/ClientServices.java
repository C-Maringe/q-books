package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.*;
import com.qbook.app.application.models.clientModels.ClientDisabledModel;
import com.qbook.app.application.models.clientModels.ClientEnabledModel;
import com.qbook.app.domain.models.PlatformUsed;

import java.util.List;


public interface ClientServices {

	RegisteredClientModel registerClient(NewClientModel newClientModel, PlatformUsed platformUsed);

	LoggedInClientModel login(LoginClientModel loginClientModel);

	ViewClientModel editProfile(String id, UpdateClientModel updateClientModel);

	ViewClientModel editClientProfile(String id, UpdateClientProfileModel updateClientProfileModel);

	ViewClientModel addNoteToClientProfile(String authorisation, String clientId, ClientNewNoteModel clientNewNoteModel);

	ClientDisabledModel disableAccount(String clientId);
	
	ClientEnabledModel enableAccount(String clientId);

	List<ViewClientModel> getAllClients();

	List<ViewFullClientModel> getAllClientsToEdit();

	ViewClientModel viewClientProfile(String id);

	ViewClientReceiveMarketingEmailModel activateMarketingEmailsRetrieval(String clientId, ReceiveMarketingEmailModel receiveMarketingEmail);

	ReceiveMarketingEmailModel findReceiveMarketingEmailsPermissionOfUser(final String clientId);
}
