package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ApplicationImageDAO;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;

@Service("viewUserService")
@Transactional
public class ViewUserServiceImpl
		implements ViewUserService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	ApplicationImageDAO imageDAO;

	@Override
	public User getUserById(int userId)
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
}
