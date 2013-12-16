package org.teiath.service.values;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.FeedCategoryDAO;
import org.teiath.data.dao.FeedDAO;
import org.teiath.data.dao.FeedTypeDAO;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.domain.aggregator.FeedType;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("createFeedService")
@Transactional
public class CreateFeedServiceImpl
		implements CreateFeedService {

	@Autowired
	FeedDAO feedDAO;
	@Autowired
	FeedTypeDAO feedTypeDAO;
	@Autowired
	FeedCategoryDAO feedCategoryDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveFeed(Feed feed)
			throws ServiceException {
		try {
			feedDAO.save(feed);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	public Collection<FeedType> getFeedTypes()
			throws ServiceException {
		Collection<FeedType> feedTypes;

		try {
			feedTypes = feedTypeDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feedTypes;
	}

	@Override
	public Collection<FeedCategory> getFeedCategories()
			throws ServiceException {
		Collection<FeedCategory> feedCategories;

		try {
			feedCategories = feedCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feedCategories;
	}
}
