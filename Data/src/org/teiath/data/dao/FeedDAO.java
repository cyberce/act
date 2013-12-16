package org.teiath.data.dao;

import org.teiath.data.domain.aggregator.Feed;

import java.util.Collection;

public interface FeedDAO {

	public Collection<Feed> findActiveFeeds();

	public void save(Feed feed);

	public Collection<Feed> findAll();

	public void delete(Feed feed);

	public Feed findById(Integer id);
}
