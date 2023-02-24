package com.qbook.app.domain.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@Data
@EqualsAndHashCode(callSuper = true)
public class RevenueGoal extends Goal {
    private double revenueGoalBestCase;
    private double revenueGoalWorstCase;
}
