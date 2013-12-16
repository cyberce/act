package org.teiath.data.domain.act;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "act_event_categories")
public class EventCategory {

	@Id
	@Column(name = "event_category_id")
	@SequenceGenerator(name = "event_categories_seq", sequenceName = "act_event_categories_event_category_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_categories_seq")
	private Integer id;

	@Column(name = "event_category_name", length = 2000, nullable = false)
	private String name;
	@Column(name = "event_category_description", length = 2000, nullable = true)
	private String description;

	@ManyToOne(cascade = {CascadeType.ALL})
	@JoinColumn(name = "parent_id")
	private EventCategory parentCategory;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "parentCategory")
	@OrderBy("id")
	private Set<EventCategory> subCategories = new HashSet<EventCategory>();

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

	public EventCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(EventCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Set<EventCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(Set<EventCategory> subCategories) {
		this.subCategories = subCategories;
	}

}
