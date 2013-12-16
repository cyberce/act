package org.teiath.service.act;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;

public interface AuthorizeTwitterService {

	public void saveUser(User user)
			throws ServiceException;
}
