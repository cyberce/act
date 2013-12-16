package org.teiath.service.ntf;

import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;

public interface ViewNotificationCriteriaService {

	public EventNotificationCriteria getNotificationCriteriaById(Integer notificationCriteriaId)
			throws ServiceException;
}


