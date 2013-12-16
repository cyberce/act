package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.NotificationCriteriaDAO;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("listNotificationsCriteriaService")
@Transactional
public class ListNotificationsCriteriaServiceImpl
		implements ListNotificationsCriteriaService {

	@Autowired
	NotificationCriteriaDAO notificationCriteriaDAO;

	@Override
	public SearchResults<NotificationCriteria> searchNotificationsCriteriaByCriteria(
			NotificationsCriteriaSearchCriteria criteria)
			throws ServiceException {
		SearchResults<NotificationCriteria> results;

		try {
			results = notificationCriteriaDAO.search(criteria);
			for (NotificationCriteria notificationCriteria : results.getData()) {
				notificationCriteria.getNotifications().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteNotificationCriteria(NotificationCriteria notificationCriteria)
			throws ServiceException {
		try {
			notificationCriteriaDAO.delete(notificationCriteria);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}
}
