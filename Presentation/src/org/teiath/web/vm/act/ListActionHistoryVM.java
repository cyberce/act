package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;
import org.teiath.service.act.ListActionsHistoryService;
import org.teiath.service.exceptions.ServiceException;
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
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class ListActionHistoryVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListActionHistoryVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#tree")
	private Tree tree;

	@WireVariable
	private ListActionsHistoryService listActionsHistoryService;

	private EventInterestSearchCriteria eventInterestSearchCriteria;
	private ListModelList<EventInterest> eventInterestsList;
	private ListModelList<EventCategory> categories;
	private EventInterest selectedEventInterest;
	private Set<DefaultTreeNode<EventCategory>> selectedItems = new HashSet<DefaultTreeNode<EventCategory>>();

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		Selectors.wireEventListeners(view, this);
		paging.setPageSize(10);

		//Initial search criteria
		eventInterestSearchCriteria = new EventInterestSearchCriteria();
		eventInterestSearchCriteria.setPageSize(paging.getPageSize());
		eventInterestSearchCriteria.setPageNumber(0);
		eventInterestSearchCriteria.setParticipant(loggedUser);
		eventInterestSearchCriteria.setStatus(2);

		eventInterestsList = new ListModelList<>();

		try {
			SearchResults<EventInterest> results = listActionsHistoryService
					.searchEventInterestsByCriteria(eventInterestSearchCriteria);
			Collection<EventInterest> eventInterests = results.getData();
			eventInterestsList.addAll(eventInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventInterestSearchCriteria.getPageNumber());
			if (eventInterestsList.isEmpty()) {
				empty.setVisible(true);
			}

			Collection<EventCategory> parentalCategories = null;

			try {
				parentalCategories = listActionsHistoryService.getEventCategoriesByParentId(1000);
				DefaultTreeModel model = new DefaultTreeModel(
						new DefaultTreeNode("ROOT", createTree(parentalCategories)));
				model.setMultiple(true);
				tree.setModel(model);
				tree.setItemRenderer(new EventCategoryRenderer());
			} catch (ServiceException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

		String sortField = listheader.getId();
		switch (sortField) {
			case "eventTitle":
				eventInterestSearchCriteria.setOrderField("evt.eventTitle");
				break;
			case "eventCategory":
				eventInterestSearchCriteria.setOrderField("evt.eventCategory");
				break;
			case "dateFrom":
				eventInterestSearchCriteria.setOrderField("evt.dateFrom");
				break;
			case "dateTo":
				eventInterestSearchCriteria.setOrderField("evt.dateTo");
				break;
			case "eventLocation":
				eventInterestSearchCriteria.setOrderField("evt.eventLocation");
				break;
			case "eventStatus":
				eventInterestSearchCriteria.setOrderField("evt.eventStatus");
				break;
		}

		eventInterestSearchCriteria.setOrderDirection(listheader.getSortDirection());
		eventInterestSearchCriteria.setPageNumber(0);
		selectedEventInterest = null;
		eventInterestsList.clear();

		try {
			SearchResults<EventInterest> results = listActionsHistoryService
					.searchEventInterestsByCriteria(eventInterestSearchCriteria);
			Collection<EventInterest> eventInterests = results.getData();
			eventInterestsList.addAll(eventInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedEventInterest")
	public void onPaging() {
		if (eventInterestsList != null) {
			eventInterestSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<EventInterest> results = listActionsHistoryService
						.searchEventInterestsByCriteria(eventInterestSearchCriteria);
				selectedEventInterest = null;
				eventInterestsList.clear();
				eventInterestsList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(eventInterestSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange("selectedEventInterest")
	public void onSearch() {
		selectedEventInterest = null;
		eventInterestsList.clear();
		eventInterestSearchCriteria.setPageNumber(0);
		eventInterestSearchCriteria.setPageSize(paging.getPageSize());

		eventInterestSearchCriteria.setEventCategories(new HashSet<EventCategory>());

		if (selectedItems != null) {
			for (DefaultTreeNode defaultTreeNode : selectedItems) {
				EventCategory eventCategory = (EventCategory) defaultTreeNode.getData();
				eventInterestSearchCriteria.getEventCategories().add(eventCategory);
			}
		}

		try {
			SearchResults<EventInterest> results = listActionsHistoryService
					.searchEventInterestsByCriteria(eventInterestSearchCriteria);
			Collection<EventInterest> events = results.getData();
			eventInterestsList.addAll(events);
			if (events.isEmpty()) {
				Messagebox.show(MessageBuilder
						.buildErrorMessage(Labels.getLabel("action.notFound"), Labels.getLabel("action")),
						Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
			}
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventInterestSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedCategory", "selectedEventInterest", "eventInterestSearchCriteria"})
	public void onResetSearch() {
		selectedEventInterest = null;
		eventInterestSearchCriteria.setEventCategories(null);
		eventInterestSearchCriteria.setEventKeyword(null);
		eventInterestSearchCriteria.setDateFrom(null);
		eventInterestSearchCriteria.setDateTo(null);
		eventInterestSearchCriteria.setEventCode(null);
		eventInterestSearchCriteria.setStatus(0);
		selectedItems = null;
		tree.clearSelection();
	}

	@Command
	public void onView() {
		if (selectedEventInterest != null) {
			ZKSession.setAttribute("eventId", selectedEventInterest.getEvent().getId());
			ZKSession.sendRedirect(PageURL.ACTION_HISTORY_VIEW);
		}
	}

	@Command
	public void onRate() {

		if ((selectedEventInterest != null) && (selectedEventInterest.getEvent().getDateTo().before(new Date()))) {
			ZKSession.setAttribute("event", selectedEventInterest.getEvent());
			ZKSession.sendRedirect(PageURL.ACTION_RATE);
		} else {
			Messagebox.show(MessageBuilder
					.buildErrorMessage("Δε μπορείτε να αξιολογήσετε δράση που βρίσκεται σε εξέλιξη,", Labels.getLabel("action")),
					"Αξιολόγηση", Messagebox.OK, Messagebox.INFORMATION);
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<EventInterest> results = null;
		eventInterestSearchCriteria.setPageNumber(0);
		eventInterestSearchCriteria.setPageSize(0);
		try {
			results = listActionsHistoryService.searchEventInterestsByCriteria(eventInterestSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<EventInterest> eventInterests = results.getData();

		Report report = ReportToolkit.eventHistoryReport(eventInterests, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public ListModelList<EventInterest> getEventInterestsList() {
		return eventInterestsList;
	}

	public void setEventInterestsList(ListModelList<EventInterest> eventInterestsList) {
		this.eventInterestsList = eventInterestsList;
	}

	public void setCategories(ListModelList<EventCategory> categories) {
		this.categories = categories;
	}

	public EventInterest getSelectedEventInterest() {
		return selectedEventInterest;
	}

	public void setSelectedEventInterest(EventInterest selectedEventInterest) {
		this.selectedEventInterest = selectedEventInterest;
	}

	public EventInterestSearchCriteria getEventInterestSearchCriteria() {
		return eventInterestSearchCriteria;
	}

	public void setEventInterestSearchCriteria(EventInterestSearchCriteria eventInterestSearchCriteria) {
		this.eventInterestSearchCriteria = eventInterestSearchCriteria;
	}

	public Paging getPaging() {
		return paging;
	}

	public void setPaging(Paging paging) {
		this.paging = paging;
	}

	public Set<DefaultTreeNode<EventCategory>> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(Set<DefaultTreeNode<EventCategory>> selectedItems) {
		this.selectedItems = selectedItems;
	}
}
