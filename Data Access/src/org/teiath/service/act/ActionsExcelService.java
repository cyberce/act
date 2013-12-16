package org.teiath.service.act;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;
import java.util.Date;

public interface ActionsExcelService {

	public Collection<Event> getOnGoingEvents(Date dateFrom, Date dateTo)
			throws ServiceException;

	public Collection<Event> getEventsByEventCategory(EventCategory eventCategory)
			throws ServiceException;
}
