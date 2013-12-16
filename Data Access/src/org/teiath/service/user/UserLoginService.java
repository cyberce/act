package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;

public interface UserLoginService {

	public User login(String username, String password)
			throws ServiceException, AuthenticationException;

	public User wsLogin(String username, String password) throws ServiceException, AuthenticationException;

	public User ssoLogin(User user) throws ServiceException, AuthenticationException;

}
