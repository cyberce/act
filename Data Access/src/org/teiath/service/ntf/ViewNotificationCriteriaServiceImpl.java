package org.teiath.service.ntf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventNotificationCriteriaDAO;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("viewNotificationCriteriaService")
@Transactional
public class ViewNotificationCriteriaServiceImpl
		implements ViewNotificationCriteriaService {

	@Autowired
	EventNotificationCriteriaDAO notificationCriteriaDAO;

	@Override
	public EventNotificationCriteria getNotificationCriteriaById(Integer notificationId)
			throws ServiceException {
		EventNotificationCriteria notificationCriteria;
		try {
			notificationCriteria = notificationCriteriaDAO.findById(notificationId);
			notificationCriteria.getEventCategories().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return notificationCriteria;
	}
}
