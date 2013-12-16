package org.teiath.web.vm.reports;

import org.teiath.data.domain.act.EventCategory;
import org.teiath.service.act.ActionsByCategoryDialogService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class SelectEventReportVM
		extends BaseVM {

	@Wire("#typesCombo")
	Combobox typesCombo;
	@Wire("#dateFromRow")
	Row dateFromRow;
	@Wire("#dateToRow")
	Row dateToRow;
	@Wire("#categoryRow")
	Row categoryRow;
	@Wire("#tree")
	private Tree tree;

	@WireVariable
	private ActionsByCategoryDialogService actionsByCategoryDialogService;

	private Date dateFrom;
	private Date dateTo;
	private ListModelList<EventCategory> categories;
	private EventCategory selectedCategory;
	private TreeNode selectedParentEventCategory;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);

		Collection<EventCategory> parentalCategories = null;

		try {
			parentalCategories = actionsByCategoryDialogService.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryRenderer());
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
	public void onSelectReportType() {

		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			dateFromRow.setVisible(true);
			dateToRow.setVisible(true);
			categoryRow.setVisible(false);
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			dateFromRow.setVisible(false);
			dateToRow.setVisible(false);
			categoryRow.setVisible(true);
		}
	}

	@Command
	public void onPrintPDF() {

		if (selectedParentEventCategory != null)
			selectedCategory = (EventCategory) selectedParentEventCategory.getData();

		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestOnGoinfActionsReport(dateFrom, dateTo, ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			if (selectedCategory != null) {
				Report report = ReportToolkit.requestActionsByCategoryReport(selectedCategory.getId(), ReportType.PDF);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη κατηγορία δράσεων", Labels.getLabel("common.messages.read_title"),
						Messagebox.OK, Messagebox.ERROR);
			}
		}
	}

	@Command
	public void onPrintXLS() {
		selectedCategory = (EventCategory) selectedParentEventCategory.getData();
		if (typesCombo.getSelectedItem().getValue().toString().equals("0")) {
			if (dateFrom != null && dateTo != null) {
				Report report = ReportToolkit.requestOnGoinfActionsReport(dateFrom, dateTo, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.ONGOING_ACTIONS);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		} else if (typesCombo.getSelectedItem().getValue().toString().equals("1")) {
			if (selectedCategory != null) {
				Report report = ReportToolkit.requestActionsByCategoryReportXLS(selectedCategory, ReportType.EXCEL);
				report.setExcelReport(ExcelToolkit.ACTIONS_BY_CATEGORY);
				ZKSession.setAttribute("REPORT", report);
				ZKSession.sendPureRedirect(
						"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(),
						"_self");
			} else {
				Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
						Messagebox.ERROR);
			}
		}
	}

	public ListModelList<EventCategory> getCategories() {
		if (categories == null) {
			categories = new ListModelList<>();
			selectedCategory = new EventCategory();
			selectedCategory.setId(- 1);
			selectedCategory.setName("");
			categories.add(selectedCategory);
			try {
				categories.addAll(actionsByCategoryDialogService.getEventCategories());
			} catch (ServiceException e) {
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action.theme")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		}
		return categories;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public EventCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(EventCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public TreeNode getSelectedParentEventCategory() {
		return selectedParentEventCategory;
	}

	public void setSelectedParentEventCategory(TreeNode selectedParentEventCategory) {
		this.selectedParentEventCategory = selectedParentEventCategory;
	}
}
