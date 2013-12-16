package org.teiath.service.act;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.ActionAssessmentDAO;
import org.teiath.data.domain.act.ActionAssessment;
import org.teiath.data.domain.act.Event;
import org.teiath.service.exceptions.ServiceException;

@Service("createActionAssessmentService")
@Transactional
public class CreateActionAssessmentServiceImpl
		implements CreateActionAssessmentService {

	@Autowired
	ActionAssessmentDAO actionAssessmentDAO;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void saveAssessment(ActionAssessment actionAssessment)
			throws ServiceException {
		try {
			actionAssessmentDAO.save(actionAssessment);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
	}

	@Override
	public ActionAssessment getActionAssessment(Event event)
			throws ServiceException {
		ActionAssessment actionAssessment;
		try {
			actionAssessment = actionAssessmentDAO.findByEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return actionAssessment;
	}

	/*	@Override
		public Route getRouteById(Integer routeId)
				throws ServiceException {
			Route route;
			try {
				route = routeDAO.findById(routeId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new ServiceException(ServiceException.DATABASE_ERROR);
			}

			return route;
		}

	 */
}
