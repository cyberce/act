package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.pdf.EventPDF;

import java.util.Collection;

@Repository("eventPDFDAO")
public class EventPDFDAOImpl
		implements EventPDFDAO {

	@Autowired
	private SessionFactory sessionFactory;

/*	@Override
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
	}                        */

	@Override
	public void save(EventPDF eventPDF) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(eventPDF);
	}

	@Override
	public Collection<EventPDF> findByEvent(Event event) {

		Session session = sessionFactory.getCurrentSession();
		Collection<EventPDF> pdfs;
		Criteria criteria = session.createCriteria(EventPDF.class);

		criteria.add(Restrictions.eq("event.id", event.getId()));
		pdfs = criteria.list();

		return pdfs;
	}

	@Override
	public void deleteAll(Event event) {
		Session session = sessionFactory.getCurrentSession();

		String hql = "delete from EventPDF as eventPDF " + "where eventPDF.event.id=:eventId";
		Query query = session.createQuery(hql);
		query.setParameter("eventId", event.getId());

		query.executeUpdate();
	}

/*	@Override
	public void delete(ApplicationImage applicationImage) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(applicationImage);
	}      */
}
