package org.teiath.service.act;

import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ListNotificationsCriteriaService {

	public SearchResults<NotificationCriteria> searchNotificationsCriteriaByCriteria(
			NotificationsCriteriaSearchCriteria criteria)
			throws ServiceException;

	public void deleteNotificationCriteria(NotificationCriteria notificationCriteria)
			throws ServiceException;
}
