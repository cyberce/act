package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.image.ApplicationImage;

import java.util.Collection;

@Repository("applicationImageDAO")
public class ApplicationImageDAOImpl
		implements ApplicationImageDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public ApplicationImage findByUser(User user) {

		Session session = sessionFactory.getCurrentSession();
		ApplicationImage image;

		String hql = "select applicationImage " +
				"from  ApplicationImage as applicationImage " +
				"where applicationImage.user.id=:userId ";
		Query query = session.createQuery(hql);
		query.setParameter("userId", user.getId());

		image = (ApplicationImage) query.uniqueResult();

		return image;
	}

	@Override
	public void save(ApplicationImage applicationImage) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(applicationImage);
	}

	@Override
	public void delete(ApplicationImage applicationImage) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(applicationImage);
	}

	@Override
	public Collection<ApplicationImage> findByEvent(Event event) {

		Session session = sessionFactory.getCurrentSession();
		Collection<ApplicationImage> images;
		Criteria criteria = session.createCriteria(ApplicationImage.class);

		criteria.add(Restrictions.eq("event.id", event.getId()));
		images = criteria.list();

		return images;
	}

	@Override
	public void deleteAll(Event event) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "delete from ApplicationImage as applicationImage " + "where applicationImage.event.id=:eventId";
		Query query = session.createQuery(hql);
		query.setParameter("eventId", event.getId());

		query.executeUpdate();
	}
}
