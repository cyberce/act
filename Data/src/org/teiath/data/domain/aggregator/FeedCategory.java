package org.teiath.data.domain.aggregator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "aggr_feed_categories")
public class FeedCategory {

	@Id
	@Column(name = "aggr_categ_id")
	@SequenceGenerator(name = "aggr_feed_categories_seq", sequenceName = "aggr_feed_categories_categ_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aggr_feed_categories_seq")
	private Integer id;

	@Column(name = "aggr_categ_title", length = 100, nullable = false)
	private String title;

	public FeedCategory() {
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

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == FeedCategory.class && this.id
				.equals(((FeedCategory) obj).getId());
	}
}
