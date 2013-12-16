package org.teiath.service.act;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.service.exceptions.ServiceException;
import org.zkoss.image.AImage;
import org.zkoss.zul.ListModelList;

import javax.mail.internet.AddressException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service("copyActionService")
@Transactional
public class CopyActionServiceImpl
		implements CopyActionService {

	@Autowired
	EventDAO eventDAO;
	@Autowired
	EventPDFDAO eventPDFDAO;
	@Autowired
	EventCategoryDAO eventCategoryDAO;
	@Autowired
	EventNotificationCriteriaDAO eventNotificationCriteriaDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	private NotificationProperties notificationProperties;
	@Autowired
	ApplicationImageDAO applicationImageDAO;
	@Autowired
	CurrencyDAO currencyDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveEvent(Event event, ListModelList<AImage> uploadedImages, ListModelList<EventPDF> uploadedPDFs)
			throws ServiceException {
		try {
			event.setEventCreationDate(new Date());
			event.setCode(RandomStringUtils.randomAlphanumeric(15).toUpperCase());
			eventDAO.save(event);

			//save Images
			for (AImage image : uploadedImages) {
				ApplicationImage applicationImage = new ApplicationImage();
				applicationImage.setEvent(event);
				applicationImage.setImageBytes(image.getByteData());
				applicationImageDAO.save(applicationImage);
			}

			//save PDFs
			for (EventPDF eventPDF : uploadedPDFs) {
				eventPDFDAO.save(eventPDF);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public void sendNotifications(Event event)
			throws ServiceException {

		Collection<EventNotificationCriteria> eventNotificationCriteria;
		eventNotificationCriteria = eventNotificationCriteriaDAO.checkCriteria(event);
		for (EventNotificationCriteria matchedCriteria : eventNotificationCriteria) {
			//Create and save Notification
			Notification notification = new Notification();
			notification.setUser(matchedCriteria.getUser());
			notification.setEvent(event);
			notification.setType(Notification.TYPE_ACTIONS);
			notification.setSentDate(new Date());
			notification.setTitle("Νεα δράση");
			String body = notificationProperties.getNotificationBody();
			notification.setBody(body);
			notification.setNotificationCriteria(matchedCriteria);
			notificationDAO.save(notification);

			//Create and send Email
			String mailSubject = emailProperties.getRouteCreateSubject()
					.replace("$1", "[Υπηρεσία εύρεσης Δράσεων]:").replace("$2", "δράσης");
			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			htmlMessageBuiler
					.append(emailProperties.getRouteCreateBody());
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler.append("<br>Κωδικός δράσης: " + event.getCode());
			htmlMessageBuiler.append("<br>Τίτλος δράσης: " + event.getEventTitle());
			htmlMessageBuiler.append("<br>Τόπος Διεξαγωγής: " + event.getEventLocation());
			htmlMessageBuiler.append("<br>Ημερομηνία έναρξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
					.format(event.getDateFrom()));
			htmlMessageBuiler.append("<br>Ημερομηνία λήξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
					.format(event.getDateTo()));
			String link = "https://swaps.teiath.gr/previewEvent?code=" + event.getCode();
			if (event.getPrice() != null) {
				htmlMessageBuiler.append("<br>Τιμή: " + event.getPrice() + " Ευρώ");
			}
			htmlMessageBuiler.append("<br><a href="+link+">"+link+"</a>");
			htmlMessageBuiler.append("</body> </html>");
			try {
				mailManager
						.sendMail(emailProperties.getFromAddress(), matchedCriteria.getUser().getEmail(), mailSubject,
								htmlMessageBuiler.toString());
			} catch (AddressException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	@Override
	public Collection<EventCategory> getEventCategories()
			throws ServiceException {
		Collection<EventCategory> categories;

		try {
			categories = eventCategoryDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

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
			images = applicationImageDAO.findByEvent(event);
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteEventPhotos(Event event)
			throws ServiceException {
		try {
			applicationImageDAO.deleteAll(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteEventPDFs(Event event)
			throws ServiceException {
		try {
			eventPDFDAO.deleteAll(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public Collection<EventCategory> getEventCategoriesByParentId(Integer parentId)
			throws ServiceException {
		Collection<EventCategory> categories;

		try {
			categories = eventCategoryDAO.findByParentId(parentId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return categories;
	}

	@Override
	public ListModelList<String> getPopularLocations()
			throws ServiceException {
		Collection<Event> events;
		ListModelList<String> locations = new ListModelList<>();

		try {
			events = eventDAO.findAll();
			for (Event event : events) {
				locations.add(event.getEventLocation());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return locations;
	}

	public Collection<Currency> getCurrencies()
			throws ServiceException {
		Collection<Currency> currencies;

		try {
			currencies = currencyDAO.findAll();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currencies;
	}
}
