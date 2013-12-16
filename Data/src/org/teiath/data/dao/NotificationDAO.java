package org.teiath.data.dao;

import org.teiath.data.domain.Notification;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;

public interface NotificationDAO {

	public Notification findById(Integer id);

	public SearchResults<Notification> search(NotificationSearchCriteria criteria);

	public void save(Notification notification);

	public void delete(Notification notification);
}
