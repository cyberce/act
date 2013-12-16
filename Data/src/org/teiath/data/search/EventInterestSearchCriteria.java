package org.teiath.data.search;

import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class EventInterestSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private Event event;
	private String eventKeyword;
	private Date dateFrom;
	private Date dateTo;
	private User participant;
	private String eventCode;
	private Integer status;
	private Set<EventCategory> eventCategories;

	public EventInterestSearchCriteria() {
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public String getEventKeyword() {
		return eventKeyword;
	}

	public void setEventKeyword(String eventKeyword) {
		this.eventKeyword = eventKeyword;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public User getParticipant() {
		return participant;
	}

	public void setParticipant(User participant) {
		this.participant = participant;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Set<EventCategory> getEventCategories() {
		return eventCategories;
	}

	public void setEventCategories(Set<EventCategory> eventCategories) {
		this.eventCategories = eventCategories;
	}
}
