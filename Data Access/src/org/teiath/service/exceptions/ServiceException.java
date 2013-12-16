package org.teiath.service.exceptions;

public class ServiceException
		extends Exception {

	public static final String DATABASE_ERROR = "Πρόβλημα στην επικοινωνία με τη βάση δεδομένων";
	public static final String EMAIL_ERROR = "Πρόβλημα στην αποστολή email";

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}
}
