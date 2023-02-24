package com.qbook.app.utilities.factory;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.salesModels.ProductModel;
import com.qbook.app.application.models.salesModels.SaleSpecificBookingModel;
import com.qbook.app.domain.models.Booking;
import com.qbook.app.domain.models.CashupItem;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class SalesFactory {
    private final ApplicationProperties applicationProperties;

    public SaleSpecificBookingModel buildSaleSpecificBookingModel(Booking booking) {
        SaleSpecificBookingModel saleSpecificBookingModel = new SaleSpecificBookingModel();
        saleSpecificBookingModel.setClientId(booking.getClient().getId().toString());
        saleSpecificBookingModel.setClientFullName(booking.getClient().getFirstName() + " " + booking.getClient().getLastName());
        saleSpecificBookingModel.setClientEmail(booking.getClient().getContactDetails().getEmailAddress());
        saleSpecificBookingModel.setEmployeeId(booking.getEmployee().getId().toString());
        saleSpecificBookingModel.setEmployeeFullName(booking.getEmployee().getFirstName() + " " + booking.getEmployee().getLastName());
        saleSpecificBookingModel.setEmployeeEmail(booking.getEmployee().getContactDetails().getEmailAddress());
        saleSpecificBookingModel.setBookingDate(applicationProperties.getShortDateTimeFormatter().print(new DateTime(booking.getStartDateTime()).toDate().getTime()));
        saleSpecificBookingModel.setStartTime(applicationProperties.getSimpleTimeFormat().format(new DateTime(booking.getStartDateTime()).toDate()));


        return saleSpecificBookingModel;
    }

    public ProductModel buildProductModel(CashupItem cashupItem) {
        ProductModel productModel = new ProductModel();
        productModel.setName(cashupItem.getItemName());
        productModel.setPrice(cashupItem.getItemPrice());
        productModel.setQuantity(cashupItem.getQuantity());
        return productModel;
    }
}
