package org.teiath.webservices.viewers;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;
import org.teiath.data.properties.EmailProperties;
import org.teiath.webservices.model.ServiceEvent;
import org.teiath.webservices.model.ServiceEventList;
import org.teiath.webservices.model.ServiceNotification;
import org.teiath.webservices.model.ServiceNotificationList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RssViewer
		extends AbstractRssFeedView {

	@Autowired
	private EmailProperties emailProperties;

	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {
		feed.setTitle("RSS Web Service");
		feed.setDescription("Feed provided by T.E.I of Athens");
		feed.setLink("www.teiath.gr");
	}

	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request,
	                                    HttpServletResponse response)
			throws Exception {

		List<Item> items = new ArrayList<>();
		response.setContentType("application/xml;charset=UTF-8");

		if (model.get("serviceEventList") instanceof ServiceEventList) {
			ServiceEventList serviceEventList = (ServiceEventList) model.get("serviceEventList");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if (serviceEventList != null) {
				// Create feed items
				Item item;
				for (ServiceEvent serviceEvent : serviceEventList.getServiceEvents()) {
					item = new Item();
					item.setAuthor("T.E.I of Athens");
					item.setTitle(sdf.format(serviceEvent.getDateFrom()).toString() + "-" + sdf
							.format(serviceEvent.getDateTo()).toString() + ": " + serviceEvent
							.getTitle() + ", " + serviceEvent.getPlace());
					item.setPubDate(serviceEvent.getEventCreationDate());
					//todo link
					item.setLink(emailProperties.getDomain() + "previewEvent?code=" + serviceEvent.getCode());
					items.add(item);
				}
			}
		}

		if (model.get("serviceEvent") instanceof ServiceEvent) {
			ServiceEvent serviceEvent = (ServiceEvent) model.get("serviceEvent");

			if (serviceEvent != null) {
				// Create feed items
				Item item;
				item = new Item();
				item.setAuthor("ΤΕΙ Αθήνας");
				item.setTitle(serviceEvent.getTitle());
				item.setPubDate(serviceEvent.getEventCreationDate());
				item.setLink(emailProperties.getDomain() + "previewEvent?code=" + serviceEvent.getCode());
				items.add(item);
			}
		}

		if (model.get("serviceNotificationList") instanceof ServiceNotificationList) {
			ServiceNotificationList serviceNotificationList = (ServiceNotificationList) model.get("serviceNotificationList");

			if (serviceNotificationList != null) {
				// Create feed items
				Item item;
				for (ServiceNotification serviceNotification : serviceNotificationList.getServiceNotifications()) {
					item = new Item();
					item.setAuthor("TEI of Athens Carpooling Service");
					item.setTitle(serviceNotification.getTitle());
					item.setComments(serviceNotification.getBody());
					item.setPubDate(serviceNotification.getSentDate());
					if (serviceNotification.getEventCode() != null)
						item.setLink(emailProperties.getDomain() + "previewEvent?code=" + serviceNotification.getEventCode());
					else
						item.setLink(emailProperties.getDomain());
					items.add(item);
				}
			}
		}

		return items;
	}
}
