package org.teiath.data.dao;

import org.teiath.data.domain.aggregator.FeedType;

import java.util.Collection;

public interface FeedTypeDAO {

	public Collection<FeedType> findAll();
}
