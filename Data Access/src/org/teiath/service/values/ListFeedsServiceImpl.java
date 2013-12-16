package org.teiath.service.values;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.FeedDAO;
import org.teiath.data.dao.FeedDataDAO;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("listFeedsService")
@Transactional
public class ListFeedsServiceImpl
		implements ListFeedsService {

	@Autowired
	FeedDAO feedDAO;
	@Autowired
	FeedDataDAO feedDataDAO;

	@Override
	public Collection<Feed> getFeeds()
			throws ServiceException {
		Collection<Feed> feeds;

		try {
			feeds = feedDAO.findAll();
			for (Feed feed : feeds) {
				feed.getFeedData().iterator();
				feed.getFeedJobs().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feeds;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteFeed(Feed feed)
			throws ServiceException, DeleteViolationException {
		try {
			feedDAO.delete(feed);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void deleteFeedData(Feed feed)
			throws ServiceException, DeleteViolationException {
		try {
			feedDataDAO.deleteByFeed(feed);
		} catch (ConstraintViolationException e) {
			throw new DeleteViolationException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
