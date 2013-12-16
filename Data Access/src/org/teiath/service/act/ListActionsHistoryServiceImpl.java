package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventCategoryDAO;
import org.teiath.data.dao.EventInterestDAO;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listActionsHistoryService")
@Transactional
public class ListActionsHistoryServiceImpl
		implements ListActionsHistoryService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	EventCategoryDAO eventCategoryDAO;
	@Autowired
	EventInterestDAO eventInterestDAO;

	@Override
	public SearchResults<EventInterest> searchEventInterestsByCriteria(EventInterestSearchCriteria criteria)
			throws ServiceException {
		SearchResults<EventInterest> results;

		try {
			results = eventInterestDAO.search(criteria);
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
}
