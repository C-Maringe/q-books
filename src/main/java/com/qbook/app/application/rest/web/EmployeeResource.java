package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.employeeModels.*;
import com.qbook.app.application.services.appservices.SuperUserServices;
import com.qbook.app.domain.models.EmployeeWorkingDay;
import com.qbook.app.domain.models.UserPermission;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/employees")
@AllArgsConstructor
public class EmployeeResource {
    private final SuperUserServices superUserServices;

    @GetMapping
    public ResponseEntity<List<ViewEmployeeModel>> getAllEmployees(){
        log.info("EmployeeResource.getAllEmployees() called at " + System.currentTimeMillis());
        List<ViewEmployeeModel> allEmployees =  superUserServices.getAllEmployees();
        log.info("EmployeeResource.getAllEmployees() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(allEmployees, HttpStatus.OK);
    }

    @PutMapping("disable/{employeeId}")
    public ResponseEntity<EmployeeUpdatedModel> disableEmployee(@PathVariable("employeeId") String employeeId){
        log.info("EmployeeResource.disableEmployee() called at " + System.currentTimeMillis());
        EmployeeUpdatedModel employeeUpdatedModel = superUserServices.disableAccount(employeeId);
        log.info("EmployeeResource.disableEmployee() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(employeeUpdatedModel, HttpStatus.OK);
    }

    @PutMapping("enable/{employeeId}")
    public ResponseEntity<EmployeeUpdatedModel> enableEmployee(@PathVariable("employeeId") String employeeId){
        log.info("EmployeeResource.enableEmployee() called at " + System.currentTimeMillis());
        EmployeeUpdatedModel employeeUpdatedModel = superUserServices.enableAccount(employeeId);
        log.info("EmployeeResource.enableEmployee() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(employeeUpdatedModel, HttpStatus.OK);
    }

    @GetMapping("{employeeId}")
    public ResponseEntity<ViewFullEmployeeModel> getEmployee(@PathVariable("employeeId") String employeeId){
        log.info("EmployeeResource.getEmployee() called at " + System.currentTimeMillis());
        ViewFullEmployeeModel viewFullEmployeeModel = superUserServices.getEmployee(employeeId);
        log.info("EmployeeResource.getEmployee() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(viewFullEmployeeModel, HttpStatus.OK);
    }

	@PostMapping
	public ResponseEntity<EmployeeRegisteredModel> registerNewEmployee(@RequestBody NewEmployeeModel newEmployeeModel){
        log.info("EmployeeResource.registerNewEmployee() called at " + System.currentTimeMillis());
        EmployeeRegisteredModel employeeRegisteredModel = superUserServices.registerNewEmployee(newEmployeeModel);
        log.info("EmployeeResource.registerNewEmployee() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(employeeRegisteredModel, HttpStatus.OK);
	}

    @PutMapping
    public ResponseEntity<EmployeeUpdatedModel> updateEmployee(@RequestBody UpdateEmployeeModel updateEmployeeModel){
        log.info("EmployeeResource.updateEmployee() called at " + System.currentTimeMillis());
        EmployeeUpdatedModel employeeUpdatedModel = superUserServices.updateEmployee(updateEmployeeModel);
        log.info("EmployeeResource.updateEmployee() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(employeeUpdatedModel, HttpStatus.OK);
    }


	@GetMapping("permissions")
	public ResponseEntity<List<UserPermission>> getAllPermissions(){
        log.info("EmployeeResource.getAllPermissions() called at " + System.currentTimeMillis());
        List<UserPermission> userPermissions = superUserServices.viewAllSystemPermissions();
        log.info("EmployeeResource.getAllPermissions() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(userPermissions, HttpStatus.OK);
	}

	@GetMapping("employee-types")
	public ResponseEntity<List<ViewEmployeeType>> viewEmployeeTypes(){
        log.info("EmployeeResource.viewEmployeeTypes() called at " + System.currentTimeMillis());
        List<ViewEmployeeType> viewEmployeeTypes = superUserServices.getAllEmployeeTypes();
        log.info("EmployeeResource.viewEmployeeTypes() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(viewEmployeeTypes, HttpStatus.OK);
	}

	@PutMapping("working-day")
	public ResponseEntity<CreatedEmployeeWorkingDayModel> addEmployeeWorkingDay(@RequestBody NewEmployeeWorkingDayModel newEmployeeWorkingDayModel){
        log.info("EmployeeResource.addEmployeeWorkingDay() called at " + System.currentTimeMillis());
        CreatedEmployeeWorkingDayModel createdEmployeeWorkingDayModel =
                superUserServices.createEmployeeWorkingDayModel(newEmployeeWorkingDayModel);
        log.info("EmployeeResource.addEmployeeWorkingDay() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(createdEmployeeWorkingDayModel, HttpStatus.OK);
	}

//    @PostMapping("working-day")
//    public ResponseEntity<EmployeeWorkingDay> addEmployeeWorkingDay(@RequestBody NewEmployeeWorkingDayModel newEmployeeWorkingDayModel){
//        log.info("EmployeeResource.addEmployeeWorkingDay() called at " + System.currentTimeMillis());
//        EmployeeWorkingDay addNewEmployeeWorkingDay =
//                superUserServices.addNewEmployeeWorkingDay(newEmployeeWorkingDayModel);
//        log.info("EmployeeResource.addEmployeeWorkingDay() completed at " + System.currentTimeMillis());
//        return new ResponseEntity<>(addNewEmployeeWorkingDay, HttpStatus.OK);
//    }


	@GetMapping("working-day/{employeeId}/{month}/{year}")
	public ResponseEntity<List<EmployeeWorkingDayModel>> getAllEmployeeWorkingDays(
			@PathVariable("employeeId") String employeeId,
			@PathVariable("month") int month,
			@PathVariable("year") int year
	){
        log.info("EmployeeResource.getAllEmployeeWorkingDays() called at " + System.currentTimeMillis());
        List<EmployeeWorkingDayModel> employeeWorkingDayModels = superUserServices.viewAllEmployeeWorkingDays(employeeId, month, year);
        log.info("EmployeeResource.getAllEmployeeWorkingDays() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(employeeWorkingDayModels, HttpStatus.OK);
    }

	@DeleteMapping("working-day/{employeeId}/{employeeWorkingDayId}")
	public ResponseEntity<RemovedEmployeeWorkingDayModel> getAllEmployeeWorkingDays(
			@PathVariable("employeeId") String employeeId,
			@PathVariable("employeeWorkingDayId") String employeeWorkingDayId
	){
        log.info("EmployeeResource.getAllEmployeeWorkingDays() called at " + System.currentTimeMillis());
        RemovedEmployeeWorkingDayModel removedEmployeeWorkingDayModel = superUserServices.removeEmployeeWorkingDay(employeeId, employeeWorkingDayId);
        log.info("EmployeeResource.getAllEmployeeWorkingDays() completed at " + System.currentTimeMillis());
        return new ResponseEntity<>(removedEmployeeWorkingDayModel, HttpStatus.OK);
    }
}

