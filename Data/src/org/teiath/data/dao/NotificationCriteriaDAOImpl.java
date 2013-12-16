package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;

@Repository("notificationCriteriaDAO")
public class NotificationCriteriaDAOImpl
		implements NotificationCriteriaDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public NotificationCriteria findById(Integer id) {
		NotificationCriteria notificationCriteria;

		Session session = sessionFactory.getCurrentSession();
		notificationCriteria = (NotificationCriteria) session.get(NotificationCriteria.class, id);

		return notificationCriteria;
	}

	@Override
	public SearchResults<NotificationCriteria> search(
			NotificationsCriteriaSearchCriteria notificationsCriteriaSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<NotificationCriteria> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(NotificationCriteria.class);

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

	@Override
	public void save(NotificationCriteria notificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(notificationCriteria);
	}

	@Override
	public void delete(NotificationCriteria notificationCriteria) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(notificationCriteria);
	}
}
