package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.act.ViewActionOperatorService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
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

import java.io.IOException;
import java.util.Collection;

@SuppressWarnings("UnusedDeclaration")
public class ViewActionOperatorVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewActionOperatorVM.class.getName());

	@Wire("#listingViewSellerWin")
	private Window win;
	@Wire("#ameaLabel")
	private Label ameaLabel;
	@Wire
	private Label markerData;
	@Wire("#statusLabel")
	private Label statusLabel;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;
	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#interestsListbox")
	private Listbox interestsListbox;
	@Wire("#XLButton")
	private Toolbarbutton xLButton;

	@WireVariable
	private ViewActionOperatorService viewActionOperatorService;

	private Event event;
	private ListModelList<ApplicationImage> images;
	private ListModelList<EventPDF> uploadedPDFs;
	private EventPDF selectedPDF;
	private ListModelList<EventInterest> eventInterestsList;
	private EventInterest selectedInterest;
	private User user;

	@AfterCompose
	@NotifyChange("event")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		eventInterestsList = new ListModelList<>();

		try {
			if (ZKSession.getAttribute("eventCode") != null)
				event = viewActionOperatorService.getEventByCode((String) ZKSession.getAttribute("eventCode"));
			else {
				event = viewActionOperatorService.getEventById((Integer) ZKSession.getAttribute("eventId"));
			}

			if (event.getAverageRating() != null) {
				Clients.evalJavaScript("doLoad('" + event.getAverageRating() + "'," +
						" '" + event.getAverageRating() + "')");
			}

			eventInterestsList.addAll(viewActionOperatorService.getEventInterestsByUser(event));

			if (eventInterestsList.isEmpty()) {
				interestsListbox.setVisible(false);
				empty.setVisible(true);
				xLButton.setVisible(false);
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
					images.addAll(viewActionOperatorService.getImages(event));
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

			if (event.getEventStatus() == Event.EVENT_STATUS_PENDING) {
				statusLabel.setValue(Labels.getLabel("action.pending"));
			} else if (event.getEventStatus() == Event.EVENT_STATUS_IN_PROGRESS) {
				statusLabel.setValue(Labels.getLabel("action.inProgress"));
			} else {
				statusLabel.setValue(Labels.getLabel("action.over"));
			}

			Clients.evalJavaScript("loadData('" + event.getEventAddress() + "', true)");
			Clients.evalJavaScript("mapme('" + event.getEventAddress() + "')");
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ACTION_LIST);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onPrintXLS() {

		try {
			Collection<EventInterest> eventInterests = viewActionOperatorService.getEventInterestsByUser(event);
			Report report = ReportToolkit.requestEventInterestsReportXLS(eventInterests, ReportType.EXCEL);
			report.setExcelReport(ExcelToolkit.EVENT_INTERESTS);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onBack() {
		if (ZKSession.getAttribute("fromUserAction") != null) {
			ZKSession.removeAttribute("fromUserAction");
			ZKSession.sendRedirect(PageURL.USER_ACTION_LIST);
		}
		else
			ZKSession.sendRedirect(PageURL.ACTION_LIST);
	}

	@Command
	public void onDownload() {
		Filedownload.save(selectedPDF.getFileBytes(), "pdf", selectedPDF.getFileName());
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(viewActionOperatorService.getImages(event));
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
			uploadedPDFs.addAll(viewActionOperatorService.getPDFs(event));
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

	public EventInterest getSelectedInterest() {
		return selectedInterest;
	}

	public void setSelectedInterest(EventInterest selectedInterest) {
		this.selectedInterest = selectedInterest;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public ListModelList<EventInterest> getEventInterestsList() {
		return eventInterestsList;
	}

	public void setEventInterestsList(ListModelList<EventInterest> eventInterestsList) {
		this.eventInterestsList = eventInterestsList;
	}
}
