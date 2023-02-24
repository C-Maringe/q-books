package com.qbook.app.application.rest.web;


import com.qbook.app.application.models.*;
import com.qbook.app.application.models.employeeModels.ViewEmployeeModel;
import com.qbook.app.application.models.productModels.ProductItemToCaptureModel;
import com.qbook.app.application.models.productModels.ViewProductModel;
import com.qbook.app.application.models.salesModels.SalesCashupCompleteModel;
import com.qbook.app.application.models.salesModels.SalesCashupCompletedModel;
import com.qbook.app.application.models.salesModels.SalesItemCaptured;
import com.qbook.app.application.services.appservices.GoalService;
import com.qbook.app.application.services.appservices.SuperUserServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth/goals")
@AllArgsConstructor
public class GoalsResource {

    private final GoalService goalService;
    private final SuperUserServices superUserServices;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void updateEmployeeGoal(@RequestBody NewEmployeeGoalModel newEmployeeGoalModel){
        goalService.newEmployeeGoal(newEmployeeGoalModel);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateEmployeeGoal(@RequestBody UpdateEmployeeGoalModel updateEmployeeGoalModel){
        goalService.updateEmployeeGoal(updateEmployeeGoalModel);
    }

    @GetMapping("{goal-id}/{employee-id}")
    public ResponseEntity<ViewGoalModel> getGoal(
            @PathVariable("goal-id") String goalId,
            @PathVariable("employee-id") String employeeId){
        return new ResponseEntity<>(goalService.viewGoal(employeeId, goalId), HttpStatus.OK);
    }

    @GetMapping("{employee-id}")
    public ResponseEntity<List<GoalModel>> getEmployeeGoals(@PathVariable("employee-id") String employeeId){
        return new ResponseEntity<>(goalService.getEmployeeGoals(employeeId), HttpStatus.OK);
    }

    @GetMapping("{employee-id}/current")
    public ResponseEntity<CurrentRevenueGoalModel> getEmployeeCurrentGoal(@PathVariable("employee-id") String employeeId){
        return new ResponseEntity<>(goalService.getEmployeeCurrentGoal(employeeId), HttpStatus.OK);
    }

    @GetMapping("employees")
    public ResponseEntity<List<ViewEmployeeModel>> getAllEmployees(){
        return new ResponseEntity<>(superUserServices.getAllEmployees(), HttpStatus.OK);
    }
}
