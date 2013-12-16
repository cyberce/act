package org.teiath.web.vm.user.values;

import org.apache.log4j.Logger;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.EditFeedCategoryService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Messagebox;
import org.zkoss.zk.ui.Component;

@SuppressWarnings("UnusedDeclaration")
public class EditFeedCategoryVM
		extends BaseVM    {


		static Logger log = Logger.getLogger(EditFeedCategoryVM.class.getName());

		@WireVariable
		private EditFeedCategoryService editFeedCategoryService;

		private FeedCategory feedCategory;

		@AfterCompose
		@NotifyChange("feedCategory")
		public void afterCompose(
			@ContextParam(ContextType.VIEW)
					Component view) {
		Selectors.wireComponents(view, this, false);

		feedCategory = new FeedCategory();
		try {
			feedCategory = editFeedCategoryService
					.getFeedCategoryById((Integer) ZKSession.getAttribute("feedCategoryId"));
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.FEED_CATEGORY_LIST);
		}
	}

		@Command
		public void onSave() {
		try {
			editFeedCategoryService.saveFeedCategory(feedCategory);
			Messagebox.show(Labels.getLabel("value.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.FEED_CATEGORY_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
					Labels.getLabel("common.messages.edit_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.FEED_CATEGORY_LIST);
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
