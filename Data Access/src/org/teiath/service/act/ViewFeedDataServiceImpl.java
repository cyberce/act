package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.FeedDataDAO;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.service.exceptions.ServiceException;

@Service("viewFeedDataService")
@Transactional
public class ViewFeedDataServiceImpl
		implements ViewFeedDataService {

	@Autowired
	FeedDataDAO feedDataDAO;

	@Override
	public FeedData getFeedDataById(Integer eventId)
			throws ServiceException {
		FeedData feedData;

		try {
			feedData = feedDataDAO.findById(eventId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feedData;
	}
}
