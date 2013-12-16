package org.teiath.service.social;

import org.teiath.data.domain.act.Event;
import org.teiath.service.exceptions.ServiceException;

public interface FacebookShareService {

	public Event getEventById(Integer eventId)
			throws ServiceException;
}
