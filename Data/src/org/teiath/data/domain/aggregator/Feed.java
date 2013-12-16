package org.teiath.data.domain.aggregator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "aggr_feeds")
public class Feed {

	@Id
	@Column(name = "aggr_feed_id")
	@SequenceGenerator(name = "aggr_feeds_seq", sequenceName = "aggr_feeds_feed_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggr_feeds_seq")
	private Integer id;

	@Column(name = "aggr_feed_title", length = 1000, nullable = false)
	private String title;
	@Column(name = "aggr_feed_url", length = 1000, nullable = false)
	private String url;
	@Column(name = "aggr_feed_active", nullable = false)
	private Boolean active;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "aggr_feed_categ_id", nullable = false)
	private FeedCategory feedCategory;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "aggr_feed_type_id", nullable = false)
	private FeedType feedType;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "aggr_it_feeds_jobs", joinColumns = {@JoinColumn(name = "aggr_feed_id")},
			inverseJoinColumns = {@JoinColumn(name = "aggr_job_id")})
	private Set<FeedJob> feedJobs = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "feed")
	private Set<FeedData> feedData;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public FeedType getFeedType() {
		return feedType;
	}

	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}

	public Set<FeedJob> getFeedJobs() {
		return feedJobs;
	}

	public void setFeedJobs(Set<FeedJob> feedJobs) {
		this.feedJobs = feedJobs;
	}

	public Set<FeedData> getFeedData() {
		return feedData;
	}

	public void setFeedData(Set<FeedData> feedData) {
		this.feedData = feedData;
	}

	public FeedCategory getFeedCategory() {
		return feedCategory;
	}

	public void setFeedCategory(FeedCategory feedCategory) {
		this.feedCategory = feedCategory;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Feed.class && this.id
				.equals(((Feed) obj).getId());
	}
}
