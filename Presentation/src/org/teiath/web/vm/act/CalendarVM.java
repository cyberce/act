package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventInterest;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.act.CalendarService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarVM
		extends BaseVM {

	static Logger log = Logger.getLogger(CalendarVM.class.getName());

	@Wire("#cal")
	private Calendars cal;
	@Wire("#fromLabel")
	private Label fromLabel;
	@Wire("#toLabel")
	private Label toLabel;

	@WireVariable
	private CalendarService calendarService;

	private EventSearchCriteria eventSearchCriteria;
	private SimpleCalendarModel calendarModel;

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		Selectors.wireEventListeners(view, this);

		//Initial search criteria
		eventSearchCriteria = new EventSearchCriteria();
		calendarModel = new SimpleCalendarModel();
		SearchResults<Event> results = null;
		try {
			results = calendarService.searchEventsByCriteria(eventSearchCriteria);
			Collection<Event> events = results.getData();
			for (Event event : results.getData()) {
				calendarModel.add(event);
				for (EventInterest eventInterest : event.getEventInterests()) {
					 if (eventInterest.getUser().getId() == loggedUser.getId()) {
						 event.setContentColor("#33FF33");
						 event.setHeaderColor("#33FF33");
					 }

				}
			}
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("listing")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}

		fromLabel.setValue(new SimpleDateFormat("MMMM", Locale.forLanguageTag("el-GR")).format(cal.getBeginDate()));
		toLabel.setValue(new SimpleDateFormat("MMMM YYYY", Locale.forLanguageTag("el-GR")).format(cal.getEndDate()));
	}

	@Command
	public void gotoPrev() {
		cal.previousPage();
		fromLabel.setValue(new SimpleDateFormat("MMMM", Locale.forLanguageTag("el-GR")).format(cal.getBeginDate()));
		toLabel.setValue(new SimpleDateFormat("MMMM YYYY", Locale.forLanguageTag("el-GR")).format(cal.getEndDate()));
	}

	@Command
	public void gotoNext() {
		cal.nextPage();
		fromLabel.setValue(new SimpleDateFormat("MMMM", Locale.forLanguageTag("el-GR")).format(cal.getBeginDate()));
		toLabel.setValue(new SimpleDateFormat("MMMM YYYY", Locale.forLanguageTag("el-GR")).format(cal.getEndDate()));
	}

	@Command
	public void gotoToday() {
		TimeZone timeZone = cal.getDefaultTimeZone();
		cal.setCurrentDate(Calendar.getInstance(timeZone).getTime());
	}

	@Listen("onEventEdit=#cal")
	public void onClickEvent(CalendarsEvent event) {
		Event cEvent = (Event) event.getCalendarEvent();
		viewEvent(cEvent.getId());
	}

	public void viewEvent(Integer eventId) {
		ZKSession.setAttribute("fromCalendar", true);
		ZKSession.setAttribute("eventId", eventId);
		ZKSession.sendRedirect(PageURL.SEARCH_ACTION_VIEW);
	}

	public EventSearchCriteria getEventSearchCriteria() {
		return eventSearchCriteria;
	}

	public void setEventSearchCriteria(EventSearchCriteria eventSearchCriteria) {
		this.eventSearchCriteria = eventSearchCriteria;
	}

	public SimpleCalendarModel getCalendarModel() {
		return calendarModel;
	}

	public void setCalendarModel(SimpleCalendarModel calendarModel) {
		this.calendarModel = calendarModel;
	}
}
