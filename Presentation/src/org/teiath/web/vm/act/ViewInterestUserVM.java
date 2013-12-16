package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.act.ViewActionInterestUserService;
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

import java.io.IOException;

@SuppressWarnings("UnusedDeclaration")
public class ViewInterestUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ViewInterestUserVM.class.getName());

	@Wire("#ameaLabel")
	private Label ameaLabel;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#imageLibrary")
	private Hbox imageLibrary;

	@WireVariable
	private ViewActionInterestUserService viewActionInterestUserService;

	private Event event;
	private ListModelList<ApplicationImage> images;
	private ListModelList<EventPDF> uploadedPDFs;
	private EventPDF selectedPDF;

	@AfterCompose
	@NotifyChange("event")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			event = viewActionInterestUserService.getEventById((Integer) ZKSession.getAttribute("eventId"));

			if (event.getAverageRating() != null) {
				Clients.evalJavaScript("doLoad('" + event.getAverageRating() + "'," +
						" '" + event.getAverageRating() + "')");
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
					images.addAll(viewActionInterestUserService.getImages(event));
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
				} catch (IOException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}

			if (event.isDisabledAccess()) {
				ameaLabel.setValue(Labels.getLabel("common.yes"));
			} else {
				ameaLabel.setValue(Labels.getLabel("common.no"));
			}

			Clients.evalJavaScript("loadData('" + event.getEventAddress() + "', true)");
			Clients.evalJavaScript("mapme('" + event.getEventAddress() + "')");
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(e.getMessage(), Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.ACTION_INTERESTS_USER_LIST);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	@Command
	public void onBack() {
		ZKSession.sendRedirect(PageURL.ACTION_INTERESTS_USER_LIST);
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
				images.addAll(viewActionInterestUserService.getImages(event));
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
			uploadedPDFs.addAll(viewActionInterestUserService.getPDFs(event));
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

}
