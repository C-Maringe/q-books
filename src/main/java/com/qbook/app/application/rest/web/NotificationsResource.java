package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.NotificationSuccessModel;
import com.qbook.app.application.models.NotifySpecificClientModel;
import com.qbook.app.application.services.appservices.NotificationsService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationsResource {

    private final NotificationsService notificationsService;

    @PutMapping("{date}")
    public ResponseEntity<NotificationSuccessModel> notifyAllClientsOnDay(@PathVariable("date") String date) {
        return new ResponseEntity<>(notificationsService.sendReminderNotificationForAllClientsForSpecificDay(date), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<NotificationSuccessModel> notifySpecificClientOnDay(@RequestBody NotifySpecificClientModel notifySpecificClientModel) {
        return new ResponseEntity<>(notificationsService.sendReminderNotificationForToSpecificClientsForSpecificDay(notifySpecificClientModel), HttpStatus.OK);
    }
}
