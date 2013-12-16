package org.teiath.data.search;

import org.teiath.data.domain.User;
import org.teiath.data.domain.act.EventCategory;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class EventSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private String eventTitle;
	private Set<EventCategory> eventCategories;
	private String eventCategoryName;
	private String eventKeyword;
	private String location;
	private Date dateFrom;
	private Date dateTo;
	private User creator;
	private Boolean disabledAccess;
	private Integer eventStatus;
	private String code;
	private String eventDescription;

	public EventSearchCriteria() {
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

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public Set<EventCategory> getEventCategories() {
		return eventCategories;
	}

	public void setEventCategories(Set<EventCategory> eventCategories) {
		this.eventCategories = eventCategories;
	}

	public String getEventKeyword() {
		return eventKeyword;
	}

	public void setEventKeyword(String eventKeyword) {
		this.eventKeyword = eventKeyword;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Boolean getDisabledAccess() {
		return disabledAccess;
	}

	public void setDisabledAccess(Boolean disabledAccess) {
		this.disabledAccess = disabledAccess;
	}

	public Integer getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(Integer eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getEventCategoryName() {
		return eventCategoryName;
	}

	public void setEventCategoryName(String eventCategoryName) {
		this.eventCategoryName = eventCategoryName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}
}
