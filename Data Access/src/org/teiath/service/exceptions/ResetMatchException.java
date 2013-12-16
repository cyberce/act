package org.teiath.service.exceptions;

public class ResetMatchException
		extends Exception {

	public ResetMatchException() {
		super("Το url της επαναφοράς κωδικού είναι λανθασμένο.");
	}

	public ResetMatchException(String message) {
		super(message);
	}
}
