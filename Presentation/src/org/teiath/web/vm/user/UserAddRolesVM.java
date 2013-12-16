package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.UserRole;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserAddRolesService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class UserAddRolesVM
		extends BaseVM {

	static Logger log = Logger.getLogger(UserAddRolesVM.class.getName());

	@Wire
	private Listbox availableRolesLB;
	@Wire
	private Listbox selectedRolesLB;

	@WireVariable
	private UserAddRolesService userAddRolesService;

	private User user;
	private ListModelList<UserRole> availableRoles;
	private ListModelList<UserRole> selectedRoles;

	@AfterCompose
	@NotifyChange("user")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		availableRoles = new ListModelList<>();
		selectedRoles = new ListModelList<>();
		try {
			availableRoles.addAll(userAddRolesService.getAvailableUserRoles());
			if (ZKSession.getAttribute("userId") != null) {
				user = userAddRolesService.getUserById((Integer) ZKSession.getAttribute("userId"));
				selectedRoles.addAll(user.getRoles());
				availableRoles.removeAll(user.getRoles());
			}
		} catch (ServiceException e) {
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			log.error(e.getMessage());
		}
	}

	@Command
	public void onChooseAllBtnClick() {
		selectedRoles.addAll(availableRoles);
		availableRoles.clear();
	}

	@Command
	public void onChooseBtnClick() {
		for (UserRole selectedRole : availableRoles.getSelection()) {
			selectedRoles.add(selectedRole);
			availableRoles.remove(selectedRole);
		}
	}

	@Command
	public void onRemoveBtnClick() {
		for (UserRole selectedRole : selectedRoles.getSelection()) {
			availableRoles.add(selectedRole);
			selectedRoles.remove(selectedRole);
		}
	}

	@Command
	public void onRemoveAllBtnClick() {
		availableRoles.addAll(selectedRoles);
		selectedRoles.clear();
	}

	@Command
	public void onSave() {

		if (! selectedRoles.isEmpty()) {
			user.setRoles(selectedRoles);
			try {
				userAddRolesService.saveUser(user);
				Messagebox.show(Labels.getLabel("user.message.edit.success"), Labels.getLabel("user.users"),
						Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
					public void onEvent(Event evt) {
						ZKSession.sendRedirect(PageURL.USER_LIST);
					}
				});
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.users")),
						Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
				ZKSession.sendRedirect(PageURL.USER_LIST);
			}
		} else {
			Messagebox.show(MessageBuilder
					.buildErrorMessage(Labels.getLabel("users.noRolesError"), Labels.getLabel("user.users")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.USER_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ListModelList<UserRole> getAvailableRoles() {
		return availableRoles;
	}

	public void setAvailableRoles(ListModelList<UserRole> availableRoles) {
		this.availableRoles = availableRoles;
	}

	public ListModelList<UserRole> getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(ListModelList<UserRole> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
}
