package org.teiath.data.exceptions;

public class ServiceException
		extends Exception {

	public static final String DATABASE_ERROR = "Πρόβλημα στην επικοινωνία με τη βάση δεδομένων";

	public ServiceException() {
	}

	public ServiceException(String message) {
		super(message);
	}
}
