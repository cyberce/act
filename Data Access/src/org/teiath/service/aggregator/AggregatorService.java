package org.teiath.service.aggregator;

import org.teiath.data.domain.aggregator.Feed;

import java.sql.Timestamp;
import java.util.Collection;

public interface AggregatorService {

	public Collection<Feed> fetchActiveFeeds();

	public void run(Timestamp timestamp);

	public void updateFeed(Timestamp timestamp, Feed feed);
}
