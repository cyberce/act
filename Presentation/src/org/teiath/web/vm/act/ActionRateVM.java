package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.ActionAssessment;
import org.teiath.data.domain.act.Event;
import org.teiath.service.act.CreateActionAssessmentService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.act.validator.ActionAssessmentValidator;
import org.zkoss.bind.Validator;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;

import java.util.Date;

//import org.zkoss.zk.ui.event.Event;

@SuppressWarnings("UnusedDeclaration")
public class ActionRateVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ActionRateVM.class.getName());

	@Wire
	private Intbox rating;

	@WireVariable
	private CreateActionAssessmentService createActionAssessmentService;

	private Event event;
	private ActionAssessment actionAssessment;
	private Validator actionAssessmentValidator;

	@AfterCompose
	@NotifyChange("event")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		actionAssessmentValidator = new ActionAssessmentValidator();
		event = (Event) ZKSession.getAttribute("event");
		actionAssessment = new ActionAssessment();

		try {
			actionAssessment = createActionAssessmentService.getActionAssessment(event);

			if (actionAssessment != null) {
				rating.setValue(actionAssessment.getRating());
				Clients.evalJavaScript("doEdit('" + actionAssessment.getRating() + "')");
			}
			else {
				actionAssessment = new ActionAssessment();
				rating.setValue(0);
				Clients.evalJavaScript("doLoad()");
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ACTION_HISTORY_LIST);
		}

		if (actionAssessment == null) {
			actionAssessment = new ActionAssessment();
		}
	}

	@Command
	public void onSave() {

		actionAssessment.setAssessmentDate(new Date());
		actionAssessment.setUser(loggedUser);
		actionAssessment.setAssessedEvent(event);
		actionAssessment.setRating(rating.getValue());
		try {
			createActionAssessmentService.saveAssessment(actionAssessment);
			Messagebox.show(Labels.getLabel("route.rating.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					ZKSession.sendRedirect(PageURL.ACTION_HISTORY_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ACTION_HISTORY_LIST);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<org.zkoss.zk.ui.event.Event>() {
			public void onEvent(org.zkoss.zk.ui.event.Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.ACTION_HISTORY_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public ActionAssessment getActionAssessment() {
		return actionAssessment;
	}

	public void setActionAssessment(ActionAssessment actionAssessment) {
		this.actionAssessment = actionAssessment;
	}

	public Validator getActionAssessmentValidator() {
		return actionAssessmentValidator;
	}

	public void setActionAssessmentValidator(Validator actionAssessmentValidator) {
		this.actionAssessmentValidator = actionAssessmentValidator;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}
}
