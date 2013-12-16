package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.properties.EmailProperties;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.act.ListEventsService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.facebook.act.FacebookToolKitEvents;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class ListActionsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListActionsVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#tree")
	private Tree tree;
	@Wire("#any")
	private Popup any;

	@WireVariable
	private ListEventsService listEventsService;
	@Autowired
	private EmailProperties emailProperties;

	private EventSearchCriteria eventSearchCriteria;
	private ListModelList<Event> eventsList;
	private ListModelList<EventCategory> categories;
	private Event selectedEvent;
	private Integer status;
	private Set<DefaultTreeNode<EventCategory>> selectedItems = new HashSet<DefaultTreeNode<EventCategory>>();
	private EventCategory selectedEventCategory;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		Selectors.wireEventListeners(view, this);
		paging.setPageSize(10);

		//Initial search criteria
		eventSearchCriteria = new EventSearchCriteria();
		eventSearchCriteria.setPageSize(paging.getPageSize());
		eventSearchCriteria.setPageNumber(0);
		eventSearchCriteria.setEventStatus(- 1);
		eventSearchCriteria.setOrderField("eventCreationDate");
		eventSearchCriteria.setOrderDirection("descending");

		eventsList = new ListModelList<>();

		try {

			Collection<EventCategory> parentalCategories = null;

			try {
				parentalCategories = listEventsService.getEventCategoriesByParentId(1000);
				DefaultTreeModel model = new DefaultTreeModel(
						new DefaultTreeNode("ROOT", createTree(parentalCategories)));
				model.setMultiple(true);
				tree.setModel(model);
				tree.setItemRenderer(new EventCategoryRenderer());



			} catch (ServiceException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}

			SearchResults<Event> results = listEventsService.searchEventsByCriteria(eventSearchCriteria);
			Collection<Event> events = results.getData();
			eventsList.addAll(events);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventSearchCriteria.getPageNumber());
			if (eventsList.isEmpty()) {
				empty.setVisible(true);
			}

		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
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

	@Listen("onSelect = #tree")
	public void onSelect(SelectEvent<Treeitem, String> event) {
		Treeitem ref = event.getReference();
		if (ref.isSelected()) {
			recursiveCheck((TreeNode<EventCategory>) ref.getValue());
		} else {
			recursiveUnCheck((TreeNode<EventCategory>) ref.getValue());
		}
	}

	public void recursiveCheck(TreeNode<EventCategory> treeNode) {
		for (TreeNode<EventCategory> node : treeNode.getChildren()) {
			selectedItems.add((DefaultTreeNode<EventCategory>) node);
			recursiveCheck(node);
		}
	}

	public void recursiveUnCheck(TreeNode<EventCategory> treeNode) {
		for (TreeNode<EventCategory> node : treeNode.getChildren()) {
			selectedItems.removeAll(treeNode.getChildren());
			recursiveUnCheck(node);
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		org.zkoss.zk.ui.event.Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		eventSearchCriteria.setOrderField(listheader.getId());
		eventSearchCriteria.setOrderDirection(listheader.getSortDirection());
		eventSearchCriteria.setPageNumber(0);
		selectedEvent = null;
		eventsList.clear();

		try {
			SearchResults<Event> results = listEventsService.searchEventsByCriteria(eventSearchCriteria);
			Collection<Event> events = results.getData();
			eventsList.addAll(events);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedEvent")
	public void onPaging() {
		if (eventsList != null) {
			eventSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Event> results = listEventsService.searchEventsByCriteria(eventSearchCriteria);
				selectedEvent = null;
				eventsList.clear();
				eventsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(eventSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedEvent")
	public void onSearch() {
		selectedEvent = null;
		eventsList.clear();
		eventSearchCriteria.setPageNumber(0);
		eventSearchCriteria.setPageSize(paging.getPageSize());

		eventSearchCriteria.setEventCategories(new HashSet<EventCategory>());

		if (selectedItems != null) {
			for (DefaultTreeNode defaultTreeNode : selectedItems) {
				EventCategory eventCategory = (EventCategory) defaultTreeNode.getData();
				eventSearchCriteria.getEventCategories().add(eventCategory);

			}
		}

		try {
			SearchResults<Event> results = listEventsService.searchEventsByCriteria(eventSearchCriteria);
			Collection<Event> events = results.getData();
			eventsList.addAll(events);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventSearchCriteria.getPageNumber());
			if (events.isEmpty()) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(Labels.getLabel("action.noActions"), Labels.getLabel("action")),
						Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}






	@Command
	@NotifyChange({"selectedEvent", "eventSearchCriteria"})
	public void onResetSearch() {
		eventSearchCriteria.setEventCategories(null);
		eventSearchCriteria.setEventKeyword(null);
		eventSearchCriteria.setDateFrom(null);
		eventSearchCriteria.setDateTo(null);
		eventSearchCriteria.setEventCategories(null);
		eventSearchCriteria.setCode(null);
		selectedItems = null;
		tree.clearSelection();
	}

	@Command
	public void onCreate() {
		ZKSession.sendRedirect(PageURL.ACTION_CREATE);
	}

	@Command
	public void onView() {
		ZKSession.setAttribute("eventId", selectedEvent.getId());
		ZKSession.sendRedirect(PageURL.ACTION_VIEW);
	}

	@Command
	public void onEdit() {
		ZKSession.setAttribute("eventId", selectedEvent.getId());
		ZKSession.sendRedirect(PageURL.ACTION_EDIT);
	}

	@Command
	public void onDelete() {
		if (selectedEvent != null) {
			Messagebox.show(Labels.getLabel("action.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<org.zkoss.zk.ui.event.Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listEventsService.createUserAction(selectedEvent);
								listEventsService.deleteEvent(selectedEvent);
								Messagebox.show(Labels.getLabel("action.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.ACTION_LIST);
									}
								});
								break;
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
								ZKSession.sendRedirect(PageURL.ACTION_LIST);
							}
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}




	@Command
	public void onCopy() {
		ZKSession.setAttribute("eventId", selectedEvent.getId());
		ZKSession.sendRedirect(PageURL.ACTION_COPY);
	}

	@Command
	public void facebookShare() {
		if (selectedEvent != null) {
			//stelnoume to route id ws state wste na mas stalei pisw apo to facebook
			ZKSession.fireNewWindow(FacebookToolKitEvents.getLoginRedirectURL() + "&state=" + selectedEvent.getId());
		}
	}

	@Command
	public void tweet() {
		if (selectedEvent != null) {

			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true).setOAuthConsumerKey("q618nZCQHse9t3Lpwbudw")
					.setOAuthConsumerSecret("MN1gZeGxBf3mlBywDq9BdkCtj4fXmHi1xQDW3d4AsnU");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();

			if (loggedUser.getTwitterAccessToken() != null) {
				//TODO tweet content(140 chars max)
				String tweet = emailProperties.getDomain() + "previewEvent?code=" + selectedEvent.getCode() +" #educult";
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
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Event> results = null;
		eventSearchCriteria.setPageNumber(0);
		eventSearchCriteria.setPageSize(0);
		try {
			results = listEventsService.searchEventsByCriteria(eventSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<Event> events = results.getData();

		Report report = ReportToolkit.requestEventsReport(events, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public EventSearchCriteria getEventSearchCriteria() {
		return eventSearchCriteria;
	}

	public void setEventSearchCriteria(EventSearchCriteria eventSearchCriteria) {
		this.eventSearchCriteria = eventSearchCriteria;
	}

	public ListModelList<Event> getEventsList() {
		return eventsList;
	}

	public void setEventsList(ListModelList<Event> eventsList) {
		this.eventsList = eventsList;
	}

	public void setCategories(ListModelList<EventCategory> categories) {
		this.categories = categories;
	}

	public Set<DefaultTreeNode<EventCategory>> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(Set<DefaultTreeNode<EventCategory>> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public Event getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public EventCategory getSelectedEventCategory() {
		return selectedEventCategory;
	}

	public void setSelectedEventCategory(EventCategory selectedEventCategory) {
		this.selectedEventCategory = selectedEventCategory;
	}
}


