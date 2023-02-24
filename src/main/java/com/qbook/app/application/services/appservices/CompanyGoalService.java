package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.*;

import java.util.List;

public interface CompanyGoalService {
    void newCompanyGoal(NewCompanyGoalModel newCompanyGoalModel);
    void updateCompanyGoal(UpdateCompanyGoalModel updateCompanyGoalModel);
    List<CompanyGoalModel> getCompanyGoals();
    CurrentCompanyRevenueGoalModel getCompanyCurrentGoal();
    ViewCompanyGoalModel viewCompany(String goalId);
}
