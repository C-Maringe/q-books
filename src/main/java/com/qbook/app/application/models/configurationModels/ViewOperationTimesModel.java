package com.qbook.app.application.models.configurationModels;

import com.qbook.app.domain.models.WorkingDay;
import lombok.Data;

import java.util.List;

@Data
public class ViewOperationTimesModel {
	private List<WorkingDay> workingDays;
}
