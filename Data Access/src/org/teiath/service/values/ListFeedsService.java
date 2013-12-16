package org.teiath.service.values;

import org.teiath.data.domain.aggregator.Feed;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ListFeedsService {

	public Collection<Feed> getFeeds()
			throws ServiceException;

	public void deleteFeed(Feed feed)
			throws ServiceException, DeleteViolationException;

	public void deleteFeedData(Feed feed)
			throws ServiceException, DeleteViolationException;

/*	public void deleteFeedWithData(Feed feed)
			throws ServiceException, DeleteViolationException;*/
}

