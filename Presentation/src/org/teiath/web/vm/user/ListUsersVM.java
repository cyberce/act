package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.UserSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.ListUsersService;
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
public class ListUsersVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListUsersVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#dateFrom")
	private Datebox dateFrom;
	@Wire("#dateTo")
	private Datebox dateTo;

	@WireVariable
	private ListUsersService listUsersService;

	private UserSearchCriteria userSearchCriteria;
	private ListModelList<User> usersList;
	private User selectedUser;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		//Initial search criteria
		userSearchCriteria = new UserSearchCriteria();
		userSearchCriteria.setPageSize(paging.getPageSize());
		userSearchCriteria.setPageNumber(0);
		userSearchCriteria.setUserType(User.USER_TYPE_EXTERNAL);
		userSearchCriteria.setOrderField("registrationDate");
		userSearchCriteria.setOrderDirection("descending");

		usersList = new ListModelList<>();

		try {
			SearchResults<User> results = listUsersService.searchUsersByCriteria(userSearchCriteria);
			Collection<User> users = results.getData();
			usersList.addAll(users);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(userSearchCriteria.getPageNumber());
			if (usersList.isEmpty()) {
				empty.setVisible(true);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		userSearchCriteria.setOrderField(listheader.getId());
		userSearchCriteria.setOrderDirection(listheader.getSortDirection());
		userSearchCriteria.setPageNumber(0);
		selectedUser = null;
		usersList.clear();

		try {
			SearchResults<User> results = listUsersService.searchUsersByCriteria(userSearchCriteria);
			Collection<User> users = results.getData();
			usersList.addAll(users);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(userSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedUser")
	public void onPaging() {
		if (usersList != null) {
			userSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<User> results = listUsersService.searchUsersByCriteria(userSearchCriteria);
				selectedUser = null;
				usersList.clear();
				usersList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(userSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedNotification")
	public void onSearch() {
		selectedUser = null;
		usersList.clear();
		userSearchCriteria.setPageNumber(0);
		userSearchCriteria.setPageSize(paging.getPageSize());

		try {
			SearchResults<User> results = listUsersService.searchUsersByCriteria(userSearchCriteria);
			Collection<User> users = results.getData();
			usersList.addAll(users);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(userSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedUser", "userSearchCriteria"})
	public void onResetSearch() {
		dateFrom.setRawValue(null);
		dateTo.setRawValue(null);
		userSearchCriteria.setUserKeyword(null);
		userSearchCriteria.setUserName(null);
		userSearchCriteria.setDateFrom(null);
		userSearchCriteria.setDateTo(null);
		usersList.clear();
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("userId", selectedUser.getId());
		ZKSession.sendRedirect(PageURL.USER_VIEW_ADMINISTRATOR);
	}

	@Command
	public void addRoles() {
		ZKSession.setAttribute("userId", selectedUser.getId());
		ZKSession.sendRedirect(PageURL.USER_ADD_ROLES);
	}

	@Command
	@NotifyChange("selectedUser")
	public void onToggleBan() {
		if (selectedUser != null) {
			Messagebox.show(! selectedUser.isBanned() ? Labels.getLabel("user.messages.inactivate_confirm") : Labels
					.getLabel("user.messages.restore_confirm"),
					! selectedUser.isBanned() ? Labels.getLabel("user.messages.inactivate_title") : Labels
							.getLabel("user.messages.restore_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							if (selectedUser != null) {
								try {
									listUsersService.toggleBan(selectedUser);
									selectedUser = null;
									onSearch();
								} catch (ServiceException e) {
									Messagebox.show(MessageBuilder
											.buildErrorMessage(e.getMessage(), Labels.getLabel("user.externals")),
											Labels.getLabel("common.messages.save_title"), Messagebox.OK,
											Messagebox.ERROR);
								}
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	public UserSearchCriteria getUserSearchCriteria() {
		return userSearchCriteria;
	}

	public void setUserSearchCriteria(UserSearchCriteria userSearchCriteria) {
		this.userSearchCriteria = userSearchCriteria;
	}

	public ListModelList<User> getUsersList() {
		return usersList;
	}

	public void setUsersList(ListModelList<User> usersList) {
		this.usersList = usersList;
	}

	public User getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(User selectedUser) {
		this.selectedUser = selectedUser;
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
}
