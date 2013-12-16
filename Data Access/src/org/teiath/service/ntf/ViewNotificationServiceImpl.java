package org.teiath.service.ntf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.NotificationDAO;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;

@Service("viewNotificationService")
@Transactional
public class ViewNotificationServiceImpl
		implements ViewNotificationService {

	@Autowired
	NotificationDAO notificationDAO;

	@Override
	public Notification getNotificationById(Integer notificationId)
			throws ServiceException {
		Notification notification;
		try {
			notification = notificationDAO.findById(notificationId);
			notification.getNotificationCriteria().getEventCategories().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return notification;
	}
}
