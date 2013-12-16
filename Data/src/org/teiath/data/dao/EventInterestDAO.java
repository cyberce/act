package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;

import java.util.Collection;

public interface EventInterestDAO {

	public EventInterest findByUser(Event event, User user);

	public void save(EventInterest eventInterest);

	public SearchResults<EventInterest> search(EventInterestSearchCriteria criteria);

	public Collection<EventInterest> findByEvent(Event event);
}

/*	public EventInterest findById(Integer id);





	public void delete(ListingInterest listingInterest); */

