package org.teiath.service.values;


import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListCategoriesFeedsService {

	public Collection<FeedCategory> getFeedCategories()
			throws ServiceException;

	public void deleteFeedCategory(FeedCategory feedCategory)
			throws ServiceException, DeleteViolationException;

}
