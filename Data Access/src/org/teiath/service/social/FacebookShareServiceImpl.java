package org.teiath.service.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.service.exceptions.ServiceException;

@Service("facebookShareService")
@Transactional
public class FacebookShareServiceImpl
		implements FacebookShareService {

	@Autowired
	EventDAO eventDAO;

	@Override
	public Event getEventById(Integer eventId)
			throws ServiceException {
		Event event;

		try {
			event = eventDAO.findById(eventId);
			event.getEventInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return event;
	}
}
