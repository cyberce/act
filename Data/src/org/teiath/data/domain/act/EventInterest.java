package org.teiath.data.domain.act;

import org.teiath.data.domain.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "act_event_interests")
public class EventInterest
		implements Serializable {

	public final static int STATUS_PENDING = 0;
	public final static int STATUS_APPROVED = 1;
	public final static int STATUS_REJECTED = 2;

	@Id
	@Column(name = "evint_id")
	@SequenceGenerator(name = "event_interests_seq", sequenceName = "act_event_interests_evint_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_interests_seq")
	private Integer id;

	@Column(name = "evint_interest_date", nullable = false)
	private Date interestDate;
	@Column(name = "evint_status", nullable = false)
	private int status;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public EventInterest() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getInterestDate() {
		return interestDate;
	}

	public void setInterestDate(Date interestDate) {
		this.interestDate = interestDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == EventInterest.class && this.id
				.equals(((EventInterest) obj).getId());
	}
}
