package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ViewNotificationCriteriaService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class ViewNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewNotificationCriteriaVM.class.getName());

	@Wire("#ameaLabel")
	private Label ameaLabel;
	@Wire("#categoriesLabel")
	private Label categoriesLabel;

	@WireVariable
	private ViewNotificationCriteriaService viewNotificationCriteriaService;

	private EventNotificationCriteria notificationCriteria;

	@AfterCompose
	@NotifyChange("notificationCriteria")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		try {
			notificationCriteria = viewNotificationCriteriaService
					.getNotificationCriteriaById((Integer) ZKSession.getAttribute("notificationCriteriaId"));
			if (notificationCriteria.getDisabledAccess() != null) {
				if (notificationCriteria.getDisabledAccess()) {
					ameaLabel.setValue(Labels.getLabel("common.yes"));
				} else {
					ameaLabel.setValue(Labels.getLabel("common.no"));
				}
			} else {
				ameaLabel.setValue(Labels.getLabel("common.not_specified"));
			}

			StringBuilder sb = new StringBuilder();
			String prefix = "";
			for (EventCategory eventCategory : notificationCriteria.getEventCategories()) {
				sb.append(prefix);
				prefix = ", ";
				sb.append(eventCategory.getName());
			}
			categoriesLabel.setValue(sb.toString());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
	}

	public EventNotificationCriteria getNotificationCriteria() {
		return notificationCriteria;
	}

	public void setNotificationCriteria(EventNotificationCriteria notificationCriteria) {
		this.notificationCriteria = notificationCriteria;
	}
}
