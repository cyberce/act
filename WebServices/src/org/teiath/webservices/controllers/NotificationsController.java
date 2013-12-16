package org.teiath.webservices.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.teiath.data.domain.Notification;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.NotificationSearchCriteria;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.service.ntf.ListNotificationsService;
import org.teiath.webservices.model.ServiceNotification;
import org.teiath.webservices.model.ServiceNotificationList;

import java.util.ArrayList;

@Controller
public class NotificationsController {

	@Autowired
	private ListNotificationsService listNotificationsService;


	private static final Logger logger_c = Logger.getLogger(NotificationsController.class);

	@RequestMapping(value = "/services/notifications", method = RequestMethod.GET)
	public ServiceNotificationList searchNotifications(String userCode) {

		ServiceNotificationList serviceNotificationList = new ServiceNotificationList();
		serviceNotificationList.setServiceNotifications(new ArrayList<ServiceNotification>());
		ServiceNotification serviceNotification = null;

		if (userCode != null) {
			NotificationSearchCriteria criteria = new NotificationSearchCriteria();
			try {
				criteria.setUserCode(userCode != null? userCode: "");

				criteria.setOrderField("sentDate");
				criteria.setOrderDirection("descending");
				criteria.setPageNumber(0);
				criteria.setPageSize(Integer.MAX_VALUE);

				SearchResults<Notification> results = listNotificationsService.searchNotificationsByCriteria(criteria);

				for (Notification notification : results.getData()) {
					serviceNotification = new ServiceNotification();
					serviceNotification.setTitle(notification.getTitle());
					serviceNotification.setBody(notification.getBody());
					serviceNotification.setSentDate(notification.getSentDate());
					serviceNotification.setUserName(notification.getUser().getFullName());

					if (notification.getEvent() != null) {
						serviceNotification.setEventCode(notification.getEvent().getCode());
					}

					serviceNotificationList.getServiceNotifications().add(serviceNotification);
				}
			}
			catch (ServiceException e) {
				e.printStackTrace();
			}
		}

		logger_c.debug("Returing Notifications: " + serviceNotification);
		return serviceNotificationList;
	}

}
