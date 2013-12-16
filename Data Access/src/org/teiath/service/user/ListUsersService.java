package org.teiath.service.user;

import org.teiath.data.domain.User;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListUsersService {

	public SearchResults<User> searchUsersByCriteria(UserSearchCriteria criteria)
			throws ServiceException;

	public void toggleBan(User user)
			throws ServiceException;
}
