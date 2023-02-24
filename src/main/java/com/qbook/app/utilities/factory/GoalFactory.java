package com.qbook.app.utilities.factory;

import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.*;
import com.qbook.app.domain.models.CompanyRevenueGoal;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.Goal;
import com.qbook.app.domain.models.RevenueGoal;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GoalFactory {

    private ApplicationProperties applicationProperties;

    public RevenueGoal buildGoal(final NewEmployeeGoalModel newEmployeeGoalModel) {
        final RevenueGoal goal = new RevenueGoal();
        goal.setGoalId(new ObjectId());
        goal.setRevenueGoalBestCase(newEmployeeGoalModel.getRevenueGoalBestCase());
        goal.setRevenueGoalWorstCase(newEmployeeGoalModel.getRevenueGoalWorstCase());
        goal.setGoalName(newEmployeeGoalModel.getGoalName());
        goal.setGoalAchieved(false);
        goal.setGoalActive(false);
        goal.setGoalStartDate(applicationProperties.getShortDateTimeFormatter().parseDateTime(newEmployeeGoalModel.getGoalStartDate()).withTimeAtStartOfDay().getMillis());
        goal.setGoalMeasureDate(applicationProperties.getShortDateTimeFormatter().parseDateTime(newEmployeeGoalModel.getGoalMeasureDate()).withTime(23, 59, 59, 0).getMillis());
        goal.setGoalCreatedDate(DateTime.now().getMillis());
        return goal;
    }

    public GoalModel buildGoalModel(final RevenueGoal revenueGoal) {
        final GoalModel goal = new GoalModel();
        goal.setGoalName(revenueGoal.getGoalName());
        goal.setRevenueGoal(applicationProperties.getDecimalFormat().format(revenueGoal.getRevenueGoalBestCase()));
        goal.setMeasureDate(applicationProperties.getDisplayShortDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        return goal;
    }

    public CurrentRevenueGoalModel buildCurrentRevenueGoalModel(final RevenueGoal revenueGoal, final Employee employee) {
        final CurrentRevenueGoalModel currentRevenueGoalModel = new CurrentRevenueGoalModel();
        currentRevenueGoalModel.setGoalName(revenueGoal.getGoalName());
        currentRevenueGoalModel.setEmployeeName(employee.getFirstName() + " " + employee.getLastName());
        currentRevenueGoalModel.setGoalId(revenueGoal.getGoalId().toString());
        currentRevenueGoalModel.setMeasureDate(applicationProperties.getDisplayShortDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        currentRevenueGoalModel.setRevenueGoal(applicationProperties.getDecimalFormat().format(revenueGoal.getRevenueGoalBestCase()));
        return currentRevenueGoalModel;
    }

    public ViewGoalModel buildViewGoalModel(final RevenueGoal revenueGoal) {
        final ViewGoalModel viewGoalModel = new ViewGoalModel();
        viewGoalModel.setGoalName(revenueGoal.getGoalName());
        viewGoalModel.setGoalMeasureDate(applicationProperties.getShortDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        viewGoalModel.setGoalStartDate(applicationProperties.getShortDateTimeFormatter().print(revenueGoal.getGoalStartDate()));
        viewGoalModel.setRevenueGoalBestCase(revenueGoal.getRevenueGoalBestCase());
        viewGoalModel.setRevenueGoalWorstCase(revenueGoal.getRevenueGoalWorstCase());
        return viewGoalModel;
    }

    public EmployeeGoalProgress buildEmployeeGoalProgress(final RevenueGoal revenueGoal, final Employee employee) {
        final EmployeeGoalProgress employeeGoalProgress = new EmployeeGoalProgress();
        employeeGoalProgress.setGoal("R " + applicationProperties.getDecimalFormat().format(revenueGoal.getRevenueGoalBestCase()));
        employeeGoalProgress.setFullName(employee.getFirstName() + " " + employee.getLastName());
        employeeGoalProgress.setEmployeeEmail(employee.getContactDetails().getEmailAddress());
        employeeGoalProgress.setMeasureDate(applicationProperties.getDisplayNormalDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        int daysBetween = Days.daysBetween(DateTime.now(), new DateTime(revenueGoal.getGoalMeasureDate())).getDays();
        employeeGoalProgress.setDaysToMeasureDate(
                (daysBetween > 1) ? daysBetween + " days " : daysBetween + " day "
        );
        return employeeGoalProgress;
    }

    public CompanyRevenueGoal buildCompanyGoal(final NewCompanyGoalModel newCompanyGoalModel) {
        final CompanyRevenueGoal goal = new CompanyRevenueGoal();
        goal.setGoalId(new ObjectId());
        goal.setRevenueGoalBestCase(newCompanyGoalModel.getRevenueGoalBestCase());
        goal.setRevenueGoalWorstCase(newCompanyGoalModel.getRevenueGoalWorstCase());
        goal.setGoalName(newCompanyGoalModel.getGoalName());
        goal.setGoalAchieved(false);
        goal.setGoalActive(false);
        goal.setGoalStartDate(applicationProperties.getShortDateTimeFormatter().parseDateTime(newCompanyGoalModel.getGoalStartDate()).withTimeAtStartOfDay().getMillis());
        goal.setGoalMeasureDate(applicationProperties.getShortDateTimeFormatter().parseDateTime(newCompanyGoalModel.getGoalMeasureDate()).withTime(23, 59, 59, 0).getMillis());
        goal.setGoalCreatedDate(DateTime.now().getMillis());
        return goal;
    }

    public CompanyGoalModel buildCompanyGoalModel(final CompanyRevenueGoal revenueGoal) {
        final CompanyGoalModel goal = new CompanyGoalModel();
        goal.setGoalName(revenueGoal.getGoalName());
        goal.setRevenueGoal(applicationProperties.getDecimalFormat().format(revenueGoal.getRevenueGoalBestCase()));
        goal.setMeasureDate(applicationProperties.getDisplayShortDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        return goal;
    }

    public CurrentCompanyRevenueGoalModel buildCurrentCompanyRevenueGoalModel(final CompanyRevenueGoal revenueGoal) {
        final CurrentCompanyRevenueGoalModel currentRevenueGoalModel = new CurrentCompanyRevenueGoalModel();
        currentRevenueGoalModel.setGoalName(revenueGoal.getGoalName());
        currentRevenueGoalModel.setGoalId(revenueGoal.getId().toString());
        currentRevenueGoalModel.setMeasureDate(applicationProperties.getDisplayShortDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        currentRevenueGoalModel.setRevenueGoal(applicationProperties.getDecimalFormat().format(revenueGoal.getRevenueGoalBestCase()));
        return currentRevenueGoalModel;
    }

    public ViewCompanyGoalModel buildViewCompanyGoalModel(final CompanyRevenueGoal revenueGoal) {
        final ViewCompanyGoalModel viewGoalModel = new ViewCompanyGoalModel();
        viewGoalModel.setGoalName(revenueGoal.getGoalName());
        viewGoalModel.setGoalMeasureDate(applicationProperties.getShortDateTimeFormatter().print(revenueGoal.getGoalMeasureDate()));
        viewGoalModel.setGoalStartDate(applicationProperties.getShortDateTimeFormatter().print(revenueGoal.getGoalStartDate()));
        viewGoalModel.setRevenueGoalBestCase(revenueGoal.getRevenueGoalBestCase());
        viewGoalModel.setRevenueGoalWorstCase(revenueGoal.getRevenueGoalWorstCase());
        return viewGoalModel;
    }
}
