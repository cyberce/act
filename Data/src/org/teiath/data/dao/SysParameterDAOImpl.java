package org.teiath.data.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.sys.SysParameter;

@Repository("sysParameterDAO")
public class SysParameterDAOImpl implements SysParameterDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public SysParameter findById(Integer id) {
		Session session = sessionFactory.getCurrentSession();

		return (SysParameter) session.get(SysParameter.class, id);
	}

	@Override
	public void save(SysParameter sysParameter) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(sysParameter);
	}
}
