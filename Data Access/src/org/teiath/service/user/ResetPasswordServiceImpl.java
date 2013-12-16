package org.teiath.service.user;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.teiath.data.dao.UserDAO;
import org.teiath.data.domain.User;
import org.teiath.data.email.IMailManager;
import org.teiath.data.properties.EmailProperties;
import org.teiath.service.exceptions.ResetExpirationException;
import org.teiath.service.exceptions.ResetMatchException;
import org.teiath.service.util.PasswordService;

import javax.mail.internet.AddressException;
import java.util.Calendar;
import java.util.Date;

@Service("resetPasswordService")
@Transactional
public class ResetPasswordServiceImpl
		implements ResetPasswordService {

	@Autowired
	UserDAO userDAO;
	@Autowired
	private IMailManager mailManager;
	@Autowired
	private EmailProperties emailProperties;

	@Override
	public boolean request(String value) {
		User user = userDAO.findByUsername(value);
		if (user == null) {
			user = userDAO.findByEmail(value);
		}

		if (user != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 1);
			user.setResetExpiration(calendar.getTime());

			user.setResetToken(RandomStringUtils.randomAlphanumeric(40));

			userDAO.save(user);

			String mailSubject = emailProperties.getUserResetPasswordSubject();
			String link = emailProperties.getDomain() + "reset_passwd.zul?id=" + user.getUserName() + "&token=" + user
					.getResetToken();
			String mailBody = emailProperties.getUserResetPasswordBody();

			StringBuilder htmlMessageBuiler = new StringBuilder();
			htmlMessageBuiler.append("<html> <body>");
			htmlMessageBuiler.append("Αγαπητό μέλος," + "<br>");
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler.append(mailBody + "<br>");
			htmlMessageBuiler.append("<a href=" + link + ">Επαναφορά κωδικού πρόσβασης</a>");
			htmlMessageBuiler.append("<br>");
			htmlMessageBuiler
					.append("<br>" + "Στη συνέχεια ακολουθείστε τις οδηγίες που εμφανίζονται στην οθόνη σας." + "<br>");
			htmlMessageBuiler.append("<br>" + "<i>Υπηρεσία Ενημέρωσης για Δράσεις</i>" + "<br>");
			htmlMessageBuiler.append("<br>" + "<b>Τ.Ε.Ι Αθήνας</b>" + "<br>");
			htmlMessageBuiler.append("</body> </html>");
			try {
				mailManager.sendMail(emailProperties.getFromAddress(), user.getEmail(), mailSubject,
						htmlMessageBuiler.toString());
			} catch (AddressException e) {
				e.printStackTrace();
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean reset(String username, String token, String passwd)
			throws ResetExpirationException, ResetMatchException {
		User user = userDAO.findByUsername(username);

		if (user != null) {
			if (! user.getResetToken().equals(token)) {
				throw new ResetMatchException();
			} else if (new Date().after(user.getResetExpiration())) {
				throw new ResetExpirationException();
			} else {
				user.setPassword(PasswordService.encrypt(passwd));
				user.setResetToken(null);
				user.setResetExpiration(null);
				userDAO.save(user);
				return true;
			}
		} else {
			throw new ResetMatchException();
		}
	}

	@Override
	public void validate(String username, String token)
			throws ResetExpirationException, ResetMatchException {
		User user = userDAO.findByUsername(username);

		if (user != null) {
			if (! token.equals(user.getResetToken())) {
				throw new ResetMatchException();
			} else if (new Date().after(user.getResetExpiration())) {
				throw new ResetExpirationException();
			}
		} else {
			throw new ResetMatchException();
		}
	}
}
