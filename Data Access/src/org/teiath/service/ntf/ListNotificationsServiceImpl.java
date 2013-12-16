package org.teiath.service.ntf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.NotificationDAO;
import org.teiath.data.domain.Notification;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("listNotificationsService")
@Transactional
public class ListNotificationsServiceImpl
		implements ListNotificationsService {

	@Autowired
	NotificationDAO notificationDAO;

	@Override
	public SearchResults<Notification> searchNotificationsByCriteria(NotificationSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Notification> results;

		try {
			results = notificationDAO.search(criteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteNotification(Notification notification)
			throws ServiceException {
		try {
			notificationDAO.delete(notification);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
