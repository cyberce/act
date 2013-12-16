package org.teiath.data.dao;

import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

public interface NotificationCriteriaDAO {

	public NotificationCriteria findById(Integer id);

	public SearchResults<NotificationCriteria> search(NotificationsCriteriaSearchCriteria criteria);

	public void save(NotificationCriteria notificationCriteria);

	public void delete(NotificationCriteria notificationCriteria);
}
