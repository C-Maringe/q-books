package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.treatmentModels.*;
import com.qbook.app.application.services.appservices.TreatmentServices;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.EmployeeType;
import com.qbook.app.domain.models.Treatment;
import com.qbook.app.domain.repository.EmployeeTypeRepository;
import com.qbook.app.domain.repository.TreatmentRepository;
import com.qbook.app.utilities.factory.Factory;
import com.qbook.app.utilities.factory.TreatmentFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
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
public class TreatmentServicesImpl implements TreatmentServices {
    private final EmployeeTypeRepository employeeTypeRepository;
    private final TreatmentRepository treatmentRepository;
    private final TreatmentFactory treatmentFactory;

    @Override
    public TreatmentCreatedModel createNewTreatment(NewTreatmentModel newTreatmentModel) {
        Treatment treatment = treatmentFactory.buildTreatment(newTreatmentModel);

        Optional<EmployeeType> employeeTypeOptional = employeeTypeRepository.findByEmployeeType(newTreatmentModel.getEmployeeType());

        if(!employeeTypeOptional.isPresent()) {
            throw new ResourceNotFoundException("The employee type was not found.");
        }
        EmployeeType employeeType = employeeTypeOptional.get();

        treatment.setEmployeeType(employeeType);
        treatmentRepository.save(treatment);

        return TreatmentCreatedModel.
                builder()
                .success(true)
                .message("The treatment was successfully registered")
                .treatmentId(treatment.getId().toString())
                .build();
    }

    @Override
    public TreatmentUpdatedModel updateTreatment(UpdateTreatmentModel updateTreatmentModel) {
        Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(updateTreatmentModel.getTreatmentId()));

        if(!treatmentOptional.isPresent()) {
            throw new ResourceNotFoundException("The treatment was not found.");
        }

        Treatment t = treatmentOptional.get();

        Optional<EmployeeType> employeeTypeOptional = employeeTypeRepository.findByEmployeeType(updateTreatmentModel.getEmployeeType());

        if(!employeeTypeOptional.isPresent()) {
            throw new ResourceNotFoundException("The employee type was not found.");
        }

        treatmentFactory.updateTreatment(t, updateTreatmentModel, employeeTypeOptional.get());
        treatmentRepository.save(t);

        return TreatmentUpdatedModel
                .builder()
                .success(true)
                .message("The treatment was successfully updated")
                .build();
    }

    @Override
    public TreatmentDisabledModel disableTreatment(String id) {
        Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(id));

        if(!treatmentOptional.isPresent()) {
            throw new ResourceNotFoundException("The treatment was not found.");
        }

        Treatment t = treatmentOptional.get();

        t.setActive(false);

        treatmentRepository.save(t);

        return TreatmentDisabledModel
                .builder()
                .success(true)
                .message("The treatment was successfully disabled")
                .build();
    }

    @Override
    public TreatmentEnabledModel enableTreatment(String id) {
        Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(id));

        if(!treatmentOptional.isPresent()) {
            throw new ResourceNotFoundException("The treatment was not found.");
        }

        Treatment t = treatmentOptional.get();

        t.setActive(true);

        treatmentRepository.save(t);

        return TreatmentEnabledModel
                .builder()
                .success(true)
                .message("The treatment was successfully enabled")
                .build();
    }

    @Override
    public ViewTreatmentModel viewTreatment(String id) {
        Optional<Treatment> treatmentOptional = treatmentRepository.findById(new ObjectId(id));

        if(!treatmentOptional.isPresent()) {
            throw new ResourceNotFoundException("The treatment was not found.");
        }

	    return treatmentFactory.buildViewTreatmentModel(treatmentOptional.get());
    }

    @Override
    public List<ViewTreatmentModel> viewAllTreatments() {
        List<Treatment> treatments = treatmentRepository.findAll();

        return treatments
                .stream()
                .map(treatmentFactory::buildViewTreatmentModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ViewTreatmentModel> viewAllTreatmentsByEmployeeType(String employeeType) {
        List<Treatment> treatments;
        if(employeeType.equals("All")) {
            treatments = treatmentRepository.findAll(Sort.by(new Sort.Order(Sort.Direction.ASC, "treatmentName")));
        } else {
            Optional<EmployeeType> employeeTypeOptional = employeeTypeRepository.findByEmployeeType(employeeType);
            if(!employeeTypeOptional.isPresent()) {
                throw new ResourceNotFoundException("We are unable to find the treatments");
            }

            treatments = treatmentRepository.findAllByEmployeeTypeId(employeeTypeOptional.get().getId(), Sort.by(new Sort.Order(Sort.Direction.ASC, "treatmentName")));
        }

        return treatments
                .stream()
                .map(treatmentFactory::buildViewTreatmentModel)
                .collect(Collectors.toList());
    }
}
