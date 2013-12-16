package org.teiath.web.vm;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

public class LoginVM {

	static Logger log = Logger.getLogger(LoginVM.class.getName());

	@Wire("#loginWin")
	private Window win;
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
	}

	@Command
	public void onLogin() {
		User user;

		try {
			user = userLoginService.login(username, password);
			if (user != null) {
				ZKSession.setAttribute("AUTH_USER", user);
				ZKSession.sendRedirect("/index.zul");
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
	public void onCancel() {
		win.detach();
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
