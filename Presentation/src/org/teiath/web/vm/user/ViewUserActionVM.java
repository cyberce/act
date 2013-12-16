package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.UserAction;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.ViewUserActionService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Toolbarbutton;

@SuppressWarnings("UnusedDeclaration")
public class ViewUserActionVM
		extends BaseVM {

	@Wire("#transitionButton")
	private Toolbarbutton transitionButton;
	@Wire("#typeLabel")
	private Label typeLabel;
	@Wire("#actionTypeLabel")
	private Label actionTypeLabel;
	@Wire("#routesLabel")
	private Label routesLabel;
	@Wire("#routesGrid")
	private Grid routesGrid;

	static Logger log = Logger.getLogger(ViewUserActionVM.class.getName());

	@WireVariable
	private ViewUserActionService viewUserActionService;

	private UserAction userAction;
	private Event event;

	@AfterCompose
	@NotifyChange("userAction")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		try {
			userAction = viewUserActionService
					.getUserActionById((Integer) ZKSession.getAttribute("userActionId"));

			event = new Event();

			if (viewUserActionService.getEventByCode(userAction.getEventCode()) != null) {
				event = viewUserActionService.getEventByCode(userAction.getEventCode());
			}


			if (event.getCode() == null)
				transitionButton.setVisible(false);

			if (userAction.getUser().getUserType() == User.USER_TYPE_EXTERNAL) {
				typeLabel.setValue(Labels.getLabel("user.external"));
			} else if (userAction.getUser().getUserType() == User.USER_TYPE_STUDENT) {
				typeLabel.setValue(Labels.getLabel("user.student"));
			} else if (userAction.getUser().getUserType() == User.USER_TYPE_PROFESSOR) {
				typeLabel.setValue(Labels.getLabel("user.professor"));
			} else if (userAction.getUser().getUserType() == User.USER_TYPE_ADMINISTRATION_CLERK) {
				typeLabel.setValue(Labels.getLabel("user.administrationClerk"));
			}

			if (userAction.getType() == UserAction.TYPE_CREATE)
				actionTypeLabel.setValue(Labels.getLabel("user.actionCreateEvent"));
			else if (userAction.getType() == UserAction.TYPE_DELETE)
				actionTypeLabel.setValue(Labels.getLabel("user.actionEditEvent"));
			else if (userAction.getType() == UserAction.TYPE_LOGIN)  {
				actionTypeLabel.setValue(Labels.getLabel("user.actionLogin"));
				routesGrid.setVisible(false);
				routesLabel.setVisible(false);
			}

		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.USER_ACTION_LIST);
	}

	@Command
	public void onTransition() {
			ZKSession.setAttribute("eventCode", userAction.getEventCode());
			ZKSession.setAttribute("fromUserAction", true);
			ZKSession.sendRedirect(PageURL.ACTION_VIEW);
	}

	public UserAction getUserAction() {
		return userAction;
	}

	public void setUserAction(UserAction userAction) {
		this.userAction = userAction;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
}
