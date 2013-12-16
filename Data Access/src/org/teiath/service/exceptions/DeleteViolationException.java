package org.teiath.service.exceptions;

public class DeleteViolationException
		extends Exception {

	public DeleteViolationException() {
		super("Δεν επιτρέπεται η διαγραφή της εγγραφής.");
	}

	public DeleteViolationException(String message) {
		super(message);
	}
}
