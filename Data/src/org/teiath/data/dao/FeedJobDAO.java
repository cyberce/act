package org.teiath.data.dao;

import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedJob;

public interface FeedJobDAO {

	public void save(FeedJob feedJob);

	public FeedJob findLatestJob();

	public FeedJob findLatestJobByFeed(Feed feed);
}
