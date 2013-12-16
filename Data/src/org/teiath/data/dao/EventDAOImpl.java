package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.fts.FullTextSearch;
import org.teiath.data.fts.Levenshtein;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;

import java.util.*;

@Repository("eventDAO")
public class EventDAOImpl
		implements EventDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Event findById(Integer id) {
		Event event;

		Session session = sessionFactory.getCurrentSession();
		event = (Event) session.get(Event.class, id);

		return event;
	}

	@Override
	public SearchResults<Event> search(EventSearchCriteria eventSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Event> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(Event.class);
		criteria.createAlias("eventCategory", "category");

		//Status Restriction
		if ((eventSearchCriteria.getEventStatus() != null) && (eventSearchCriteria.getEventStatus() != - 1)) {
			Date currentDate = new Date();
			if (eventSearchCriteria.getEventStatus() == Event.EVENT_STATUS_PENDING) {
				criteria.add(Restrictions.gt("dateFrom", currentDate));
			} else if (eventSearchCriteria.getEventStatus() == Event.EVENT_STATUS_IN_PROGRESS) {
				criteria.add(Restrictions.le("dateFrom", currentDate));
				criteria.add(Restrictions.ge("dateTo", currentDate));
			} else if (eventSearchCriteria.getEventStatus() == Event.EVENT_STATUS_OVER) {
				criteria.add(Restrictions.lt("dateTo", currentDate));
			}
			//exclude finished events
			else if (eventSearchCriteria.getEventStatus() == 3) {
				criteria.add(Restrictions.ge("dateTo", new Date()));
			}
		}

		//DateTo
		if (eventSearchCriteria.getDateTo() != null) {
			criteria.add(Restrictions.le("dateTo", eventSearchCriteria.getDateTo()));
		}

		//DateFrom
		if (eventSearchCriteria.getDateFrom() != null) {
			criteria.add(Restrictions.ge("dateFrom", eventSearchCriteria.getDateFrom()));
		}

		//Code restriction
		if ((eventSearchCriteria.getCode() != null) && (!eventSearchCriteria.getCode().equals(""))) {
			criteria.add(Restrictions.eq("code", eventSearchCriteria.getCode()));
		}

		//Event Category Restriction
		if ((eventSearchCriteria.getEventCategories() != null) && (! eventSearchCriteria.getEventCategories()
				.isEmpty())) {
			criteria.add(Restrictions.in("eventCategory", eventSearchCriteria.getEventCategories()));
		}

		//Event Category Restriction by name
		if (eventSearchCriteria.getEventCategoryName() != null) {
			criteria.add(Restrictions.eq("category.name", eventSearchCriteria.getEventCategoryName()));
		}

		//AMEA restriction
		if (eventSearchCriteria.getDisabledAccess() != null) {
			criteria.add(Restrictions.eq("disabledAccess", eventSearchCriteria.getDisabledAccess()));
		}

		//User restriction
		if (eventSearchCriteria.getCreator() != null) {
			criteria.add(Restrictions.eq("user", eventSearchCriteria.getCreator()));
		}

		//Keyword
		if ((eventSearchCriteria.getEventKeyword() != null) && (!eventSearchCriteria.getEventKeyword().equals(""))){
			FullTextSearch fullTextSearch = new FullTextSearch();
			Collection<String> keywords = fullTextSearch.transformKeyword(eventSearchCriteria.getEventKeyword());

			if (! keywords.isEmpty()) {
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

				if (! resultset.isEmpty()) {
					criteria.add(Restrictions.in("id", resultset.toArray()));

					results.setData(criteria.list());
					Collection<String> words;
					for (Event event : results.getData()) {
						event.setEventRanking(0.0);
						for (String keyword : keywords) {
							words = fullTextSearch.transformKeyword(event.getEventTitle());
							for (String word : words) {
								event.setEventRanking(event.getEventRanking() + Levenshtein
										.computeLevenshteinDistance(keyword, word));
							}
							words = fullTextSearch.transformKeyword(event.getEventDescription());
							for (String word : words) {
								event.setEventRanking(event.getEventRanking() + Levenshtein
										.computeLevenshteinDistance(keyword, word));
							}
						}
					}

					Comparator<Event> comparator = new Comparator<Event>() {
						public int compare(Event c1, Event c2) {
							return c1.getEventRanking().compareTo(c2.getEventRanking());
						}
					};

					List<Event> list = new ArrayList<>(results.getData());
					Collections.sort(list, comparator);
					results.setData(list);
					results.setTotalRecords(list.size());
				} else {
					criteria.add(Restrictions.in("id", new Object[] {- 1})); // display nothing
				}
			}
		} else {
			//Total records
			results.setTotalRecords(criteria.list().size());

			//Paging
			criteria.setFirstResult(eventSearchCriteria.getPageNumber() * eventSearchCriteria.getPageSize());
			criteria.setMaxResults(eventSearchCriteria.getPageSize());

			//Sorting
			if (eventSearchCriteria.getOrderField() != null) {
				if (eventSearchCriteria.getOrderDirection().equals("ascending")) {
					criteria.addOrder(Order.asc(eventSearchCriteria.getOrderField()));
				} else {
					criteria.addOrder(Order.desc(eventSearchCriteria.getOrderField()));
				}
			}

			//Fetch data
			results.setData(criteria.list());

			for (Event event : results.getData()) {
				event.setEventRanking(0.0);
			}
		}

		return results;
	}

	@Override
	public void save(Event event) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(event);
	}

	@Override
	public void delete(Event event) {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
		session.delete(event);
	}

	@Override
	public Collection<Event> findOnGoingEvents(Date dateFrom, Date dateTo) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Event> events;
		Criteria criteria = session.createCriteria(Event.class);

		criteria.add(Restrictions.ge("dateFrom", dateFrom));
		criteria.add(Restrictions.le("dateTo", dateTo));
		events = criteria.list();

		return events;
	}

	@Override
	public Collection<Event> findEventsByEventCategory(EventCategory eventCategory) {
		Session session = sessionFactory.getCurrentSession();
		Collection<Event> events;
		Criteria criteria = session.createCriteria(Event.class);

		criteria.add(Restrictions.eq("eventCategory", eventCategory));
		events = criteria.list();

		return events;
	}

	@Override
	public Event findByCode(String code) {
		Event event;

		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(Event.class);

		criteria.add(Restrictions.eq("code", code));
		event = (Event) criteria.uniqueResult();

		return event;
	}

	@Override
	public Collection<Event> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<Event> events;

		String hql = "from Event";
		Query query = session.createQuery(hql);

		events = query.list();

		return events;
	}
}
