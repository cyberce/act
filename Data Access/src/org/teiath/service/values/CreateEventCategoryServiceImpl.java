package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventCategoryDAO;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("createEventCategoryService")
@Transactional
public class CreateEventCategoryServiceImpl
		implements CreateEventCategoryService {

	@Autowired
	EventCategoryDAO eventCategoryDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveEventCategory(EventCategory eventCategory)
			throws ServiceException {
		try {
			eventCategoryDAO.save(eventCategory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
	public EventCategory getEventCategoryByParentId(Integer parentId)
			throws ServiceException {
		EventCategory rootCategory;

		try {
			rootCategory = eventCategoryDAO.findById(parentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return rootCategory;
	}
}
