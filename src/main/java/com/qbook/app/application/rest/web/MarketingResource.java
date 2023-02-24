package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.marketingModels.*;
import com.qbook.app.application.services.appservices.MarketingServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/marketing")
@AllArgsConstructor
public class MarketingResource {
    private final MarketingServices marketingServices;

    @GetMapping("clientBase")
    public ResponseEntity<BatchEmailClientsModel> getClientBase(){
        return new ResponseEntity<>(marketingServices.getAllClientsToBatchEmailTo(), HttpStatus.OK);
    }

    @PostMapping("setupBatchList")
    public ResponseEntity<BatchEmailClientSetupModel> setupBatchEmailingList(@RequestBody BatchEmailClientModel batchEmailClientModel){
	    return new ResponseEntity<>(marketingServices.setupBatchEmailList(batchEmailClientModel), HttpStatus.OK);
    }

    @PutMapping("setupBatchEmailContent")
    public ResponseEntity<BatchEmailContentSetupModel> setupBatchEmailContent(@RequestBody BatchEmailContentModel batchEmailContentModel){
	    return new ResponseEntity<>(marketingServices.setupBatchEmailContent(batchEmailContentModel), HttpStatus.OK);
    }

    @PutMapping("sendBatchEmail/{id}")
    public ResponseEntity<BatchEmailSentModel> sendBatchEmail(@PathVariable("id") String batchEmailId){
        marketingServices.sendBatchEmail(batchEmailId);
        return new ResponseEntity<>(new BatchEmailSentModel(
                "Your promotional email will be sent out as soon as we are done processing it.",
                true
        ), HttpStatus.OK);
    }

    @GetMapping("batchEmails")
    public ResponseEntity<List<BatchEmailModel>> getBatchEmails(){
	    return new ResponseEntity<>(marketingServices.viewBatchEmails(), HttpStatus.OK);
    }

    @GetMapping("batchEmail/{id}")
    public ResponseEntity<BatchEmailClientsModel> getSpecificBatchEmailSentOut(@PathVariable("id") String batchEmailId){
	    return new ResponseEntity<>(marketingServices.viewSpecificBatchEmail(batchEmailId), HttpStatus.OK);
    }
}
