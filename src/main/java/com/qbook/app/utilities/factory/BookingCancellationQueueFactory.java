package com.qbook.app.utilities.factory;

import com.qbook.app.application.models.BookingCancellationQueueMember;
import com.qbook.app.domain.models.BookingCancellationQueue;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;

public class BookingCancellationQueueFactory {
    private static ModelMapper modelMapper = new ModelMapper();

    public static BookingCancellationQueue buildBookingCancellationQueue(BookingCancellationQueueMember bookingCancellationQueueMember) {

        BookingCancellationQueue bookingCancellationQueue = modelMapper.map(bookingCancellationQueueMember, BookingCancellationQueue.class);
        bookingCancellationQueue.setDateAdded(new DateTime().getMillis());

        return bookingCancellationQueue;
    }

}
