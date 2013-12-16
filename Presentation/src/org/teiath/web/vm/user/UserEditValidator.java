package org.teiath.web.vm.user;

import org.teiath.data.domain.User;
import org.teiath.web.validation.Validation;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

public class UserEditValidator
		extends AbstractValidator {

	@Override
	public void validate(ValidationContext ctx) {
		User user = new User();
		user.setEmail((String) ctx.getProperties("email")[0].getValue());

		if (! Validation.validateEmail(user.getEmail())) {
			addInvalidMessage(ctx, "fx_email", Labels.getLabel("validation.common.email"));
		}
	}
}
