package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedJob;

@Repository("feedJobDAO")
public class FeedJobDAOImpl
		implements FeedJobDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public void save(FeedJob feedJob) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(feedJob);
	}

	@Override
	public FeedJob findLatestJob() {
		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery("from FeedJob feedJob order by feedJob.date DESC");
		query.setMaxResults(1);

		return (FeedJob) query.uniqueResult();
	}

	@Override
	public FeedJob findLatestJobByFeed(Feed feed) {
		Session session = sessionFactory.getCurrentSession();

		/*Query query = session.createQuery("from FeedJob feedJob where feedJob.feeds.id = :feedId order by feedJob.date DESC")
				.setParameter("feedId", feed.getId());*/
		Query query = session.createQuery("select feedJob from FeedJob feedJob join feedJob.feeds feeds where feeds.id = :feedId " +
				"order by feedJob.date DESC")
				.setParameter("feedId", feed.getId());
		query.setMaxResults(1);

		return (FeedJob) query.uniqueResult();

	}
}
