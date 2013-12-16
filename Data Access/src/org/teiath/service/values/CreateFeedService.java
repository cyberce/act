package org.teiath.service.values;

import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.domain.aggregator.FeedType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface CreateFeedService {

	public void saveFeed(Feed feed)
			throws ServiceException;

	public Collection<FeedType> getFeedTypes()
			throws ServiceException;

	public Collection<FeedCategory> getFeedCategories()
			throws ServiceException;
}
