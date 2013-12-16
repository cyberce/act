package org.teiath.service.act;

import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.FeedDataSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

public interface SearchFeedDataService {

	public SearchResults<FeedData> searchFeedData(FeedDataSearchCriteria criteria)
			throws ServiceException;

	public Collection<FeedCategory> getFeedCategories()
			throws ServiceException;

	public Collection<Feed> getFeeds()
			throws ServiceException;

	public void saveFeeds(Collection<FeedData> feedData)
			throws ServiceException;

	public void deleteFeedDataByDate(Date dateFrom, Date dateTo)
			throws ServiceException;

/*	public Event getEventByCode(String code)
			throws ServiceException;*/
}
