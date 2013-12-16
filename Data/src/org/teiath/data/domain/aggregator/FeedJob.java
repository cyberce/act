package org.teiath.data.domain.aggregator;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "aggr_feed_jobs")
public class FeedJob {

	@Id
	@Column(name = "aggr_job_id")
	@SequenceGenerator(name = "aggr_feed_jobs_seq", sequenceName = "aggr_feed_jobs_job_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggr_feed_jobs_seq")
	private Integer id;

	@Column(name = "aggr_job_date", nullable = false)
	private Timestamp date;

	@ManyToMany(mappedBy = "feedJobs")
	private Set<Feed> feeds = new HashSet<>();

	public FeedJob() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Set<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(Set<Feed> feeds) {
		this.feeds = feeds;
	}
}
