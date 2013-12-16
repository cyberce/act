package org.teiath.service.act;

import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.service.exceptions.ServiceException;

public interface ViewFeedDataService {

	public FeedData getFeedDataById(Integer eventId)
			throws ServiceException;
}




