package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.LoggedInClientModel;
import com.qbook.app.application.models.LoginClientModel;
import com.qbook.app.application.models.NewClientModel;
import com.qbook.app.application.models.RegisteredClientModel;
import com.qbook.app.application.services.appservices.ClientServices;
import com.qbook.app.domain.models.PlatformUsed;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/public/mobile/")
@AllArgsConstructor
public class MobileNonAuthResource {
    private final ClientServices clientServices;

    @PostMapping("register")
    public ResponseEntity<RegisteredClientModel> registerClient(@RequestBody NewClientModel newClientModel) {
        log.info( "MobileNonAuthResource.registerClient() called at " + System.currentTimeMillis());
        log.info( "MobileNonAuthResource.registerClient() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(clientServices.registerClient(newClientModel, PlatformUsed.MOBILE), HttpStatus.CREATED);
    }

    @PutMapping("login")
    public ResponseEntity<LoggedInClientModel> login(@RequestBody LoginClientModel loginClientModel) {
        log.info( "MobileNonAuthResource.login() called at " + System.currentTimeMillis());
        log.info( "MobileNonAuthResource.login() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(clientServices.login(loginClientModel), HttpStatus.OK);
    }
}
