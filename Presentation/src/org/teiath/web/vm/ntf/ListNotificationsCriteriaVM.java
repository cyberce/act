package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.domain.User;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationsCriteriaSearchCriteria;
import org.teiath.service.act.ListNotificationsCriteriaService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ListNotificationsCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListNotificationsCriteriaVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;

	@WireVariable
	private ListNotificationsCriteriaService listNotificationsCriteriaService;

	private NotificationsCriteriaSearchCriteria notificationsCriteriaSearchCriteria;
	private ListModelList<NotificationCriteria> notificationCriteriaList;
	private NotificationCriteria selectedNotificationCriteria;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Initial search criteria
		notificationsCriteriaSearchCriteria = new NotificationsCriteriaSearchCriteria();
		notificationsCriteriaSearchCriteria.setPageSize(paging.getPageSize());
		notificationsCriteriaSearchCriteria.setPageNumber(0);
		notificationsCriteriaSearchCriteria.setUser(loggedUser);
		notificationsCriteriaSearchCriteria.setType(NotificationCriteria.TYPE_ACTIONS);

		notificationCriteriaList = new ListModelList<>();

		try {
			SearchResults<NotificationCriteria> results = listNotificationsCriteriaService
					.searchNotificationsCriteriaByCriteria(notificationsCriteriaSearchCriteria);
			Collection<NotificationCriteria> notificationCriterias = results.getData();
			notificationCriteriaList.addAll(notificationCriterias);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(notificationsCriteriaSearchCriteria.getPageNumber());
			if (notificationCriteriaList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notificationCriteria")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		notificationsCriteriaSearchCriteria.setOrderField(listheader.getId());
		notificationsCriteriaSearchCriteria.setOrderDirection(listheader.getSortDirection());
		notificationsCriteriaSearchCriteria.setPageNumber(0);
		selectedNotificationCriteria = null;
		notificationCriteriaList.clear();

		try {
			SearchResults<NotificationCriteria> results = listNotificationsCriteriaService
					.searchNotificationsCriteriaByCriteria(notificationsCriteriaSearchCriteria);
			Collection<NotificationCriteria> notificationCriterias = results.getData();
			notificationCriteriaList.addAll(notificationCriterias);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(notificationsCriteriaSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notificationCriteria")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedNotificationCriteria")
	public void onPaging() {
		if (notificationCriteriaList != null) {
			notificationsCriteriaSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<NotificationCriteria> results = listNotificationsCriteriaService
						.searchNotificationsCriteriaByCriteria(notificationsCriteriaSearchCriteria);
				selectedNotificationCriteria = null;
				notificationCriteriaList.clear();
				notificationCriteriaList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(notificationsCriteriaSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox
						.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notificationCriteria")),
								Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	public void onView() {

		ZKSession.setAttribute("notificationCriteriaId", selectedNotificationCriteria.getId());
		ZKSession.sendRedirect(PageURL.CRITERION_VIEW);
	}

	@Command
	public void onEdit() {
		ZKSession.setAttribute("notificationCriteriaId", selectedNotificationCriteria.getId());
		ZKSession.sendRedirect(PageURL.CRITERION_EDIT);
	}

	@Command
	public void onDelete() {
		if (selectedNotificationCriteria != null) {
			Messagebox.show(Labels.getLabel("notificationsCriteria.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listNotificationsCriteriaService
										.deleteNotificationCriteria(selectedNotificationCriteria);
								Messagebox.show(Labels.getLabel("notificationsCriteria.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("notificationCriteria")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	@Command
	public void onCreate() {
		ZKSession.sendRedirect(PageURL.ACTION_NOTIFICATION_CRITERIA_CREATE);
	}

	public NotificationsCriteriaSearchCriteria getNotificationsCriteriaSearchCriteria() {
		return notificationsCriteriaSearchCriteria;
	}

	public void setNotificationsCriteriaSearchCriteria(
			NotificationsCriteriaSearchCriteria notificationsCriteriaSearchCriteria) {
		this.notificationsCriteriaSearchCriteria = notificationsCriteriaSearchCriteria;
	}

	public ListModelList<NotificationCriteria> getNotificationCriteriaList() {
		return notificationCriteriaList;
	}

	public void setNotificationCriteriaList(ListModelList<NotificationCriteria> notificationCriteriaList) {
		this.notificationCriteriaList = notificationCriteriaList;
	}

	public NotificationCriteria getSelectedNotificationCriteria() {
		return selectedNotificationCriteria;
	}

	public void setSelectedNotificationCriteria(NotificationCriteria selectedNotificationCriteria) {
		this.selectedNotificationCriteria = selectedNotificationCriteria;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}
}
