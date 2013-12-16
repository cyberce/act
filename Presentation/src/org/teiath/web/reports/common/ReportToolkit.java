package org.teiath.web.reports.common;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.domain.act.EventInterest;

import java.util.Collection;
import java.util.Date;

public class ReportToolkit {

	public static Report requestOnGoinfActionsReport(Date dateFrom, Date dateTo, int reportType) {
		Report report = new Report();
		report.setDateFrom(dateFrom);
		report.setDateTo(dateTo);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("onGoingActionsReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("onGoingActionsReport.jasper");

		return report;
	}

	public static Report requestActionsByCategoryReport(int eventCategoryId, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("actionsByCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("actionsByCategoryReport.jasper");
		report.getParameters().put("EVENT_CATEGORY_ID", eventCategoryId);

		return report;
	}

	public static Report requestEventsReport(Collection<Event> events, int reportType) {
		Report report = new Report();
		report.setEvents(events);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("events" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("searchEventsReport.jasper");

		return report;
	}

	public static Report eventHistoryReport(Collection<EventInterest> eventInterests, int reportType) {
		Report report = new Report();
		report.setEventInterests(eventInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("events" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("eventHistoryReport.jasper");

		return report;
	}

	public static Report requestActionsByCategoryReportXLS(EventCategory eventCategory, int reportType) {
		Report report = new Report();
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("actionsByCategoryReport" + findFileExtension(reportType));
		report.setReportLocation("/reports/actions/");
		report.setImagesLocation("/img/");
		report.setRootReportFile("actionsByCategoryRepor.jasper");
		report.getParameters().put("EVENT_CATEGORY", eventCategory);

		return report;
	}

	public static Report requestEventInterestsReportXLS(Collection<EventInterest> eventInterests, int reportType) {
		Report report = new Report();
		report.setEventInterests(eventInterests);
		report.setReportType(reportType);
		report.setDisplayName("");
		report.setOutputFileName("participants" + findFileExtension(reportType));

		return report;
	}

	private static String findFileExtension(int reportType) {
		switch (reportType) {
			case ReportType.PDF:
				return ".pdf";
			case ReportType.EXCEL:
				return ".xls";
		}

		return null;
	}
}
