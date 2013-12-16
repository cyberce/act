package org.teiath.web.vm.user;

import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.EditUserService;
import org.teiath.web.validation.Validation;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.select.annotation.WireVariable;

public class ResetPasswordValidator
		extends AbstractValidator {

	private EditUserService editUserService;

	public ResetPasswordValidator(EditUserService editUserService) {
		this.editUserService = editUserService;
	}

	@Override
	public void validate(ValidationContext ctx) {
		User user = new User();
		user.setOldPassword((String) ctx.getProperties("oldPassword")[0].getValue());
		user.setPassword((String) ctx.getProperties("password")[0].getValue());
		String passwordRetype = (String) ctx.getProperties("passwordVerification")[0].getValue();

		try {
			if (!editUserService.oldPasswordIsCorrect(user.getOldPassword())) {
				addInvalidMessage(ctx, "fx_oldPassword", Labels.getLabel("validation.common.passwordUnmatched"));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		if ((user.getPassword() != null) && (! Validation.validatePassword(user.getPassword()))) {
			addInvalidMessage(ctx, "fx_password", Labels.getLabel("validation.common.invalid_password"));
		}
		if ((passwordRetype != null) && (!passwordRetype.equals(user.getPassword()))) {
			addInvalidMessage(ctx, "fx_passwordRetype", Labels.getLabel("validation.common.retypeFailed"));
		}
	}
}
