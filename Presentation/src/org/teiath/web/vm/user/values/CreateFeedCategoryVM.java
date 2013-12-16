package org.teiath.web.vm.user.values;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.CreateEventCategoryService;
import org.teiath.service.values.CreateFeedCategoryService;
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
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@SuppressWarnings("UnusedDeclaration")
public class CreateFeedCategoryVM {

	static Logger log = Logger.getLogger(CreateFeedCategoryVM.class.getName());

	@WireVariable
	private CreateFeedCategoryService createFeedCategoryService;

	private FeedCategory feedCategory;

	@AfterCompose
	@NotifyChange("feedCategory")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		feedCategory = new FeedCategory();
	}

	@Command
	public void onSave() {
		try {
			createFeedCategoryService.saveFeedCategory(feedCategory);
			Messagebox.show(Labels.getLabel("value.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.FEED_CATEGORY_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
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
						ZKSession.sendRedirect(PageURL.FEED_CATEGORY_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public FeedCategory getFeedCategory() {
		return feedCategory;
	}

	public void setFeedCategory(FeedCategory feedCategory) {
		this.feedCategory = feedCategory;
	}
}
