package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.*;
import com.qbook.app.application.models.clientModels.ClientDisabledModel;
import com.qbook.app.application.models.clientModels.ClientEnabledModel;
import com.qbook.app.application.services.appservices.AuthTokenServices;
import com.qbook.app.application.services.appservices.ClientServices;
import com.qbook.app.domain.models.Client;
import com.qbook.app.domain.models.PlatformUsed;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log
@RestController
@RequestMapping("/api/auth/clients")
@AllArgsConstructor
public class ClientManagementResource {
    private final ClientServices clientServices;
    @GetMapping
    public ResponseEntity<List<ViewFullClientModel>> getClientList() {
        log.info("ClientManagementResource.getClientList() called at " + System.currentTimeMillis());
        List<ViewFullClientModel> viewFullClientModels = clientServices.getAllClientsToEdit();
        log.info("ClientManagementResource.getClientList() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(viewFullClientModels, HttpStatus.OK);
    }

    // view specific client
    @GetMapping("{id}")
    public ResponseEntity<ViewClientModel> getSpecificClientProfile(@PathVariable("id") String id) {
        log.info("ClientManagementResource.getSpecificClientProfile() started at " + System.currentTimeMillis());
        ViewClientModel viewClientModel = clientServices.viewClientProfile(id);
        log.info("ClientManagementResource.getSpecificClientProfile() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(viewClientModel, HttpStatus.OK);
    }

    // edit client details
    @PutMapping("{id}")
    public ResponseEntity<ViewClientModel> updateProfile(@PathVariable("id") String id, @RequestBody UpdateClientProfileModel updateClientProfileModel) {
        log.info("ClientManagementResource.updateProfile() started at " + System.currentTimeMillis());
        ViewClientModel viewClientModel = clientServices.editClientProfile(id, updateClientProfileModel);
        log.info("ClientManagementResource.updateProfile() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(viewClientModel, HttpStatus.OK);
    }

    // add new notes to client profile
    @PutMapping("{id}/notes")
    public ResponseEntity<ViewClientModel> updateProfileWithNotes(@RequestHeader("Authorization") String authorization, @PathVariable("id") String id, @RequestBody ClientNewNoteModel clientNewNoteModel) {
        log.info("ClientManagementResource.updateProfileWithNotes() started at " + System.currentTimeMillis());
        ViewClientModel viewClientModel = clientServices.addNoteToClientProfile(authorization, id, clientNewNoteModel);
        log.info("ClientManagementResource.updateProfileWithNotes() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(viewClientModel, HttpStatus.OK);
    }

    @PutMapping("{id}/disable")
    public ResponseEntity<ClientDisabledModel> disableAccount(@PathVariable("id") String clientId) {
        log.info("ClientManagementResource.disableAccount() started at " + System.currentTimeMillis());
        ClientDisabledModel clientDisabledModel = clientServices.disableAccount(clientId);
        log.info("ClientManagementResource.disableAccount() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(clientDisabledModel, HttpStatus.OK);
    }

    @PutMapping("{id}/enable")
    public ResponseEntity<ClientEnabledModel> enableAccount(@PathVariable("id") String clientId) {
        log.info("ClientManagementResource.enableAccount() started at " + System.currentTimeMillis());
        ClientEnabledModel clientEnabledModel = clientServices.enableAccount(clientId);
        log.info("ClientManagementResource.enableAccount() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(clientEnabledModel, HttpStatus.OK);
    }
}
