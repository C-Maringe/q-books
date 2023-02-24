package com.qbook.app.application.tasks;

import com.qbook.app.domain.models.Employee;
import com.qbook.app.domain.models.Goal;
import com.qbook.app.domain.models.RevenueGoal;
import com.qbook.app.domain.models.Sale;
import com.qbook.app.domain.repository.EmployeeRepository;
import com.qbook.app.domain.repository.SaleRepository;
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
public class GoalInvalidationTask {
    private final EmployeeRepository employeeRepository;
    private final SaleRepository saleRepository;

        @Scheduled(cron = "0 0 5 * * *") // Run 5 am everyday
//    @Scheduled(cron = "0 * * * * *") // Run every minute for testing
    public void run() {
        log.log(Level.INFO, "Starting process to invalidate goals each month. Time -> " + System.currentTimeMillis());
        final DateTime currentDate = new DateTime().withTimeAtStartOfDay();
        employeeRepository
                .findAllByIsActive(true, Sort.by(new Sort.Order(Sort.Direction.ASC, "firstName")))
                .forEach(employee -> employee
                        .getRevenueGoals()
                        .stream()
                        .filter(Goal::isGoalActive)
                        .findFirst()
                        .ifPresent(revenueGoal -> {
                            // for every user that has a goal check the measureDate and if it expired yesterday then invalidate
                            final DateTime measureDate = new DateTime(revenueGoal.getGoalMeasureDate()); // set to 23:59:59 of current day

                            if(measureDate.isBefore(currentDate)) {
                                revenueGoal.setGoalActive(false);
                                revenueGoal.setGoalAchieved(employeeRevenuePerGoalReached(
                                    employee,
                                    revenueGoal,
                                    new DateTime(revenueGoal.getGoalStartDate()).getMillis(),
                                    new DateTime(revenueGoal.getGoalMeasureDate()).getMillis()
                                ));
                                revenueGoal.setGoalUpdatedDate(DateTime.now().getMillis());

                                employeeRepository.save(employee);
                            }
                        }));


        log.log(Level.INFO, "Completed process to invalidate goals each month. Time -> " + System.currentTimeMillis());
    }

    private boolean employeeRevenuePerGoalReached(final Employee employee, final RevenueGoal revenueGoal, long from, long to) {
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

        return (totalRevenueExclVat >= revenueGoal.getRevenueGoalBestCase());
    }
}
