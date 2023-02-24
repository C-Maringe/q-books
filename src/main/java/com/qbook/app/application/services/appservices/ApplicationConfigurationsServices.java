package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.configurationModels.*;
import com.qbook.app.application.models.webPlatformModels.ViewDepositThreshold;

public interface ApplicationConfigurationsServices {

	ApplicationConfigurationsCreatedModel setupAppConfigurations(NewApplicationConfigurationModel newApplicationConfigurationModel);

	ApplicationConfigurationsUpdatedModel editAppConfigurations(UpdateApplicationConfigurationModel updateApplicationConfigurationModel);
	
	ApplicationConfigurationModel viewApplicationConfiguration();

	ViewOperationTimesModel viewOperatingTimes();

	ViewDepositThreshold viewDepositThreshold();
}
