package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.data.fts.FullTextSearch;
import org.teiath.data.fts.Levenshtein;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

import java.util.*;

@Repository("eventNotificationCriteriaDAO")
public class EventNotificationCriteriaDAOImpl
		implements EventNotificationCriteriaDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void save(EventNotificationCriteria eventNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(eventNotificationCriteria);
	}

	@Override
	public void delete(EventNotificationCriteria eventNotificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(eventNotificationCriteria);
	}

	@Override
	public Collection<EventNotificationCriteria> checkCriteria(Event event) {
		Session session = sessionFactory.getCurrentSession();
		Collection<EventNotificationCriteria> eventNotificationCriterias;
		Criteria criteria = session.createCriteria(EventNotificationCriteria.class);


		//Event categories restriction
		criteria.createAlias("eventCategories", "categories");
		Junction junc = Restrictions.conjunction();
		junc.add(Restrictions.eq("categories.id", event.getEventCategory().getId()));
		criteria.add(junc);

		//Date restriction
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("dateFrom"), Restrictions.isNotNull("id")),
				Restrictions.le("dateFrom", event.getDateFrom())));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("dateTo"), Restrictions.isNotNull("id")),
				Restrictions.ge("dateTo", event.getDateFrom())));

		//AMEA access restriction
/*		criteria.add(Restrictions
				.or(Restrictions.and(Restrictions.eq("disabledAccess", false), Restrictions.isNotNull("id")),
						Restrictions.eq("disabledAccess", true)));*/
		//criteria.add(Restrictions.eq("disabledAccess", event.isDisabledAccess()));
		criteria.add(Restrictions.or(Restrictions.isNull("disabledAccess"),Restrictions.eq("disabledAccess", event.isDisabledAccess()) ));

		//Keyword restriction
//		criteria.add(Restrictions.or(Restrictions.and(Restrictions.isNull("keywords"), Restrictions.isNotNull("id")),
//				Restrictions.and(Restrictions.ne("keywords", ""),
//						Restrictions.like("keywords", event.getEventTitle(), MatchMode.ANYWHERE),
//						Restrictions.like("keywords", event.getEventDescription(), MatchMode.ANYWHERE))));


		eventNotificationCriterias = criteria.list();

		Collection<EventNotificationCriteria> acceptedCriteria = new ArrayList<>();

			for (EventNotificationCriteria criter: eventNotificationCriterias) {
				if (criter.getKeywords() != null) {
					if (!criter.getKeywords().isEmpty()) {
						FullTextSearch fullTextSearch = new FullTextSearch();
						Collection<String> keywords = fullTextSearch.transformKeyword(criter.getKeywords());

						StringBuffer eventNameQuery = new StringBuffer();
						StringBuffer eventDescriptionQuery = new StringBuffer();
						int threshold;
						for (String keyword : keywords) {
							threshold = (int) Math.ceil(keyword.length() * 50 / 100); // 50% distance
							eventNameQuery
									.append("SELECT distinct link FROM indx_event_name WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
							eventNameQuery.append(" UNION ");

							eventDescriptionQuery
									.append("SELECT distinct link FROM indx_event_description WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
							eventDescriptionQuery.append(" UNION ");
						}
						eventNameQuery = eventNameQuery
								.replace(eventNameQuery.lastIndexOf(" UNION "), eventNameQuery.length(), "");
						eventNameQuery.insert(0, "(");
						eventNameQuery.append(")");

						eventDescriptionQuery = eventDescriptionQuery
								.replace(eventDescriptionQuery.lastIndexOf(" UNION "), eventDescriptionQuery.length(), "");
						eventDescriptionQuery.insert(0, "(");
						eventDescriptionQuery.append(")");

						String q = "(" + eventNameQuery.toString() + " UNION " + eventDescriptionQuery.toString() + ")";
						System.out.println(q);

						List<Integer> resultset = session.createSQLQuery(q).list();

						if (!resultset.isEmpty()) {
							if (resultset.contains(event.getId())) {
								acceptedCriteria.add(criter);
							}
						}
					}
				} else {
					acceptedCriteria.add(criter);
				}

			}

			Criteria criteria2 = session.createCriteria(EventNotificationCriteria.class);
			criteria2.add(Restrictions.isNotNull("keywords"));
			Collection<EventNotificationCriteria> eventNotificationCriterias2 = criteria2.list();
			for (EventNotificationCriteria criter: eventNotificationCriterias2) {
				if (criter.getKeywords() != null) {
					if (!criter.getKeywords().isEmpty()) {
						FullTextSearch fullTextSearch = new FullTextSearch();
						Collection<String> keywords = fullTextSearch.transformKeyword(criter.getKeywords());

						StringBuffer eventNameQuery = new StringBuffer();
						StringBuffer eventDescriptionQuery = new StringBuffer();
						int threshold;
						for (String keyword : keywords) {
							threshold = (int) Math.ceil(keyword.length() * 50 / 100); // 50% distance
							eventNameQuery
									.append("SELECT distinct link FROM indx_event_name WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
							eventNameQuery.append(" UNION ");

							eventDescriptionQuery
									.append("SELECT distinct link FROM indx_event_description WHERE levenshtein(value, '" + keyword + "') <= " + threshold + " AND substring(value, 1,3) = substring('" + keyword + "', 1,3)");
							eventDescriptionQuery.append(" UNION ");
						}
						eventNameQuery = eventNameQuery
								.replace(eventNameQuery.lastIndexOf(" UNION "), eventNameQuery.length(), "");
						eventNameQuery.insert(0, "(");
						eventNameQuery.append(")");

						eventDescriptionQuery = eventDescriptionQuery
								.replace(eventDescriptionQuery.lastIndexOf(" UNION "), eventDescriptionQuery.length(), "");
						eventDescriptionQuery.insert(0, "(");
						eventDescriptionQuery.append(")");

						String q = "(" + eventNameQuery.toString() + " UNION " + eventDescriptionQuery.toString() + ")";
						System.out.println(q);

						List<Integer> resultset = session.createSQLQuery(q).list();

						if (!resultset.isEmpty()) {
							if (resultset.contains(event.getId())) {
								boolean exists = false;
								for (EventNotificationCriteria acceptedEventNotificationCriteria : acceptedCriteria) {
									if (acceptedEventNotificationCriteria.getId() == criter.getId())
										exists = true;
								}
								if (!exists)
									acceptedCriteria.add(criter);
							}
						}
					}
				} else {
					if (!acceptedCriteria.contains(criter))
						acceptedCriteria.add(criter);
				}




		}

		return acceptedCriteria;
	}

	@Override
	public EventNotificationCriteria findById(Integer id) {
		EventNotificationCriteria eventNotificationCriteria;

		Session session = sessionFactory.getCurrentSession();
		eventNotificationCriteria = (EventNotificationCriteria) session.get(EventNotificationCriteria.class, id);

		return eventNotificationCriteria;
	}

	@Override
	public SearchResults<EventNotificationCriteria> search(
			NotificationsCriteriaSearchCriteria notificationsCriteriaSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<EventNotificationCriteria> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(EventNotificationCriteria.class);

		//Type
		if (notificationsCriteriaSearchCriteria.getType() != null) {
			criteria.add(Restrictions.eq("type", notificationsCriteriaSearchCriteria.getType()));
		}

		//User
		if (notificationsCriteriaSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("user", notificationsCriteriaSearchCriteria.getUser()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(
				notificationsCriteriaSearchCriteria.getPageNumber() * notificationsCriteriaSearchCriteria
						.getPageSize());
		criteria.setMaxResults(notificationsCriteriaSearchCriteria.getPageSize());

		//Sorting
		if (notificationsCriteriaSearchCriteria.getOrderField() != null) {
			if (notificationsCriteriaSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(notificationsCriteriaSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(notificationsCriteriaSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}
}
