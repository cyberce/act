package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.util.PasswordService;

@Service("editUserService")
@Transactional
public class EditUserServiceImpl
		implements EditUserService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;

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

	@Override
	public User getUserById(Integer userId)
			throws ServiceException {
		User user;
		try {
			user = userDAO.findById(userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return user;
	}

	@Override
	public boolean oldPasswordIsCorrect(String oldPassword)
			throws ServiceException {
		try {
			User user = userDAO.checkPassword(PasswordService.encrypt(oldPassword));
			if (user != null)
				return true;
			else
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
