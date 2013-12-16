package org.teiath.data.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Repository;
import org.teiath.data.domain.User;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserSearchCriteria;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Collection;
import java.util.List;

@Repository("userDAO")
public class UserDAOImpl
		implements UserDAO {

	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	LdapTemplate ldapTemplate;

	@Override
	public User findById(Integer id) {
		User user;

		Session session = sessionFactory.getCurrentSession();
		user = (User) session.get(User.class, id);

		return user;
	}

	@Override
	public Collection<User> findDriversByPassenger(User user) {
		Session session = sessionFactory.getCurrentSession();
		Collection<User> users;
		String hql = "select distinct user " +
				"from User as user, RouteInterest as routeInterest, Route as route " +
				"where user.id = route.user.id " +
				"and route.id = routeInterest.route.id " +
				"and routeInterest.user.id=:passengerId";

		Query query = session.createQuery(hql);
		query.setParameter("passengerId", user.getId());

		users = query.list();

		return users;
	}

	@Override
	public User findByUsername(String username) {
		User user;

		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);

		criteria.add(Restrictions.eq("userName", username));
		user = (User) criteria.uniqueResult();

		return user;
	}

	@Override
	public User findByEmail(String email) {
		User user;

		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);

		criteria.add(Restrictions.eq("email", email));
		user = (User) criteria.uniqueResult();

		return user;
	}

	@Override
	public void save(User user) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(user);
	}

	@Override
	public User authorize(String username, String password) {
		User user;

		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);

		criteria.add(Restrictions.eq("userName", username));
		criteria.add(Restrictions.eq("password", password));
		criteria.add(Restrictions.eq("banned", false));

		user = (User) criteria.uniqueResult();

		return user;
	}

	@Override
	public SearchResults<User> search(UserSearchCriteria userSearchCriteria) {
		Session session = sessionFactory.getCurrentSession();
		SearchResults<User> results = new SearchResults<>();
		Criteria criteria = session.createCriteria(User.class);

		//User Keyword
		if (userSearchCriteria.getUserKeyword() != null) {
			criteria.add(Restrictions.or(Restrictions.like("lastName", userSearchCriteria.getUserKeyword()),
					Restrictions.like("firstName", userSearchCriteria.getUserKeyword())));
		}

		//UserName
		if (userSearchCriteria.getUserName() != null) {
			criteria.add(Restrictions.like("userName", userSearchCriteria.getUserName()));
		}

		//Date restriction
		if ((userSearchCriteria.getDateFrom() != null) && (userSearchCriteria.getDateTo() != null)) {
			criteria.add(Restrictions.ge("registrationDate", userSearchCriteria.getDateFrom()));
			criteria.add(Restrictions.le("registrationDate", userSearchCriteria.getDateTo()));
		}

		//Type
		if (userSearchCriteria.getUser() != null) {
			criteria.add(Restrictions.eq("userType", userSearchCriteria.getUserType()));
		}

		//Total records
		results.setTotalRecords(criteria.list().size());

		//Paging
		criteria.setFirstResult(userSearchCriteria.getPageNumber() * userSearchCriteria.getPageSize());
		criteria.setMaxResults(userSearchCriteria.getPageSize());

		//Sorting
		if (userSearchCriteria.getOrderField() != null) {
			if (userSearchCriteria.getOrderDirection().equals("ascending")) {
				criteria.addOrder(Order.asc(userSearchCriteria.getOrderField()));
			} else {
				criteria.addOrder(Order.desc(userSearchCriteria.getOrderField()));
			}
		}

		//Fetch data
		results.setData(criteria.list());

		return results;
	}

	@Override
	public User checkPassword(String oldPassword) {
		User user;

		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);

		criteria.add(Restrictions.eq("password", oldPassword));
		user = (User) criteria.uniqueResult();

		return user;
	}

	public User authorizeUserLDAP(String username, String password) {
		User user = null;

		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("uid", username))
				.and(new EqualsFilter("status", "active"));

		boolean authenticated = ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password);
		if (authenticated) {
			filter = new AndFilter();
			filter.and(new EqualsFilter("objectclass", "person"));
			filter.and(new EqualsFilter("uid", username));
			filter.and(new EqualsFilter("status", "active"));
			List<User> l = ldapTemplate
					.search(DistinguishedName.EMPTY_PATH, filter.encode(), new UserAttributesMapper());
			if (! l.isEmpty()) {
				user = l.get(0);
			}
		}

		return user;
	}

	public User findByLDAPUsername(String username) {
		User user = null;

		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass", "person"));
		filter.and(new EqualsFilter("uid", username));
		List<User> l = ldapTemplate.search(DistinguishedName.EMPTY_PATH, filter.encode(), new UserAttributesMapper());
		if (! l.isEmpty()) {
			user = l.get(0);
		}

		return user;
	}

	private static class UserAttributesMapper
			implements AttributesMapper {

		public User mapFromAttributes(Attributes attrs)
				throws NamingException {
			User user = new User();
			user.setUserName((String) attrs.get("uid").get());
			user.setFirstName((String) attrs.get("givenname").get());
			user.setLastName((String) attrs.get("sn").get());
			user.setEmail((String) attrs.get("mail").get());
			switch ((String) attrs.get("eduPersonAffiliation").get()) {
				case "faculty":
					user.setUserType(User.USER_TYPE_PROFESSOR);
					break;
				case "student":
					user.setUserType(User.USER_TYPE_STUDENT);
					break;
				case "alum":
					user.setUserType(User.USER_TYPE_GRADUATE);
					break;
				case "staff":
					user.setUserType(User.USER_TYPE_STAFF);
					break;
				case "employee":
					user.setUserType(User.USER_TYPE_ADMINISTRATION_CLERK);
					break;
				case "affiliate":
					user.setUserType(User.USER_TYPE_AFFILIATE);
					break;
				default:
					user.setUserType(User.USER_TYPE_EXTERNAL);
					break;
			}
			return user;
		}
	}
}
