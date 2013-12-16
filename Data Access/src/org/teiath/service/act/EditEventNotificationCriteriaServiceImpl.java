package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventCategoryDAO;
import org.teiath.data.dao.EventNotificationCriteriaDAO;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("editEventNotificationCriteriaService")
@Transactional
public class EditEventNotificationCriteriaServiceImpl
		implements EditEventNotificationCriteriaService {

	@Autowired
	EventNotificationCriteriaDAO eventNotificationCriteriaDAO;
	@Autowired
	EventCategoryDAO eventCategoryDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveEventNotificationCriteria(EventNotificationCriteria eventNotificationCriteria)
			throws ServiceException {
		try {
			eventNotificationCriteriaDAO.save(eventNotificationCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public EventNotificationCriteria getEventNotificationCriteriaById(Integer notificationId)
			throws ServiceException {
		EventNotificationCriteria eventNotificationCriteria;
		try {
			eventNotificationCriteria = eventNotificationCriteriaDAO.findById(notificationId);
			eventNotificationCriteria.getEventCategories().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return eventNotificationCriteria;
	}

	@Override
	public Collection<EventCategory> getEventCategories()
			throws ServiceException {
		Collection<EventCategory> eventCategories;

		try {
			eventCategories = eventCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return eventCategories;
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
