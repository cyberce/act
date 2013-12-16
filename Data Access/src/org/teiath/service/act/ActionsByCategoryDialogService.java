package org.teiath.service.act;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ActionsByCategoryDialogService {

	public Collection<EventCategory> getEventCategories()
			throws ServiceException;

	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException;
}
