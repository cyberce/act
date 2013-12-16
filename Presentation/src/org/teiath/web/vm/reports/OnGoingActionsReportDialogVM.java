package org.teiath.web.vm.reports;

import org.apache.log4j.Logger;
import org.teiath.web.reports.common.ExcelToolkit;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Messagebox;

import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
public class OnGoingActionsReportDialogVM {

	static Logger log = Logger.getLogger(OnGoingActionsReportDialogVM.class.getName());

	private Date dateFrom;
	private Date dateTo;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
	}

	@Command
	public void onPrintPDF() {
		if (dateFrom != null && dateTo != null) {
			Report report = ReportToolkit.requestOnGoinfActionsReport(dateFrom, dateTo, ReportType.PDF);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} else {
			Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onPrintXLS() {
		if (dateFrom != null && dateTo != null) {
			Report report = ReportToolkit.requestOnGoinfActionsReport(dateFrom, dateTo, ReportType.EXCEL);
			report.setExcelReport(ExcelToolkit.ONGOING_ACTIONS);
			ZKSession.setAttribute("REPORT", report);
			ZKSession.sendPureRedirect(
					"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
		} else {
			Messagebox.show("Μη έγκυρη ημερομηνία", Labels.getLabel("common.messages.read_title"), Messagebox.OK,
					Messagebox.ERROR);
		}
	}

	@Command
	public void onCancel() {
		//		ZKSession.sendRedirect(PageURL.ROUTE_LIST);
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
}
