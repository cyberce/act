package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.exceptions.ServiceException;

import java.util.Collection;

@Repository("feedCategoryDAO")
public class FeedCategoryDAOImpl
		implements FeedCategoryDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<FeedCategory> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<FeedCategory> feedCategories;

		String hql = "from FeedCategory";
		Query query = session.createQuery(hql);

		feedCategories = query.list();

		return feedCategories;
	}


	@Override
	public void save(FeedCategory feedCategory) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(feedCategory);
	}


	@Override
	public void delete(FeedCategory feedCategory)
			throws ServiceException {
		Session session = sessionFactory.getCurrentSession();
		session.delete(feedCategory);
		session.flush();
	}

	@Override
	public FeedCategory findById(Integer id) {
		FeedCategory feedCategory;

		Session session = sessionFactory.getCurrentSession();
		feedCategory = (FeedCategory) session.get(FeedCategory.class, id);

		return feedCategory;
	}

}
