package com.qbook.app.application.tasks;

import com.qbook.app.application.models.EmployeeGoalProgress;
import com.qbook.app.application.services.appservices.EmailService;
import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.Goal;
import com.qbook.app.domain.models.RevenueGoal;
import com.qbook.app.domain.models.Sale;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.SaleRepository;
import com.qbook.app.utilities.factory.GoalFactory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;

@Log
@Component
@AllArgsConstructor
public class GoalProgressTask {
    private final EmployeeRepository employeeRepository;
    private final GoalFactory goalFactory;
    private final SaleRepository saleRepository;
    private final EmailService emailService;

        @Scheduled(cron = "0 0 6 * * *") // Run 6 am everyday
//    @Scheduled(cron = "0 * * * * *") // Run every minute for testing
    public void run() {
        log.log(Level.INFO, "Starting process to send out goal tracking emails. Time -> " + System.currentTimeMillis());
        employeeRepository
                .findAllByIsActive(true, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")))
                .forEach(employee -> employee
                        .getRevenueGoals()
                        .stream()
                        .filter(Goal::isGoalActive)
                        .findFirst()
                        .ifPresent(revenueGoal -> {
                            final EmployeeGoalProgress employeeGoalProgress = goalFactory.buildEmployeeGoalProgress(revenueGoal, employee);
                            viewEmployeeRevenuePerGoalPeriodProgress(
                                    employee,
                                    revenueGoal,
                                    employeeGoalProgress,
                                    new DateTime(revenueGoal.getGoalStartDate()).getMillis(),
                                    new DateTime(revenueGoal.getGoalMeasureDate()).getMillis()
                            );

                            // send the email
                            emailService.sendEmployeeGoalProgressEmail(employeeGoalProgress);
                        }));


        log.log(Level.INFO, "Completed process to send out goal tracking emails. Time -> " + System.currentTimeMillis());
    }

    private void viewEmployeeRevenuePerGoalPeriodProgress(final Employee employee, final RevenueGoal revenueGoal, final EmployeeGoalProgress employeeGoalProgress, long from, long to) {
        final List<Sale> sales = saleRepository.findAllByAssistedByAndDateTimeOfSaleBetween(
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
        employeeGoalProgress.setGoalProgress(((int)progressValue) + "%");
    }
}
