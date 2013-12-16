package org.teiath.web.reports.common;

import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Report
		implements Serializable {

	private Date dateFrom;
	private Date dateTo;
	private Integer numberFrom;
	private Integer numberTo;
	private String displayName;
	private String outputFileName;
	private String reportLocation;
	private String rootReportFile;
	private String imagesLocation;
	private int reportType;
	private int excelReport;
	private Map<String, Object> parameters;
	private Collection<Event> events;
	private Collection<EventInterest> eventInterests;

	public Report() {
		parameters = new HashMap<String, Object>();
	}

	public Report(int reportType) {
		parameters = new HashMap<>();
		this.reportType = reportType;
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

	public Integer getNumberFrom() {
		return numberFrom;
	}

	public void setNumberFrom(Integer numberFrom) {
		this.numberFrom = numberFrom;
	}

	public Integer getNumberTo() {
		return numberTo;
	}

	public void setNumberTo(Integer numberTo) {
		this.numberTo = numberTo;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	public String getReportLocation() {
		return reportLocation;
	}

	public void setReportLocation(String reportLocation) {
		this.reportLocation = reportLocation;
	}

	public String getRootReportFile() {
		return rootReportFile;
	}

	public void setRootReportFile(String rootReportFile) {
		this.rootReportFile = rootReportFile;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getImagesLocation() {
		return imagesLocation;
	}

	public void setImagesLocation(String imagesLocation) {
		this.imagesLocation = imagesLocation;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public int getExcelReport() {
		return excelReport;
	}

	public void setExcelReport(int excelReport) {
		this.excelReport = excelReport;
	}

	public Collection<Event> getEvents() {
		return events;
	}

	public void setEvents(Collection<Event> events) {
		this.events = events;
	}

	public Collection<EventInterest> getEventInterests() {
		return eventInterests;
	}

	public void setEventInterests(Collection<EventInterest> eventInterests) {
		this.eventInterests = eventInterests;
	}
}
