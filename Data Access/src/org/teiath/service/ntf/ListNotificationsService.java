package org.teiath.service.ntf;

import org.teiath.data.domain.Notification;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListNotificationsService {

	public SearchResults<Notification> searchNotificationsByCriteria(NotificationSearchCriteria criteria)
			throws ServiceException;

	public void deleteNotification(Notification notification)
			throws ServiceException;
}
