package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement(name = "event")
@JsonRootName(value = "event")
public class ServiceEvent {

	private String title;
	private String description;
	private String eventCategoryName;
	private Integer eventCategory;
	private String productStatusName;
	private Date dateFrom;
	private Date dateTo;
	private String place;
	private String address;
	private BigDecimal price;
	private Boolean disabledAccess;
	private Integer participantNumber;
	private Integer maximumAttendants;
	private Date eventCreationDate;
	private String code;
	private Integer currencyId;
	private String currencyName;
	private String url;

	public ServiceEvent() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventCategoryName() {
		return eventCategoryName;
	}

	public void setEventCategoryName(String eventCategoryName) {
		this.eventCategoryName = eventCategoryName;
	}

	public String getProductStatusName() {
		return productStatusName;
	}

	public void setProductStatusName(String productStatusName) {
		this.productStatusName = productStatusName;
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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Boolean isDisabledAccess() {
		return disabledAccess;
	}

	public void setDisabledAccess(Boolean disabledAccess) {
		this.disabledAccess = disabledAccess;
	}

	public Integer getParticipantNumber() {
		return participantNumber;
	}

	public void setParticipantNumber(Integer participantNumber) {
		this.participantNumber = participantNumber;
	}

	public Date getEventCreationDate() {
		return eventCreationDate;
	}

	public void setEventCreationDate(Date eventCreationDate) {
		this.eventCreationDate = eventCreationDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(Integer eventCategory) {
		this.eventCategory = eventCategory;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getMaximumAttendants() {
		return maximumAttendants;
	}

	public void setMaximumAttendants(Integer maximumAttendants) {
		this.maximumAttendants = maximumAttendants;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
}
