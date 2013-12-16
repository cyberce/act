package org.teiath.web.vm.user;

import org.apache.log4j.Logger;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Currency;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.SearchCriteria;
import org.teiath.service.exceptions.DeleteViolationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.ListValuesService;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryListRenderer;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.BindContext;
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
public class ListValuesVM
		extends BaseVM {

	static Logger log = Logger.getLogger(ListValuesVM.class.getName());

	@Wire("#tree")
	private Tree tree;
	@Wire("#paging")
	private Paging paging;

	@WireVariable
	private ListValuesService listValuesService;

	private User user;
	private EventCategory rootCategory;
	private TreeNode selectedEventCategory;
	private ListModelList<Currency> currencies;
	private Currency selectedCurrency;
	private SearchCriteria searchCriteria;
	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			rootCategory = listValuesService.getEventCategoryByParentId(1000);
			Collection<EventCategory> parentalCategories = listValuesService
					.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryListRenderer());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
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
	public void onCreateActionCategories() {
		ZKSession.sendRedirect(PageURL.EVENT_CATEGORY_CREATE);
	}

	@Command
	public void onCreateCurrencies() {
		ZKSession.sendRedirect(PageURL.CURRENCY_CREATE);
	}

	@Command
	public void onEditActionCategories() {
		if (selectedEventCategory != null) {
			EventCategory eventCategory = (EventCategory) selectedEventCategory.getData();
			ZKSession.setAttribute("eventCategoryId", eventCategory.getId());
			ZKSession.sendRedirect(PageURL.EVENT_CATEGORY_EDIT);
		}
	}

	@Command
	public void onEditCurrencies() {
		if (selectedCurrency != null) {
			ZKSession.setAttribute("currencyId", selectedCurrency.getId());
			ZKSession.sendRedirect(PageURL.CURRENCY_EDIT);
		}
	}


	@Command
	public void onDeleteActionCategories() {
		if (selectedEventCategory != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								EventCategory eventCategoryToDelete = (EventCategory) selectedEventCategory.getData();
								listValuesService.deleteEventCategory(eventCategoryToDelete);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("action.categories")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("action.categories")),
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


	@Command
	public void onDeleteCurrencies() {
		if (selectedCurrency != null) {
			Messagebox.show(Labels.getLabel("value.message.deleteQuestion"),
					Labels.getLabel("common.messages.delete_title"), Messagebox.YES | Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
				public void onEvent(org.zkoss.zk.ui.event.Event evt) {
					switch ((Integer) evt.getData()) {
						case Messagebox.YES:
							try {
								listValuesService.deleteCurrency(selectedCurrency);
								Messagebox.show(Labels.getLabel("value.message.deleteConfirmation"),
										Labels.getLabel("common.messages.confirm"), Messagebox.OK,
										Messagebox.INFORMATION, new EventListener<org.zkoss.zk.ui.event.Event>() {
									public void onEvent(org.zkoss.zk.ui.event.Event evt) {
										ZKSession.sendRedirect(PageURL.VALUES_LIST);
									}
								});
							} catch (DeleteViolationException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction.types")),
										Labels.getLabel("common.messages.delete_title"), Messagebox.OK,
										Messagebox.ERROR);
							} catch (ServiceException e) {
								log.error(e.getMessage());
								Messagebox.show(MessageBuilder
										.buildErrorMessage(e.getMessage(), Labels.getLabel("transaction.types")),
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

	@Command
	@NotifyChange("selectedCurrency")
	public void onPaging() {
		if (currencies != null) {
			searchCriteria.setPageNumber(paging.getActivePage());
			try {
				SearchResults<Currency> results = listValuesService
						.searchCurrenciesByCriteria(searchCriteria);
				selectedCurrency = null;
				currencies.clear();
				currencies.addAll(results.getData());
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(searchCriteria.getPageNumber());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		searchCriteria.setOrderField(listheader.getId());
		searchCriteria.setOrderDirection(listheader.getSortDirection());
		searchCriteria.setPageNumber(0);
		selectedCurrency = null;
		currencies.clear();

		try {
			SearchResults<Currency> results = listValuesService
					.searchCurrenciesByCriteria(searchCriteria);
			selectedCurrency = null;
			currencies.clear();
			currencies.addAll(results.getData());
			paging.setTotalSize(results.getTotalRecords());
			paging.setActivePage(searchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}



	public ListModelList<Currency> getCurrencies() {
		if (currencies == null) {
			currencies = new ListModelList<>();
			//Initial search criteria
			searchCriteria = new SearchCriteria();
			searchCriteria.setPageSize(paging.getPageSize());
			searchCriteria.setPageNumber(0);
			searchCriteria.setOrderField("code");
			searchCriteria.setOrderDirection("ascending");
			try {
				SearchResults<Currency> results = listValuesService
						.searchCurrenciesByCriteria(searchCriteria);
				paging.setTotalSize(results.getTotalRecords());
				paging.setActivePage(searchCriteria.getPageNumber());
				currencies.addAll(results.getData());
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}

		return currencies;
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

	public TreeNode getSelectedEventCategory() {
		return selectedEventCategory;
	}

	public void setSelectedEventCategory(TreeNode selectedEventCategory) {
		this.selectedEventCategory = selectedEventCategory;
	}

	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	public void setSelectedCurrency(Currency selectedCurrency) {
		this.selectedCurrency = selectedCurrency;
	}
}
