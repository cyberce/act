package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserActionDAO;
import org.teiath.data.domain.act.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserActionSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("listUserActionsService")
@Transactional
public class ListUserActionsServiceImpl
		implements ListUserActionsService {

	@Autowired
	UserActionDAO userActionDAO;


	@Override
	public SearchResults<UserAction> searchUserActionsByCriteria(UserActionSearchCriteria criteria)
			throws ServiceException {
		SearchResults<UserAction> results;

		try {
			results = userActionDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return results;
	}
}
