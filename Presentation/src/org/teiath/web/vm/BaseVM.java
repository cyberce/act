package org.teiath.web.vm;

import org.teiath.data.domain.User;
import org.teiath.web.session.ZKSession;

public class BaseVM {

	protected User loggedUser;

	public BaseVM() {
		if (ZKSession.getAttribute("AUTH_USER") != null) {
			loggedUser = (User) ZKSession.getAttribute("AUTH_USER");
		}
	}
}

