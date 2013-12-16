package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.ViewUserService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.image.AImage;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class ViewUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewUserVM.class.getName());

	@Wire("#userViewrWin")
	private Window win;
	@Wire("#userPhoto")
	private Image userPhoto;
	@Wire("#genderLabel")
	private Label genderLabel;
	@Wire("#typeLabel")
	private Label typeLabel;

	@WireVariable
	private ViewUserService viewUserService;

	private User user;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			user = viewUserService.getUserById(loggedUser.getId());
			if (user.getApplicationImage() == null) {
				userPhoto.setSrc("/img/default-avatar.png");
			} else {
				AImage aImage = new AImage("", user.getApplicationImage().getImageBytes());
				userPhoto.setContent(aImage);
			}

			if (user.getGender() != null) {
				if (user.getGender() == User.GENDER_MALE) {
					genderLabel.setValue(Labels.getLabel("user.male"));
				} else {
					genderLabel.setValue(Labels.getLabel("user.female"));
				}
			}

			if (user.getUserType() == User.USER_TYPE_EXTERNAL) {
				typeLabel.setValue(Labels.getLabel("user.external"));
			} else if (user.getUserType() == User.USER_TYPE_STUDENT) {
				typeLabel.setValue(Labels.getLabel("user.student"));
			} else if (user.getUserType() == User.USER_TYPE_PROFESSOR) {
				typeLabel.setValue(Labels.getLabel("user.professor"));
			} else if (user.getUserType() == User.USER_TYPE_ADMINISTRATION_CLERK) {
				typeLabel.setValue(Labels.getLabel("user.administrationClerk"));
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		} catch (IOException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.profile")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onUpdate() {
		ZKSession.sendRedirect(PageURL.USER_EDIT);
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
}
