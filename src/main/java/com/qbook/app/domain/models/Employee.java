package com.qbook.app.domain.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(value = "employee_collection")
@EqualsAndHashCode(callSuper = true)
public class Employee extends User {

    private String employeeLevel; //Junior, Senior

    @DBRef
    private List<Booking> booking = new ArrayList<>();

    @DBRef
    private EmployeeType employeeType;
    private boolean mustBookConsultationFirstTime = false;

    private List<EmployeeWorkingDay> employeeWorkingDays = new ArrayList<>();

    private List<RevenueGoal> revenueGoals = new ArrayList<>();
}
