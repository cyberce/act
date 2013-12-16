package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;

import java.util.Collection;
import java.util.Date;

@Repository("eventInterestDAO")
public class EventInterestDAOImpl
		implements EventInterestDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public EventInterest findByUser(Event event, User user) {
		EventInterest eventInterest;

		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(EventInterest.class);

		criteria.add(Restrictions.eq("event", event));
		criteria.add(Restrictions.eq("user", user));

		eventInterest = (EventInterest) criteria.uniqueResult();

		return eventInterest;
	}

	@Override
	public void save(EventInterest eventInterest) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(eventInterest);
	}

	@Override
	public SearchResults<EventInterest> search(EventInterestSearchCriteria eventInterestSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<EventInterest> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(EventInterest.class);
		criteria.createAlias("event", "evt");

		//Participant restriction
		if (eventInterestSearchCriteria.getParticipant() != null) {
			criteria.add(Restrictions.eq("user", eventInterestSearchCriteria.getParticipant()));
		}

		//DateTo restriction
		if (eventInterestSearchCriteria.getDateTo() != null) {
			criteria.add(Restrictions.le("evt.dateTo", eventInterestSearchCriteria.getDateTo()));
		}

		//DateFrom restriction
		if (eventInterestSearchCriteria.getDateFrom() != null) {
			criteria.add(Restrictions.ge("evt.dateFrom", eventInterestSearchCriteria.getDateFrom()));
		}

		//Event Code Restriction
		if ((eventInterestSearchCriteria.getEventCode() != null) && (!eventInterestSearchCriteria.getEventCode().equals(""))) {
			criteria.add(Restrictions.eq("evt.code", eventInterestSearchCriteria.getEventCode()));
		}

		//Event Category Restriction
		if ((eventInterestSearchCriteria.getEventCategories() != null) && (! eventInterestSearchCriteria
				.getEventCategories().isEmpty())) {

			criteria.add(Restrictions.in("evt.eventCategory", eventInterestSearchCriteria.getEventCategories()));
		}

		//Keyword Restriction
		if ((eventInterestSearchCriteria.getEventKeyword() != null) && (!eventInterestSearchCriteria.getEventKeyword().equals(""))){

			criteria.add(Restrictions.or(Restrictions
					.like("evt.eventTitle", eventInterestSearchCriteria.getEventKeyword(), MatchMode.ANYWHERE),
					Restrictions.like("evt.eventDescription", eventInterestSearchCriteria.getEventKeyword(),
							MatchMode.ANYWHERE)));
		}

		//Event Restriction
		if (eventInterestSearchCriteria.getEvent() != null) {

			criteria.add(Restrictions.eq("event", eventInterestSearchCriteria.getEvent()));
		}

		//Status
		if (eventInterestSearchCriteria.getStatus() != null) {
			Date currentDate = new Date();
			if (eventInterestSearchCriteria.getStatus() == Event.EVENT_STATUS_OVER) {
				criteria.add(Restrictions.lt("evt.dateTo", currentDate));
			}
			else if (eventInterestSearchCriteria.getStatus() == Event.EVENT_STATUS_IN_PROGRESS) {
				criteria.add(Restrictions.gt("evt.dateTo", currentDate));
			}
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(
				eventInterestSearchCriteria.getPageNumber() * eventInterestSearchCriteria.getPageSize());
		criteria.setMaxResults(eventInterestSearchCriteria.getPageSize());

		//Sorting
		if (eventInterestSearchCriteria.getOrderField() != null) {
			if (eventInterestSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(eventInterestSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(eventInterestSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public Collection<EventInterest> findByEvent(Event event) {
		Session session = sessionFactory.getCurrentSession();
		Collection<EventInterest> eventInterests;
		Criteria criteria = session.createCriteria(EventInterest.class);

		criteria.add(Restrictions.eq("event", event));
		eventInterests = criteria.list();

		return eventInterests;
	}
}
