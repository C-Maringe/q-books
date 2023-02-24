package com.qbook.app.application.models.userPermissionsModels;

import lombok.Data;

@Data
public class Schedule {
	private boolean canRead;
	private boolean canWrite;
}
