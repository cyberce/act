package org.teiath.data.dao;

import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.FeedDataSearchCriteria;

import java.util.Date;

public interface FeedDataDAO {

	public FeedData findById(Integer id);

	public SearchResults<FeedData> search(FeedDataSearchCriteria criteria);

	public void save(FeedData feedData);

	public void deleteByDate(Date dateFrom, Date dateTo);

	public void deleteByFeed(Feed feed);
}
