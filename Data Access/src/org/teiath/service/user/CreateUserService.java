package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;

public interface CreateUserService {

	public void saveUser(User user)
			throws ServiceException, AuthenticationException;

	public UserRole getDefaultUserRole(int defaultUserRoleCode)
			throws ServiceException;
}
