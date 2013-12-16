package org.teiath.data.domain.act;

import org.teiath.data.domain.Notification;
import org.teiath.data.domain.User;
import org.teiath.data.domain.image.ApplicationImage;
import org.teiath.data.domain.image.EventMainImage;
import org.teiath.data.domain.pdf.EventPDF;
import org.zkoss.calendar.impl.SimpleCalendarEvent;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "act_events")
public class Event
		extends SimpleCalendarEvent
		implements Serializable {

	public final static int EVENT_STATUS_PENDING = 0;
	public final static int EVENT_STATUS_IN_PROGRESS = 1;
	public final static int EVENT_STATUS_OVER = 2;

	@Id
	@Column(name = "event_id")
	@SequenceGenerator(name = "events_seq", sequenceName = "act_events_event_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_seq")
	private Integer id;

	@Column(name = "event_title", length = 2000, nullable = false)
	private String eventTitle;
	@Column(name = "event_description", length = 2000, nullable = true)
	private String eventDescription;
	@Column(name = "event_dateFrom", nullable = false)
	private Date dateFrom;
	@Column(name = "event_dateTo", nullable = false)
	private Date dateTo;
	@Column(name = "event_location", length = 2000, nullable = true)
	private String eventLocation;
	@Column(name = "event_price", precision = 6, scale = 2, nullable = true)
	private BigDecimal price;
	@Column(name = "event_disabledAccess", nullable = false)
	private boolean disabledAccess;
	@Column(name = "event_creation_date", nullable = true)
	private Date eventCreationDate;
	@Column(name = "event_code", length = 2000, nullable = false)
	private String code;
	@Column(name = "event_url", length = 200, nullable = true)
	private String url;
	@Column(name = "event_maximun_attendants", nullable = true)
	private Integer maximumAttendants;
	@Column(name = "event_address", length = 2000, nullable = false)
	private String eventAddress;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event_category_id", nullable = false)
	private EventCategory eventCategory;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "currency_id", nullable = false)
	private Currency currency;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
	private Set<EventInterest> eventInterests;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
	private Set<Notification> notifications;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
	private Set<ApplicationImage> eventImages;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "event_main_image_id", nullable = true)
	private EventMainImage eventMainImage;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "event")
	private Set<EventPDF> eventPDFs;

	private Double averageRating;

	private Integer eventStatus;

	@Transient
	private String priceWithCurrency;

	@Transient
	private Double eventRanking;
	@Transient
	private String eventRankingString;

	public Event() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getInterests() {
		int interests = 0;

		for (EventInterest eventInterest : this.eventInterests) {
			interests++;
		}

		return interests;
	}

	public Set<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<Notification> notifications) {
		this.notifications = notifications;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
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

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public boolean isDisabledAccess() {
		return disabledAccess;
	}

	public void setDisabledAccess(boolean disabledAccess) {
		this.disabledAccess = disabledAccess;
	}

	public EventCategory getEventCategory() {
		return eventCategory;
	}

	public void setEventCategory(EventCategory eventCategory) {
		this.eventCategory = eventCategory;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<EventInterest> getEventInterests() {
		return eventInterests;
	}

	public void setEventInterests(Set<EventInterest> eventInterests) {
		this.eventInterests = eventInterests;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public Date getEventCreationDate() {
		return eventCreationDate;
	}

	public void setEventCreationDate(Date eventCreationDate) {
		this.eventCreationDate = eventCreationDate;
	}

	public String getPriceWithCurrency() {
		return this.price.toString() + " " + this.currency.getName();
	}

	public void setPriceWithCurrency(String priceWithCurrency) {
		this.priceWithCurrency = priceWithCurrency;
	}

	public Integer getEventStatus() {

		Date today = new Date();

		if (today.before(this.getDateFrom())) {
			return EVENT_STATUS_PENDING;
		} else if (today.after(this.getDateFrom()) && today.before(this.getDateTo())) {
			return EVENT_STATUS_IN_PROGRESS;
		} else {
			return EVENT_STATUS_OVER;
		}
	}

	public void setEventStatus(Integer eventStatus) {
		this.eventStatus = eventStatus;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && this.id != null && obj.getClass() == Event.class && this.id.equals(((Event) obj).getId());
	}

	public EventMainImage getEventMainImage() {
		return eventMainImage;
	}

	public void setEventMainImage(EventMainImage eventMainImage) {
		this.eventMainImage = eventMainImage;
	}

	public Set<ApplicationImage> getEventImages() {
		return eventImages;
	}

	public void setEventImages(Set<ApplicationImage> eventImages) {
		this.eventImages = eventImages;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Set<EventPDF> getEventPDFs() {
		return eventPDFs;
	}

	public void setEventPDFs(Set<EventPDF> eventPDFs) {
		this.eventPDFs = eventPDFs;
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

	public String getEventAddress() {
		return eventAddress;
	}

	public void setEventAddress(String eventAddress) {
		this.eventAddress = eventAddress;
	}

	public Double getEventRanking() {
		return eventRanking;
	}

	public void setEventRanking(Double eventRanking) {
		this.eventRanking = eventRanking;
	}

	public String getEventRankingString() {
		return new Double(eventRanking.doubleValue() / 1000).toString();
	}

	public void setEventRankingString(String eventRankingString) {
		this.eventRankingString = eventRankingString;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
}
