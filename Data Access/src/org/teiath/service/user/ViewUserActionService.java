package org.teiath.service.user;


import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.UserAction;
import org.teiath.service.exceptions.ServiceException;

public interface ViewUserActionService {

	public UserAction getUserActionById(Integer userActionId)
			throws ServiceException;

	public Event getEventByCode(String code)
			throws ServiceException;
}


