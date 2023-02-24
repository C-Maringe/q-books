package com.qbook.app.domain.models;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "employee_type")
public class EmployeeType {
    @Id
    private ObjectId id;
    @Expose
    private String employeeType;
}
