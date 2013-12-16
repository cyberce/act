package org.teiath.service.act;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface CreateActionNotificationCriteriaService {

	public void saveEventNotificationCriteria(EventNotificationCriteria eventNotificationCriteria)
			throws ServiceException;

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;
}
