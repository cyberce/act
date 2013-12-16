package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.exceptions.ServiceException;

import java.util.Collection;

@Repository("eventCategotyDAO")
public class EventCategoryDAOImpl
		implements EventCategoryDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Collection<EventCategory> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<EventCategory> categories;

		String hql = "from EventCategory as eventCategory where eventCategory.id <> 1000";
		Query query = session.createQuery(hql);

		categories = query.list();

		return categories;
	}

	@Override
	public Collection<EventCategory> findByParentId(Integer id) {
		Session session = sessionFactory.getCurrentSession();
		Collection<EventCategory> categories;

		String hql = "from EventCategory as eventCategory where eventCategory.parentCategory.id=:parentId order by eventCategory.id asc ";
		Query query = session.createQuery(hql);
		query.setParameter("parentId", id);

		categories = query.list();

		return categories;
	}

	@Override
	public void save(EventCategory eventCategory) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(eventCategory);
	}

	@Override
	public EventCategory findById(Integer id) {
		EventCategory eventCategory;

		Session session = sessionFactory.getCurrentSession();
		eventCategory = (EventCategory) session.get(EventCategory.class, id);

		return eventCategory;
	}

	@Override
	public void delete(EventCategory eventCategory)
			throws ServiceException {
		Session session = sessionFactory.getCurrentSession();
		session.delete(eventCategory);
		session.flush();
	}
}
