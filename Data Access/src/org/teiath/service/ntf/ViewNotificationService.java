package org.teiath.service.ntf;

import org.teiath.data.domain.Notification;
import org.teiath.service.exceptions.ServiceException;

public interface ViewNotificationService {

	public Notification getNotificationById(Integer notificationId)
			throws ServiceException;
}


