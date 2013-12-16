package org.teiath.web.vm.ntf;

import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class CreateNotificationCriteriaVM
		extends BaseVM {

	@Wire("#typesCombo")
	Combobox typesCombo;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onNextStep() {
		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			ZKSession.sendRedirect(PageURL.ROUTE_NOTIFICATION_CRITERIA_CREATE);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			ZKSession.sendRedirect(PageURL.PRODUCT_NOTIFICATION_CRITERIA_CREATE);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("2")) {
			ZKSession.sendRedirect(PageURL.ACTION_NOTIFICATION_CRITERIA_CREATE);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("3")) {
			ZKSession.sendRedirect(PageURL.ACCOMMODATION_NOTIFICATION_CRITERIA_CREATE);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new EventListener<Event>() {
					public void onEvent(Event evt) {
						switch ((Integer) evt.getData()) {
							case Messagebox.YES:
								ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
							case Messagebox.NO:
								break;
						}
					}
				});
	}
}
