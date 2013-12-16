package org.teiath.data.dao;

import org.teiath.data.domain.act.ActionAssessment;
import org.teiath.data.domain.act.Event;

public interface EventAssessmentDAO {

	public ActionAssessment findByEventId(Integer eventId);

	public Double getEventAverageRating(Event event);

	public void save(ActionAssessment actionAssessment);
}
