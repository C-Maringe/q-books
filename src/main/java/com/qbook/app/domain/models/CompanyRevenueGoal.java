package com.qbook.app.domain.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(value = "company_revenue_goal_collection")
public class CompanyRevenueGoal extends Goal {
    @Id
    private ObjectId id;
    private double revenueGoalBestCase;
    private double revenueGoalWorstCase;
}
