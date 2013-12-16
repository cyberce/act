package org.teiath.data.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("emailProperties")
public class EmailProperties {

	@Value("${email.domain}")
	private String domain;

	@Value("${email.host}")
	private String host;

	@Value("${email.port}")
	private int port;

	@Value("${email.from}")
	private String fromAddress;

	@Value("${email.enabled}")
	private Boolean emailEnabled;

	@Value("${email.route_create.subject}")
	private String routeCreateSubject;

	@Value("${email.route_create.body}")
	private String routeCreateBody;

	@Value("${email.route_edit.subject}")
	private String routeEditSubject;

	@Value("${email.route_edit.body}")
	private String routeEditBody;

	@Value("${email.route_delete.subject}")
	private String routeDeleteSubject;

	@Value("${email.route_delete.body}")
	private String routeDeleteBody;

	@Value("${email.routeInterest_approve.body}")
	private String routeInterestApproveBody;

	@Value("${email.routeInterest_approve.subject}")
	private String routeInterestApproveSubject;

	@Value("${email.routeInterest_reject.body}")
	private String routeInterestRejectBody;

	@Value("${email.routeInterest_reject.subject}")
	private String routeInterestRejectSubject;

	@Value("${email.routeInterest.body}")
	private String routeInterestBody;

	@Value("${email.routeInterest.subject}")
	private String routeInterestSubject;

	@Value("${email.routeInterest_withdraw.body}")
	private String routeInterestWithdrawBody;

	@Value("${email.routeInterest_withdraw.subject}")
	private String routeInterestWithdrawSubject;

	@Value("${email.accommodation_enable.subject}")
	private String accommodationEnableSubject;

	@Value("${email.accommodation_enable.body}")
	private String accommodationEnableBody;

	@Value("${email.user_create.subject}")
	private String userCreateSubject;

	@Value("${email.user_create.body}")
	private String userCreateBody;

	@Value("${email.user_resetPassword.subject}")
	private String userResetPasswordSubject;

	@Value("${email.user_resetPassword.body}")
	private String userResetPasswordBody;

	@Value("${email.user_ban.subject}")
	private String userBanSubject;

	@Value("${email.user_ban.body}")
	private String userBanBody;

	@Value("${email.user_restore.subject}")
	private String userRestoreSubject;

	@Value("${email.user_restore.body}")
	private String userRestoreBody;

	@Value("${email.reservations.subject}")
	private String reservationSubject;

	@Value("${email.reservations.body}")
	private String reservationBody;


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public Boolean isEmailEnabled() {
		return emailEnabled;
	}

	public Boolean getEmailEnabled() {
		return emailEnabled;
	}

	public void setEmailEnabled(Boolean emailEnabled) {
		this.emailEnabled = emailEnabled;
	}

	public String getRouteCreateSubject() {
		return routeCreateSubject;
	}

	public void setRouteCreateSubject(String routeCreateSubject) {
		this.routeCreateSubject = routeCreateSubject;
	}

	public String getRouteCreateBody() {
		return routeCreateBody;
	}

	public void setRouteCreateBody(String routeCreateBody) {
		this.routeCreateBody = routeCreateBody;
	}

	public String getRouteEditSubject() {
		return routeEditSubject;
	}

	public void setRouteEditSubject(String routeEditSubject) {
		this.routeEditSubject = routeEditSubject;
	}

	public String getRouteEditBody() {
		return routeEditBody;
	}

	public void setRouteEditBody(String routeEditBody) {
		this.routeEditBody = routeEditBody;
	}

	public String getRouteDeleteSubject() {
		return routeDeleteSubject;
	}

	public void setRouteDeleteSubject(String routeDeleteSubject) {
		this.routeDeleteSubject = routeDeleteSubject;
	}

	public String getRouteDeleteBody() {
		return routeDeleteBody;
	}

	public void setRouteDeleteBody(String routeDeleteBody) {
		this.routeDeleteBody = routeDeleteBody;
	}

	public String getRouteInterestApproveBody() {
		return routeInterestApproveBody;
	}

	public void setRouteInterestApproveBody(String routeInterestApproveBody) {
		this.routeInterestApproveBody = routeInterestApproveBody;
	}

	public String getRouteInterestApproveSubject() {
		return routeInterestApproveSubject;
	}

	public void setRouteInterestApproveSubject(String routeInterestApproveSubject) {
		this.routeInterestApproveSubject = routeInterestApproveSubject;
	}

	public String getRouteInterestRejectBody() {
		return routeInterestRejectBody;
	}

	public void setRouteInterestRejectBody(String routeInterestRejectBody) {
		this.routeInterestRejectBody = routeInterestRejectBody;
	}

	public String getRouteInterestRejectSubject() {
		return routeInterestRejectSubject;
	}

	public void setRouteInterestRejectSubject(String routeInterestRejectSubject) {
		this.routeInterestRejectSubject = routeInterestRejectSubject;
	}

	public String getRouteInterestBody() {
		return routeInterestBody;
	}

	public void setRouteInterestBody(String routeInterestBody) {
		this.routeInterestBody = routeInterestBody;
	}

	public String getRouteInterestSubject() {
		return routeInterestSubject;
	}

	public void setRouteInterestSubject(String routeInterestSubject) {
		this.routeInterestSubject = routeInterestSubject;
	}

	public String getRouteInterestWithdrawBody() {
		return routeInterestWithdrawBody;
	}

	public void setRouteInterestWithdrawBody(String routeInterestWithdrawBody) {
		this.routeInterestWithdrawBody = routeInterestWithdrawBody;
	}

	public String getRouteInterestWithdrawSubject() {
		return routeInterestWithdrawSubject;
	}

	public void setRouteInterestWithdrawSubject(String routeInterestWithdrawSubject) {
		this.routeInterestWithdrawSubject = routeInterestWithdrawSubject;
	}

	public String getAccommodationEnableSubject() {
		return accommodationEnableSubject;
	}

	public void setAccommodationEnableSubject(String accommodationEnableSubject) {
		this.accommodationEnableSubject = accommodationEnableSubject;
	}

	public String getAccommodationEnableBody() {
		return accommodationEnableBody;
	}

	public void setAccommodationEnableBody(String accommodationEnableBody) {
		this.accommodationEnableBody = accommodationEnableBody;
	}

	public String getUserCreateSubject() {
		return userCreateSubject;
	}

	public void setUserCreateSubject(String userCreateSubject) {
		this.userCreateSubject = userCreateSubject;
	}

	public String getUserCreateBody() {
		return userCreateBody;
	}

	public void setUserCreateBody(String userCreateBody) {
		this.userCreateBody = userCreateBody;
	}

	public String getUserResetPasswordBody() {
		return userResetPasswordBody;
	}

	public void setUserResetPasswordBody(String userResetPasswordBody) {
		this.userResetPasswordBody = userResetPasswordBody;
	}

	public String getUserResetPasswordSubject() {
		return userResetPasswordSubject;
	}

	public void setUserResetPasswordSubject(String userResetPasswordSubject) {
		this.userResetPasswordSubject = userResetPasswordSubject;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getUserBanSubject() {
		return userBanSubject;
	}

	public void setUserBanSubject(String userBanSubject) {
		this.userBanSubject = userBanSubject;
	}

	public String getUserBanBody() {
		return userBanBody;
	}

	public void setUserBanBody(String userBanBody) {
		this.userBanBody = userBanBody;
	}

	public String getUserRestoreSubject() {
		return userRestoreSubject;
	}

	public void setUserRestoreSubject(String userRestoreSubject) {
		this.userRestoreSubject = userRestoreSubject;
	}

	public String getUserRestoreBody() {
		return userRestoreBody;
	}

	public void setUserRestoreBody(String userRestoreBody) {
		this.userRestoreBody = userRestoreBody;
	}

	public String getReservationSubject() {
		return reservationSubject;
	}

	public void setReservationSubject(String reservationSubject) {
		this.reservationSubject = reservationSubject;
	}

	public String getReservationBody() {
		return reservationBody;
	}

	public void setReservationBody(String reservationBody) {
		this.reservationBody = reservationBody;
	}
}
