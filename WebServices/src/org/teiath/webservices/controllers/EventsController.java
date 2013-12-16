package org.teiath.webservices.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.teiath.data.domain.User;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.act.CreateActionService;
import org.teiath.service.act.EditActionService;
import org.teiath.service.act.ListEventsService;
import org.teiath.service.act.SearchEventsService;
import org.teiath.service.exceptions.AuthenticationException;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.user.UserLoginService;
import org.teiath.service.values.EditCurrencyService;
import org.teiath.webservices.authentication.Utils;
import org.teiath.webservices.model.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class EventsController {

	@Autowired
	private SearchEventsService searchEventsService;
	@Autowired
	private CreateActionService createActionService;
	@Autowired
	private EditActionService editActionService;
	@Autowired
	private ListEventsService listEventsService;
	@Autowired
	private EditCurrencyService editCurrencyService;
	@Autowired
	private UserLoginService userLoginService;

	private static final Logger logger_c = Logger.getLogger(EventsController.class);

	@RequestMapping(value = "/services/events", method = RequestMethod.GET)
	public ServiceEventList searchEvents(String category, String dFrom, String dTo, Boolean dAccess, String keyword, String orderBy, String orderDir, Integer pn, Integer ps) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		ServiceEventList serviceEventList = new ServiceEventList();
		serviceEventList.setServiceEvents(new ArrayList<ServiceEvent>());
		ServiceEvent serviceEvent = null;

		EventSearchCriteria criteria = new EventSearchCriteria();
		try {
			// Multi category criteria
			if (category != null) {
				StringTokenizer st = new StringTokenizer(category, "|");
				Set<EventCategory> eventCategoryHashSet = new HashSet<>();
				EventCategory eventCategory;
				while (st.hasMoreTokens()) {
					try {
						eventCategory = searchEventsService.getEventCategory(Integer.parseInt(st.nextToken()));
						if (eventCategory != null) {
							eventCategoryHashSet.add(eventCategory);
						}
					} catch (NumberFormatException e) {	}
				}
				if (!eventCategoryHashSet.isEmpty()) {
					criteria.setEventCategories(eventCategoryHashSet);
				}
			}
			
			criteria.setDateFrom(dFrom != null ? sdf.parse(dFrom) : null);
			criteria.setDateTo(dTo != null ? sdf.parse(dTo) : null);
			criteria.setEventKeyword(keyword != null ? new String(keyword.getBytes("ISO-8859-1"), "UTF-8") : null);
			criteria.setDisabledAccess(dAccess);

			// Paging and sorting criteria
			criteria.setPageNumber(pn == null? 0: pn);
			criteria.setPageSize(ps == null? Integer.MAX_VALUE: ps);
			criteria.setOrderField(orderBy);
			if (orderDir != null) {
				if (orderDir.equals("asc")) {
					criteria.setOrderDirection("ascending");
				} else if (orderDir.equals("desc")) {
					criteria.setOrderDirection("descending");
				}
			}

			SearchResults<Event> results = searchEventsService.searchEvents(criteria);

			for (Event event : results.getData()) {
				serviceEvent = new ServiceEvent();
				serviceEvent.setCode(event.getCode());
				serviceEvent.setTitle(event.getEventTitle());
				serviceEvent.setDescription(event.getEventDescription());
				serviceEvent.setEventCategoryName(event.getEventCategory().getName());
				serviceEvent.setDateFrom(event.getDateFrom());
				serviceEvent.setDateTo(event.getDateTo());
				serviceEvent.setPlace(event.getEventLocation());
				serviceEvent.setAddress(event.getEventAddress());
				serviceEvent.setPrice(event.getPrice());
				serviceEvent.setCurrencyName(event.getCurrency().getName());
				serviceEvent.setUrl(event.getUrl());

				serviceEvent.setDisabledAccess(event.isDisabledAccess());
				serviceEvent.setParticipantNumber(event.getInterests());
				serviceEvent.setEventCreationDate(event.getEventCreationDate());

				serviceEventList.getServiceEvents().add(serviceEvent);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return serviceEventList;
	}

	@RequestMapping(value = "/services/event", method = RequestMethod.GET)
	public ServiceEvent searchEventByCode(String code) {

		ServiceEvent serviceEvent = new ServiceEvent();
		try {

			Event event = searchEventsService.getEventByCode(code);
			serviceEvent.setTitle(event.getEventTitle());
			serviceEvent.setDescription(event.getEventDescription());
			serviceEvent.setEventCategoryName(event.getEventCategory().getName());
			serviceEvent.setDateFrom(event.getDateFrom());
			serviceEvent.setDateTo(event.getDateTo());
			serviceEvent.setPlace(event.getEventLocation());
			serviceEvent.setAddress(event.getEventAddress());
			serviceEvent.setDisabledAccess(event.isDisabledAccess());
			serviceEvent.setParticipantNumber(event.getInterests());
			serviceEvent.setEventCreationDate(event.getEventCreationDate());
			serviceEvent.setCode(event.getCode());
			serviceEvent.setPrice(event.getPrice());
			serviceEvent.setCurrencyName(event.getCurrency().getName());
			serviceEvent.setUrl(event.getUrl());
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Event: " + serviceEvent);

		return serviceEvent;
	}

	@RequestMapping(value = "/services/event", method = RequestMethod.POST, headers="Accept=application/xml, application/json")
	public @ResponseBody ResponseMessages saveEvent(@RequestBody ServiceEvent event, HttpServletRequest request) {
		ResponseMessages responseMessages = new ResponseMessages();

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			responseMessages.addMessage("This method needs authorization");
			return responseMessages;
		} else {
			try {
				String[] credentials = Utils.decodeHeader(authorization);
				User user = userLoginService.wsLogin(credentials[0], credentials[1]);

				if (user == null) {
					throw new AuthenticationException("Invalid Username/Password");
				}
				
				if (event.getTitle() == null) {
					responseMessages.addMessage("Title is mandatory");
				}
				if (event.getDescription() == null) {
					responseMessages.addMessage("Description is mandatory");
				}
				if (event.getEventCategory() == null) {
					responseMessages.addMessage("Category is mandatory");
				}
				if (event.getDateFrom() == null) {
					responseMessages.addMessage("Date From is mandatory");
				}
				if (event.getDateTo() == null) {
					responseMessages.addMessage("Date To is mandatory");
				}
				if (event.getPlace() == null) {
					responseMessages.addMessage("Place is mandatory");
				}
				if (event.getAddress() == null) {
					responseMessages.addMessage("Address is mandatory");
				}

				if (responseMessages.getMessage() != null) {
					return responseMessages;
				} else {
					Event dataEvent = new Event();
					dataEvent.setEventTitle(event.getTitle());
					dataEvent.setEventDescription(event.getDescription());
					dataEvent.setEventCategory(searchEventsService.getEventCategory(event.getEventCategory()));
					dataEvent.setDateFrom(event.getDateFrom());
					dataEvent.setDateTo(event.getDateTo());
					dataEvent.setEventLocation(event.getPlace());
					dataEvent.setEventAddress(event.getAddress());
					if (event.getCurrencyId() != null) {
						dataEvent.setCurrency(editCurrencyService.getCurrencyById(event.getCurrencyId()));
					} else {
						// Default value (EUR)
						dataEvent.setCurrency(editCurrencyService.getCurrencyById(1));
					}
					if (event.getUrl() != null) {
						dataEvent.setUrl(event.getUrl());
					}
					if (event.getPrice() != null) {
						dataEvent.setPrice(event.getPrice());
					} else {
						// Default value (0.0)
						dataEvent.setPrice(new BigDecimal(0.0));
					}
					if (event.isDisabledAccess() != null) {
						dataEvent.setDisabledAccess(event.isDisabledAccess());
					} else {
						// Default value (true)
						dataEvent.setDisabledAccess(true);
					}
					if (event.getMaximumAttendants() != null) {
						dataEvent.setMaximumAttendants(event.getMaximumAttendants());
					}
					dataEvent.setEventStatus(0);
					dataEvent.setUser(user);

					createActionService.saveEvent(dataEvent);

					responseMessages.addMessage("Event is successfully created");
				}
			} catch (ServiceException e) {
				responseMessages.addMessage("Event persist has failed");
			} catch (AuthenticationException e) {
				responseMessages.addMessage(e.getMessage());
			}
		}

		return responseMessages;
	}

	@RequestMapping(value = "/services/event", method = RequestMethod.PUT, headers="Accept=application/xml, application/json")
	public @ResponseBody ResponseMessages updateEvent(String code, @RequestBody ServiceEvent event, HttpServletRequest request) {
		ResponseMessages responseMessages = new ResponseMessages();

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			responseMessages.addMessage("This method needs authorization");
			return responseMessages;
		} else {
			try {
				String[] credentials = Utils.decodeHeader(authorization);
				User user = userLoginService.wsLogin(credentials[0], credentials[1]);

				if (user == null) {
					throw new AuthenticationException("Invalid Username/Password");
				}

				if (code == null) {
					responseMessages.addMessage("Parameter 'code' is missing");
					return responseMessages;
				}

				if (responseMessages.getMessage() != null) {
					return responseMessages;
				} else {
					Event dataEvent = editActionService.getEventByCode(code);

					if (dataEvent.getUser().getId().intValue() != user.getId().intValue()) {
						responseMessages.addMessage("Permission Denied. You don't own the selected Event");
						return responseMessages;
					}

					if (event.getTitle() != null) {
						dataEvent.setEventTitle(event.getTitle());
					}
					if (event.getDescription() != null) {
						dataEvent.setEventDescription(event.getDescription());
					}
					if (event.getEventCategory() != null) {
						dataEvent.setEventCategory(searchEventsService.getEventCategory(event.getEventCategory()));
					}
					if (event.getDateFrom() != null) {
						dataEvent.setDateFrom(event.getDateFrom());
					}
					if (event.getDateTo() != null) {
						dataEvent.setDateTo(event.getDateTo());
					}
					if (event.getPlace() != null) {
						dataEvent.setEventLocation(event.getPlace());
					}
					if (event.getAddress() != null) {
						dataEvent.setEventAddress(event.getAddress());
					}
					if (event.getCurrencyId() != null) {
						dataEvent.setCurrency(editCurrencyService.getCurrencyById(event.getCurrencyId()));
					}
					if (event.getUrl() != null) {
						dataEvent.setUrl(event.getUrl());
					}
					if (event.getPrice() != null) {
						dataEvent.setPrice(event.getPrice());
					}
					if (event.isDisabledAccess() != null) {
						dataEvent.setDisabledAccess(event.isDisabledAccess());
					}
					if (event.getMaximumAttendants() != null) {
						dataEvent.setMaximumAttendants(event.getMaximumAttendants());
					}

					editActionService.saveEvent(dataEvent);

					responseMessages.addMessage("Event is successfully updated");
				}
			} catch (AuthenticationException e) {
				responseMessages.addMessage(e.getMessage());
			} catch (ServiceException e) {
				responseMessages.addMessage("Event update has failed");
				responseMessages.addMessage(e.getMessage());
				e.printStackTrace();
			}
		}

		return responseMessages;
	}

	@RequestMapping(value = "/services/event", method = RequestMethod.DELETE)
	public @ResponseBody ResponseMessages deleteEvent(String code, HttpServletRequest request) {
		ResponseMessages responseMessages = new ResponseMessages();

		String authorization = request.getHeader("Authorization");
		if (authorization == null) {
			responseMessages.addMessage("This method needs authorization");
			return responseMessages;
		} else {
			try {
				String[] credentials = Utils.decodeHeader(authorization);
				User user = userLoginService.wsLogin(credentials[0], credentials[1]);

				if (user == null) {
					throw new AuthenticationException("Invalid Username/Password");
				}

				if (code == null) {
					responseMessages.addMessage("Parameter 'code' is missing");
					return responseMessages;
				}

				Event dataEvent = editActionService.getEventByCode(code);

				if (dataEvent.getUser().getId().intValue() != user.getId().intValue()) {
					responseMessages.addMessage("Permission Denied. You don't own the selected Event");
					return responseMessages;
				}

				listEventsService.deleteEvent(dataEvent);

				responseMessages.addMessage("Event is successfully deleted");

			} catch (AuthenticationException e) {
				responseMessages.addMessage(e.getMessage());
			} catch (ServiceException e) {
				responseMessages.addMessage("Event delete has failed");
				e.printStackTrace();
			}
		}

		return responseMessages;
	}

	@RequestMapping(value = "/services/categories", method = RequestMethod.GET)
	public ServiceCategoryList getEventCategories() {
		ServiceCategoryList serviceCategoryList = new ServiceCategoryList();
		serviceCategoryList.setServiceCategories(new ArrayList<ServiceCategory>());
		ServiceCategory serviceCategory;

		try {

			Collection<EventCategory> results = searchEventsService.getEventCategories();

			for (EventCategory eventCategory : results) {
				serviceCategory = new ServiceCategory();
				serviceCategory.setId(eventCategory.getId());
				serviceCategory.setTitle(eventCategory.getName());
				serviceCategory.setParentId(eventCategory.getParentCategory().getId());

				serviceCategoryList.getServiceCategories().add(serviceCategory);
			}

		} catch (ServiceException e) {
			e.printStackTrace();
		}

		return serviceCategoryList;
	}

}
