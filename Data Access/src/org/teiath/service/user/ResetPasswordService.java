package org.teiath.service.user;

import org.teiath.service.exceptions.ResetExpirationException;
import org.teiath.service.exceptions.ResetMatchException;

public interface ResetPasswordService {

	public boolean request(String value);

	public boolean reset(String username, String token, String passwd)
			throws ResetExpirationException, ResetMatchException;

	public void validate(String username, String token)
			throws ResetExpirationException, ResetMatchException;
}
