package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface UserAddRolesService {

	public User getUserById(Integer userId)
			throws ServiceException;

	public Collection<UserRole> getAvailableUserRoles()
			throws ServiceException;

	public void saveUser(User user)
			throws ServiceException;
}
