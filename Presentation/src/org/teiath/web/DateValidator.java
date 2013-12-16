package org.teiath.web;

import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.util.resource.Labels;

import java.util.Date;

public class DateValidator
		extends AbstractValidator {

	public DateValidator() {

	}

	@Override
	public void validate(ValidationContext ctx) {
		Date dateFrom = (Date) ctx.getProperties("dateFrom")[0].getValue();
		Date dateTo = (Date) ctx.getProperties("dateTo")[0].getValue();


		if ((dateFrom == null)) {
			addInvalidMessage(ctx, "fx_dateFrom", Labels.getLabel("validation.common.emptyDates"));
		}

		if ((dateTo == null)) {
			addInvalidMessage(ctx, "fx_dateTo", Labels.getLabel("validation.common.emptyDates"));
		} else if ((dateFrom != null) && (dateTo.before(dateFrom))) {
			addInvalidMessage(ctx, "fx_dateTo", Labels.getLabel("validation.common.dateTo"));
		}
	}
}
