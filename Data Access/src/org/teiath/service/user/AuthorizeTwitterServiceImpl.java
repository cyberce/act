package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.service.act.AuthorizeTwitterService;
import org.teiath.service.exceptions.ServiceException;

@Service("authorizeTwitterService")
@Transactional
public class AuthorizeTwitterServiceImpl
		implements AuthorizeTwitterService {

	@Autowired
	UserDAO userDAO;

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
