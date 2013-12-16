package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserActionSearchCriteria;

@Repository("userActionDAO")
public class UserActionDAOImpl
		implements UserActionDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public UserAction findById(Integer id) {
		UserAction userAction;

		Session session = sessionFactory.getCurrentSession();
		userAction = (UserAction) session.get(UserAction.class, id);

		return userAction;
	}

	@Override
	public SearchResults<UserAction> search(UserActionSearchCriteria userActionSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<UserAction> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(UserAction.class);
		criteria.createAlias("user", "usr");

		//Date restriction
		if ((userActionSearchCriteria.getDateFrom() != null) && (userActionSearchCriteria.getDateTo() != null)) {
			criteria.add(Restrictions.ge("date", userActionSearchCriteria.getDateFrom()));
			criteria.add(Restrictions.le("date", userActionSearchCriteria.getDateTo()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(userActionSearchCriteria.getPageNumber() * userActionSearchCriteria.getPageSize());
		criteria.setMaxResults(userActionSearchCriteria.getPageSize());

		//Sorting
		if (userActionSearchCriteria.getOrderField() != null) {
			if (userActionSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(userActionSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(userActionSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public void save(UserAction userAction) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(userAction);
	}
}
