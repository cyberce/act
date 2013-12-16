package org.teiath.service.act;

import org.teiath.data.domain.act.Event;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface CalendarService {

	public SearchResults<Event> searchEventsByCriteria(EventSearchCriteria criteria)
			throws ServiceException;
}

