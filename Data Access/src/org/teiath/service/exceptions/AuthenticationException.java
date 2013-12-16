package org.teiath.service.exceptions;

public class AuthenticationException
		extends Exception {

	public static final String WRONG_CREDENTIALS_ERROR = "Ο συνδυασμός username / password δεν αντιστοιχεί σε υπαρκτό χρηστη.";

	public AuthenticationException() {
	}

	public AuthenticationException(String reason) {
		super(reason);
	}
}
