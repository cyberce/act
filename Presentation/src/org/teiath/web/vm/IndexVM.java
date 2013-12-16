package org.teiath.web.vm;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.East;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.West;

public class IndexVM {

	static Logger log = Logger.getLogger(IndexVM.class.getName());

	@Wire("#east")
	East east;
	@Wire("#west")
	West west;
	@Wire("#serviceLabel")
	Label serviceLabel;

	@WireVariable
	UserLoginService userLoginService;

	private String username;
	private String password;
	private String messageLabel;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if (ZKSession.getAttribute("AUTH_USER") != null) {
			east.setSize("0%");
			west.setSize("100%");
		}
	}

	@GlobalCommand
	@NotifyChange
	public void isPortrait() {
		west.setSize("0%");
		east.setSize("100%");
		serviceLabel.setSclass("headerTextMobile");
	}

	@GlobalCommand
	@NotifyChange
	public void isLandscape() {
		west.setSize("70%");
		east.setSize("20%");
		serviceLabel.setSclass("headerText");
	}

	@Command
	public void onLogin() {
		User user;

		try {
			user = userLoginService.login(username, password);
			if (user != null) {
				ZKSession.setAttribute("AUTH_USER", user);
				ZKSession.setAttribute("LDAP_USER", false);
				Object routeCode = Sessions.getCurrent().getAttribute("EVENT_CODE");
				Sessions.getCurrent().removeAttribute("EVENT_CODE");
				if (routeCode != null) {
					ZKSession.sendPureRedirect("/zul/act/act_actions_view_search.zul?ukeysession=" + ZKSession
							.getCurrentWinID() + "&code=" + routeCode.toString());
				} else {
					ZKSession.sendRedirect("/zul/act/act_actions_interestsUser_list.zul");
				}
			} else {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(Labels.getLabel("user.access.fail"), Labels.getLabel("user.access")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		} catch (AuthenticationException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.access")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.access")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	public void onRegister() {
		ZKSession.sendRedirect("/zul/user/user_create.zul");
	}

	@Command
	public void iForgot() {
		ZKSession.sendRedirect("/reset_request.zul");
	}

	@Command
	public void onSSOLogin() {
		ZKSession.sendPureRedirect("https://educult.teiath.gr/secure");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMessageLabel() {
		return messageLabel;
	}

	public void setMessageLabel(String messageLabel) {
		this.messageLabel = messageLabel;
	}
}
