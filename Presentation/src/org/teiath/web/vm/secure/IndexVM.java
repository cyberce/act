package org.teiath.web.vm.secure;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import javax.servlet.ServletRequest;
import java.io.UnsupportedEncodingException;

public class IndexVM {

	@WireVariable
	UserLoginService userLoginService;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		String username = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("uid").toString();
		String firstname = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("givenName").toString();
		String lastname = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("sn").toString();
		String email = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("mail").toString();
		String userType = ((ServletRequest) Executions.getCurrent().getNativeRequest()).getAttribute("unscoped-affiliation").toString();

		String firstnameUTF8;
		String lastnameUTF8;
		try {
			firstnameUTF8 = new String(firstname.getBytes("ISO-8859-1"), "UTF-8");
			lastnameUTF8 = new String(lastname.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			firstnameUTF8 = "";
			lastnameUTF8 = "";

		}

		User user = new User();
		user.setUserName(username);
		user.setFirstName(firstnameUTF8);
		user.setLastName(lastnameUTF8);
		user.setEmail(email);
		switch (userType) {
			case "faculty":
				user.setUserType(User.USER_TYPE_PROFESSOR);
				break;
			case "student":
				user.setUserType(User.USER_TYPE_STUDENT);
				break;
			case "alum":
				user.setUserType(User.USER_TYPE_GRADUATE);
				break;
			case "staff":
				user.setUserType(User.USER_TYPE_STAFF);
				break;
			case "employee":
				user.setUserType(User.USER_TYPE_ADMINISTRATION_CLERK);
				break;
			case "affiliate":
				user.setUserType(User.USER_TYPE_AFFILIATE);
				break;
			default:
				user.setUserType(User.USER_TYPE_EXTERNAL);
				break;
		}

		try {
			ZKSession.setAttribute("AUTH_USER", userLoginService.ssoLogin(user));
			ZKSession.setAttribute("LDAP_USER", true);
			ZKSession.sendRedirect("/zul/act/act_actions_interestsUser_list.zul");
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}

	}

}
