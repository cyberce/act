package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.aggregator.FeedCategory;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.ListCategoriesFeedsService;
import org.teiath.service.values.ListValuesService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;


@SuppressWarnings("UnusedDeclaration")
public class ListCategoriesFeedsVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListFeedsVM.class.getName());

	@WireVariable
	private ListCategoriesFeedsService listCategoriesFeedsService;

	private User user;
	private ListModelList<FeedCategory> feedCategories;
	private FeedCategory selectedFeedCategory;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onCreateFeedCategories() {
		ZKSession.sendRedirect(PageURL.FEED_CATEGORY_CREATE);
	}

	@Command
	public void onEditFeedCategories() {
		if (selectedFeedCategory != null) {
			ZKSession.setAttribute("feedCategoryId", selectedFeedCategory.getId());
			ZKSession.sendRedirect(PageURL.FEED_CATEGORY_EDIT);
		}
	}

	@Command
	public void onDeleteFeedCategories() {
		if (selectedFeedCategory != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listCategoriesFeedsService.deleteFeedCategory(selectedFeedCategory);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.FEED_CATEGORY_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("feed.categories")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("feed.categories")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							}
							break;
						case Messagebox.NO:
							break;
					}
				}
			});
		}
	}

	public ListModelList<FeedCategory> getFeedCategories() {
		if (feedCategories == null) {
			feedCategories= new ListModelList<>();
			try {
				feedCategories.addAll(listCategoriesFeedsService.getFeedCategories());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return feedCategories;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public FeedCategory getSelectedFeedCategory() {
		return selectedFeedCategory;
	}

	public void setSelectedFeedCategory(FeedCategory selectedFeedCategory) {
		this.selectedFeedCategory = selectedFeedCategory;
	}
}
