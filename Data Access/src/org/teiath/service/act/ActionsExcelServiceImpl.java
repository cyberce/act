package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

@Service("actionsExcelService")
@Transactional
public class ActionsExcelServiceImpl
		implements ActionsExcelService {

	@Autowired
	EventDAO eventDAO;

	@Override
	public Collection<Event> getOnGoingEvents(Date dateFrom, Date dateTo)
			throws ServiceException {
		Collection<Event> events;

		try {
			events = eventDAO.findOnGoingEvents(dateFrom, dateTo);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return events;
	}

	@Override
	public Collection<Event> getEventsByEventCategory(EventCategory eventCategory)
			throws ServiceException {
		Collection<Event> events;

		try {
			events = eventDAO.findEventsByEventCategory(eventCategory);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return events;
	}
}
