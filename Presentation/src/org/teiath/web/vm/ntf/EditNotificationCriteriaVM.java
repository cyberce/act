package org.teiath.web.vm.ntf;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.act.EditEventNotificationCriteriaService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryEditRenderer;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.*;

@SuppressWarnings("UnusedDeclaration")
public class EditNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(EditNotificationCriteriaVM.class.getName());

	@Wire("#tree")
	private Tree tree;
	@Wire("#itemTransRG")
	private Radiogroup itemTransRG;
	@Wire("#emptyRG")
	private Radiogroup emptyRG;
	@Wire("#notSpecifiedRadio")
	private Radio notSpecifiedRadio;

	@WireVariable
	private EditEventNotificationCriteriaService editEventNotificationCriteriaService;

	private EventNotificationCriteria eventNotificationCriteria;
	private TreeNode selectedParentEventCategory;
	private Set<DefaultTreeNode<EventCategory>> selectedItems ;

	@AfterCompose
	@NotifyChange("eventNotificationCriteria")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		Selectors.wireEventListeners(view, this);

		eventNotificationCriteria = new EventNotificationCriteria();
		Collection<EventCategory> parentalCategories = null;

		try {
			eventNotificationCriteria = editEventNotificationCriteriaService
					.getEventNotificationCriteriaById((Integer) ZKSession.getAttribute("notificationCriteriaId"));

			if (eventNotificationCriteria.getDisabledAccess() == null) {
				emptyRG.setSelectedItem(notSpecifiedRadio);
			}

			parentalCategories = editEventNotificationCriteriaService.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			model.setMultiple(true);

			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryEditRenderer());

		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications.criteria")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			log.error(e.getMessage());
			ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
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

/*	@NotifyChange("tree")
	public void populateTree() {
		for (EventCategory eventCategory : eventNotificationCriteria.getEventCategories()) {
			for (Treeitem treeitem : tree.getItems()) {
				System.out.println(treeitem.getValue());
			}
		}
	}*/

	@Listen("onAfterRender = #tree")
	public void onAfterRender(Event event) {
		selectedItems = new HashSet<DefaultTreeNode<EventCategory>>();
		Tree tree1 = (Tree) event.getTarget();
		for (Treeitem ref : tree1.getItems()) {
/*			TreeNode<EventCategory> node = ref.getValue();  */
			for (EventCategory eventCategory : eventNotificationCriteria.getEventCategories()) {
				EventCategory eventCategory1 = ref.getValue();
				if ((eventCategory1.getName() == eventCategory.getName()) || (eventCategory1.getName().equals(eventCategory.getName()))) {
					DefaultTreeNode<EventCategory> node = new DefaultTreeNode<EventCategory>(eventCategory1);
					selectedItems.add(node);
					ref.setSelected(true);
				}
			}
			ref.setOpen(false);
		}
	}

	@Listen("onSelect = #tree")
	public void onSelect(SelectEvent<Treeitem, String> event) {
		Treeitem ref = event.getReference();
		if (ref.isSelected()) {
			recursiveCheck(ref);
		} else {
			recursiveUnCheck(ref);
		}
	}

	public void recursiveCheck(Treeitem treeitem) {
		for (Treeitem item : treeitem.getTreechildren().getItems()) {
			item.setSelected(true);
			recursiveCheck(item);
		}
	}

	public void recursiveUnCheck(Treeitem treeitem) {
		for (Treeitem item : treeitem.getTreechildren().getItems()) {
			item.setSelected(false);
			recursiveCheck(item);
		}
	}

	@Command
	public void onSave() {
		eventNotificationCriteria.getEventCategories().clear();
		try {
			for (Treeitem item : tree.getSelectedItems()) {

				eventNotificationCriteria.getEventCategories().add((EventCategory) item.getValue());
			}


			editEventNotificationCriteriaService.saveEventNotificationCriteria(eventNotificationCriteria);
			Messagebox.show(Labels.getLabel("notifications.message.success"),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.INFORMATION,
					new EventListener<Event>() {
						public void onEvent(Event evt) {
							ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
						}
					});
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
			ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
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
						ZKSession.sendRedirect(PageURL.EVENT_NOTIFICATION_CRITERIA_LIST);
					case Messagebox.NO:
						break;
				}
			}
		});
	}

	@Command
	public void onToggleTrueFalse() {
		emptyRG.setSelectedItem(null);
	}

	@Command
	public void onToggleNotSpecified() {
		eventNotificationCriteria.setDisabledAccess(null);
		itemTransRG.setSelectedItem(null);
	}

	public EventNotificationCriteria getEventNotificationCriteria() {
		return eventNotificationCriteria;
	}

	public void setEventNotificationCriteria(EventNotificationCriteria eventNotificationCriteria) {
		this.eventNotificationCriteria = eventNotificationCriteria;
	}

	public EditEventNotificationCriteriaService getEditEventNotificationCriteriaService() {
		return editEventNotificationCriteriaService;
	}

	public void setEditEventNotificationCriteriaService(
			EditEventNotificationCriteriaService editEventNotificationCriteriaService) {
		this.editEventNotificationCriteriaService = editEventNotificationCriteriaService;
	}

	public TreeNode getSelectedParentEventCategory() {
		return selectedParentEventCategory;
	}

	public void setSelectedParentEventCategory(TreeNode selectedParentEventCategory) {
		this.selectedParentEventCategory = selectedParentEventCategory;
	}

	public Set<DefaultTreeNode<EventCategory>> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(Set<DefaultTreeNode<EventCategory>> selectedItems) {
		this.selectedItems = selectedItems;
	}
}
