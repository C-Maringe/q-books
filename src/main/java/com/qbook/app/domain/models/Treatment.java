package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "treatment_collection")
public class Treatment {

    @Id
    private ObjectId id;
    private String treatmentName;
    private String treatmentDescription;
    private double seniorPrice;
    private boolean doneBySenior = false;
    private double juniorPrice;
    private boolean doneByJunior = false;
    private int duration;
    private boolean isActive = false;
    private boolean special = false;
    private double specialPrice = 0.0;
    private Long specialEndDate = 0L;

    @DBRef
    private EmployeeType employeeType;
}
