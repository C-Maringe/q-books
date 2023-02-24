package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface GoalService {
    void newEmployeeGoal(NewEmployeeGoalModel newEmployeeGoalModel);
    void updateEmployeeGoal(UpdateEmployeeGoalModel updateEmployeeGoalModel);
    List<GoalModel> getEmployeeGoals(String employeeId);
    CurrentRevenueGoalModel getEmployeeCurrentGoal(String employeeId);
    ViewGoalModel viewGoal(String employeeId, String goalId);
}
