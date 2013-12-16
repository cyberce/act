package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ApplicationImageDAO;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.dao.EventInterestDAO;
import org.teiath.data.dao.EventPDFDAO;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.exceptions.ServiceException;

import java.text.SimpleDateFormat;
import java.util.Collection;

@Service("viewActionSearchService")
@Transactional
public class ViewActionSearchServiceImpl
		implements ViewActionSearchService {

	@Autowired
	EventDAO eventDAO;
	@Autowired
	EventInterestDAO eventInterestDAO;
	@Autowired
	ApplicationImageDAO imageDAO;
	@Autowired
	EventPDFDAO eventPDFDAO;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private IMailManager mailManager;

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
	public EventInterest getEventInterestByUser(Event event, User user)
			throws ServiceException {
		EventInterest eventInterest;

		try {
			eventInterest = eventInterestDAO.findByUser(event, user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return eventInterest;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveEventInterest(EventInterest eventInterest)
			throws ServiceException {
		try {
			eventInterestDAO.save(eventInterest);

			//Create and send Email
			String mailSubject = emailProperties.getReservationSubject().replace("$1", "[Υπηρεσία εύρεσης Δράσεων]:");
			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			String mailBody = emailProperties.getReservationBody();
			htmlMessageBuiler.append(mailBody);
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler.append("<br>Κωδικός δράσης: " + eventInterest.getEvent().getCode());
			htmlMessageBuiler.append("<br>Τίτλος δράσης: " + eventInterest.getEvent().getEventTitle());
			htmlMessageBuiler.append("<br>Τόπος Διεξαγωγής: " + eventInterest.getEvent().getEventLocation());
			htmlMessageBuiler.append("<br>Ημερομηνία έναρξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
					.format(eventInterest.getEvent().getDateFrom()));
			htmlMessageBuiler.append("<br>Ημερομηνία λήξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
					.format(eventInterest.getEvent().getDateTo()));
			htmlMessageBuiler.append("</body> </html>");
			mailManager
					.sendMail(emailProperties.getFromAddress(), eventInterest.getUser().getEmail(), mailSubject,
							htmlMessageBuiler.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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
