package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.BookingCancellationModel;
import com.qbook.app.application.models.BookingCreatedModel;
import com.qbook.app.application.models.productModels.ProductItemToCaptureModel;
import com.qbook.app.application.models.salesModels.*;
import org.bson.types.ObjectId;

public interface SalesServices {
	
    BookingCancellationModel cancelBooking(ObjectId bookingId, ObjectId userId);

	SaleSpecificBookingModel viewSpecificSaleToUpdate(String id);

	SalesBookingOverviewModel startCashingUpSpecificDay(String employeeId, String dayCashingUp);

	SalesEditedBookingModel editBookingAndComplete(SalesEditBookingModel salesEditBookingModel);

	SalesItemCaptured captureSalesItem(SalesItemToCaptureModel salesItemToCaptureModel);

	SalesItemCaptured captureSalesProductItem(ProductItemToCaptureModel productItemToCaptureModel);

	SalesCashupCompletedModel completeCashup(String authorization, SalesCashupCompleteModel salesCashupCompleteModel);

	BookingCreatedModel createBookingForClientDuringCashUp(SaleNewBookingModel saleNewBookingModel);
}
