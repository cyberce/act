package org.teiath.service.values;

import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.ServiceException;

public interface EditFeedCategoryService {

	public void saveFeedCategory(FeedCategory feedCategory)
			throws ServiceException;


	public FeedCategory getFeedCategoryById(Integer feedCategoryId)
			throws ServiceException;

}
