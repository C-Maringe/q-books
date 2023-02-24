package com.qbook.app.application.tasks;

import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.CompanyRevenueGoalRepository;
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
public class CompanyGoalInvalidationTask {
    private final CompanyRevenueGoalRepository companyRevenueGoalRepository;
    private final SaleRepository saleRepository;

        @Scheduled(cron = "0 0 6 * * *") // Run 6 am everyday
//    @Scheduled(cron = "0 * * * * *") // Run every minute for testing
    public void run() {
        log.log(Level.INFO, "Starting process to invalidate company goals each month. Time -> " + System.currentTimeMillis());
        final DateTime currentDate = new DateTime().withTimeAtStartOfDay();
            companyRevenueGoalRepository
                .findAllByGoalActive(true)
                .forEach(companyRevenueGoal -> {
                    // check the measureDate and if it expired yesterday then invalidate
                    final DateTime measureDate = new DateTime(companyRevenueGoal.getGoalMeasureDate()); // set to 23:59:59 of current day

                    if(measureDate.isBefore(currentDate)) {
                        companyRevenueGoal.setGoalActive(false);
                        companyRevenueGoal.setGoalAchieved(companyRevenuePerGoalReached(
                                companyRevenueGoal,
                                new DateTime(companyRevenueGoal.getGoalStartDate()).getMillis(),
                                new DateTime(companyRevenueGoal.getGoalMeasureDate()).getMillis()
                        ));
                        companyRevenueGoal.setGoalUpdatedDate(DateTime.now().getMillis());

                        companyRevenueGoalRepository.save(companyRevenueGoal);
                    }
                });


        log.log(Level.INFO, "Completed process to invalidate company goals each month. Time -> " + System.currentTimeMillis());
    }

    private boolean companyRevenuePerGoalReached(final CompanyRevenueGoal revenueGoal, long from, long to) {
        final List<Sale> sales = saleRepository.findAllByDateTimeOfSaleBetween(
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
