package org.teiath.data.search;

import java.util.Date;

public class UserActionSearchCriteria
		extends SearchCriteria {

	private Date dateFrom;
	private Date dateTo;

	public UserActionSearchCriteria() {
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
}
