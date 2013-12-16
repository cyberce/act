package org.teiath.data.dao;

import org.teiath.data.domain.UserRole;

import java.util.Collection;

public interface UserRoleDAO {

	public UserRole findById(Integer id);

	public Collection<UserRole> findAll();

	public UserRole findDefaultUserRole(Integer code);
}
