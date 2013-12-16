package org.teiath.data.email;

import javax.mail.internet.AddressException;

public interface IMailManager {

	public void sendMail(String mailFrom, String mailTo, String subject, String mailBody)
			throws AddressException;
}
