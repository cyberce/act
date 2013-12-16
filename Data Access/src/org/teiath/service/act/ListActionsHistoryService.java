package org.teiath.service.act;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListActionsHistoryService {

	public SearchResults<EventInterest> searchEventInterestsByCriteria(EventInterestSearchCriteria criteria)
			throws ServiceException;

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;
}
