package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ApplicationImageDAO;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.dao.EventInterestDAO;
import org.teiath.data.dao.EventPDFDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;

@Service("viewActionOperatorService")
@Transactional
public class ViewActionOperatorServiceImpl
		implements ViewActionOperatorService {

	@Autowired
	EventDAO eventDAO;
	@Autowired
	ApplicationImageDAO imageDAO;
	@Autowired
	EventPDFDAO eventPDFDAO;
	@Autowired
	EventInterestDAO eventInterestDAO;

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

	@Override
	public Collection<ApplicationImage> getImages(Event event)
			throws ServiceException {
		Collection<ApplicationImage> images;

		try {
			images = imageDAO.findByEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return images;
	}

	@Override
	public Collection<EventPDF> getPDFs(Event event)
			throws ServiceException {
		Collection<EventPDF> pdfs;

		try {
			pdfs = eventPDFDAO.findByEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return pdfs;
	}

	@Override
	public Collection<EventInterest> getEventInterestsByUser(Event event)
			throws ServiceException {
		Collection<EventInterest> vehicles;

		try {
			vehicles = eventInterestDAO.findByEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return vehicles;
	}

	@Override
	public Event getEventByCode(String code)
			throws ServiceException {
		Event event;

		try {
			event = eventDAO.findByCode(code);
			event.getEventInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return event;
	}
}
