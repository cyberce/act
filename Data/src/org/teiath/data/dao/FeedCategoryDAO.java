package org.teiath.data.dao;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.exceptions.ServiceException;

import java.util.Collection;

public interface FeedCategoryDAO {

	public Collection<FeedCategory> findAll();

	public void save(FeedCategory feedCategory);

	public void delete(FeedCategory feedCategory)
			throws ServiceException;

	public FeedCategory findById(Integer id);

}
