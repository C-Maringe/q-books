package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "special_package_collection")
public class SpecialPackage {

    @Id
    private ObjectId id;
    private String specialName;
    private String specialDescription;
    private double seniorPrice;
    private boolean doneBySenior = false;
    private double juniorPrice;
    private boolean doneByJunior = false;
    private int duration;
    private boolean isActive = true;

    @DBRef
    private EmployeeType employeeType;
}
