package com.qbook.app.application.rest.mobile;

import com.qbook.app.application.models.ReceiveMarketingEmailModel;
import com.qbook.app.application.models.UpdateClientModel;
import com.qbook.app.application.models.ViewClientModel;
import com.qbook.app.application.models.ViewClientReceiveMarketingEmailModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.ClientServices;
import com.qbook.app.domain.models.Client;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/mobile/profile")
@AllArgsConstructor
public class MobileProfileResource {
	private final ClientServices clientServices;
	private final AuthTokenServices authTokenServices;

	@GetMapping
	public ResponseEntity<ViewClientModel> viewClientProfile(@RequestHeader("Authorization") String Authorization) {
		if(Authorization.startsWith("Bearer")){
			Authorization = Authorization.substring(7);
		}
        log.info("MobileProfileResource.viewClientProfile() called at " + System.currentTimeMillis());
        log.info("MobileProfileResource.viewClientProfile() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(clientServices.viewClientProfile(authTokenServices.extractUserId(Authorization)), HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<ViewClientModel> updateProfile(@RequestHeader("Authorization") String Authorization,@RequestBody UpdateClientModel updateClientModel) {
		if(Authorization.startsWith("Bearer")){
			Authorization = Authorization.substring(7);
		}
        log.info("MobileProfileResource.updateProfile() called at " + System.currentTimeMillis());
        log.info("MobileProfileResource.updateProfile() ended at " + System.currentTimeMillis());
        return new ResponseEntity<>(clientServices.editProfile(authTokenServices.extractUserId(Authorization), updateClientModel), HttpStatus.OK);
	}
	@PutMapping("/marketing-email")
	public ResponseEntity<ViewClientReceiveMarketingEmailModel> receiveMarketingEmail(
			@RequestHeader("Authorization") String Authorization,
			final @RequestBody ReceiveMarketingEmailModel receiveMarketingEmail
	) {
		if(Authorization.startsWith("Bearer")){
			Authorization = Authorization.substring(7);
		}
		return new ResponseEntity<>(clientServices.activateMarketingEmailsRetrieval(authTokenServices.extractUserId(Authorization), receiveMarketingEmail), HttpStatus.CREATED);
	}

	@GetMapping("/marketing-email")
	public ResponseEntity<ReceiveMarketingEmailModel> getUser(@RequestHeader("Authorization") String Authorization)
	{
		if(Authorization.startsWith("Bearer")){
			Authorization = Authorization.substring(7);
		}
		ReceiveMarketingEmailModel receiveMarketingEmailModel = clientServices.findReceiveMarketingEmailsPermissionOfUser(authTokenServices.extractUserId(Authorization));
		return new ResponseEntity<>(receiveMarketingEmailModel, HttpStatus.OK);
	}
}
