package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.service.aggregator.AggregatorService;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.ListFeedsService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class ListFeedsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListFeedsVM.class.getName());

	@Wire("#feedListWin")
	private Window win;

	@WireVariable
	private ListFeedsService listFeedsService;
	@WireVariable
	private AggregatorService aggregatorService;

	private ListModelList<Feed> feeds;
	private Feed selectedFeed;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onRssUpdate() {
			aggregatorService.updateFeed(new Timestamp(new Date().getTime()), selectedFeed);
			Messagebox.show(MessageBuilder.buildErrorMessage("Η ενημέρωση ολοκληρώθηκε με επιτυχία.", Labels.getLabel("feed")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.INFORMATION);

	}

	@Command
	public void onCreateFeed() {
		ZKSession.sendRedirect(PageURL.FEED_CREATE);
	}

	@Command
	public void onEditFeed() {
		if (selectedFeed != null) {
			ZKSession.setAttribute("feedId", selectedFeed.getId());
			ZKSession.sendRedirect(PageURL.FEED_EDIT);
		}
	}

	@Command
	public void onDeleteFeed() {
		if (selectedFeed != null) {

			Map params = new HashMap();
			Executions.createComponents("/zul/user/values/feed_delete_popup.zul", win, params);

			/*Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.OK |Messagebox.NO ,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listFeedsService.deleteFeed(selectedFeed);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.FEED_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox
										.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
												Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
												Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox
										.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
												Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
												Messagebox.ERROR);
							}
							break;
						case Messagebox.OK:
							try {
								listFeedsService.deleteFeedData(selectedFeed);
								listFeedsService.deleteFeed(selectedFeed);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<Event>() {
									public void onEvent(Event evt) {
										ZKSession.sendRedirect(PageURL.FEED_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox
										.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
												Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
												Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox
										.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
												Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
												Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});*/
		}
	}

	@GlobalCommand
	public void deleteFeedData() {
		try {
			listFeedsService.deleteFeedData(selectedFeed);
			listFeedsService.deleteFeed(selectedFeed);
			Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
					Labels.getLabel("common.messages.confirm"), Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.FEED_LIST);
				}
			});
		} catch (DeleteViolationException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
							Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
							Messagebox.ERROR);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
							Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
							Messagebox.ERROR);
		}
	}

	@GlobalCommand
	public void deleteFeed() {
		try {
			listFeedsService.deleteFeed(selectedFeed);
			Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
					Labels.getLabel("common.messages.confirm"), Messagebox.OK,
					Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.FEED_LIST);
				}
			});
		} catch (DeleteViolationException e) {
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
							Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
							Messagebox.ERROR);
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox
					.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
							Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
							Messagebox.ERROR);
		}
	}

	public ListModelList<Feed> getFeeds() {
		if (feeds == null) {
			feeds = new ListModelList<>();
			try {
				feeds.addAll(listFeedsService.getFeeds());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return feeds;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public Feed getSelectedFeed() {
		return selectedFeed;
	}

	public void setSelectedFeed(Feed selectedFeed) {
		this.selectedFeed = selectedFeed;
	}
}
