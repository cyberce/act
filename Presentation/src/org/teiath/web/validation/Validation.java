package org.teiath.web.validation;

public class Validation {

	public static boolean validateEmail(String email) {
		final String EMAIL_VALID_REGULAR_EXPRESSION = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

		return email.matches(EMAIL_VALID_REGULAR_EXPRESSION);
	}

	public static boolean validatePassword(String password) {
		final String PASSWORD_VALID_REGULAR_EXPRESSION = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

		return password.matches(PASSWORD_VALID_REGULAR_EXPRESSION);
	}
}
