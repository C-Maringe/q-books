package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.exception.applicationConfigurationExceptions.ConfigurationTestEmailException;
import com.qbook.app.application.models.configurationModels.*;
import com.qbook.app.application.models.webPlatformModels.ViewDepositThreshold;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import com.qbook.app.domain.models.ApplicationConfigurations;
import com.qbook.app.domain.repository.ApplicationConfigurationsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.logging.Level;

@Log
@Service
@AllArgsConstructor
public class ApplicationConfigurationServicesImpl implements ApplicationConfigurationsServices {
	private final ApplicationConfigurationsRepository applicationConfigurationsRepository;
	private final ModelMapper modelMapper;

	@Override
	public ApplicationConfigurationsCreatedModel setupAppConfigurations(NewApplicationConfigurationModel newApplicationConfigurationModel) {
		return generateCreatedModel(newApplicationConfigurationModel);
	}

	private ApplicationConfigurationsCreatedModel generateCreatedModel(NewApplicationConfigurationModel newApplicationConfigurationModel) {

		ApplicationConfigurations applicationConfigurations = modelMapper.map(newApplicationConfigurationModel, ApplicationConfigurations.class);

		applicationConfigurationsRepository.save(applicationConfigurations);

		ApplicationConfigurationsCreatedModel applicationConfigurationsCreatedModel = new ApplicationConfigurationsCreatedModel();
		applicationConfigurationsCreatedModel.setSuccess(true);
		applicationConfigurationsCreatedModel.setMessage("The application configuration was successfully added.");
		return applicationConfigurationsCreatedModel;
	}

	@Override
	public ApplicationConfigurationsUpdatedModel editAppConfigurations(UpdateApplicationConfigurationModel updateApplicationConfigurationModel) {
		Optional<ApplicationConfigurations> toEditOptional = applicationConfigurationsRepository.findById(new ObjectId(updateApplicationConfigurationModel.getId()));

		if(toEditOptional.isPresent()){
			ModelMapper modelMapper = new ModelMapper();

			ApplicationConfigurations toEdit = modelMapper.map(updateApplicationConfigurationModel, ApplicationConfigurations.class);
			toEdit.setId(new ObjectId(updateApplicationConfigurationModel.getId()));
			applicationConfigurationsRepository.save(toEdit);

			ApplicationConfigurationsUpdatedModel applicationConfigurationsUpdatedModel = new ApplicationConfigurationsUpdatedModel();
			applicationConfigurationsUpdatedModel.setSuccess(true);
			applicationConfigurationsUpdatedModel.setMessage("The application configuration was successfully updated.");
			return applicationConfigurationsUpdatedModel;

		} else {
			throw new ResourceNotFoundException("No configurations were found. Please refresh and try again, if it still persists please contact the administrator.");
		}
	}

	@Override
	public ApplicationConfigurationModel viewApplicationConfiguration() {
		List<ApplicationConfigurations> applicationConfigurations = applicationConfigurationsRepository.findAll();

		if(applicationConfigurations.size() > 0) {
			ApplicationConfigurations recent = applicationConfigurations.get(0);

			ModelMapper modelMapper = new ModelMapper();

			return modelMapper.map(recent, ApplicationConfigurationModel.class);
		} else {
			throw new ResourceNotFoundException("You have not yet setup the configuration settings. Lets do it now.");
		}
	}

    @Override
    public ViewOperationTimesModel viewOperatingTimes() {
	    List<ApplicationConfigurations> appConfigs = applicationConfigurationsRepository.findAll();
	    ApplicationConfigurations recent = appConfigs.get(0);
	    ViewOperationTimesModel viewOperationTimesModel = new ViewOperationTimesModel();
		viewOperationTimesModel.setWorkingDays(recent.getWorkingDays());
	    return viewOperationTimesModel;
    }

	@Override
	public ViewDepositThreshold viewDepositThreshold() {
		List<ApplicationConfigurations> appConfigs = applicationConfigurationsRepository.findAll();

		ApplicationConfigurations recent = appConfigs.get(0);

		return modelMapper.map(recent, ViewDepositThreshold.class);
	}
}
