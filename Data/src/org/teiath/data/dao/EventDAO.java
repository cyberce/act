package org.teiath.data.dao;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;

import java.util.Collection;
import java.util.Date;

public interface EventDAO {

	public Event findById(Integer id);

	public SearchResults<Event> search(EventSearchCriteria criteria);

	public void save(Event event);

	public void delete(Event event);

	public Collection<Event> findOnGoingEvents(Date dateFrom, Date dateTo);

	public Collection<Event> findEventsByEventCategory(EventCategory eventCategoryCategory);

	public Event findByCode(String code);

	public Collection<Event> findAll();
}
