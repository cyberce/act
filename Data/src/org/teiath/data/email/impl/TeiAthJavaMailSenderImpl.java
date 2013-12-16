package org.teiath.data.email.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.teiath.data.properties.EmailProperties;

@Component
public class TeiAthJavaMailSenderImpl
		extends JavaMailSenderImpl {

	@Autowired
	private EmailProperties emailProperties;

	@Override
	public String getHost() {
		return emailProperties.getHost();
	}

	@Override
	public int getPort() {
		return emailProperties.getPort();
	}
}
