package org.teiath.web.vm.templates;

import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

@SuppressWarnings("UnusedDeclaration")
public class MainTemplateVM {

	@Wire("#eventReportsMenu")
	private Menuitem eventReportsMenu;
	@Wire("#accommodationReportsMenu")
	private Menuitem accommodationReportsMenu;
	@Wire("#myActionsMenu")
	private Menuitem myActionsMenu;
	@Wire("#createActionMenu")
	private Menuitem createActionMenu;
	@Wire("#adminMenu")
	private Menu adminMenu;
	@Wire("#userLabel")
	private Label userLabel;
	@Wire("#usersMenu")
	private Menuitem usersMenu;
	@Wire("#valuesMenu")
	private Menuitem valuesMenu;
	@Wire("#feedMenu")
	private Menuitem feedMenu;
	@Wire("#feedActionsMenu")
	private Menuitem feedActionsMenu;
	@Wire("#feedCategoriesMenu")
	private Menuitem feedCategoriesMenu;
	@Wire("#systemMenu")
	private Menuitem systemMenu;
	@Wire("#userActionsMenu")
	private Menuitem userActionsMenu;
	@Wire("#notificationsMenu")
	private Menu notificationsMenu;
	@Wire("#myHistoryMenu")
	private Menu myHistoryMenu;
	@Wire("#profileMenu")
	private Menuitem profileMenu;

	private User user;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if (ZKSession.getAttribute("AUTH_USER") != null) {
			user = (User) ZKSession.getAttribute("AUTH_USER");

			for (UserRole userRole : user.getRoles()) {
				if (userRole.getCode() == User.USER_ROLE_EVENT_MANAGER) {
					adminMenu.setVisible(true);
					myActionsMenu.setVisible(true);
					eventReportsMenu.setVisible(true);
					feedActionsMenu.setVisible(true);
				}

				if (userRole.getCode() == UserRole.ROLE_ADMINISTRATOR) {
					adminMenu.setVisible(true);
					valuesMenu.setVisible(true);
					usersMenu.setVisible(true);
					feedMenu.setVisible(true);
					feedCategoriesMenu.setVisible(true);
					systemMenu.setVisible(true);
					userActionsMenu.setVisible(true);
				}
			}
		} else {
			ZKSession.invalidate();
			ZKSession.sendPureRedirect("/index.zul");
		}

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {
			userLabel.setVisible(false);
			adminMenu.setVisible(false);
		}
	}

	@Command
	public void updateClientInfo(@BindingParam("orientation") String orientation ){

		if (orientation.equals("portrait")) {
			myHistoryMenu.setVisible(false);
			notificationsMenu.setVisible(false);
			profileMenu.setVisible(false);
		}
		else if (orientation.equals("landscape")) {
			myHistoryMenu.setVisible(true);
			notificationsMenu.setVisible(true);
			profileMenu.setVisible(true);
		}

	}

	@Command
	public void onMenuSelect(
			@BindingParam("selectedMenu")
			String selectedMenu) {
		ZKSession.removeAttribute("fromCalendar");
		ZKSession.removeAttribute("feedDataId");
		ZKSession.removeAttribute("feedDataSearchCriteria");
		ZKSession.removeAttribute("fromFeed");
		ZKSession.removeAttribute("fromNotification");
		ZKSession.removeAttribute("eventSearchCriteria");
		ZKSession.removeAttribute("fromUserAction");
		ZKSession.sendRedirect("/zul" + selectedMenu + ".zul");
	}

	@Command
	public void logout() {
		if (ZKSession.getAttribute("LDAP_USER") == true) {
			Messagebox.show(Labels.getLabel("template.logout_message_ldap"), Labels.getLabel("template.logout"),
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							ZKSession.invalidate();
							ZKSession.sendPureRedirect("/index.zul");
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
		else {
			Messagebox.show(Labels.getLabel("template.logout_message"), Labels.getLabel("template.logout"),
					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							ZKSession.invalidate();
							ZKSession.sendPureRedirect("/index.zul");
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
		ZKSession.removeAttribute("LDAP_USER");
	}

	@Command
	public void presentation() {
		ZKSession.sendRedirect("/zul/aux_material/presentationMainTemplate.zul");
	}

	@Command
	public void manual() {
		ZKSession.sendRedirect("/zul/aux_material/html/userManual.htm");
	}

	@Command
	public void api() {
		ZKSession.sendRedirect("/zul/aux_material/html/api.htm");
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
}