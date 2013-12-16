package org.teiath.service.values;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface CreateEventCategoryService {

	public void saveEventCategory(EventCategory eventCategory)
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;

	public EventCategory getEventCategoryByParentId(Integer parentId)
			throws ServiceException;
}
