package org.teiath.data.domain;

import org.springframework.context.annotation.DependsOn;
import org.teiath.data.domain.act.EventCategory;
import org.zkoss.util.resource.Labels;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "notifications_criteria")
@Inheritance(strategy = InheritanceType.JOINED)
public class NotificationCriteria {

	public final static int TYPE_ROUTES = 0;
	public final static int TYPE_GOODS = 1;
	public final static int TYPE_ACTIONS = 2;
	public final static int TYPE_ROOMMATES = 3;

	@Id
	@Column(name = "ntf_criteria_id")
	@SequenceGenerator(name = "ntf_criteria_seq", sequenceName = "notifications_criteria_ntf_criteria_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ntf_criteria_seq")
	private Integer id;

	@Column(name = "notification_criteria_title", length = 2000, nullable = false)
	private String title;
	@Column(name = "notification_criteria_description", length = 2000, nullable = true)
	private String description;
	@Column(name = "notification_criteria_type", nullable = false)
	private int type;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "notificationCriteria")
	private Set<Notification> notifications;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "NOTIFICATION_CRITERIA_EVENT_CATEGORIES", joinColumns = {@JoinColumn(name = "ntf_criteria_id")},
			inverseJoinColumns = {@JoinColumn(name = "event_category_id")})
	private Set<EventCategory> eventCategories;

	private String typeString;

	@DependsOn("type")
	public String getTypeString() {

		if (type == 0) {
			return Labels.getLabel("notifications.routes");
		} else if (type == 1) {
			return Labels.getLabel("notifications.goods");
		} else if (type == 2) {
			return Labels.getLabel("notifications.actions");
		} else {
			return Labels.getLabel("notifications.roommates");
		}
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public NotificationCriteria() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public int getTotalNotifications() {
		int totalNotifications = 0;

		for (Notification notification : this.notifications) {
			totalNotifications++;
		}

		return totalNotifications;
	}

	public Set<EventCategory> getEventCategories() {
		return eventCategories;
	}

	public void setEventCategories(Set<EventCategory> eventCategories) {
		this.eventCategories = eventCategories;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == NotificationCriteria.class && this.id
				.equals(((NotificationCriteria) obj).getId());
	}
}
