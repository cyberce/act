package org.teiath.service.act;

import org.teiath.data.domain.act.ActionAssessment;
import org.teiath.data.domain.act.Event;
import org.teiath.service.exceptions.ServiceException;

public interface CreateActionAssessmentService {

	public void saveAssessment(ActionAssessment actionAssessment)
			throws ServiceException;

	public ActionAssessment getActionAssessment(Event event)
			throws ServiceException;
}
