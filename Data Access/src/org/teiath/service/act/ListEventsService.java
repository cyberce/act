package org.teiath.service.act;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListEventsService {

	public SearchResults<Event> searchEventsByCriteria(EventSearchCriteria criteria)
			throws ServiceException;

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public void deleteEvent(Event event)
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;

	public void createUserAction(Event event)
			throws ServiceException;

}

