package com.zoxial.fanpage.spider;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.joda.time.DateTime;

import akka.actor.UntypedActor;

public class MailActor extends UntypedActor {

	private static final String SMTP_HOST_NAME = "smtp.sendgrid.net";
	private static final String SMTP_AUTH_USER = "ideasba";
	private static final String SMTP_AUTH_PWD = "secret";

	@Override
	public void onReceive(Object msg) throws Exception {

		String m = (String) msg;

		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", SMTP_HOST_NAME);
		props.put("mail.smtp.port", 587);
		props.put("mail.smtp.auth", "true");

		Authenticator auth = new SMTPAuthenticator();
		Session mailSession = Session.getDefaultInstance(props, auth);
		// uncomment for debugging infos to stdout
		// mailSession.setDebug(true);
		Transport transport = mailSession.getTransport();

		MimeMessage message = new MimeMessage(mailSession);

		Multipart multipart = new MimeMultipart("alternative");

		BodyPart part1 = new MimeBodyPart();
		part1.setText(m);


		multipart.addBodyPart(part1);

		message.setContent(multipart);
		message.setFrom(new InternetAddress("facebookFetcher@noti-empresa.com"));
		message.setSubject("Facebook Fetcher Report " + new DateTime().toString());
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				"nfmelendez@gmail.com"));

		transport.connect();
		transport.sendMessage(message,
				message.getRecipients(Message.RecipientType.TO));
		transport.close();

	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		public PasswordAuthentication getPasswordAuthentication() {
			String username = SMTP_AUTH_USER;
			String password = SMTP_AUTH_PWD;
			return new PasswordAuthentication(username, password);
		}
	}

}
