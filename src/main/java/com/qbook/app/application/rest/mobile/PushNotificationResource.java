package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.DeviceRegistrationModel;
import com.qbook.app.application.models.PushNotificationBodyModel;
import com.qbook.app.application.models.PushNotificationRegisterDeviceModel;
import com.qbook.app.application.services.appservices.PushNotificationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/mobile/push-notification")
@AllArgsConstructor
public class PushNotificationResource {
    private final PushNotificationService service;

    @PutMapping("/register-user-device")
    public ResponseEntity<Void> register(@RequestHeader("Authorization") String Authorization, final @RequestBody PushNotificationRegisterDeviceModel model) {
        service.registerUserDevice(model, Authorization);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send/{id}")//NOPMD
    public ResponseEntity<Void> sendNotifications(final @RequestBody PushNotificationBodyModel model, @PathVariable("id") String userId) {//NOPMD
        service.sendNotification(model, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user-device")
    public ResponseEntity<DeviceRegistrationModel> checkDeviceRegistered(@RequestHeader("Authorization") String Authorization) {
        return new ResponseEntity<>(service.checkDeviceRegistration(Authorization), HttpStatus.OK);
    }
}
