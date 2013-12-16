package org.teiath.data.dao;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.exceptions.ServiceException;

import java.util.Collection;

public interface EventCategoryDAO {

	public Collection<EventCategory> findAll();

	public Collection<EventCategory> findByParentId(Integer id);

	public void save(EventCategory eventCategory);

	public EventCategory findById(Integer id);

	public void delete(EventCategory eventCategory)
			throws ServiceException;
}
