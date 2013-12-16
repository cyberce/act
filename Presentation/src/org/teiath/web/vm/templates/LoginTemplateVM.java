package org.teiath.web.vm.templates;

import org.teiath.data.domain.User;
import org.teiath.web.session.ZKSession;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

@SuppressWarnings("UnusedDeclaration")
public class LoginTemplateVM {

	@Wire("#loginMenu")
	private Menuitem loginMenu;

	private User user;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void updateClientInfo(@BindingParam("orientation") String orientation ){

		if (orientation.equals("portrait")) {
			BindUtils.postGlobalCommand(null, null, "isPortrait", null);
		}

		else if (orientation.equals("Landscape")) {
			BindUtils.postGlobalCommand(null, null, "isLandscape", null);
		}

	}

	@Command
	public void onMenuSelect(
			@BindingParam("selectedMenu")
			String selectedMenu) {
		ZKSession.sendRedirect("/zul" + selectedMenu + ".zul");
	}

	@Command
	public void loginWindow() {
		Window window = (Window) Executions.createComponents("/login.zul", null, null);
		window.doModal();
	}

	@Command
	public void logout() {
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

	@Command
	public void home() {
		ZKSession.sendRedirect("/index.zul");
	}

	@Command
	public void presentation() {
		ZKSession.sendRedirect("/zul/aux_material/presentation.zul");
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
		ZKSession.sendRedirect("/zul/act/terms/act_terms.zul");
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}