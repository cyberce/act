package org.teiath.data.domain.act;

import org.teiath.data.domain.NotificationCriteria;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "act_event_notifications_criteria")
@PrimaryKeyJoinColumn(name = "ntf_criteria_id")
public class EventNotificationCriteria
		extends NotificationCriteria {

	@Column(name = "event_notification_criteria_dateFrom", nullable = true)
	private Date dateFrom;

	@Column(name = "event_notification_criteria_dateTo", nullable = true)
	private Date dateTo;

	@Column(name = "event_notification_criteria_disabledAccess", nullable = true)
	private Boolean disabledAccess;

	@Column(name = "event_notification_criteria_keywords", length = 2000, nullable = true)
	private String keywords;


	public EventNotificationCriteria() {
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

	public Boolean getDisabledAccess() {
		return disabledAccess;
	}

	public void setDisabledAccess(Boolean disabledAccess) {
		this.disabledAccess = disabledAccess;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
}
