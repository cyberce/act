package org.teiath.service.act;

import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

public interface ViewActionSearchService {

	public Event getEventById(Integer eventId)
			throws ServiceException;

	public EventInterest getEventInterestByUser(Event event, User user)
			throws ServiceException;

	public void saveEventInterest(EventInterest listingInterest)
			throws ServiceException;

	public Event getEventByCode(String code)
			throws ServiceException;

	public Collection<ApplicationImage> getImages(Event event)
			throws ServiceException;

	public Collection<EventPDF> getPDFs(Event event)
			throws ServiceException;
}


