package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.act.ViewActionSearchService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.IOException;
import java.util.Date;

public class ViewActionSearchVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewActionSearchVM.class.getName());

	@Autowired
	private EmailProperties emailProperties;

	@Wire
	private Label markerData;
	@Wire("#ameaLabel")
	private Label ameaLabel;
	@Wire("#inquiryButton")
	private Toolbarbutton inquiryButton;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;

	@WireVariable
	private ViewActionSearchService viewActionSearchService;

	private Event event;
	private EventInterest eventInterest;
	private ListModelList<ApplicationImage> images;
	private ListModelList<EventPDF> uploadedPDFs;
	private EventPDF selectedPDF;

	@AfterCompose
	@NotifyChange("event")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		//Clients.evalJavaScript("renderWidget()");

		event = new Event();

		try {
			if (Executions.getCurrent().getParameter("code") != null) {
				event = viewActionSearchService.getEventByCode(Executions.getCurrent().getParameter("code"));
			} else {
				event = viewActionSearchService.getEventById((Integer) ZKSession.getAttribute("eventId"));
			}
			if (loggedUser == null) {
				inquiryButton.setVisible(false);
			}

			if (event.getEventMainImage() != null) {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setContent(event.getEventMainImage().getImage());
				imageComponent.addEventListener(Events.ON_MOUSE_OVER, new EventListener<org.zkoss.zk.ui.event.Event>() {
					@Override
					public void onEvent(org.zkoss.zk.ui.event.Event event)
							throws Exception {
						((Image) event.getTarget()).setStyle("cursor: pointer");
					}
				});
				imageComponent.addEventListener(Events.ON_CLICK, new EventListener<org.zkoss.zk.ui.event.Event>() {
					@Override
					public void onEvent(org.zkoss.zk.ui.event.Event event)
							throws Exception {

						ZKSession.setAttribute("image", event.getTarget());
						Window window = (Window) Executions.createComponents("/zul/act/image_view.zul", null, null);
						window.doModal();
					}
				});
				photoVBox.appendChild(imageComponent);
			} else {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}

			if (images == null) {
				images = new ListModelList<>();
				try {
					images.addAll(viewActionSearchService.getImages(event));
					if (! images.isEmpty()) {
						Image image;
						Div div;
						for (ApplicationImage aimage : images) {
							div = new Div();
							div.setStyle("width: 100%; text-align:center;");
							image = new Image();
							image.setWidth("80px");
							image.setHeight("80px");

							image.setContent(aimage.getImage());
							image.addEventListener(Events.ON_MOUSE_OVER,
									new EventListener<org.zkoss.zk.ui.event.Event>() {
										@Override
										public void onEvent(org.zkoss.zk.ui.event.Event event)
												throws Exception {
											((Image) event.getTarget()).setStyle("cursor: pointer");
										}
									});
							image.addEventListener(Events.ON_CLICK, new EventListener<org.zkoss.zk.ui.event.Event>() {
								@Override
								public void onEvent(org.zkoss.zk.ui.event.Event event)
										throws Exception {

									ZKSession.setAttribute("image", event.getTarget());
									Window window = (Window) Executions
											.createComponents("/zul/act/image_view.zul", null, null);
									window.doModal();
								}
							});
							div.appendChild(image);
							imageLibrary.appendChild(div);
						}
					}
				} catch (ServiceException e) {
					Messagebox.show(MessageBuilder
							.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
					log.error(e.getMessage());
				}
			}

			if (event.isDisabledAccess()) {
				ameaLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				ameaLabel.setValue(Labels.getLabel("common.no"));
			}

			if (event.getDateTo().before(new Date()))
				inquiryButton.setVisible(false);

			eventInterest = viewActionSearchService.getEventInterestByUser(event, loggedUser);

			Clients.evalJavaScript("loadData('" + event.getEventAddress() + "', true)");
			Clients.evalJavaScript("mapme('" + event.getEventAddress() + "')");
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("fromCalendar") != null) {
			ZKSession.removeAttribute("fromCalendar");
			ZKSession.sendRedirect(PageURL.ACTION_CALENDAR);
		} else if (ZKSession.getAttribute("fromFeed") != null) {
			ZKSession.removeAttribute("fromFeed");
			ZKSession.sendRedirect(PageURL.FEED_DATA_LIST);
		} else if (ZKSession.getAttribute("fromNotification") != null) {
			ZKSession.removeAttribute("fromNotification");
			ZKSession.sendRedirect(PageURL.NOTIFICATIONS_LIST);
		} else {
			ZKSession.sendRedirect(PageURL.SEARCH_ACTION);
		}
	}

	@Command
	public void tweet() {
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey("q618nZCQHse9t3Lpwbudw")
					.setOAuthConsumerSecret("MN1gZeGxBf3mlBywDq9BdkCtj4fXmHi1xQDW3d4AsnU");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();

			if (loggedUser.getTwitterAccessToken() != null) {
				//TODO tweet content(140 chars max)
				String tweet = emailProperties.getDomain() + "previewEvent?code=" + event.getCode();
				try {
					twitter.setOAuthAccessToken(loggedUser.getTwitterAccessToken());
					Status status = twitter.updateStatus(tweet);
					Messagebox.show(MessageBuilder
							.buildErrorMessage("Το μήνυμα δημοσιεύτηκε επιτυχώς στο προφίλ σας", "Twitter"),
							"Social Networks", Messagebox.OK, Messagebox.INFORMATION);
				} catch (TwitterException e) {
					Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("user.twitter")),
							Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
					log.error(e.getMessage());
				}
			} else {
				try {
					ZKSession.setAttribute("twitter", twitter);
					RequestToken requestToken = twitter.getOAuthRequestToken();
					ZKSession.setAttribute("requestToken", requestToken);
					Executions.getCurrent().sendRedirect(requestToken.getAuthorizationURL(), "_blank");
					Window window = (Window) Executions.createComponents("/zul/act/authorize_twitter.zul", null, null);
					window.doModal();
				} catch (TwitterException te) {
					log.error(te.getMessage());
				}
			}
	}

	@Command
	public void onInquiry() {

		Messagebox.show(Labels.getLabel("action.message.inquiryQuestion"), Labels.getLabel("action"),
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener<org.zkoss.zk.ui.event.Event>() {
			public void onEvent(org.zkoss.zk.ui.event.Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						eventInterest = new EventInterest();
						eventInterest.setInterestDate(new Date());
						eventInterest.setUser(loggedUser);
						eventInterest.setEvent(event);
						try {
							viewActionSearchService.saveEventInterest(eventInterest);
							Messagebox.show(Labels.getLabel("action.message.inquiryConfirmation"),
									Labels.getLabel("common.messages.confirm"), Messagebox.OK, Messagebox.INFORMATION,
									new EventListener<org.zkoss.zk.ui.event.Event>() {
										public void onEvent(org.zkoss.zk.ui.event.Event evt) {
											if (ZKSession.getAttribute("fromCalendar") != null) {
												ZKSession.removeAttribute("fromCalendar");
												ZKSession.sendRedirect(PageURL.ACTION_CALENDAR);
											} else
												ZKSession.sendRedirect(PageURL.SEARCH_ACTION);
										}
									});
							break;
						} catch (ServiceException e) {
							log.error(e.getMessage());
							Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
									Labels.getLabel("common.messages.inquiry_title"), Messagebox.OK, Messagebox.ERROR);

							ZKSession.sendRedirect(PageURL.SEARCH_ACTION);
						}
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	public void onDownload() {
		Filedownload.save(selectedPDF.getFileBytes(), "pdf", selectedPDF.getFileName());
	}

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(viewActionSearchService.getImages(event));
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}
		return images;
	}

	public ListModelList<EventPDF> getUploadedPDFs() {
		uploadedPDFs = new ListModelList<>();
		try {
			uploadedPDFs.addAll(viewActionSearchService.getPDFs(event));
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
		return uploadedPDFs;
	}

	public EventPDF getSelectedPDF() {
		return selectedPDF;
	}

	public void setSelectedPDF(EventPDF selectedPDF) {
		this.selectedPDF = selectedPDF;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public EventInterest getEventInterest() {
		return eventInterest;
	}

	public void setEventInterest(EventInterest eventInterest) {
		this.eventInterest = eventInterest;
	}
}
