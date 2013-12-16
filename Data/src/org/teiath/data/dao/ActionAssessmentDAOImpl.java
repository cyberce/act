package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.act.ActionAssessment;
import org.teiath.data.domain.act.Event;

@Repository("actionAssessmentDAO")
public class ActionAssessmentDAOImpl
		implements ActionAssessmentDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public ActionAssessment findByEvent(Event event) {
		ActionAssessment actionAssessment;
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ActionAssessment.class);

		criteria.add(Restrictions.eq("assessedEvent", event));
		actionAssessment = (ActionAssessment) criteria.uniqueResult();

		return actionAssessment;
	}

	@Override
	public void save(ActionAssessment actionAssessment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(actionAssessment);
	}

	@Override
	public Double getEventAverageRating(Event event) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ActionAssessment.class);

		criteria.add(Restrictions.eq("assessedEvent", event));
		criteria.setProjection(Property.forName("rating").avg());
		Double avgRating = (Double) criteria.uniqueResult();

		return avgRating;
	}
}
