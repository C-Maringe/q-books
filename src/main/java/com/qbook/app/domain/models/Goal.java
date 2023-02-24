package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class Goal {
    private ObjectId goalId;
    private String goalName;
    private boolean goalActive;
    private boolean goalAchieved;
    private long goalStartDate;
    private long goalMeasureDate;
    private long goalCreatedDate;
    private long goalUpdatedDate;
    private int goalIndex;
}
