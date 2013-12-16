package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventInterestSearchCriteria;
import org.teiath.service.act.ListActionsInterestsUserService;
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
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class ListActionInterestsUserVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListActionInterestsUserVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#empty")
	private Label empty;
	@Wire("#tree")
	private Tree tree;

	@WireVariable
	private ListActionsInterestsUserService listActionsInterestsUserService;

	private EventInterestSearchCriteria eventInterestSearchCriteria;
	private ListModelList<EventInterest> eventInterestsList;
	private ListModelList<EventCategory> categories;
	private EventInterest selectedEventInterest;


	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		//Initial search criteria
		eventInterestSearchCriteria = new EventInterestSearchCriteria();
		eventInterestSearchCriteria.setPageSize(paging.getPageSize());
		eventInterestSearchCriteria.setPageNumber(0);
		eventInterestSearchCriteria.setParticipant(loggedUser);
		eventInterestSearchCriteria.setStatus(1);

		eventInterestsList = new ListModelList<>();

		try {
			SearchResults<EventInterest> results = listActionsInterestsUserService
					.searchEventInterestsByCriteria(eventInterestSearchCriteria);
			Collection<EventInterest> eventInterests = results.getData();
			eventInterestsList.addAll(eventInterests);
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(eventInterestSearchCriteria.getPageNumber());
			if (eventInterestsList.isEmpty()) {
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
			SearchResults<EventInterest> results = listActionsInterestsUserService
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
				SearchResults<EventInterest> results = listActionsInterestsUserService
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
	public void onView() {
		if (selectedEventInterest != null) {
			ZKSession.setAttribute("eventId", selectedEventInterest.getEvent().getId());
			ZKSession.sendRedirect(PageURL.ACTION_INTEREST_VIEW);
		}
	}


	@Command
	public void onPrintPDF() {
		SearchResults<EventInterest> results = null;
		eventInterestSearchCriteria.setPageNumber(0);
		eventInterestSearchCriteria.setPageSize(0);
		try {
			results = listActionsInterestsUserService.searchEventInterestsByCriteria(eventInterestSearchCriteria);
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


}
