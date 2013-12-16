package org.teiath.data.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

@Service("mailManager")
public class MailManager
		implements IMailManager {

	private JavaMailSender mailSender;

	@Autowired
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendMail(String mailFrom, String mailTo, String subject, String mailBody)
			throws AddressException {
		this.sendMail(new InternetAddress(mailFrom), new InternetAddress(mailTo), subject, mailBody);
	}

	public void sendMail(final InternetAddress sender, final InternetAddress recipient, final String subject,
	                     final String body) {
		try {
			mailSender.send(new MimeMessagePreparator() {
				public void prepare(MimeMessage mimeMessage)
						throws Exception {
					MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "UTF-8");
					message.setFrom(sender);
					message.addTo(recipient);
					message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
					message.setText(body, true);
				}
			});
		} catch (MailSendException e) {
			e.printStackTrace();
		}
	}
}
