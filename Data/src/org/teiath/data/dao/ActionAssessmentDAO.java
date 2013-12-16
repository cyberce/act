package org.teiath.data.dao;

import org.teiath.data.domain.act.ActionAssessment;
import org.teiath.data.domain.act.Event;

public interface ActionAssessmentDAO {

	public ActionAssessment findByEvent(Event event);

	public Double getEventAverageRating(Event event);

	public void save(ActionAssessment actionAssessment);
}
