package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.NotificationCriteria;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventNotificationCriteria;
import org.teiath.service.act.CreateActionNotificationCriteriaService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
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
public class CreateActionNotificationCriteriaVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CreateActionNotificationCriteriaVM.class.getName());

	@Wire("#tree")
	private Tree tree;
	@Wire("#itemTransRG")
	private Radiogroup itemTransRG;
	@Wire("#emptyRG")
	private Radiogroup emptyRG;

	@WireVariable
	private CreateActionNotificationCriteriaService createActionNotificationCriteriaService;

	private EventNotificationCriteria eventNotificationCriteria;
	private TreeNode selectedParentEventCategory;
	private Set<DefaultTreeNode<EventCategory>> selectedItems = new HashSet<DefaultTreeNode<EventCategory>>();

	@AfterCompose
	@NotifyChange("eventNotificationCriteria")
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		Selectors.wireEventListeners(view, this);

		eventNotificationCriteria = new EventNotificationCriteria();
		eventNotificationCriteria.setEventCategories(new HashSet<EventCategory>());
		eventNotificationCriteria.setUser(loggedUser);

		Collection<EventCategory> parentalCategories = null;

		try {
			parentalCategories = createActionNotificationCriteriaService.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			model.setMultiple(true);
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryRenderer());
		} catch (ServiceException e) {
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("notifications")),
					Labels.getLabel("common.messages.save_title"), Messagebox.OK, Messagebox.ERROR);
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

	@Listen("onSelect = #tree")
	public void onSelect(SelectEvent<Treeitem, String> event) {
		Treeitem ref = event.getReference();
		if (ref.isSelected()) {
			recursiveCheck((TreeNode<EventCategory>) ref.getValue());
		} else {
			recursiveUnCheck((TreeNode<EventCategory>) ref.getValue());
		}
	}

	public void recursiveCheck(TreeNode<EventCategory> treeNode) {
		for (TreeNode<EventCategory> node : treeNode.getChildren()) {
			selectedItems.add((DefaultTreeNode<EventCategory>) node);
			recursiveCheck(node);
		}
	}

	public void recursiveUnCheck(TreeNode<EventCategory> treeNode) {
		for (TreeNode<EventCategory> node : treeNode.getChildren()) {
			selectedItems.removeAll(treeNode.getChildren());
			recursiveUnCheck(node);
		}
	}

	@Command
	public void onSave() {

		if (selectedItems != null) {

			eventNotificationCriteria.setType(NotificationCriteria.TYPE_ACTIONS);
			if (selectedItems != null) {
				for (DefaultTreeNode defaultTreeNode : selectedItems) {
					EventCategory eventCategory = (EventCategory) defaultTreeNode.getData();
					eventNotificationCriteria.getEventCategories().add(eventCategory);
				}
			}
			try {
				createActionNotificationCriteriaService.saveEventNotificationCriteria(eventNotificationCriteria);
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
		} else {
			Messagebox.show(MessageBuilder.buildErrorMessage("Παρακαλώ επιλέξετε κατηγορία δράσης για να συνεχίσετε",
					Labels.getLabel("action")), Labels.getLabel("common.messages.save_title"), Messagebox.OK,
					Messagebox.ERROR);
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
