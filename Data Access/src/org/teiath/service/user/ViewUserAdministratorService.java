package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;

public interface ViewUserAdministratorService {

	public User getUserById(int userId)
			throws ServiceException;
}


