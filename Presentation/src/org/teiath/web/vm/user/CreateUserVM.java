package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.data.ldap.LdapProperties;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.CreateUserService;
import org.teiath.service.user.UserValidationService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class CreateUserVM {

	static Logger log = Logger.getLogger(CreateUserVM.class.getName());

	@Wire("#genderCombo")
	private Combobox genderCombo;
	@Wire("#registerBtn")
	private Toolbarbutton registerBtn;
	@Wire("#tosCheckbox")
	private Checkbox tosCheckbox;

	@WireVariable
	private UserValidationService userValidationService;
	@WireVariable
	private CreateUserService createUserService;
	@WireVariable
	private LdapProperties ldapProperties;

	private User user;
	private Validator userValidator;
	private UserRole defaultUserRole;

	@AfterCompose
	@NotifyChange("user")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		user = new User();
		userValidator = new UserValidator(userValidationService, ldapProperties);
		try {
			defaultUserRole = createUserService.getDefaultUserRole(UserRole.ROLE_USER);
		} catch (ServiceException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	public void onSave() {
		try {
			ListModelList<UserRole> userRoles = new ListModelList<>();
			userRoles.add(defaultUserRole);
			user.setRoles(userRoles);
			user.setRegistrationDate(new Date());
			if (genderCombo.getValue().equals(Labels.getLabel("user.male"))) {
				user.setGender(User.GENDER_MALE);
			} else {
				user.setGender(User.GENDER_FEMALE);
			}
			user.setUserType(User.USER_TYPE_EXTERNAL);
			createUserService.saveUser(user);
			Messagebox.show(Labels.getLabel("user.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					ZKSession.sendRedirect(PageURL.INDEX);
				}
			});
		} catch (AuthenticationException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.register")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.register")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
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

	@Command
	public void toggleRegisterButton() {
		if (registerBtn.isDisabled()) {
			registerBtn.setDisabled(false);
		} else {
			registerBtn.setDisabled(true);
		}
	}

	@Command
	public void terms() {
		Window window = (Window) Executions.createComponents("/zul/act/terms/termsPopup.zul", null, null);
		window.doModal();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Validator getUserValidator() {
		return userValidator;
	}

	public void setUserValidator(Validator userValidator) {
		this.userValidator = userValidator;
	}
}
