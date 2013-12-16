package org.teiath.data.domain;

import javax.persistence.*;

@Entity
@Table(name = "cmn_user_roles")
public class UserRole {

	public final static int ROLE_PROVIDER = 0;
	public final static int ROLE_ADMINISTRATOR = 1;
	public final static int ROLE_USER = 2;

	@Id
	@Column(name = "user_role_id")
	@SequenceGenerator(name = "user_roles_seq",
			sequenceName = "cmn_user_roles_user_role_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_roles_seq")
	private Integer id;

	@Column(name = "user_role_code", nullable = false)
	private Integer code;

	@Column(name = "user_role_name", length = 2000, nullable = false)
	private String name;

	@Column(name = "user_role_description", length = 2000, nullable = false)
	private String description;

	public UserRole() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == UserRole.class && this.id
				.equals(((UserRole) obj).getId());
	}
}
