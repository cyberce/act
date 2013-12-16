package org.teiath.data.search;

public class NotificationsCriteriaSearchCriteria
		extends SearchCriteria {

	private Integer type;
	private String title;
	private Integer notificationsNumber;

	public NotificationsCriteriaSearchCriteria() {
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getNotificationsNumber() {
		return notificationsNumber;
	}

	public void setNotificationsNumber(Integer notificationsNumber) {
		this.notificationsNumber = notificationsNumber;
	}
}
