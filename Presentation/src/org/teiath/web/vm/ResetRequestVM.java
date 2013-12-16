package org.teiath.web.vm;

import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.ResetPasswordService;
import org.teiath.service.user.ResetPasswordValidationService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.zkforge.bwcaptcha.Captcha;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

public class ResetRequestVM {

	@Wire("#userNameOrEmailLabel")
	private Label userNameOrEmailLabel;
	@Wire("#captchaLabel")
	private Label captchaLabel;
	@Wire("#captcha")
	private Captcha captcha;

	@WireVariable
	ResetPasswordValidationService resetPasswordValidationService;

	@WireVariable
	ResetPasswordService resetPasswordService;

	private String value;
	private String captchaValue;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onReset() {
		if (value != null) {
			try {
				if ((resetPasswordValidationService.usernameExists(value) || (resetPasswordValidationService
						.emailExists(value)))) {
					if (captchaValue != null) {
						if (verifyCaptchaIgnoreCase(captchaValue, captcha)) {
							resetPasswordService.request(value);
							userNameOrEmailLabel.setValue(null);
							captchaLabel.setValue(null);
							Messagebox.show(Labels.getLabel("user.passwordResetMessage"),
									Labels.getLabel("user.message.success"), Messagebox.OK, Messagebox.INFORMATION,
									new EventListener<Event>() {
										public void onEvent(org.zkoss.zk.ui.event.Event evt) {
											ZKSession.sendRedirect(PageURL.INDEX);
										}
									});
						} else {
							captchaLabel.setValue(Labels.getLabel("validation.captcha"));
							userNameOrEmailLabel.setValue(null);
						}
					} else {
						captchaLabel.setValue(Labels.getLabel("validation.mandatoryField"));
						userNameOrEmailLabel.setValue(null);
					}
				} else {
					userNameOrEmailLabel.setValue(Labels.getLabel("validation.notFound"));
					captchaLabel.setValue(null);
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		} else {
			userNameOrEmailLabel.setValue(Labels.getLabel("validation.mandatoryField"));
			captchaLabel.setValue(null);
		}
	}

	@Command
	public boolean verifyCaptchaIgnoreCase(String captchaValue, Captcha captcha) {
		if (! captcha.getValue().equalsIgnoreCase(captchaValue)) {
			return false;
		} else {
			return true;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCaptchaValue() {
		return captchaValue;
	}

	public void setCaptchaValue(String captchaValue) {
		this.captchaValue = captchaValue;
	}
}
