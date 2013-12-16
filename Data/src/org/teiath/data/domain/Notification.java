package org.teiath.data.domain;

import org.springframework.context.annotation.DependsOn;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.zkoss.util.resource.Labels;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "crp_notifications")
public class Notification {

	public final static int TYPE_ROUTES = 0;
	public final static int TYPE_GOODS = 1;
	public final static int TYPE_ACTIONS = 2;
	public final static int TYPE_ROOMMATES = 3;

	@Id
	@Column(name = "notification_id")
	@SequenceGenerator(name = "notifications_seq", sequenceName = "crp_notifications_notification_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_seq")
	private Integer id;

	@Column(name = "notification_title", length = 2000, nullable = false)
	private String title;
	@Column(name = "notification_body", length = 2000, nullable = false)
	private String body;
	@Column(name = "notification_type", nullable = false)
	private int type;
	@Column(name = "notification_sent_date", nullable = false)
	private Date sentDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id", nullable = true)
	private Event event;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ntf_criteria_id", nullable = true)
	private EventNotificationCriteria notificationCriteria;

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

	public Notification() {
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

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public EventNotificationCriteria getNotificationCriteria() {
		return notificationCriteria;
	}

	public void setNotificationCriteria(EventNotificationCriteria notificationCriteria) {
		this.notificationCriteria = notificationCriteria;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Notification.class && this.id
				.equals(((Notification) obj).getId());
	}
}
