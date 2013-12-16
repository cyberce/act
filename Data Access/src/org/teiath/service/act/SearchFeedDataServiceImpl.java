package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.FeedCategoryDAO;
import org.teiath.data.dao.FeedDAO;
import org.teiath.data.dao.FeedDataDAO;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.FeedDataSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("searchFeedDataService")
@Transactional
public class SearchFeedDataServiceImpl
		implements SearchFeedDataService {

	@Autowired
	FeedDAO feedDAO;
	@Autowired
	FeedCategoryDAO feedCategoryDAO;
	@Autowired
	FeedDataDAO feedDataDAO;

	@Override
	public SearchResults<FeedData> searchFeedData(FeedDataSearchCriteria criteria)
			throws ServiceException {
		SearchResults<FeedData> results;

		try {
			results = feedDataDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
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

	@Override
	public Collection<Feed> getFeeds()
			throws ServiceException {
		Collection<Feed> feeds;

		try {
			feeds = feedDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feeds;
	}

	@Override
	public void saveFeeds(Collection<FeedData> feedData)
			throws ServiceException {
		for (FeedData data : feedData) {
			feedDataDAO.save(data);
		}
	}

	@Override
	public void deleteFeedDataByDate(Date dateFrom, Date dateTo)
			throws ServiceException {
		feedDataDAO.deleteByDate(dateFrom, dateTo);
	}
}
