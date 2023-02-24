package com.qbook.app.application.services.appservices;

import com.qbook.app.domain.models.User;

public interface AuthTokenServices {
	String extractUserId(String authToken);

	String extractUserIdForReset(String resetToken);

	String generateAuthToken(String id);

	String generateResetToken(String id);

	User extractUser(String authToken);
}
