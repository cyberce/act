package org.teiath.data.dao;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

import java.util.Collection;

public interface EventNotificationCriteriaDAO {

	public void save(EventNotificationCriteria eventNotificationCriteria);

	public void delete(EventNotificationCriteria eventNotificationCriteria);

	public Collection<EventNotificationCriteria> checkCriteria(Event event);

	public EventNotificationCriteria findById(Integer id);

	public SearchResults<EventNotificationCriteria> search(NotificationsCriteriaSearchCriteria criteria);
}
