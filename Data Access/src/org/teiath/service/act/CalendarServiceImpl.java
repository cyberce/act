package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

@Service("calendarService")
@Transactional
public class CalendarServiceImpl
		implements CalendarService {

	@Autowired
	EventDAO eventDAO;

	@Override
	public SearchResults<Event> searchEventsByCriteria(EventSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Event> results;

		try {
			results = eventDAO.search(criteria);
			for (Event event : results.getData()) {
				event.getEventInterests().iterator();
				event.setBeginDate(event.getDateFrom());
				//ZK Calendar BeginDate and EndDate cannot be the same date
				if (event.getDateFrom().equals(event.getDateTo())) {
					java.util.Calendar cal = java.util.Calendar.getInstance(); // creates calendar
					cal.setTime(event.getDateTo()); // sets calendar time/date
					cal.add(java.util.Calendar.HOUR_OF_DAY, 1); // adds one hour
					event.setEndDate(cal.getTime()); // returns new date object, one hour in the future
				} else {
					event.setEndDate(event.getDateTo());
				}
				event.setContent(event.getEventTitle());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
	}
}
