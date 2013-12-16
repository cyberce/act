package org.teiath.web.vm;

import org.teiath.service.exceptions.ResetExpirationException;
import org.teiath.service.exceptions.ResetMatchException;
import org.teiath.service.user.ResetPasswordService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.validation.Validation;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Vbox;

public class ResetPasswdVM {

	@WireVariable
	ResetPasswordService resetPasswordService;
	@Wire("#form")
	Vbox form;
	@Wire("#msg")
	Label msg;
	@Wire("#passwordLabel")
	Label passwordLabel;
	@Wire("#confirmPasswordLabel")
	Label confirmPasswordLabel;

	String username;
	String passwd;
	String confirmpasswd;
	String token;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		this.username = Executions.getCurrent().getParameter("id");
		this.token = Executions.getCurrent().getParameter("token");

		try {
			resetPasswordService.validate(username, token);
		} catch (ResetExpirationException e) {
			form.setVisible(false);
			msg.setValue(e.getMessage());
			msg.setVisible(true);
		} catch (ResetMatchException e) {
			form.setVisible(false);
			msg.setValue(e.getMessage());
			msg.setVisible(true);
		}
	}

	@Command
	public void onReset() {
		if ((passwd != null) && (confirmpasswd != null)) {
			if ((Validation.validatePassword(passwd))) {
				if (passwd.equals(confirmpasswd)) {
					try {
						resetPasswordService.reset(username, token, passwd);
						Messagebox.show(Labels.getLabel("user.passwordResetSuccess"),
								Labels.getLabel("user.message.success"), Messagebox.OK, Messagebox.INFORMATION,
								new EventListener<Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.INDEX);
									}
								});
					} catch (ResetExpirationException e) {
						e.printStackTrace();
					} catch (ResetMatchException e) {
						e.printStackTrace();
					}
				} else {
					passwordLabel.setValue(null);
					confirmPasswordLabel.setValue(Labels.getLabel("user.passwordsDontMatch"));
				}
			} else {
				passwordLabel.setValue(Labels.getLabel("validation.common.invalid_password"));
				confirmPasswordLabel.setValue(null);
			}
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.INDEX);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getConfirmpasswd() {
		return confirmpasswd;
	}

	public void setConfirmpasswd(String confirmpasswd) {
		this.confirmpasswd = confirmpasswd;
	}
}
