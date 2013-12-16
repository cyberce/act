package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ActionAssessmentDAO;
import org.teiath.data.dao.ApplicationImageDAO;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.dao.EventPDFDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.exceptions.ServiceException;

import java.util.Collection;


@Service("viewActionInterestUserService")
@Transactional
public class ViewActionInterestUserServiceImpl
		implements ViewActionInterestUserService {

	@Autowired
	EventDAO eventDAO;
	@Autowired
	ActionAssessmentDAO actionAssessmentDAO;
	@Autowired
	ApplicationImageDAO imageDAO;
	@Autowired
	EventPDFDAO eventPDFDAO;

	@Override
	public Event getEventById(Integer eventId)
			throws ServiceException {
		Event event;

		try {
			event = eventDAO.findById(eventId);
			event.setAverageRating(actionAssessmentDAO.getEventAverageRating(event));
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

}
