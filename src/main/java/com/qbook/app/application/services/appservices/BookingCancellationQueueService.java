package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.BookingCancellationMember;
import com.qbook.app.application.models.BookingCancellationQueueMember;
import com.qbook.app.application.models.BookingCancellationQueueModel;
import com.qbook.app.domain.models.Booking;

import java.util.List;

public interface BookingCancellationQueueService {

    BookingCancellationQueueModel addClientToQueue(BookingCancellationQueueMember bookingCancellationQueueMember);

    void notifyNextPersonInQueue(Booking booking);

    List<BookingCancellationMember> viewAllCancellationQueueClients();

    void cleanupOldItemsInQueue();
}
