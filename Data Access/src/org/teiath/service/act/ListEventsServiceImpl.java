package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.*;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.domain.act.UserAction;
import org.teiath.data.email.IMailManager;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.properties.NotificationProperties;
import org.teiath.data.search.EventInterestSearchCriteria;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.exceptions.ServiceException;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

@Service("listEventsService")
@Transactional
public class ListEventsServiceImpl
		implements ListEventsService {

	@Autowired
	EventDAO eventDAO;
	@Autowired
	EventInterestDAO eventInterestDAO;
	@Autowired
	EventCategoryDAO eventCategoryDAO;
	@Autowired
	NotificationDAO notificationDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;
	@Autowired
	UserActionDAO userActionDAO;


	@Override
	public SearchResults<Event> searchEventsByCriteria(EventSearchCriteria criteria)
			throws ServiceException {
		SearchResults<Event> results;

		try {
			results = eventDAO.search(criteria);
			for (Event event : results.getData()) {
				event.getEventInterests().iterator();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return results;
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
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteEvent(Event event)
			throws ServiceException {

			SearchResults<EventInterest> results;
			EventInterestSearchCriteria eventInterestSearchCriteria = new EventInterestSearchCriteria();

			eventInterestSearchCriteria.setEvent(event);
			try {
				results = eventInterestDAO.search(eventInterestSearchCriteria);
				for (EventInterest eventInterest : results.getData()) {

					//Create and send Email
					String mailSubject = emailProperties.getRouteDeleteSubject().replace("$1", "[Υπηρεσία εύρεσης Δράσεων]:")
							.replace("$2", "δράσης");
					StringBuilder htmlMessageBuiler = new StringBuilder();
					htmlMessageBuiler.append("<html> <body>");
					String mailBody = emailProperties.getRouteDeleteBody();
					htmlMessageBuiler.append(mailBody);
					htmlMessageBuiler.append("<br>");
					htmlMessageBuiler.append("<br>Κωδικός δράσης: " + event.getCode());
					htmlMessageBuiler.append("<br>Τίτλος δράσης: " + event.getEventTitle());
					htmlMessageBuiler.append("<br>Τόπος Διεξαγωγής: " + event.getEventLocation());
					htmlMessageBuiler.append("<br>Ημερομηνία έναρξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
							.format(event.getDateFrom()));
					htmlMessageBuiler.append("<br>Ημερομηνία λήξης: " + new SimpleDateFormat("dd/MM/yyyy, HH:mm")
							.format(event.getDateTo()));
					htmlMessageBuiler.append("</body> </html>");
					mailManager
							.sendMail(emailProperties.getFromAddress(), eventInterest.getUser().getEmail(), mailSubject,
									htmlMessageBuiler.toString());
				}

			eventDAO.delete(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createUserAction(Event event)
			throws ServiceException {

		//create and save user action trace
		UserAction userAction = new UserAction();
		userAction.setDate(new Date());
		userAction.setUser(event.getUser());
		userAction.setEventTitle(event.getEventTitle());
		userAction.setEventCode(event.getCode());
		userAction.setEventLocation(event.getEventLocation());
		userAction.setEventCategory(event.getEventCategory().getName());
		userAction.setType(UserAction.TYPE_DELETE);
		userAction.setEventCreationDate(event.getEventCreationDate());
		userActionDAO.save(userAction);
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
}
