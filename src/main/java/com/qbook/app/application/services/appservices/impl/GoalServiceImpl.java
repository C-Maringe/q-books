package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.DuplicateGoalException;
import com.qbook.app.application.configuration.exception.InvalidDateException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.*;
import com.qbook.app.application.services.appservices.GoalService;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.SaleRepository;
import com.qbook.app.utilities.factory.GoalFactory;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final ApplicationProperties applicationProperties;
    private final EmployeeRepository employeeRepository;
    private final GoalFactory goalFactory;
    private final SaleRepository saleRepository;

    @Override
    public void newEmployeeGoal(final NewEmployeeGoalModel newEmployeeGoalModel) {

        if(applicationProperties.getShortDateTimeFormatter().parseDateTime(newEmployeeGoalModel.getGoalMeasureDate()).isBeforeNow()) {
            throw new InvalidDateException("The date provided is to old, pleasure provided a measure by  date later than today.");
        }
        final Employee employee = employeeRepository
                .findById(new ObjectId(newEmployeeGoalModel.getEmployeeId()))
                .orElseThrow(() -> new ResourceNotFoundException("The employee was not found."));

        // check if there is any goals already for the month
        final DateTime currentDate = DateTime.now();

        employee
                .getRevenueGoals()
                .forEach(revenueGoal -> {
                    final DateTime revenueGoalMeasureDate = new DateTime(revenueGoal.getGoalMeasureDate());
                    if(revenueGoalMeasureDate.getMonthOfYear() == currentDate.getMonthOfYear()
                        && revenueGoalMeasureDate.getYear() == currentDate.getYear()) {
                        throw new DuplicateGoalException("A goal already exists for " + applicationProperties.getShortDateTimeFormatter().print(revenueGoalMeasureDate));
                    }
                });

        final RevenueGoal goal = goalFactory.buildGoal(newEmployeeGoalModel);

        // Auto-Activate goals if no current active
        final Optional<RevenueGoal> revenueGoalOptional = employee
                .getRevenueGoals()
                .stream()
                .filter(Goal::isGoalActive)
                .findFirst();

        if (!revenueGoalOptional.isPresent()) {
            goal.setGoalActive(true);
        }

        goal.setGoalIndex(employee.getRevenueGoals().size() + 1);

        employee.getRevenueGoals().add(goal);

        employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public void updateEmployeeGoal(final UpdateEmployeeGoalModel updateEmployeeGoalModel) {
        final Employee employee = employeeRepository
                .findById(new ObjectId(updateEmployeeGoalModel.getEmployeeId()))
                .orElseThrow(() -> new ResourceNotFoundException("The employee was not found."));

        employee
                .getRevenueGoals()
                .stream()
                .filter(revenueGoal -> revenueGoal.getGoalId().equals(new ObjectId(updateEmployeeGoalModel.getGoalId())))
                .findFirst()
                .ifPresent(revenueGoal -> {
                    revenueGoal.setGoalName(updateEmployeeGoalModel.getGoalName());
                    revenueGoal.setRevenueGoalWorstCase(updateEmployeeGoalModel.getRevenueGoalWorstCase());
                    revenueGoal.setRevenueGoalBestCase(updateEmployeeGoalModel.getRevenueGoalBestCase());
                    revenueGoal.setGoalMeasureDate(applicationProperties.getShortDateTimeFormatter().parseDateTime(updateEmployeeGoalModel.getGoalMeasureDate()).withTime(23, 59, 59, 0).getMillis());
                    revenueGoal.setGoalUpdatedDate(DateTime.now().getMillis());
                });

        employeeRepository.save(employee);
    }

    @Override
    public List<GoalModel> getEmployeeGoals(String employeeId) {

        // only show past goals
        final Employee employee = employeeRepository
                .findById(new ObjectId(employeeId))
                .orElseThrow(() -> new ResourceNotFoundException("The employee was not found."));

        return employee
                .getRevenueGoals()
                .stream()
                .filter(revenueGoal -> {
                    // do not get the current active goal
                    return !revenueGoal.isGoalActive();
                })
                .filter(revenueGoal -> new DateTime(revenueGoal.getGoalMeasureDate()).isBeforeNow())
                .map(revenueGoal -> {
                    final GoalModel goalModel = goalFactory.buildGoalModel(revenueGoal);
                    viewEmployeeRevenuePerGoalPeriod(
                            employee,
                            revenueGoal,
                            goalModel,
                            new DateTime(revenueGoal.getGoalStartDate()).getMillis(),
                            new DateTime(revenueGoal.getGoalMeasureDate()).getMillis()
                    );
                    return goalModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CurrentRevenueGoalModel getEmployeeCurrentGoal(String employeeId) {
        final Employee employee = employeeRepository
                .findById(new ObjectId(employeeId))
                .orElseThrow(() -> new ResourceNotFoundException("The employee was not found."));

        RevenueGoal revenueGoal = employee
                .getRevenueGoals()
                .stream()
                .filter(Goal::isGoalActive)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("There are no active goals for " + employee.getFirstName() + " in the current month."));

        final CurrentRevenueGoalModel currentRevenueGoalModel = goalFactory.buildCurrentRevenueGoalModel(revenueGoal, employee);
        viewEmployeeRevenuePerGoalPeriodProgress(
                employee,
                revenueGoal,
                currentRevenueGoalModel,
                new DateTime(revenueGoal.getGoalStartDate()).getMillis(),
                new DateTime(revenueGoal.getGoalMeasureDate()).getMillis()
        );

        return currentRevenueGoalModel;
    }

    @Override
    public ViewGoalModel viewGoal(String employeeId, String goalId) {
        final Employee employee = employeeRepository
                .findById(new ObjectId(employeeId))
                .orElseThrow(() -> new ResourceNotFoundException("The employee was not found."));

        RevenueGoal revenueGoal = employee
                .getRevenueGoals()
                .stream()
                .filter(revenueGoal1 -> revenueGoal1.getGoalId().toString().equals(goalId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("There are no active goals for " + employee.getFirstName() + " in the current month."));

        return goalFactory.buildViewGoalModel(revenueGoal);
    }

    private void viewEmployeeRevenuePerGoalPeriod(final Employee employee, final RevenueGoal revenueGoal, final GoalModel goalModel, long from, long to) {
        List<Sale> sales = saleRepository.findAllByAssistedByAndDateTimeOfSaleBetween(
                employee,
                from,
                to
        );

        double totalRevenue = sales
                .stream()
                .filter(sale -> sale.getBooking() != null)
                .mapToDouble(sale -> BigDecimal.valueOf(sale.getTotalSalePrice()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .reduce(0.0, Double::sum);

        double totalRevenueExclVat = totalRevenue - (totalRevenue * 0.15);
        goalModel.setRevenueActual(applicationProperties.getDecimalFormat().format(totalRevenueExclVat));
        goalModel.setRevenueDifference(applicationProperties.getDecimalFormat().format(totalRevenueExclVat - revenueGoal.getRevenueGoalBestCase()));
    }

    private void viewEmployeeRevenuePerGoalPeriodProgress(final Employee employee, final RevenueGoal revenueGoal, final CurrentRevenueGoalModel currentRevenueGoalModel, long from, long to) {
        List<Sale> sales = saleRepository.findAllByAssistedByAndDateTimeOfSaleBetween(
                employee,
                from,
                to
        );

        double totalRevenue = sales
                .stream()
                .filter(sale -> sale.getBooking() != null)
                .mapToDouble(sale -> BigDecimal.valueOf(sale.getTotalSalePrice()).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .reduce(0.0, Double::sum);

        double totalRevenueExclVat = totalRevenue - (totalRevenue * 0.15);
        double progressValue = (totalRevenueExclVat/revenueGoal.getRevenueGoalBestCase()) * 100;
        currentRevenueGoalModel.setRevenueGoalProgress((int)progressValue);
    }
}
