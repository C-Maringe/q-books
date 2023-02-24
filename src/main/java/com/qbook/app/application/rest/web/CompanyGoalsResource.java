package com.qbook.app.application.rest.web;


import com.qbook.app.application.models.*;
import com.qbook.app.application.services.appservices.CompanyGoalService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/goals")
@AllArgsConstructor
public class CompanyGoalsResource {

    private final CompanyGoalService goalService;

    @PostMapping("company")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateCompanyGoal(@RequestBody NewCompanyGoalModel newCompanyGoalModel){
        goalService.newCompanyGoal(newCompanyGoalModel);
    }

    @PutMapping("company")
    @ResponseStatus(HttpStatus.OK)
    public void updateCompanyGoal(@RequestBody UpdateCompanyGoalModel updateCompanyGoalModel){
        goalService.updateCompanyGoal(updateCompanyGoalModel);
    }

    @GetMapping("company/{goal-id}")
    public ResponseEntity<ViewCompanyGoalModel> getGoal(
            @PathVariable("goal-id") String goalId){
        return new ResponseEntity<>(goalService.viewCompany(goalId), HttpStatus.OK);
    }

    @GetMapping("company")
    public ResponseEntity<List<CompanyGoalModel>> getCompanyGoals(){
        return new ResponseEntity<>(goalService.getCompanyGoals(), HttpStatus.OK);
    }

    @GetMapping("company/current")
    public ResponseEntity<CurrentCompanyRevenueGoalModel> getCompanyCurrentGoal(){
        return new ResponseEntity<>(goalService.getCompanyCurrentGoal(), HttpStatus.OK);
    }

}
