package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;

@Service("userValidationService")
@Transactional
public class UserValidationServiceImpl
		implements UserValidationService {

	@Autowired
	UserDAO userDAO;

	@Override
	public boolean usernameExists(String username)
			throws ServiceException {
		try {
			User user = userDAO.findByUsername(username);
			return (user != null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public boolean usernameLdapExists(String username)
			throws ServiceException {
		try {
			User user = userDAO.findByLDAPUsername(username);
			return (user != null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public boolean emailExists(String email)
			throws ServiceException {
		try {
			User user = userDAO.findByEmail(email);
			return (user != null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
