package org.teiath.data.domain.aggregator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "aggr_feed_data")
public class FeedData {

	public final static int NOT_INTERESTING = 0;
	public final static int INTERESTING = 1;
	public final static int NEW = 2;

	@Id
	@Column(name = "aggr_data_id")
	@SequenceGenerator(name = "aggr_feed_data_seq", sequenceName = "aggr_feed_data_data_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggr_feed_data_seq")
	private Integer id;

	@Column(name = "aggr_data_title", length = 1000, nullable = false)
	private String title;
	@Column(name = "aggr_data_description", nullable = false)
	private String description;
	@Column(name = "aggr_data_publication_date", nullable = false)
	private Timestamp publicationDate;
	@Column(name = "aggr_data_url", length = 1000, nullable = false)
	private String url;
	@Column(name = "aggr_data_status", nullable = false)
	private Integer status;
	@Column(name = "aggr_data_event_code", length = 100, nullable = true)
	private String eventCode;
	@Column(name = "aggr_data_event_id", nullable = true)
	private Integer eventId;
	@Column(name = "aggr_data_interestingFlag", nullable = false)
	private Integer interesting;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "aggr_feed_id", nullable = true)
	private Feed feed;

	public FeedData() {
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Timestamp publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public String getEventCode() {
		return eventCode;
	}

	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}

	public Integer getEventId() {
		return eventId;
	}

	public void setEventId(Integer eventId) {
		this.eventId = eventId;
	}

	public Integer getInteresting() {
		return interesting;
	}

	public void setInteresting(Integer interesting) {
		this.interesting = interesting;
	}
}
