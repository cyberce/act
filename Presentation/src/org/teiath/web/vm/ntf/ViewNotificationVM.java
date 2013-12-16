package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.Notification;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ViewNotificationService;
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
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@SuppressWarnings("UnusedDeclaration")
public class ViewNotificationVM
		extends BaseVM {

	@Wire("#notificationCriteriaDetails")
	private Rows notificationCriteriaDetails;
	@Wire("#notificationCriteriaLabel")
	private Label notificationCriteriaLabel;

	static Logger log = Logger.getLogger(ViewNotificationVM.class.getName());

	@WireVariable
	private ViewNotificationService viewNotificationService;

	//private EventNotificationCriteria eventNotificationCriteria;

	private Notification notification;
	Boolean bool = null;

	@AfterCompose
	@NotifyChange("notification")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		try {
			notification = viewNotificationService
					.getNotificationById((Integer) ZKSession.getAttribute("notificationId"));

			if (notification.getNotificationCriteria() != null) {

				notificationCriteriaLabel.setVisible(true);

				if (notification.getNotificationCriteria().getTitle() != null) {

					Label titleLabel = new Label();
					titleLabel.setValue("Κριτήριο ειδοποίησης:");
					Label criterionTitle = new Label();
					criterionTitle.setValue(notification.getNotificationCriteria().getTitle());

					criterionTitle.setWidth("100px");
					titleLabel.setWidth("100px");
					Row titleRow = new Row();
					titleRow.appendChild(titleLabel);
					titleRow.appendChild(criterionTitle);
					notificationCriteriaDetails.appendChild(titleRow);

					Label descriptionLabel = new Label();
					descriptionLabel.setValue("Περιγραφή κριτηρίου:");
					Label criterionDescription = new Label();
					criterionDescription.setValue(notification.getNotificationCriteria().getDescription());
					Row descriptionRow = new Row();
					descriptionRow.appendChild(descriptionLabel);
					descriptionRow.appendChild(criterionDescription);
					notificationCriteriaDetails.appendChild(descriptionRow);

					if (notification.getNotificationCriteria().getEventCategories() != null) {
						Label eventCategoryLabel = new Label();
						eventCategoryLabel.setValue("Θεματική κατηγορία:");
						Label criterionEventCategory = new Label();
						StringBuilder sb = new StringBuilder();
						String prefix = "";
						for (EventCategory eventCategory : notification.getNotificationCriteria().getEventCategories()) {
							sb.append(prefix);
							prefix = ", ";
							sb.append(eventCategory.getName());
						}
						criterionEventCategory.setValue(sb.toString());
						Row eventCategoryRow = new Row();
						eventCategoryRow.appendChild(eventCategoryLabel);
						eventCategoryRow.appendChild(criterionEventCategory);
						notificationCriteriaDetails.appendChild(eventCategoryRow);
					}

					if ((notification.getNotificationCriteria().getDateFrom() != null) && (notification
							.getNotificationCriteria().getDateTo() != null)) {
						Label purchaseDateLabel = new Label();
						purchaseDateLabel.setValue("Διάστημα διεξαγωγής:");
						Label criterionPurchaseDatePeriod = new Label();
						DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
						String dateFrom = df.format(notification.getNotificationCriteria().getDateFrom());
						String dateTo = df.format(notification.getNotificationCriteria().getDateTo());
						criterionPurchaseDatePeriod.setValue(dateFrom + " έως " + dateTo);
						Row purchasePeriodRow = new Row();
						purchasePeriodRow.appendChild(purchaseDateLabel);
						purchasePeriodRow.appendChild(criterionPurchaseDatePeriod);
						notificationCriteriaDetails.appendChild(purchasePeriodRow);
					}

					if (notification.getNotificationCriteria().getKeywords() != null) {
						Label keywordsLabel = new Label();
						keywordsLabel.setValue("Λέξεις κλειδιά:");
						Label criterionKeywords = new Label();
						criterionKeywords.setValue(notification.getNotificationCriteria().getKeywords());
						Row keywordsRow = new Row();
						keywordsRow.appendChild(keywordsLabel);
						keywordsRow.appendChild(criterionKeywords);
						notificationCriteriaDetails.appendChild(keywordsRow);
					}

					if (notification.getNotificationCriteria().getDisabledAccess() != null) {

						Label ameaLabel = new Label();
						ameaLabel.setValue("Εξυπηρέτηση ΑμΕΑ:");
						Label criterionAmea = new Label();
						if (notification.getNotificationCriteria().getDisabledAccess()) {
							criterionAmea.setValue(Labels.getLabel("common.yes"));
						}
						Row ameaRow = new Row();
						ameaRow.appendChild(ameaLabel);
						ameaRow.appendChild(criterionAmea);
						notificationCriteriaDetails.appendChild(ameaRow);
					}
				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
	}

	@Command
	public void onTransition() {
		if ((notification.getType() == Notification.TYPE_ACTIONS) && (loggedUser.getId() == notification.getEvent()
				.getUser().getId())) {
			ZKSession.setAttribute("eventId", notification.getEvent().getId());
			ZKSession.setAttribute("fromNotification", true);
			ZKSession.sendRedirect(PageURL.ACTION_VIEW);
		} else if (notification.getType() == Notification.TYPE_ACTIONS) {
			ZKSession.setAttribute("eventId", notification.getEvent().getId());
			ZKSession.setAttribute("fromNotification", true);
			ZKSession.sendRedirect(PageURL.ACTION_VIEW_SEARCH);
		}
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
}
