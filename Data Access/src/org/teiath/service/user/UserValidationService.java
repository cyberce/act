package org.teiath.service.user;

import org.teiath.service.exceptions.ServiceException;

public interface UserValidationService {

	public boolean usernameExists(String username)
			throws ServiceException;

	public boolean usernameLdapExists(String username)
			throws ServiceException;

	public boolean emailExists(String email)
			throws ServiceException;
}
