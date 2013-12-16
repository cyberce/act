package org.teiath.data.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.Assessment;

@Repository("assessmentDAO")
public class AssessmentDAOImpl
		implements AssessmentDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Assessment findById(Integer id) {
		Assessment assessment;

		Session session = sessionFactory.getCurrentSession();
		assessment = (Assessment) session.get(Assessment.class, id);

		return assessment;
	}

	@Override
	public void save(Assessment assessment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(assessment);
	}

	@Override
	public void delete(Assessment assessment) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(assessment);
	}
}
