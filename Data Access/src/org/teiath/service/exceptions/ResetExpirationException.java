package org.teiath.service.exceptions;

public class ResetExpirationException
		extends Exception {

	public ResetExpirationException() {
		super("Το token της επαναφοράς του κωδικού έχει λήξει.");
	}

	public ResetExpirationException(String message) {
		super(message);
	}
}
