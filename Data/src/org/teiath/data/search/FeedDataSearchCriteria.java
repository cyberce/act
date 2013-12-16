package org.teiath.data.search;

import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;

import java.io.Serializable;
import java.util.Date;

public class FeedDataSearchCriteria
		extends SearchCriteria
		implements Serializable {

	private Feed feed;
	private FeedCategory feedCategory;
	private Date dateFrom;
	private Date dateTo;
	private Integer interestingFlag;

	public FeedDataSearchCriteria() {
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public FeedCategory getFeedCategory() {
		return feedCategory;
	}

	public void setFeedCategory(FeedCategory feedCategory) {
		this.feedCategory = feedCategory;
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

	public Integer getInterestingFlag() {
		return interestingFlag;
	}

	public void setInterestingFlag(Integer interestingFlag) {
		this.interestingFlag = interestingFlag;
	}
}
