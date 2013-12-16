package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.domain.aggregator.FeedData;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.FeedDataSearchCriteria;
import org.teiath.service.act.SearchFeedDataService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ListFeedDataVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListFeedDataVM.class.getName());

	@Wire("#paging")
	private Paging paging;
	@Wire("#feedDataListbox")
	private Listbox feedDataListbox;
	@Wire("#toolbar")
	private Div toolbar;
	@Wire("#feedDataWin")
	private Window win;

	@WireVariable
	private SearchFeedDataService searchFeedDataService;

	private FeedDataSearchCriteria feedDataSearchCriteria;
	private ListModelList<FeedData> feedDataList;
	private Collection<FeedData> selectedList;
	private FeedData selectedFeedData;
	private ListModelList<FeedCategory> feedCategories;
	private ListModelList<Feed> feeds;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		paging.setPageSize(10);

		feedDataList = new ListModelList<>();
		feedDataList.setMultiple(true);
		feedDataListbox.setCheckmark(true);
		feedDataListbox.setMultiple(true);

		if (ZKSession.getAttribute("feedDataSearchCriteria") != null) {
			feedDataSearchCriteria = (FeedDataSearchCriteria) ZKSession.getAttribute("feedDataSearchCriteria");
			SearchResults<FeedData> results = null;
			try {
				results = searchFeedDataService.searchFeedData(feedDataSearchCriteria);
				Collection<FeedData> events = results.getData();
				feedDataList.addAll(events);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(feedDataSearchCriteria.getPageNumber());
				if (results.getTotalRecords() != 0) {
					feedDataListbox.setVisible(true);
					paging.setVisible(true);
					toolbar.setVisible(true);
				} else {
					feedDataListbox.setVisible(false);
					paging.setVisible(false);
					toolbar.setVisible(false);
				}
				ZKSession.removeAttribute("feedDataSearchCriteria");
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		} else {
			//Initial search criteria
			feedDataSearchCriteria = new FeedDataSearchCriteria();
			feedDataSearchCriteria.setPageSize(paging.getPageSize());
			feedDataSearchCriteria.setPageNumber(0);
			feedDataSearchCriteria.setInterestingFlag(-1);
			feedDataSearchCriteria.setOrderField("publicationDate");
			feedDataSearchCriteria.setOrderDirection("descending");
			paging.setPageSize(10);
		}
	}

	@GlobalCommand
	public void repopulate() {
		selectedFeedData = null;
		feedDataList.clear();
		feedDataSearchCriteria.setPageNumber(0);
		feedDataSearchCriteria.setPageSize(paging.getPageSize());

		try {
			SearchResults<FeedData> results = searchFeedDataService.searchFeedData(feedDataSearchCriteria);
			Collection<FeedData> events = results.getData();
			feedDataList.addAll(events);
			if (results.getTotalRecords() != 0) {
				feedDataListbox.setVisible(true);
				paging.setVisible(true);
				toolbar.setVisible(true);
			} else {
				feedDataListbox.setVisible(false);
				paging.setVisible(false);
				toolbar.setVisible(false);
			}
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(feedDataSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedFeedData")
	public void onSearch() {
		selectedFeedData = null;
		feedDataList.clear();
		feedDataSearchCriteria.setPageNumber(0);
		feedDataSearchCriteria.setPageSize(paging.getPageSize());
		feedDataSearchCriteria.setOrderField("publicationDate");
		feedDataSearchCriteria.setOrderDirection("descending");

		try {
			SearchResults<FeedData> results = searchFeedDataService.searchFeedData(feedDataSearchCriteria);
			Collection<FeedData> events = results.getData();
			feedDataList.addAll(events);
			if (results.getTotalRecords() != 0) {
				feedDataListbox.setVisible(true);
				paging.setVisible(true);
				toolbar.setVisible(true);
			} else {
				feedDataListbox.setVisible(false);
				paging.setVisible(false);
				toolbar.setVisible(false);
				Messagebox.show(MessageBuilder
						.buildErrorMessage(Labels.getLabel("action.notFound"), Labels.getLabel("action")),
						Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
			}
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(feedDataSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedFeedData", "feedDataSearchCriteria"})
	public void onResetSearch() {
		selectedFeedData = null;
		feedDataSearchCriteria.setFeedCategory(null);
		feedDataSearchCriteria.setFeed(null);
		feedDataSearchCriteria.setDateFrom(null);
		feedDataSearchCriteria.setDateTo(null);
		feedDataSearchCriteria.setInterestingFlag(FeedData.INTERESTING);
	}

	@Command
	@NotifyChange("feedDataList")
	public void onView() {
		if (selectedFeedData != null) {
			Map<String, Object> params = new HashMap();
			params.put("FEED_DATA_ID", selectedFeedData.getId());
			selectedFeedData.setInteresting(3);
			try {
				searchFeedDataService.saveFeeds(selectedList);;
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
			Executions.createComponents(PageURL.FEED_DATA_VIEW, win, params);
		}
	}

	@Command
	public void onClean() {
		Map params = new HashMap();
		//params.put("USER", user);
		Executions.createComponents("/zul/act/act_clean_feed_data_popup.zul", win, params);
	}

	@Command
	public void onViewEvent() {
		ZKSession.setAttribute("eventId", selectedFeedData.getEventId());
		ZKSession.setAttribute("fromFeed", true);
		ZKSession.setAttribute("feedDataSearchCriteria", feedDataSearchCriteria);
		ZKSession.sendRedirect(PageURL.ACTION_VIEW_SEARCH);
	}

	@Command
	@NotifyChange("selectedFeedData")
	public void onSort(BindContext ctx) {
		org.zkoss.zk.ui.event.Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		if (listheader.getId().equals("feedCategory")) {
			feedDataSearchCriteria.setOrderField("f.feedCategory");
		} else {
			feedDataSearchCriteria.setOrderField(listheader.getId());
		}

		feedDataSearchCriteria.setOrderDirection(listheader.getSortDirection());
		feedDataSearchCriteria.setPageNumber(0);
		selectedFeedData = null;
		feedDataList.clear();

		try {
			SearchResults<FeedData> results = searchFeedDataService.searchFeedData(feedDataSearchCriteria);
			feedDataList.clear();
			feedDataList.addAll(results.getData());
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(feedDataSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed" +
					"" +
					"" +
					"")), Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("selectedFeedData")
	public void onPaging() {
		if (feedDataList != null) {
			feedDataSearchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<FeedData> results = searchFeedDataService.searchFeedData(feedDataSearchCriteria);
				feedDataList.clear();
				feedDataList.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(feedDataSearchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed" +
						"" +
						"" +
						"")), Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	public void onCreate() {
		if (selectedFeedData != null) {
			Map<String, Object> params = new HashMap();
			params.put("FEED_DATA_ID", selectedFeedData.getId());
			selectedFeedData.setInteresting(3);
			try {
				searchFeedDataService.saveFeeds(selectedList);;
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
		ZKSession.setAttribute("feedDataSearchCriteria", feedDataSearchCriteria);
		ZKSession.setAttribute("feedDataId", selectedFeedData.getId());
		ZKSession.sendRedirect(PageURL.ACTION_CREATE);
	}

	@Command
	@NotifyChange("feedDataList")
	public void onFlagInteresting() {
		for (FeedData feedData : selectedList) {
			feedData.setInteresting(FeedData.INTERESTING);
		}

		try {
			searchFeedDataService.saveFeeds(selectedList);
			selectedList.clear();
			if (!feedDataSearchCriteria.getInterestingFlag().equals(-1))
				onSearch();
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange("feedDataList")
	public void onFlagNotInteresting() {
		for (FeedData feedData : selectedList) {
			feedData.setInteresting(FeedData.NOT_INTERESTING);
		}

		try {
			searchFeedDataService.saveFeeds(selectedList);
			selectedList.clear();
			if (!feedDataSearchCriteria.getInterestingFlag().equals(-1))
				onSearch();
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	public ListModelList<FeedCategory> getFeedCategories() {
		if (feedCategories == null) {
			feedCategories = new ListModelList<>();
			try {
				feedCategories.addAll(searchFeedDataService.getFeedCategories());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed.feedCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return feedCategories;
	}

	public ListModelList<Feed> getFeeds() {
		if (feeds == null) {
			feeds = new ListModelList<>();
			try {
				feeds.addAll(searchFeedDataService.getFeeds());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed.")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return feeds;
	}

	public FeedDataSearchCriteria getFeedDataSearchCriteria() {
		return feedDataSearchCriteria;
	}

	public void setFeedDataSearchCriteria(FeedDataSearchCriteria feedDataSearchCriteria) {
		this.feedDataSearchCriteria = feedDataSearchCriteria;
	}

	public FeedData getSelectedFeedData() {
		return selectedFeedData;
	}

	public void setSelectedFeedData(FeedData selectedFeedData) {
		this.selectedFeedData = selectedFeedData;
	}

	public ListModelList<FeedData> getFeedDataList() {
		return feedDataList;
	}

	public void setFeedDataList(ListModelList<FeedData> feedDataList) {
		this.feedDataList = feedDataList;
	}

	public Collection<FeedData> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(Collection<FeedData> selectedList) {
		this.selectedList = selectedList;
	}
}
