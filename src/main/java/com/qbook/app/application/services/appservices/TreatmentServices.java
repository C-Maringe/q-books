package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.treatmentModels.*;

import java.util.List;

/**
 * Created by ironhulk on 11/11/2015.
 */
public interface TreatmentServices {

    TreatmentCreatedModel createNewTreatment(NewTreatmentModel treatment);

    TreatmentUpdatedModel updateTreatment(UpdateTreatmentModel updateTreatmentModel);

    TreatmentDisabledModel disableTreatment(String id);

    TreatmentEnabledModel enableTreatment(String id);

    ViewTreatmentModel viewTreatment(String id);

    List<ViewTreatmentModel> viewAllTreatments();

    List<ViewTreatmentModel> viewAllTreatmentsByEmployeeType(String employeeType);
}
