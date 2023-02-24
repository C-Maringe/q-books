package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.ResetPasswordModel;
import com.qbook.app.application.models.UserForgotPasswordModel;
import com.qbook.app.application.models.scheduleModels.ScheduleUserAcceptedTermsModel;
import com.qbook.app.application.models.scheduleModels.ScheduleUserHasAcceptedTermsModel;
import com.qbook.app.application.models.webPlatformModels.LoggedInUserModel;
import com.qbook.app.application.models.webPlatformModels.LoginUserModel;

public interface UserServices {
	LoggedInUserModel loginPlatformUser(LoginUserModel loginUserModel);

	UserForgotPasswordModel userForgotPassword(String userEmailAddress);

	UserForgotPasswordModel userResetPassword(String userId, ResetPasswordModel resetPasswordModel);

	ScheduleUserAcceptedTermsModel checkIfUserHasAcceptedTerms(String authToken);

    ScheduleUserHasAcceptedTermsModel userHasAcceptedTerms(String authToken);
}
