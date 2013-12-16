package org.teiath.data.domain;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "crp_assessments")
@Inheritance(strategy = InheritanceType.JOINED)
public class Assessment {

	@Id
	@Column(name = "assessment_id")
	@SequenceGenerator(name = "assessmentss_seq", sequenceName = "crp_assessments_assessment_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assessmentss_seq")
	private Integer id;

	@Column(name = "assessment_rating", nullable = false)
	private int rating;
	@Column(name = "assessment_comment", length = 2000, nullable = true)
	private String comment;
	@Column(name = "assessment_date", nullable = false)
	private Date assessmentDate;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	//Assessor User
	private User user;

	public Assessment() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public Date getAssessmentDate() {
		return assessmentDate;
	}

	public void setAssessmentDate(Date assessmentDate) {
		this.assessmentDate = assessmentDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Assessment.class && this.id
				.equals(((Assessment) obj).getId());
	}
}
