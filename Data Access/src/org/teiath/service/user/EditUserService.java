package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;

public interface EditUserService {

	public void saveUser(User user)
			throws ServiceException;

	public User getUserById(Integer userId)
			throws ServiceException;

	public boolean oldPasswordIsCorrect(String oldPassword)
			throws ServiceException;
}
