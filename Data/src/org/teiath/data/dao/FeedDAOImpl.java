package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.aggregator.Feed;

import java.util.Collection;

@Repository("feedDAO")
public class FeedDAOImpl
		implements FeedDAO {

	@Autowired
	private SessionFactory sessionFactory;

	public Collection<Feed> findActiveFeeds() {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(Feed.class);

		criteria.add(Restrictions.eq("active", true));

		return criteria.list();
	}

	@Override
	public void save(Feed feed) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(feed);
	}

	@Override
	public Collection<Feed> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<Feed> feeds;

		String hql = "from Feed";
		Query query = session.createQuery(hql);

		feeds = query.list();

		return feeds;
	}

	@Override
	public void delete(Feed feed) {
		Session session = sessionFactory.getCurrentSession();
		session.clear();
		session.delete(feed);
	}

	@Override
	public Feed findById(Integer id) {
		Feed feed;

		Session session = sessionFactory.getCurrentSession();
		feed = (Feed) session.get(Feed.class, id);

		return feed;
	}
}
