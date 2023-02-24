package com.qbook.app.application.models.webPlatformModels;

import com.qbook.app.domain.models.UserPermission;
import lombok.Data;

import java.util.List;

@Data
public class LoggedInUserModel {
	private String token;
	private String fullName;
	private String role;
	private List<UserPermission> userPermissionList;
	private boolean hasAcceptedTerms;
}
