package org.teiath.data.domain.act;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "trg_user_actions")
public class UserAction {
	public final static int TYPE_CREATE = 0;
	public final static int TYPE_DELETE = 1;
	public final static int TYPE_LOGIN = 3;

	@Id
	@Column(name = "user_action_id")
	@SequenceGenerator(name = "user_actions_seq", sequenceName = "act_user_actions_user_action_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_actions_seq")
	private Integer id;

	@Column(name = "user_action_date", nullable = false)
	private Date date;
	@Column(name = "user_action_type", nullable = false)
	private int type;
	@Column(name = "event_title", length = 2000, nullable = true)
	private String eventTitle;
	@Column(name = "event_code", length = 2000, nullable = true)
	private String eventCode;
	@Column(name = "event_location", length = 2000, nullable = true)
	private String eventLocation;
	@Column(name = "event_creation_date", nullable = true)
	private Date eventCreationDate;
	@Column(name = "event_category", length = 2000, nullable = true)
	private String eventCategory;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public UserAction() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public Date getEventCreationDate() {
		return eventCreationDate;
	}

	public void setEventCreationDate(Date eventCreationDate) {
		this.eventCreationDate = eventCreationDate;
	}

	public String getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(String eventCategory) {
		this.eventCategory = eventCategory;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == UserAction.class && this.id
				.equals(((UserAction) obj).getId());
	}
}
