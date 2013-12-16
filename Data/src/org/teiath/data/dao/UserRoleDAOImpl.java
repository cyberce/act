package org.teiath.data.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.UserRole;

import java.util.Collection;

@Repository("userRoleDAO")
public class UserRoleDAOImpl
		implements UserRoleDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public UserRole findById(Integer id) {
		UserRole userRole;

		Session session = sessionFactory.getCurrentSession();
		userRole = (UserRole) session.get(UserRole.class, id);

		return userRole;
	}

	@Override
	public Collection<UserRole> findAll() {
		Session session = sessionFactory.getCurrentSession();
		Collection<UserRole> userRoles;

		String hql = "from UserRole";
		Query query = session.createQuery(hql);

		userRoles = query.list();

		return userRoles;
	}

	@Override
	public UserRole findDefaultUserRole(Integer code) {
		Session session = sessionFactory.getCurrentSession();
		UserRole defaultRole;

		String hql = "from UserRole userRole where userRole.code = :code";
		Query query = session.createQuery(hql).setParameter("code", code);

		defaultRole = (UserRole) query.uniqueResult();

		return defaultRole;
	}
}
