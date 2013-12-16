package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventCategoryDAO;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.dao.NotificationDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("searchEventsService")
@Transactional
public class SearchEventsServiceImpl
		implements SearchEventsService {

	@Autowired
	EventDAO eventDAO;
	@Autowired
	EventCategoryDAO eventCategoryDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;

	@Override
	public SearchResults<Event> searchEvents(EventSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Event> results;

		try {
			results = eventDAO.search(criteria);
			for (Event event : results.getData()) {
				event.getEventInterests().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	public Collection<EventCategory> getEventCategories()
			throws ServiceException {
		Collection<EventCategory> categories;

		try {
			categories = eventCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	public Event getEventByCode(String code)
			throws ServiceException {
		Event event;

		try {
			event = eventDAO.findByCode(code);
			event.getEventInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return event;
	}

	@Override
	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException {
		Collection<EventCategory> categories;

		try {
			categories = eventCategoryDAO.findByParentId(parentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	public EventCategory getEventCategory(Integer id) throws ServiceException {
		EventCategory eventCategory;

		try {
			eventCategory = eventCategoryDAO.findById(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return eventCategory;
	}

}
