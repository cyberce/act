package org.teiath.service.act;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.act.*;
import org.teiath.data.domain.aggregator.FeedData;
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

@Service("createActionService")
@Transactional
public class CreateActionServiceImpl
		implements CreateActionService {

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
	FeedDataDAO feedDataDAO;
	@Autowired
	CurrencyDAO currencyDAO;
	@Autowired
	UserActionDAO userActionDAO;

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

			//create and save user action trace
			UserAction userAction = new UserAction();
			userAction.setDate(new Date());
			userAction.setUser(event.getUser());
			userAction.setEventTitle(event.getEventTitle());
			userAction.setEventCode(event.getCode());
			userAction.setEventLocation(event.getEventLocation());
			userAction.setEventCategory(event.getEventCategory().getName());
			userAction.setType(UserAction.TYPE_CREATE);
			userAction.setEventCreationDate(event.getEventCreationDate());
			userActionDAO.save(userAction);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveEvent(Event event) throws ServiceException {
		Collection<EventNotificationCriteria> eventNotificationCriteria;
		try {
			event.setEventCreationDate(new Date());
			event.setCode(RandomStringUtils.randomAlphanumeric(15).toUpperCase());
			eventDAO.save(event);

//			//save Images
//			for (AImage image : uploadedImages) {
//				ApplicationImage applicationImage = new ApplicationImage();
//				applicationImage.setEvent(event);
//				applicationImage.setImageBytes(image.getByteData());
//				applicationImageDAO.save(applicationImage);
//			}
//
//			//save PDFs
//			for (EventPDF eventPDF : uploadedPDFs) {
//				eventPDFDAO.save(eventPDF);
//			}

//			eventNotificationCriteria = eventNotificationCriteriaDAO.checkCriteria(event);
//			for (EventNotificationCriteria matchedCriteria : eventNotificationCriteria) {
//				//Create and save Notification
//				Notification notification = new Notification();
//				notification.setUser(matchedCriteria.getUser());
//				notification.setEvent(event);
//				notification.setType(Notification.TYPE_ACTIONS);
//				notification.setSentDate(new Date());
//				notification.setTitle("Νεα δράση");
//				String body = notificationProperties.getNotificationBody();
//				notification.setBody(body);
//				notification.setNotificationCriteria(matchedCriteria);
//				notificationDAO.save(notification);
//
//				//Create and send Email
//				String mailSubject = emailProperties.getRouteCreateSubject()
//						.replace("$1", "[Υπηρεσία εύρεσης Δράσεων]:").replace("$2", "δράσης");
//				StringBuilder htmlMessageBuiler = new StringBuilder();
//				htmlMessageBuiler.append("<html> <body>");
//				htmlMessageBuiler
//						.append(emailProperties.getRouteCreateBody());
//				htmlMessageBuiler.append("<br>");
//				htmlMessageBuiler.append("<br>Κωδικός δράσης: " + event.getCode());
//				htmlMessageBuiler.append("<br>Τίτλος δράσης: " + event.getEventTitle());
//				htmlMessageBuiler.append("<br>Τόπος Διεξαγωγής: " + event.getEventLocation());
//				htmlMessageBuiler.append("<br>Ημερομηνία έναρξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
//						.format(event.getDateFrom()));
//				htmlMessageBuiler.append("<br>Ημερομηνία λήξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
//						.format(event.getDateTo()));
//				if (event.getPrice() != null) {
//					htmlMessageBuiler.append("<br>Τιμή: " + event.getPrice() + " Ευρώ");
//				}
//				htmlMessageBuiler.append("</body> </html>");
//				mailManager
//						.sendMail(emailProperties.getFromAddress(), matchedCriteria.getUser().getEmail(), mailSubject,
//								htmlMessageBuiler.toString());
//			}

			//create and save user action trace
//			UserAction userAction = new UserAction();
//			userAction.setDate(new Date());
//			userAction.setUser(event.getUser());
//			userAction.setEventTitle(event.getEventTitle());
//			userAction.setEventCode(event.getCode());
//			userAction.setEventLocation(event.getEventLocation());
//			userAction.setEventCategory(event.getEventCategory().getName());
//			userAction.setType(UserAction.TYPE_CREATE);
//			userAction.setEventCreationDate(event.getEventCreationDate());
//			userActionDAO.save(userAction);

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
	public ListModelList<String> getPopularLocations()
			throws ServiceException {
		Collection<Event> events;
		ListModelList<String> locations = new ListModelList<>();
		boolean exist;

		try {
			events = eventDAO.findAll();
			for (Event event : events) {
				exist = false;
				for (String location : locations) {
					if (location.equals(event.getEventLocation())) {
						exist = true;
					}
				}
				if (! exist) {
					locations.add(event.getEventLocation());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return locations;
	}

	@Override
	public FeedData getFeedDataById(Integer eventId)
			throws ServiceException {
		FeedData feedData;

		try {
			feedData = feedDataDAO.findById(eventId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return feedData;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveFeedData(FeedData feedData)
			throws ServiceException {
		Collection<EventNotificationCriteria> eventNotificationCriteria;
		try {
			feedDataDAO.save(feedData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
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

	@Override
	public Currency getDefaultCurrency()
			throws ServiceException {
		Currency currency;
		try {
			currency = currencyDAO.findDefaultCurrency();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return currency;
	}
}
