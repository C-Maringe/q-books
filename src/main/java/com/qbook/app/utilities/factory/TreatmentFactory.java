package com.qbook.app.utilities.factory;

import com.qbook.app.application.configuration.exception.TreatmentException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.treatmentModels.NewTreatmentModel;
import com.qbook.app.application.models.treatmentModels.UpdateTreatmentModel;
import com.qbook.app.application.models.treatmentModels.ViewTreatmentModel;
import com.qbook.app.domain.models.EmployeeType;
import com.qbook.app.domain.models.Treatment;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TreatmentFactory {
    private final ApplicationProperties applicationProperties;

    public Treatment buildTreatment(NewTreatmentModel treatment){
        Treatment t = new Treatment();
        t.setId(new ObjectId());
        t.setDoneByJunior(treatment.isDoneByJunior());
        t.setJuniorPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getJuniorPrice())));
        t.setDoneBySenior(treatment.isDoneBySenior());
        t.setSeniorPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice())));
        t.setSpecial(treatment.isSpecial());
        if(t.isSpecial()) {
            t.setSpecialEndDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(treatment.getSpecialEndDate()).toDate().getTime());
            t.setSpecialPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice())));
        }

        if(t.isSpecial()) {
            if(t.getSpecialEndDate() != 0L) {
                t.setSpecialEndDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(treatment.getSpecialEndDate()).toDate().getTime());
                t.setSpecialPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice())));
            } else {
                throw new TreatmentException("The treatment is being registered as a special please ensure you add an end date for the special.");
            }
        }
        t.setDuration(treatment.getDuration());
        t.setTreatmentDescription(treatment.getTreatmentDescription().trim());
        t.setTreatmentName(treatment.getTreatmentName().trim());
        return t;
    }

    public void updateTreatment(Treatment treatment, UpdateTreatmentModel updateTreatmentModel, EmployeeType employeeType) {
        treatment.setEmployeeType(employeeType);
        treatment.setDoneByJunior(updateTreatmentModel.isDoneByJunior());
        treatment.setJuniorPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(updateTreatmentModel.getJuniorPrice())));
        treatment.setDoneBySenior(updateTreatmentModel.isDoneBySenior());
        treatment.setSeniorPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(updateTreatmentModel.getSeniorPrice())));
        treatment.setSpecial(updateTreatmentModel.isSpecial());
        treatment.setDuration(updateTreatmentModel.getDuration());
        treatment.setTreatmentDescription(updateTreatmentModel.getTreatmentDescription());
        treatment.setTreatmentName(updateTreatmentModel.getTreatmentName());

        if(treatment.isSpecial()) {
            if(updateTreatmentModel.getSpecialEndDate() != null && !updateTreatmentModel.getSpecialEndDate().equals("")) {
                treatment.setSpecialEndDate(DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(updateTreatmentModel.getSpecialEndDate()).toDate().getTime());
                treatment.setSpecialPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(updateTreatmentModel.getSpecialPrice())));
            } else {
                throw new TreatmentException("The treatment is being registered as a special please ensure you add an end date for the special.");
            }
        }
    }

    public ViewTreatmentModel buildViewTreatmentModel(Treatment treatment) {
        return ViewTreatmentModel
                .builder()
                .isActive(treatment.isActive())
                .treatmentId(treatment.getId().toString())
                .isDoneBySenior(treatment.isDoneBySenior())
                .isDoneByJunior(treatment.isDoneByJunior())
                .juniorPrice(treatment.getJuniorPrice())
                .seniorPrice(treatment.getSeniorPrice())
                .special(treatment.isSpecial())
                .specialEndDate((treatment.isSpecial())?new DateTime(treatment.getSpecialEndDate()).toString(applicationProperties.getShortDateTimeFormatter()): "")
                .specialPrice(treatment.getSpecialPrice())
                .duration(treatment.getDuration())
                .treatmentDescription(treatment.getTreatmentDescription())
                .treatmentName(treatment.getTreatmentName())
                .employeeType(treatment.getEmployeeType().getEmployeeType())
                .build();
    }
}
