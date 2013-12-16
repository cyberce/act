package org.teiath.data.dao;

import org.teiath.data.domain.User;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserSearchCriteria;

import java.util.Collection;

public interface UserDAO {

	public User findById(Integer id);

	public Collection<User> findDriversByPassenger(User user);

	public User findByUsername(String username);

	public User findByEmail(String email);

	public void save(User user);

	public User authorize(String username, String password);

	public User authorizeUserLDAP(String username, String password);

	public User findByLDAPUsername(String username);

	public SearchResults<User> search(UserSearchCriteria criteria);

	public User checkPassword(String oldPassword);
}
