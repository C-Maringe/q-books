package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.employeeModels.ViewEmployeeType;
import com.qbook.app.application.models.treatmentModels.*;
import com.qbook.app.application.services.appservices.SuperUserServices;
import com.qbook.app.application.services.appservices.TreatmentServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/treatments")
@AllArgsConstructor
public class TreatmentsResource {
    private final TreatmentServices treatmentServices;
    private final SuperUserServices superUserServices;

    @GetMapping
    public ResponseEntity<List<ViewTreatmentModel>> getAllTreatments(){
        return new ResponseEntity<>(treatmentServices.viewAllTreatments(), HttpStatus.OK);
    }

    @GetMapping("employee-types")
    public ResponseEntity<List<ViewEmployeeType>> viewEmployeeTypes(){
        return new ResponseEntity<>(superUserServices.getAllEmployeeTypes(), HttpStatus.OK);
    }

    @GetMapping("/filter/{employeeType}")
    public ResponseEntity<List<ViewTreatmentModel>> getAllTreatmentsForEmployeeType(@PathVariable("employeeType") String employeeType){
        return new ResponseEntity<>(treatmentServices.viewAllTreatmentsByEmployeeType(employeeType), HttpStatus.OK);
    }

    @PutMapping("disable/{treatmentId}")
    public ResponseEntity<TreatmentDisabledModel> disableTreatment(@PathVariable("treatmentId") String treatmentId){
        return new ResponseEntity<>(treatmentServices.disableTreatment(treatmentId), HttpStatus.OK);
    }

    @PutMapping("enable/{treatmentId}")
    public ResponseEntity<TreatmentEnabledModel> enableTreatment(@PathVariable("treatmentId") String treatmentId){
        return new ResponseEntity<>(treatmentServices.enableTreatment(treatmentId), HttpStatus.OK);
    }

    @GetMapping("{treatmentId}")
    public ResponseEntity<ViewTreatmentModel> viewTreatment(@PathVariable("treatmentId") String treatmentId){
        return new ResponseEntity<>(treatmentServices.viewTreatment(treatmentId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TreatmentCreatedModel> registerTreatmentItem(@RequestBody NewTreatmentModel newServiceItem){
//        JsonReader reader = Json.createReader(new StringReader(newServiceItem));
//        JsonObject inputParams = reader.readObject();
//        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yy-MM-dd");
//
//	    if(inputParams.getBoolean("special")) {
//		    if(inputParams.getString("specialEndDate") == null || inputParams.getString("specialEndDate").equals("")) {
//			    return Response.ok(Json.createObjectBuilder()
//					    .add("success", false)
//					    .add("message", "The treatment is being registered as a special please ensure you add an end date for the special.")
//					    .build()
//					    .toString())
//					    .build();
//		    }
//	    }
//
//        JsonObject output = treatmentServices.createNewTreatment(
//                Json.createObjectBuilder()
//                        .add("treatmentName", inputParams.getString("treatmentName"))
//                        .add("treatmentDescription", inputParams.getString("treatmentDescription"))
//                        .add("isDoneByJunior",false)
//                        .add("juniorPrice", 0.0)
//                        .add("isDoneBySenior", true)
//                        .add("special", inputParams.getBoolean("special"))
//                        .add("specialPrice", inputParams.getString("specialPrice"))
//                        .add("specialEndDate", (inputParams.getBoolean("special"))?dateTimeFormatter.parseDateTime(inputParams.getString("specialEndDate")).getMillis():0L)
//                        .add("seniorPrice", inputParams.getString("seniorPrice"))
//                        .add("duration", Integer.valueOf(inputParams.getString("duration")))
//                        .add("employeeType", inputParams.getString("employeeType"))
//                        .build()
//        );
//
//        return Response.ok(output.toString()).build();
        return new ResponseEntity<>(treatmentServices.createNewTreatment(newServiceItem), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<TreatmentUpdatedModel> updateTreatmentItem(@RequestBody UpdateTreatmentModel treatmentToUpdate){

//        JsonReader reader = Json.createReader(new StringReader(treatmentToUpdate));
//        JsonObject inputParams = reader.readObject();
//	    DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yy-MM-dd");
//
//        if(inputParams.getBoolean("special")) {
//            if(inputParams.getString("specialEndDate") == null || inputParams.getString("specialEndDate").equals("")) {
//	            return Response.ok(Json.createObjectBuilder()
//			            .add("success", false)
//			            .add("message", "The treatment is being set as a special please ensure you add an end date for the special.")
//			            .build()
//			            .toString())
//			            .build();
//            }
//        }
//
//        JsonObject output = treatmentServices.updateTreatment(
//                Json.createObjectBuilder()
//                        .add("treatmentId", inputParams.getString("treatmentId"))
//                        .add("treatmentName", inputParams.getString("treatmentName"))
//                        .add("treatmentDescription", inputParams.getString("treatmentDescription"))
//                        .add("isDoneByJunior",false)
//                        .add("juniorPrice", 0.0)
//                        .add("isDoneBySenior", true)
//                        .add("special", inputParams.getBoolean("special"))
//                        .add("specialPrice", inputParams.getString("specialPrice"))
//                        .add("specialEndDate", (inputParams.getBoolean("special"))?dateTimeFormatter.parseDateTime(inputParams.getString("specialEndDate")).getMillis():0L)
//                        .add("seniorPrice", inputParams.getString("seniorPrice"))
//                        .add("duration", Integer.valueOf(inputParams.getString("duration")))
//                        .add("employeeType", inputParams.getString("employeeType"))
//                        .build()
//        );
//
//        return Response.ok(output.toString()).build();
        return new ResponseEntity<>(treatmentServices.updateTreatment(treatmentToUpdate), HttpStatus.OK);
    }
}
