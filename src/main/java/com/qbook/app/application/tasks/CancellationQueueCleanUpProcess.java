package com.qbook.app.application.tasks;

import com.qbook.app.application.services.appservices.BookingCancellationQueueService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Log
@Component
@AllArgsConstructor
public class CancellationQueueCleanUpProcess {
    private final BookingCancellationQueueService bookingCancellationQueueService;

    @Scheduled(cron = "0 0 6 * * *") // Run 6 am everyday
    public void run() {
        log.log(Level.INFO, "Starting process to remove old cancellation queue items. Time -> " + System.currentTimeMillis());
        bookingCancellationQueueService.cleanupOldItemsInQueue();
        log.log(Level.INFO, "Completed process to remove old cancellation queue items. Time -> " + System.currentTimeMillis());
    }
}
