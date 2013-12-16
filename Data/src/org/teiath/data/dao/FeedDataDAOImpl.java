package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.FeedDataSearchCriteria;

import java.util.Date;

@Repository("feedDataDAO")
public class FeedDataDAOImpl
		implements FeedDataDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public FeedData findById(Integer id) {
		FeedData feedData;

		Session session = sessionFactory.getCurrentSession();
		feedData = (FeedData) session.get(FeedData.class, id);

		return feedData;
	}

	@Override
	public SearchResults<FeedData> search(FeedDataSearchCriteria feedDataSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<FeedData> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(FeedData.class);


		//Flag Restriction
		if ((feedDataSearchCriteria.getInterestingFlag() != null) || (feedDataSearchCriteria.getInterestingFlag() != -1)) {
			if (feedDataSearchCriteria.getInterestingFlag() == FeedData.INTERESTING) {
				criteria.add(Restrictions.eq("interesting", FeedData.INTERESTING));
			}
			else if (feedDataSearchCriteria.getInterestingFlag() == FeedData.NOT_INTERESTING) {
				criteria.add(Restrictions.eq("interesting", FeedData.NOT_INTERESTING));
			}
		}

		//Feed restriction
		if (feedDataSearchCriteria.getFeed() != null) {
			criteria.createAlias("feed", "f");
			criteria.add(Restrictions.eq("feed", feedDataSearchCriteria.getFeed()));
		}

		//Feed category restriction
		if (feedDataSearchCriteria.getFeedCategory() != null) {
			criteria.add(Restrictions.eq("f.feedCategory", feedDataSearchCriteria.getFeedCategory()));
		}

		//Date restriction
		if ((feedDataSearchCriteria.getDateFrom() != null) && (feedDataSearchCriteria.getDateTo() != null)) {
			criteria.add(Restrictions.ge("publicationDate", feedDataSearchCriteria.getDateFrom()));
			criteria.add(Restrictions.le("publicationDate", feedDataSearchCriteria.getDateTo()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(feedDataSearchCriteria.getPageNumber() * feedDataSearchCriteria.getPageSize());
		criteria.setMaxResults(feedDataSearchCriteria.getPageSize());

		//Sorting
		if (feedDataSearchCriteria.getOrderField() != null) {
			if (feedDataSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(feedDataSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(feedDataSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public void save(FeedData feedData) {
		try {
			Session session = sessionFactory.getCurrentSession();
			session.saveOrUpdate(feedData);
//			session.getTransaction().commit();
		} catch (ConstraintViolationException cve) {

		}
		catch (org.hibernate.exception.GenericJDBCException e) {

		}

	}

	@Override
	public void deleteByDate(Date dateFrom, Date dateTo) {

		Session session = sessionFactory.getCurrentSession();

		String hql = "delete from FeedData where publicationDate >= :dateFrom and publicationDate <= :dateTo";
		session.createQuery(hql).setParameter("dateFrom", dateFrom).setParameter("dateTo", dateTo).executeUpdate();
	}

	@Override
	public void deleteByFeed(Feed feed) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "delete from FeedData where feed.id = :feedId";

		session.createQuery(hql).setParameter("feedId", feed.getId()).executeUpdate();
	}
}
