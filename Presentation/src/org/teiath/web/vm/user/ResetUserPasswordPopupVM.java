package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.EditUserService;
import org.teiath.service.util.PasswordService;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class ResetUserPasswordPopupVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ResetUserPasswordPopupVM.class.getName());

	@WireVariable
	private EditUserService editUserService;

	@Wire("#resetPasswordWin")
	private Window win;

	private User user;
	private ResetPasswordValidator userValidator;

	@AfterCompose
	@NotifyChange("user")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);


		user = (User) Executions.getCurrent().getArg().get("USER");
		user.setPassword(null);
		user.setOldPassword(null);
		user.setPasswordVerification(null);
		userValidator = new ResetPasswordValidator(editUserService);
	}

	@Command
	public void onSave() {
		try {
			user.setPassword(PasswordService.encrypt(user.getPassword()));
			editUserService.saveUser(user);
			Messagebox.show(Labels.getLabel("user.resetPasswordSuccessful"), Labels.getLabel("user.resetPasswordTitle"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					win.detach();
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.resetPasswordTitle")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"), Labels.getLabel("common.messages.cancel"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						win.detach();
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public ResetPasswordValidator getUserValidator() {
		return userValidator;
	}

	public void setUserValidator(ResetPasswordValidator userValidator) {
		this.userValidator = userValidator;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
