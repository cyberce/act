package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.image.EventMainImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.act.CopyActionService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class CopyActionVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CopyActionVM.class.getName());

	@Wire("#uploadButton")
	private Button uploadButton;
	@Wire("#btnDelete")
	private Button deleteButton;
	@Wire("#pdfDelete")
	private Button pdfDeleteButton;
	@Wire("#btnView")
	private Button viewButton;
	@Wire("#photosListbox")
	private Listbox photosListbox;
	@Wire("#photoVBox")
	private Vbox photoVBox;
	@Wire("#photoTab")
	private Tab photoTab;
	@Wire("#photoLayout")
	private Vlayout photoLayout;
	@Wire("#pdfsListbox")
	private Listbox pdfsListbox;
	@Wire("#tree")
	private Tree tree;
	@Wire("#autoCompleteCombo")
	private Combobox autoCompleteCombo;

	@Wire
	private Label markerData;

	@WireVariable
	private CopyActionService copyActionService;

	private Event event;
	private Event newEvent;
	private ListModelList<EventCategory> categories;
	private ListModelList<AImage> uploadedImages;
	private AImage mainImage;
	private ListModelList<ApplicationImage> images;
	private ListModelList<EventPDF> uploadedPDFs;
	private EventPDF selectedPDF;
	private TreeNode selectedParentEventCategory;
	private ListModelList<String> popularLocationsList;
	private ListModelList<Currency> currencies;

	@AfterCompose
	@NotifyChange("event")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {

			photoLayout.setVisible(false);
			photoTab.setVisible(false);
		}

		event = new Event();
		newEvent = new Event();
		uploadedImages = new ListModelList<>();
		uploadedPDFs = new ListModelList<>();
		Collection<EventCategory> parentalCategories = null;
		popularLocationsList = new ListModelList<>();

		currencies = new ListModelList<>();
		try {
			currencies.addAll(copyActionService.getCurrencies());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.price")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}

		try {
			event = copyActionService.getEventById((Integer) ZKSession.getAttribute("eventId"));
			event.setUser(loggedUser);

			parentalCategories = copyActionService.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryRenderer());

			popularLocationsList.addAll(copyActionService.getPopularLocations());

			autoCompleteCombo.setModel(new SimpleListModel<String>(popularLocationsList));

			newEvent.setDateFrom(event.getDateFrom());
			newEvent.setDateTo(event.getDateTo());
			newEvent.setDisabledAccess(event.isDisabledAccess());
			newEvent.setEventCategory(event.getEventCategory());
			if (event.getEventDescription() != null) {
				newEvent.setEventDescription(event.getEventDescription());
			}
			newEvent.setEventLocation(event.getEventLocation());
			newEvent.setEventTitle(event.getEventTitle());
			newEvent.setEventLocation(event.getEventLocation());
			newEvent.setUser(event.getUser());
			if (event.getUrl() != null) {
				newEvent.setUrl(event.getUrl());
			}
			if (event.getPrice() != null) {
				newEvent.setPrice(event.getPrice());
				newEvent.setCurrency(event.getCurrency());
			}
			if (event.getMaximumAttendants() != null) {
				newEvent.setMaximumAttendants(event.getMaximumAttendants());
			}

			if (event.getEventMainImage() != null) {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setContent(event.getEventMainImage().getImage());
				photoVBox.appendChild(imageComponent);
				mainImage = event.getEventMainImage().getImage();
			} else {
				Image imageComponent = new Image();
				imageComponent.setWidth("300px");
				imageComponent.setHeight("300px");
				imageComponent.setSrc("/img/noImage.jpg");
				photoVBox.appendChild(imageComponent);
			}

			images = this.getImages();
			if (images != null) {
				for (ApplicationImage applicationImage : images) {
					try {
						AImage aImage = new AImage("", applicationImage.getImageBytes());
						uploadedImages.add(aImage);
						Listitem listitem = new Listitem();
						Listcell listcell = new Listcell();
						Image image = new Image();
						image.setHeight("64px");
						image.setWidth("64px");
						image.setContent(aImage);

						listcell.appendChild(image);
						listitem.appendChild(listcell);
						photosListbox.appendChild(listitem);
					} catch (IOException e) {
						log.error(e.getMessage());
						Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
								Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
						ZKSession.sendRedirect(PageURL.LISTING_LIST);
					}
				}
				photosListbox.setVisible(true);
			}

			Clients.evalJavaScript("loadData('" + event.getEventAddress() + "', false)");
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR,
					new EventListener<org.zkoss.zk.ui.event.Event>() {
						public void onEvent(org.zkoss.zk.ui.event.Event evt) {
							ZKSession.sendRedirect(PageURL.ACTION_LIST);
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List createTree(Collection<EventCategory> children) {
		List childrenList = new ArrayList();

		if ((children != null) && (! children.isEmpty())) {

			for (EventCategory category : children) {

				if (category.getSubCategories() != null) {
					childrenList.add(new DefaultTreeNode(category, createTree(category.getSubCategories())));
				} else {
					childrenList.add(new DefaultTreeNode(category));
				}
			}
		}

		return childrenList;
	}

	@Command
	public void onSave(
			@ContextParam(ContextType.TRIGGER_EVENT)
			InputEvent evnt) {

		EventMainImage eventMainImage = new EventMainImage();
		if (mainImage != null) {
			eventMainImage.setImageBytes(mainImage.getByteData());
			newEvent.setEventMainImage(eventMainImage);
		} else {
			newEvent.setEventMainImage(null);
		}

		try {
			String address = ((Textbox) evnt.getTarget()).getValue();
			newEvent.setEventAddress(address);
			copyActionService.deleteEventPhotos(newEvent);
			copyActionService.deleteEventPDFs(newEvent);
			copyActionService.saveEvent(newEvent, uploadedImages, uploadedPDFs);
			copyActionService.sendNotifications(event);
			Messagebox.show(Labels.getLabel("action.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					ZKSession.sendRedirect(PageURL.ACTION_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR,
					new EventListener<org.zkoss.zk.ui.event.Event>() {
						public void onEvent(org.zkoss.zk.ui.event.Event evt) {
							ZKSession.sendRedirect(PageURL.ACTION_LIST);
						}
					});
		}
	}

	@Command
	public void onPDFUpload(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		if ((upEvent != null) && (uploadedPDFs.getSize() < 4)) {
			Media media = upEvent.getMedia();
			EventPDF eventPDF = new EventPDF();
			eventPDF.setEvent(newEvent);
			eventPDF.setFileBytes(media.getByteData());
			eventPDF.setFileName(media.getName());
			eventPDF.setContentType(media.getContentType());
			uploadedPDFs.add(eventPDF);
		}
		pdfsListbox.setVisible(true);
	}

	@Command
	public void onPDFDelete() {
		uploadedPDFs.remove(selectedPDF);
		selectedPDF = null;
	}

	@Command
	public void onImageUpload(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		if ((upEvent != null) && (uploadedImages.getSize() < 4)) {
			Media media = upEvent.getMedia();
			uploadedImages.add((AImage) media);
			Listitem listitem = new Listitem();
			Listcell listcell = new Listcell();
			Image image = new Image();
			image.setContent((AImage) media);
			image.setHeight("64px");
			image.setWidth("64px");
			listcell.appendChild(image);
			listitem.appendChild(listcell);
			photosListbox.appendChild(listitem);
		}
		photosListbox.setVisible(true);
	}

	@Command
	public void uploadMainImage(
			@ContextParam(ContextType.BIND_CONTEXT)
			BindContext ctx) {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		Media media = upEvent.getMedia();
		mainImage = (AImage) media;
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setContent((AImage) media);
		photoVBox.appendChild(imageComponent);
	}

	@Command
	public void onSelect() {
		deleteButton.setDisabled(false);
		viewButton.setDisabled(false);
	}

	@Command
	public void onDelete() {
		uploadedImages.remove(photosListbox.getSelectedIndex());
		photosListbox.removeItemAt(photosListbox.getSelectedIndex());
		deleteButton.setDisabled(true);
	}

	@Command
	public void deleteMainImage() {
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
		mainImage = null;
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("aImage", uploadedImages.get(photosListbox.getSelectedIndex()));
		Window window = (Window) Executions.createComponents("/zul/act/image_view.zul", null, null);
		window.doModal();
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<org.zkoss.zk.ui.event.Event>() {
			public void onEvent(org.zkoss.zk.ui.event.Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.ACTION_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	@NotifyChange("newEvent")
	public void onSelectCategory() {
		newEvent.setEventCategory((EventCategory) selectedParentEventCategory.getData());
	}

	public ListModelList<ApplicationImage> getImages() {
		if (images == null) {
			images = new ListModelList<>();
			try {
				images.addAll(copyActionService.getImages(event));
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
			uploadedPDFs.addAll(copyActionService.getPDFs(event));
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.productPhotos")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
		}
		return uploadedPDFs;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public EventPDF getSelectedPDF() {
		return selectedPDF;
	}

	public void setSelectedPDF(EventPDF selectedPDF) {
		this.selectedPDF = selectedPDF;
	}

	public Event getNewEvent() {
		return newEvent;
	}

	public void setNewEvent(Event newEvent) {
		this.newEvent = newEvent;
	}

	public TreeNode getSelectedParentEventCategory() {
		return selectedParentEventCategory;
	}

	public void setSelectedParentEventCategory(TreeNode selectedParentEventCategory) {
		this.selectedParentEventCategory = selectedParentEventCategory;
	}

	public ListModelList<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(ListModelList<Currency> currencies) {
		this.currencies = currencies;
	}
}
