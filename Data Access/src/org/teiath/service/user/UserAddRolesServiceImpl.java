package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.dao.UserRoleDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("userAddRolesService")
@Transactional
public class UserAddRolesServiceImpl
		implements UserAddRolesService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	UserRoleDAO userRoleDAO;

	@Override
	public User getUserById(Integer userId)
			throws ServiceException {
		User user;
		try {
			user = userDAO.findById(userId);
			user.getRoles().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return user;
	}

	@Override
	public Collection<UserRole> getAvailableUserRoles()
			throws ServiceException {
		Collection<UserRole> userRoles;

		try {
			userRoles = userRoleDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return userRoles;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveUser(User user)
			throws ServiceException {
		try {
			userDAO.save(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
