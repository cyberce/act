package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.service.act.AuthorizeTwitterService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
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
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AuthorizeTwitterVM
		extends BaseVM {

	static Logger log = Logger.getLogger(AuthorizeTwitterVM.class.getName());

	@Wire("#twitterWin")
	private Window win;
	@WireVariable
	AuthorizeTwitterService authorizeTwitterService;

	private User user;
	private TwitterFactory twitterFactory;
	private Twitter twitter;
	private AccessToken accessToken;
	private RequestToken requestToken;
	private String pin;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		user = loggedUser;
		twitter = (Twitter) ZKSession.getAttribute("twitter");
		requestToken = (RequestToken) ZKSession.getAttribute("requestToken");
		accessToken = null;
	}

	@Command
	public void onOk() {

		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			user.setTwitterAccessToken(accessToken);
			authorizeTwitterService.saveUser(user);
			win.detach();
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.twitter")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		} catch (TwitterException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.twitter")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
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
						win.detach();
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TwitterFactory getTwitterFactory() {
		return twitterFactory;
	}

	public void setTwitterFactory(TwitterFactory twitterFactory) {
		this.twitterFactory = twitterFactory;
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}

	public RequestToken getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(RequestToken requestToken) {
		this.requestToken = requestToken;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
}
