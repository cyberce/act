package org.teiath.data.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("notificationProperties")
public class NotificationProperties {

	@Value("${notification.body}")
	private String notificationBody;

	@Value("${notificationEdit.body}")
	private String notificationEditBody;

	@Value("${notificationDelete.body}")
	private String notificationDeleteBody;

	@Value("${notificationInterest.body}")
	private String notificationInterestBody;

	@Value("${notificationInterestWithdraw.body}")
	private String notificationInterestWithdrawBody;

	public String getNotificationBody() {
		return notificationBody;
	}

	public void setNotificationBody(String notificationBody) {
		this.notificationBody = notificationBody;
	}

	public String getNotificationEditBody() {
		return notificationEditBody;
	}

	public void setNotificationEditBody(String notificationEditBody) {
		this.notificationEditBody = notificationEditBody;
	}

	public String getNotificationDeleteBody() {
		return notificationDeleteBody;
	}

	public void setNotificationDeleteBody(String notificationDeleteBody) {
		this.notificationDeleteBody = notificationDeleteBody;
	}

	public String getNotificationInterestBody() {
		return notificationInterestBody;
	}

	public void setNotificationInterestBody(String notificationInterestBody) {
		this.notificationInterestBody = notificationInterestBody;
	}

	public String getNotificationInterestWithdrawBody() {
		return notificationInterestWithdrawBody;
	}

	public void setNotificationInterestWithdrawBody(String notificationInterestWithdrawBody) {
		this.notificationInterestWithdrawBody = notificationInterestWithdrawBody;
	}
}
