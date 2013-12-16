package org.teiath.web.vm.user;

import org.teiath.data.domain.User;
import org.teiath.data.ldap.LdapProperties;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserValidationService;
import org.teiath.web.validation.Validation;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class UserValidator
		extends AbstractValidator {

	private UserValidationService userValidationService;
	private LdapProperties ldapProperties;

	public UserValidator(UserValidationService userValidationService, LdapProperties ldapProperties) {
		this.userValidationService = userValidationService;
		this.ldapProperties = ldapProperties;
	}

	@Override
	public void validate(ValidationContext ctx) {
		User user = new User();
		user.setEmail((String) ctx.getProperties("email")[0].getValue());
		user.setUserName((String) ctx.getProperties("userName")[0].getValue());
		user.setPassword((String) ctx.getProperties("password")[0].getValue());
		String passwordRetype = (String) ctx.getProperties("passwordVerification")[0].getValue();

		try {
			if ((user.getEmail() != null) && ((! Validation.validateEmail(user.getEmail()) || (userValidationService
					.emailExists(user.getEmail()))))) {
				addInvalidMessage(ctx, "fx_email", Labels.getLabel("validation.common.email"));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		try {
			if (userValidationService.usernameExists(user.getUserName())) {
				addInvalidMessage(ctx, "fx_username", Labels.getLabel("validation.common.username_exists"));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		if (ldapProperties.isLdapEnabled()) {
			try {
				if (userValidationService.usernameLdapExists(user.getUserName())) {
					addInvalidMessage(ctx, "fx_username", Labels.getLabel("validation.common.username_exists"));
				}
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
		if ((user.getPassword() != null) && (! Validation.validatePassword(user.getPassword()))) {
			addInvalidMessage(ctx, "fx_password", Labels.getLabel("validation.common.invalid_password"));
		}
		if (!passwordRetype.equals(user.getPassword())) {
			addInvalidMessage(ctx, "fx_passwordRetype", Labels.getLabel("validation.common.retypeFailed"));
		}
	}
}
