package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.configurationModels.*;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/auth/configurations")
@AllArgsConstructor
public class ConfigurationsResource {
	private final ApplicationConfigurationsServices applicationConfigurationServices;

	@PostMapping
	public ResponseEntity<ApplicationConfigurationsCreatedModel> setupConfigSettings(@RequestBody NewApplicationConfigurationModel newApplicationConfigurationModel){
        log.info("ConfigurationsResource.setupConfigSettings() called at " + System.currentTimeMillis());
        ApplicationConfigurationsCreatedModel applicationConfigurationsCreatedModel =
                applicationConfigurationServices.setupAppConfigurations(newApplicationConfigurationModel);
        log.info("ConfigurationsResource.setupConfigSettings() called at " + System.currentTimeMillis());
        return new ResponseEntity<>(applicationConfigurationsCreatedModel, HttpStatus.CREATED);
	}

	@PutMapping
	public ResponseEntity<ApplicationConfigurationsUpdatedModel> editConfigSettings(@RequestBody UpdateApplicationConfigurationModel updateApplicationConfigurationModel){
        log.info("ConfigurationsResource.editConfigSettings() called at " + System.currentTimeMillis());
        ApplicationConfigurationsUpdatedModel applicationConfigurationsUpdatedModel =
                applicationConfigurationServices.editAppConfigurations(updateApplicationConfigurationModel);
        log.info("ConfigurationsResource.editConfigSettings() called at " + System.currentTimeMillis());
        return new ResponseEntity<>(applicationConfigurationsUpdatedModel, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<ApplicationConfigurationModel> viewConfigSettings(){
        log.info("ConfigurationsResource.viewConfigSettings() called at " + System.currentTimeMillis());
        ApplicationConfigurationModel applicationConfigurationModel =
                applicationConfigurationServices.viewApplicationConfiguration();
        log.info("ConfigurationsResource.viewConfigSettings() called at " + System.currentTimeMillis());
        return new ResponseEntity<>(applicationConfigurationModel, HttpStatus.OK);
	}
}
