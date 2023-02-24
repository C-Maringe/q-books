package com.qbook.app.utilities.factory;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.BookingItemModel;
import com.qbook.app.application.models.scheduleModels.ScheduleTreatmentModel;
import com.qbook.app.domain.models.Treatment;
import lombok.AllArgsConstructor;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ScheduleFactory {
    private final ApplicationProperties applicationProperties;

    public ScheduleTreatmentModel buildScheduleTreatmentModel(Treatment treatment) {
        ScheduleTreatmentModel scheduleTreatmentModel = new ScheduleTreatmentModel();
        scheduleTreatmentModel.setTreatmentId(treatment.getId().toString());
        scheduleTreatmentModel.setTreatmentName(treatment.getTreatmentName());
        scheduleTreatmentModel.setTreatmentDescription(treatment.getTreatmentDescription());
        scheduleTreatmentModel.setTreatmentDuration(treatment.getDuration());
        if(treatment.isDoneByJunior()) {
            scheduleTreatmentModel.setTreatmentPrice(
                    applicationProperties.getDecimalFormat().format(treatment.getJuniorPrice() * applicationProperties.getVatAmount())
            );
        } else {
            scheduleTreatmentModel.setTreatmentPrice(
                    applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice() * applicationProperties.getVatAmount())
            );
        }

        if(treatment.isSpecial() && treatment.getSpecialEndDate() != 0L) {
            scheduleTreatmentModel.setSpecial(true);
            scheduleTreatmentModel.setSpecialPrice(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice() * applicationProperties.getVatAmount()));
        }
        return scheduleTreatmentModel;
    }

    public ScheduleTreatmentModel buildScheduleTreatmentModel(Treatment treatment, LocalDateTime bookingDateTime) {
        ScheduleTreatmentModel scheduleTreatmentModel = new ScheduleTreatmentModel();
        scheduleTreatmentModel.setTreatmentId(treatment.getId().toString());
        scheduleTreatmentModel.setTreatmentName(treatment.getTreatmentName());
        scheduleTreatmentModel.setTreatmentDescription(treatment.getTreatmentDescription());
        scheduleTreatmentModel.setTreatmentDuration(treatment.getDuration());
        scheduleTreatmentModel.setTreatmentPrice(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice() * applicationProperties.getVatAmount()));
        if(treatment.isDoneByJunior()) {
            scheduleTreatmentModel.setTreatmentPrice(
                applicationProperties.getDecimalFormat().format(treatment.getJuniorPrice() * applicationProperties.getVatAmount())
            );
        } else {
            scheduleTreatmentModel.setTreatmentPrice(
                    applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice() * applicationProperties.getVatAmount())
            );
        }
        if(treatment.isSpecial() && treatment.getSpecialEndDate() != 0L) {
            if(treatment.getSpecialEndDate() >= bookingDateTime.toDateTime().getMillis()) {
                scheduleTreatmentModel.setSpecial(true);
                scheduleTreatmentModel.setSpecialPrice(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice() * applicationProperties.getVatAmount()));
            }
        }
        return scheduleTreatmentModel;
    }

    public BookingItemModel buildBookingItemModel(Treatment treatment) {
        BookingItemModel bookingItemModel = new BookingItemModel();
        bookingItemModel.setId(treatment.getId().toString());
        bookingItemModel.setName(treatment.getTreatmentName());
        bookingItemModel.setDescription(treatment.getTreatmentDescription());
        bookingItemModel.setDuration(treatment.getDuration());
        if(treatment.isDoneByJunior()) {
            bookingItemModel.setPrice(
                    Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getJuniorPrice() * applicationProperties.getVatAmount()))
            );
        } else {
            bookingItemModel.setPrice(
                    Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSeniorPrice() * applicationProperties.getVatAmount()))
            );
        }
        bookingItemModel.setSpecialOffer(false);
        if(treatment.isSpecial() && treatment.getSpecialEndDate() != 0L) {
            bookingItemModel.setSpecialOffer(true);
            bookingItemModel.setPrice(Double.parseDouble(applicationProperties.getDecimalFormat().format(treatment.getSpecialPrice() * applicationProperties.getVatAmount())));
        }
        return bookingItemModel;
    }
}
