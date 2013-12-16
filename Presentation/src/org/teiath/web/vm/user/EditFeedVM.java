package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.aggregator.Feed;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.data.domain.aggregator.FeedType;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.EditFeedService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

@SuppressWarnings("UnusedDeclaration")
public class EditFeedVM {

	static Logger log = Logger.getLogger(EditFeedVM.class.getName());

	@Wire("#typesCombo")
	private Combobox typesCombo;

	@WireVariable
	private EditFeedService editFeedService;

	private Feed feed;
	private ListModelList<FeedType> feedTypes;
	private ListModelList<FeedCategory> feedCategories;

	@AfterCompose
	@NotifyChange("feed")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		feed = new Feed();

		try {
			feed = editFeedService.getFeedById((Integer) ZKSession.getAttribute("feedId"));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR,
					new EventListener<org.zkoss.zk.ui.event.Event>() {
						public void onEvent(org.zkoss.zk.ui.event.Event evt) {
							ZKSession.sendRedirect(PageURL.FEED_LIST);
						}
					});
		}
	}

	@Command
	public void onSave() {

		try {
			editFeedService.saveFeed(feed);
			Messagebox.show(Labels.getLabel("value.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.FEED_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.FEED_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public ListModelList<FeedType> getFeedTypes() {
		if (feedTypes == null) {
			feedTypes = new ListModelList<>();
			try {
				feedTypes.addAll(editFeedService.getFeedTypes());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed.feedType")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
				log.error(e.getMessage());
			}
		}

		return feedTypes;
	}

	public ListModelList<FeedCategory> getFeedCategories() {
		if (feedCategories == null) {
			feedCategories = new ListModelList<>();
			try {
				feedCategories.addAll(editFeedService.getFeedCategories());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("feed.feedCategory")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return feedCategories;
	}
}
