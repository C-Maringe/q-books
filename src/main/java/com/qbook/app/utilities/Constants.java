package com.qbook.app.utilities;

public class Constants {
		//registration email message
		public static String WELCOME_EMAIL = "Thanks for registering with us, we hope you enjoy the experience with us.\n\n";
		
		//login error
		public static String ERROR_LOGIN_PASS = "The password provided is incorrect";
		public static String ERROR_LOGIN_PASS_2 = "Please enter a password";
		public static String ERROR_LOGIN_EMAIL_DE = "There is no user found with email address provided";
		public static String ERROR_LOGIN_EMAIL = "The email and password combination incorrect";
		public static String ERROR_LOGIN_EMAIL_2 = "Please enter an email address";
		public static String ERROR_ACC_DISABLED = "Your profile has been disabled please contact the administrator to re-activate it";
		
		//login success
		public static String SUCCESS_LOGIN = "Welcome back ";
		public static String SUCCESS_LOGOUT = "Goodbye ";
		
		//navigation errors
		public static String NAV_ERROR_SCHEDULE = "To access the schedule feature you need to be logged in. \nIf you are not yet a member please register to join the Personal revolution";

		public static String BOOKING_CANCELLED = "Your booking was successfully cancelled.";
	
		//profile
		public static String PROFILE_UPDATE_SUCCESS = "updated successfully";
		public static String PROFILE_DELETE_SUCCESS = "deleted successfully";

		//feedback errors
		public static String CLIENT_FEEDBACK_FAIL_NO_CLIENT_FOUND = "Your feedback could not be saved, please contact the administrator as we value your feedback.";
        public static String CLIENT_FEEDBACK_SUCCESS = "Thank you for taking the time to give us feedback.";
}
