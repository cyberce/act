package org.teiath.webservices.model;

import org.codehaus.jackson.map.annotate.JsonRootName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "notifications")
@JsonRootName(value = "notifications")
public class ServiceNotificationList {

	private List<ServiceNotification> notifications;

	public ServiceNotificationList() {

	}

	public ServiceNotificationList(List<ServiceNotification> notifications) {
		this.notifications = notifications;
	}

	@XmlElement(name = "notification")
	public List<ServiceNotification> getServiceNotifications() {
		return this.notifications;
	}

	public void setServiceNotifications(List<ServiceNotification> notifications) {
		this.notifications = notifications;
	}
}
