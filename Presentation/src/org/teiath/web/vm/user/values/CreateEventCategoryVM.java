package org.teiath.web.vm.user.values;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.values.CreateEventCategoryService;
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
public class CreateEventCategoryVM {

	static Logger log = Logger.getLogger(CreateEventCategoryVM.class.getName());

	@WireVariable
	private CreateEventCategoryService createEventCategoryService;

	@Wire("#tree")
	private Tree tree;

	private EventCategory eventCategory;
	private EventCategory rootCategory;
	private TreeNode selectedParentEventCategory;

	@AfterCompose
	@NotifyChange("eventCategory")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		try {
			rootCategory = createEventCategoryService.getEventCategoryByParentId(1000);
			Collection<EventCategory> parentalCategories = createEventCategoryService
					.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryRenderer());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("value.list")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
		}

		eventCategory = new EventCategory();
	}

	@Command
	public void onSave() {

		if (selectedParentEventCategory != null) {
			eventCategory.setParentCategory((EventCategory) selectedParentEventCategory.getData());
		} else {
			eventCategory.setParentCategory(rootCategory);
		}

		try {
			createEventCategoryService.saveEventCategory(eventCategory);
			Messagebox.show(Labels.getLabel("value.message.success"), Labels.getLabel("common.messages.save_title"),
					Messagebox.OK, Messagebox.INFORMATION, new EventListener<Event>() {
				public void onEvent(Event evt) {
					ZKSession.sendRedirect(PageURL.VALUES_LIST);
				}
			});
		} catch (ServiceException e) {
			log.error(e.getMessage());
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
	public void onCancel() {
		Messagebox.show(Labels.getLabel("common.messages.cancelQuestion"),
				Labels.getLabel("common.messages.cancel"), Messagebox.YES | Messagebox.NO,
				Messagebox.QUESTION, new EventListener<Event>() {
			public void onEvent(Event evt) {
				switch ((Integer) evt.getData()) {
					case Messagebox.YES:
						ZKSession.sendRedirect(PageURL.VALUES_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	public EventCategory getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(EventCategory eventCategory) {
		this.eventCategory = eventCategory;
	}

	public TreeNode getSelectedParentEventCategory() {
		return selectedParentEventCategory;
	}

	public void setSelectedParentEventCategory(TreeNode selectedParentEventCategory) {
		this.selectedParentEventCategory = selectedParentEventCategory;
	}
}
