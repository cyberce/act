package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.UserAction;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserActionSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.ListUserActionsService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ListUserActionsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListUserActionsVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#dateFrom")
	private Datebox dateFrom;
	@Wire("#dateTo")
	private Datebox dateTo;

	@WireVariable
	private ListUserActionsService listUserActionsService;

	private UserActionSearchCriteria userActionSearchCriteria;
	private ListModelList<UserAction> userActionsList;
	private UserAction selectedUserAction;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);
		//Initial search criteria
		userActionSearchCriteria = new UserActionSearchCriteria();
		userActionSearchCriteria.setOrderField("date");
		userActionSearchCriteria.setOrderDirection("descending");
		userActionSearchCriteria.setPageSize(paging.getPageSize());
		userActionSearchCriteria.setPageNumber(0);

		userActionsList = new ListModelList<>();

		try {
			SearchResults<UserAction> results = listUserActionsService.searchUserActionsByCriteria(
					userActionSearchCriteria);
			Collection<UserAction> userActions = results.getData();
			userActionsList.addAll(userActions);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(userActionSearchCriteria.getPageNumber());
			if (userActionsList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.actions")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		if (listheader.getId().equals("fullName"))
			userActionSearchCriteria.setOrderField("usr.lastName");
		else if (listheader.getId().equals("userName"))
			userActionSearchCriteria.setOrderField("usr.userName");
		else
			userActionSearchCriteria.setOrderField(listheader.getId());

		userActionSearchCriteria.setOrderDirection(listheader.getSortDirection());
		userActionSearchCriteria.setPageNumber(0);
		selectedUserAction = null;
		userActionsList.clear();

		try {
			SearchResults<UserAction> results = listUserActionsService.searchUserActionsByCriteria(
					userActionSearchCriteria);
			Collection<UserAction> userActions = results.getData();
			userActionsList.addAll(userActions);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(userActionSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedUserAction")
	public void onPaging() {
		if (userActionsList != null) {
			userActionSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<UserAction> results = listUserActionsService.searchUserActionsByCriteria(
						userActionSearchCriteria);
				selectedUserAction = null;
				userActionsList.clear();
				userActionsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(userActionSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedAction")
	public void onSearch() {
		selectedUserAction = null;
		userActionsList.clear();
		userActionSearchCriteria.setPageNumber(0);
		userActionSearchCriteria.setPageSize(paging.getPageSize());

		try {
			SearchResults<UserAction> results = listUserActionsService.searchUserActionsByCriteria(
					userActionSearchCriteria);
			Collection<UserAction> userActions = results.getData();
			userActionsList.addAll(userActions);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(userActionSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedUserAction", "userActionSearchCriteria"})
	public void onResetSearch() {
		dateFrom.setRawValue(null);
		dateTo.setRawValue(null);
		userActionSearchCriteria.setDateFrom(null);
		userActionSearchCriteria.setDateTo(null);
		userActionsList.clear();
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("userActionId", selectedUserAction.getId());
		ZKSession.sendRedirect(PageURL.USER_ACTION_VIEW);
	}

	public UserActionSearchCriteria getUserActionSearchCriteria() {
		return userActionSearchCriteria;
	}

	public void setUserActionSearchCriteria(UserActionSearchCriteria userActionSearchCriteria) {
		this.userActionSearchCriteria = userActionSearchCriteria;
	}

	public UserAction getSelectedUserAction() {
		return selectedUserAction;
	}

	public void setSelectedUserAction(UserAction selectedUserAction) {
		this.selectedUserAction = selectedUserAction;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public ListModelList<UserAction> getUserActionsList() {
		return userActionsList;
	}

	public void setUserActionsList(ListModelList<UserAction> userActionsList) {
		this.userActionsList = userActionsList;
	}
}
