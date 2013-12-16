package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.domain.image.EventMainImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.teiath.service.act.CreateActionService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.DateValidator;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.Validator;
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
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class CreateActionVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateActionVM.class.getName());

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


	@WireVariable
	private CreateActionService createActionService;

	private Event event;
	private Validator dateValidator;
	private ListModelList<AImage> uploadedImages;
	private AImage mainImage;
	private ListModelList<EventPDF> uploadedPDFs;
	private TreeNode selectedParentEventCategory;
	private ListModelList<String> popularLocationsList;
	private FeedData feedData;
	private ListModelList<Currency> currencies;
	private Currency defaultCurrency;

	@AfterCompose
	@NotifyChange("event")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		dateValidator = new DateValidator();

		event = new Event();
		event.setUser(loggedUser);
		event.setDisabledAccess(true);
		event.setEventStatus(Event.EVENT_STATUS_PENDING);

		Collection<EventCategory> parentalCategories = null;
		popularLocationsList = new ListModelList<>();
		try {

			if (ZKSession.getAttribute("feedDataId") != null) {
				feedData = createActionService.getFeedDataById((Integer) ZKSession.getAttribute("feedDataId"));
				event.setEventTitle(feedData.getTitle());
				event.setEventDescription(feedData.getDescription());
				event.setUrl(feedData.getUrl());
				ZKSession.removeAttribute("feedDataId");

			}

			parentalCategories = createActionService.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryRenderer());

			popularLocationsList.addAll(createActionService.getPopularLocations());

			autoCompleteCombo.setModel(new SimpleListModel<String>(popularLocationsList));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}

		if ((Executions.getCurrent().getUserAgent().contains("Android")) || (Executions.getCurrent().getUserAgent()
				.contains("iPhone")) || (Executions.getCurrent().getUserAgent().contains("iPad"))) {

			photoLayout.setVisible(false);
			photoTab.setVisible(false);
		}

		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
		uploadedImages = new ListModelList<>();
		uploadedPDFs = new ListModelList<>();
		currencies = new ListModelList<>();

		try {
			currencies.addAll(createActionService.getCurrencies());
			event.setPrice(BigDecimal.ZERO);
			defaultCurrency = createActionService.getDefaultCurrency();
			if (defaultCurrency != null) {
				event.setCurrency(createActionService.getDefaultCurrency());
			}
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing.price")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
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
			int limitBytes = 3145720; // 3 Mb
			try {
				if (media.getStreamData().available() > limitBytes) {
					Messagebox.show("Το μέγεθος του αρχείου δε θα πρέπει να ξεπερνάει τα 4MB", "Σφάλμα", Messagebox.OK, Messagebox.ERROR);
				} else {
					if (media instanceof org.zkoss.image.Image) {
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
					} else {
						Messagebox.show("Μη αποδεκτός τύπος αρχείου", "Σφάλμα", Messagebox.OK, Messagebox.ERROR);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
		photosListbox.setVisible(true);
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
			eventPDF.setEvent(event);
			eventPDF.setFileBytes(media.getByteData());
			eventPDF.setContentType(media.getContentType());
			eventPDF.setFileName(media.getName());
			uploadedPDFs.add(eventPDF);
			Listitem listitem = new Listitem();
			Listcell listcell = new Listcell();
			listcell.setLabel(media.getName());
			listitem.appendChild(listcell);
			pdfsListbox.appendChild(listitem);
		}
		pdfsListbox.setVisible(true);
	}

	@Command
	public void uploadMainImage(@BindingParam("media") Media media) {
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
	public void onPDFSelect() {
		pdfDeleteButton.setDisabled(false);
	}

	@Command
	public void onPDFDelete() {
		uploadedPDFs.remove(pdfsListbox.getSelectedIndex());
		pdfsListbox.removeItemAt(pdfsListbox.getSelectedIndex());
		pdfDeleteButton.setDisabled(true);
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("aImage", uploadedImages.get(photosListbox.getSelectedIndex()));
		Window window = (Window) Executions.createComponents("/zul/act/image_view.zul", null, null);
		window.doModal();
	}

	@Command
	public void deleteMainImage() {
		photoVBox.removeChild(photoVBox.getLastChild());
		Image imageComponent = new Image();
		imageComponent.setWidth("300px");
		imageComponent.setHeight("300px");
		imageComponent.setSrc("/img/noImage.jpg");
		photoVBox.appendChild(imageComponent);
	}

	@Command
	public void onSave(
			@ContextParam(ContextType.TRIGGER_EVENT)
			InputEvent evnt) {
		String address = ((Textbox) evnt.getTarget()).getValue();
		if ((selectedParentEventCategory != null) && !(address.equals(""))) {
			if (event.getPrice() == null) {
				event.setPrice(BigDecimal.ZERO);
			}
			try {

				if (feedData != null) {
					feedData.setEventId(event.getId());
					feedData.setEventCode(event.getCode());
					createActionService.saveFeedData(feedData);
				}

				EventMainImage eventMainImage = new EventMainImage();
				if (mainImage != null) {
					eventMainImage.setImageBytes(mainImage.getByteData());
					event.setEventMainImage(eventMainImage);
				} else {
					event.setEventMainImage(null);
				}


				event.setEventAddress(address);
				event.setEventCategory((EventCategory) selectedParentEventCategory.getData());
				createActionService.saveEvent(event, uploadedImages, uploadedPDFs);
				if (feedData != null) {
					feedData.setEventId(event.getId());
					feedData.setEventCode(event.getCode());
					createActionService.saveFeedData(feedData);
				}
				createActionService.sendNotifications(event);
				Messagebox
						.show(Labels.getLabel("action.message.success"), Labels.getLabel("common.messages.save_title"),
								Messagebox.OK, Messagebox.INFORMATION,
								new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										if (feedData != null) {
											ZKSession.sendRedirect(PageURL.FEED_DATA_LIST);
										} else {
											ZKSession.sendRedirect(PageURL.ACTION_LIST);
										}
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
		} else {
			Messagebox.show(MessageBuilder.buildErrorMessage("Παρακαλώ συμπληρώστε όλα τα υποχρεωτικά πεδία για να συνεχίσετε",
					Labels.getLabel("action")), Labels.getLabel("common.messages.save_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		if (feedData != null) {
			Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
					Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<org.zkoss.zk.ui.event.Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							ZKSession.sendRedirect(PageURL.FEED_DATA_LIST);
						case Messagebox.NO:
							break;
					}
				}
			});
		} else {
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
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Validator getDateValidator() {
		return dateValidator;
	}

	public void setDateValidator(Validator dateValidator) {
		this.dateValidator = dateValidator;
	}

	public TreeNode getSelectedParentEventCategory() {
		return selectedParentEventCategory;
	}

	public void setSelectedParentEventCategory(TreeNode selectedParentEventCategory) {
		this.selectedParentEventCategory = selectedParentEventCategory;
	}

	public Currency getDefaultCurrency() {
		return defaultCurrency;
	}

	public void setDefaultCurrency(Currency defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}

	public ListModelList<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(ListModelList<Currency> currencies) {
		this.currencies = currencies;
	}
}
