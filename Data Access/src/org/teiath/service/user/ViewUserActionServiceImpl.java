package org.teiath.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.EventDAO;
import org.teiath.data.dao.UserActionDAO;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.UserAction;
import org.teiath.service.exceptions.ServiceException;

@Service("viewUserActionService")
@Transactional
public class ViewUserActionServiceImpl
		implements ViewUserActionService {

	@Autowired
	UserActionDAO userActionDAO;
	@Autowired
	EventDAO eventDAO;

	@Override
	public UserAction getUserActionById(Integer userActionId)
			throws ServiceException {
		UserAction userAction;
		try {
			userAction = userActionDAO.findById(userActionId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}
		return userAction;
	}

	@Override
	public Event getEventByCode(String code)
			throws ServiceException {
		Event event;

		try {
			event = eventDAO.findByCode(code);
			if (event != null)
				event.getEventInterests().iterator();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(ServiceException.DATABASE_ERROR);
		}

		return event;
	}
}
