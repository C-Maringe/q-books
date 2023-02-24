package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.*;
import com.qbook.app.application.models.configurationModels.ViewOperationTimesModel;
import com.qbook.app.application.models.webPlatformModels.LoggedInUserModel;
import com.qbook.app.application.models.webPlatformModels.LoginUserModel;
import com.qbook.app.application.models.webPlatformModels.ViewDepositThreshold;
import com.qbook.app.application.services.appservices.ApplicationConfigurationsServices;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.ClientServices;
import com.qbook.app.application.services.appservices.UserServices;
import com.qbook.app.domain.models.PlatformUsed;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/public/web")
@AllArgsConstructor
@CrossOrigin
public class WebNonAuthResource {
    private final ClientServices clientServices;
    private final UserServices userServices;
	private final ApplicationConfigurationsServices applicationConfigurationServices;
	private final AuthTokenServices authTokenServices;
	
    @PostMapping("register")
    public ResponseEntity<RegisteredClientModel> registerClient(@RequestBody NewClientModel newClientModel) {
        log.info( "WebNonAuthResource.registerClient() called at " + System.currentTimeMillis());
        log.info( "WebNonAuthResource.registerClient() ended at " + System.currentTimeMillis());

        return new ResponseEntity<>(clientServices.registerClient(newClientModel, PlatformUsed.WEB), HttpStatus.CREATED);
    }

    @PutMapping("login")
    public ResponseEntity<LoggedInUserModel> login(@RequestBody LoginUserModel loginUserModel) {
        log.info( "WebNonAuthResource.login() called at " + System.currentTimeMillis());
        log.info( "WebNonAuthResource.login() ended at " + System.currentTimeMillis());

        return new ResponseEntity<>(userServices.loginPlatformUser(loginUserModel), HttpStatus.OK);
    }

    @GetMapping("operatingTime")
    public ResponseEntity<ViewOperationTimesModel> viewOperatingTimes(){
        log.info( "WebNonAuthResource.viewOperatingTimes() called at " + System.currentTimeMillis());
        log.info( "WebNonAuthResource.viewOperatingTimes() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(applicationConfigurationServices.viewOperatingTimes(), HttpStatus.OK);
    }

	@PutMapping("forgot-password")
	public ResponseEntity<UserForgotPasswordModel> userForgotPassword(@RequestBody ForgotPasswordModel forgotPasswordModel) {
        log.info( "WebNonAuthResource.userForgotPassword() called at " + System.currentTimeMillis());
        log.info( "WebNonAuthResource.userForgotPassword() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(userServices.userForgotPassword(forgotPasswordModel.getEmailAddress()), HttpStatus.OK);
	}

	@PutMapping("reset-password")
	public ResponseEntity<UserForgotPasswordModel> userResetPassword(@RequestBody ResetPasswordModel resetPasswordModel) {
        log.info( "WebNonAuthResource.userResetPassword() called at " + System.currentTimeMillis());
        String userId = this.authTokenServices.extractUserIdForReset(resetPasswordModel.getToken());
        log.info( "WebNonAuthResource.userResetPassword() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(userServices.userResetPassword(userId, resetPasswordModel), HttpStatus.OK);
	}

    @GetMapping("deposit-threshold")
    public ResponseEntity<ViewDepositThreshold> viewDepositThreshold(){
        log.info( "WebNonAuthResource.viewDepositThreshold() called at " + System.currentTimeMillis());
        log.info( "WebNonAuthResource.viewDepositThreshold() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(applicationConfigurationServices.viewDepositThreshold(), HttpStatus.OK);
    }
}
