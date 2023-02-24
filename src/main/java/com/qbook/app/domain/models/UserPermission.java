package com.qbook.app.domain.models;

import lombok.Data;

@Data
public class UserPermission {
	private String permissionFeature;
	private boolean canRead;
	private boolean canWrite;
}
