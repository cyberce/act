package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.Notification;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;

@Repository("notificationDAO")
public class NotificationDAOImpl
		implements NotificationDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Notification findById(Integer id) {
		Notification notification;

		Session session = sessionFactory.getCurrentSession();
		notification = (Notification) session.get(Notification.class, id);

		return notification;
	}

	@Override
	public SearchResults<Notification> search(NotificationSearchCriteria notificationSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<Notification> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(Notification.class);

		//User
		if (notificationSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("user.id", notificationSearchCriteria.getUser().getId()));
		}

		//User code
		if (notificationSearchCriteria.getUserCode() != null) {
			criteria.createAlias("user", "usr");
			criteria.add(Restrictions.eq("usr.code", notificationSearchCriteria.getUserCode()));
		}

		//Date restriction
		if ((notificationSearchCriteria.getDateFrom() != null) && (notificationSearchCriteria.getDateTo() != null)) {
			criteria.add(Restrictions.ge("sentDate", notificationSearchCriteria.getDateFrom()));
			criteria.add(Restrictions.le("sentDate", notificationSearchCriteria.getDateTo()));
		}

		//Type
		if (notificationSearchCriteria.getType() != null) {
			criteria.add(Restrictions.eq("type", notificationSearchCriteria.getType()));
		}

		////Get distinct values
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(notificationSearchCriteria.getPageNumber() * notificationSearchCriteria.getPageSize());
		criteria.setMaxResults(notificationSearchCriteria.getPageSize());

		//Sorting
		if (notificationSearchCriteria.getOrderField() != null) {
			if (notificationSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(notificationSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(notificationSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public void save(Notification notification) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(notification);
	}

	@Override
	public void delete(Notification notification) {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
		session.delete(notification);
	}
}
