package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.DuplicateGoalException;
import com.qbook.app.application.configuration.exception.InvalidDateException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.*;
import com.qbook.app.application.services.appservices.CompanyGoalService;
import com.qbook.app.domain.models.*;
import com.qbook.app.domain.repository.CompanyRevenueGoalRepository;
import com.qbook.app.domain.repository.SaleRepository;
import com.qbook.app.utilities.factory.GoalFactory;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyGoalServiceImpl implements CompanyGoalService {

    private final ApplicationProperties applicationProperties;
    private final CompanyRevenueGoalRepository companyRevenueGoalRepository;
    private final GoalFactory goalFactory;
    private final SaleRepository saleRepository;

    @Override
    public void newCompanyGoal(NewCompanyGoalModel newCompanyGoalModel) {
        if(applicationProperties.getShortDateTimeFormatter().parseDateTime(newCompanyGoalModel.getGoalMeasureDate()).isBeforeNow()) {
            throw new InvalidDateException("The date provided is to old, pleasure provided a measure by  date later than today.");
        }

        // check if there is any goals already for the month
        final DateTime currentDate = DateTime.now();

        companyRevenueGoalRepository
                .findAll()
                .forEach(companyRevenueGoal -> {
                    final DateTime revenueGoalMeasureDate = new DateTime(companyRevenueGoal.getGoalMeasureDate());
                    if(revenueGoalMeasureDate.getMonthOfYear() == currentDate.getMonthOfYear()
                            && revenueGoalMeasureDate.getYear() == currentDate.getYear()) {
                        throw new DuplicateGoalException("A goal already exists for " + applicationProperties.getShortDateTimeFormatter().print(revenueGoalMeasureDate));
                    }
                });

        final CompanyRevenueGoal goal = goalFactory.buildCompanyGoal(newCompanyGoalModel);

        // Auto-Activate goals if no current active
        final Optional<CompanyRevenueGoal> revenueGoalOptional = companyRevenueGoalRepository
                .findByGoalActive(true);

        if (!revenueGoalOptional.isPresent()) {
            goal.setGoalActive(true);
        }

        goal.setGoalIndex(((int)companyRevenueGoalRepository.count()) + 1);

        companyRevenueGoalRepository.save(goal);
    }

    @Override
    public void updateCompanyGoal(UpdateCompanyGoalModel updateCompanyGoalModel) {

        final Optional<CompanyRevenueGoal> revenueGoalOptional = companyRevenueGoalRepository
                .findById(new ObjectId(updateCompanyGoalModel.getGoalId()));

        if(revenueGoalOptional.isPresent()) {
            final CompanyRevenueGoal companyRevenueGoal = revenueGoalOptional.get();
            companyRevenueGoal.setGoalName(updateCompanyGoalModel.getGoalName());
            companyRevenueGoal.setRevenueGoalWorstCase(updateCompanyGoalModel.getRevenueGoalWorstCase());
            companyRevenueGoal.setRevenueGoalBestCase(updateCompanyGoalModel.getRevenueGoalBestCase());
            companyRevenueGoal.setGoalMeasureDate(applicationProperties.getShortDateTimeFormatter().parseDateTime(updateCompanyGoalModel.getGoalMeasureDate()).withTime(23, 59, 59, 0).getMillis());
            companyRevenueGoal.setGoalUpdatedDate(DateTime.now().getMillis());
            companyRevenueGoalRepository.save(companyRevenueGoal);
        } else {
            throw new ResourceNotFoundException("We could not find the goal to update");
        }
    }

    @Override
    public List<CompanyGoalModel> getCompanyGoals() {
        return companyRevenueGoalRepository
                .findAllByGoalActiveAndGoalMeasureDateBefore(false, DateTime.now().getMillis())
                .stream()
                .map(revenueGoal -> {
                    final CompanyGoalModel goalModel = goalFactory.buildCompanyGoalModel(revenueGoal);
                    viewCompanyRevenuePerGoalPeriod(
                            revenueGoal,
                            goalModel,
                            new DateTime(revenueGoal.getGoalStartDate()).getMillis(),
                            new DateTime(revenueGoal.getGoalMeasureDate()).getMillis()
                    );
                    return goalModel;
                })
                .collect(Collectors.toList());
    }

    private void viewCompanyRevenuePerGoalPeriod(final CompanyRevenueGoal revenueGoal, final CompanyGoalModel goalModel, long from, long to) {
        List<Sale> sales = saleRepository.findAllByDateTimeOfSaleBetween(
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

    @Override
    public CurrentCompanyRevenueGoalModel getCompanyCurrentGoal() {
       final CompanyRevenueGoal revenueGoal = companyRevenueGoalRepository
                .findByGoalActive(true)
                .orElseThrow(() -> new ResourceNotFoundException("There are no active goals for the company in the current month."));

        final CurrentCompanyRevenueGoalModel currentRevenueGoalModel = goalFactory.buildCurrentCompanyRevenueGoalModel(revenueGoal);
        viewCompanyRevenuePerGoalPeriodProgress(
                revenueGoal,
                currentRevenueGoalModel,
                new DateTime(revenueGoal.getGoalStartDate()).getMillis(),
                new DateTime(revenueGoal.getGoalMeasureDate()).getMillis()
        );

        return currentRevenueGoalModel;
    }

    private void viewCompanyRevenuePerGoalPeriodProgress(final CompanyRevenueGoal revenueGoal, final CurrentCompanyRevenueGoalModel currentRevenueGoalModel, long from, long to) {
        List<Sale> sales = saleRepository.findAllByDateTimeOfSaleBetween(
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

    @Override
    public ViewCompanyGoalModel viewCompany(String goalId) {
        CompanyRevenueGoal revenueGoal = companyRevenueGoalRepository
                .findById(new ObjectId(goalId))
                .orElseThrow(() -> new ResourceNotFoundException("There are no active goals for the company in the current month."));

        return goalFactory.buildViewCompanyGoalModel(revenueGoal);
    }
}
