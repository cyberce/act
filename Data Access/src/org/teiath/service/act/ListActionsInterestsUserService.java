package org.teiath.service.act;

import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListActionsInterestsUserService {

	public SearchResults<EventInterest> searchEventInterestsByCriteria(EventInterestSearchCriteria criteria)
			throws ServiceException;

}
