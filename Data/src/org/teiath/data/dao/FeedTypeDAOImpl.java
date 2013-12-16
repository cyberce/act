package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.aggregator.FeedType;

import java.util.Collection;

@Repository("feedTypeDAO")
public class FeedTypeDAOImpl
		implements FeedTypeDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<FeedType> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<FeedType> feedTypes;

		String hql = "from FeedType";
		Query query = session.createQuery(hql);

		feedTypes = query.list();

		return feedTypes;
	}
}
