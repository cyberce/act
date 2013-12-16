package org.teiath.data.domain.act;

import org.teiath.data.domain.Assessment;

import javax.persistence.*;

@Entity
@Table(name = "act_action_assessments")
@PrimaryKeyJoinColumn(name = "assessment_id")
public class ActionAssessment
		extends Assessment {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id", nullable = false)
	private Event assessedEvent;

	public ActionAssessment() {
	}

	public Event getAssessedEvent() {
		return assessedEvent;
	}

	public void setAssessedEvent(Event assessedEvent) {
		this.assessedEvent = assessedEvent;
	}
}
