/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */

package com.elasticpath.commons.util.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.MimeTypeParseException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.MailMessage;
import com.elasticpath.commons.util.Mailer;


/**
 * This class is responsible for generating email body and sending email.
 */
public final class MailerImpl implements Mailer {
	
	private static final Logger LOG = Logger.getLogger(MailerImpl.class);

	/**
	 * Send a text email with the given contentEncoding. If contentEncoding is null, it will use the platform's
	 * 
	 * @param contentEncoding the content encoding for the text email.
	 * @param mailMessage the mailer bean holding the email related information
	 * @throws EpSystemException on failure
	 */
	public void sendEmail(final String contentEncoding, final MailMessage mailMessage) throws EpSystemException {
		this.sanityCheck(mailMessage);
		if (mailMessage.getBody() == null) {
			throw new EpSystemException("Email body must be set before sending email.");
		}

		MimeMessage message = null;
		try {
			// Get the system properties
			Properties properties =  new Properties(System.getProperties());
			// Setup the mail server
			properties.put("mail.smtp.host", mailMessage.getMailHost());
			properties.put("mail.smtp.localhost", mailMessage.getMailHost());
			properties.put("mail.smtp.port", mailMessage.getMailPort());

			// Get the session
			Session session = Session.getDefaultInstance(properties, null);
			// Define the message
			message = new MimeMessage(session);
			message.setFrom(new InternetAddress(mailMessage.getAddressFrom()));
			message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(mailMessage.getAddressTo()));

			setAddressCcToMessage(message, mailMessage);
			setAddressBccToMessage(message, mailMessage);
			message.setSubject(mailMessage.getSubject());
			//message.setText(this.body, contentEncoding);
			if (contentEncoding == null) {
				message.setText(mailMessage.getBody());
			} else {
				message.setText(mailMessage.getBody(), contentEncoding);
			}

			// Send the message
			Transport.send(message);
		} catch (AddressException e) {
			throw new EpSystemException("Caught an exception", e);
		} catch (MessagingException e) {
			// When this exception occurs, log it and do not re-throw an exception so
			// that the system continues to work despite not being able to send mail
			LOG.error("Failed to send email!", e);
			logMessage(message);
		}
	}

	private void logMessage(final MimeMessage message) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("--------- Email message Start -----------\n" + mimeMessageToString(message));
			LOG.debug("--------- Email message end -----------");
		}
	}

	private String mimeMessageToString(final MimeMessage message) {
		if (message == null) {
			return null;
		}

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			message.writeTo(outputStream);
		} catch (IOException e) {
			throw new EpSystemException("Caught an exception.", e);
		} catch (MessagingException e) {
			LOG.error("Failed to convert message to string", e);
		}

		return outputStream.toString();
	}

	/**
	 * Send an html email.
	 * 
	 * @param mailMessage the mailer bean holding the email related information
	 * @throws EpSystemException on failure
	 */
	public void sendHtmlEmail(final MailMessage mailMessage) throws EpSystemException {
		this.sanityCheck(mailMessage);
		if (mailMessage.getBodyHtml() == null) {
			throw new EpSystemException("Email html body must be set before sending email.");
		}

		final MimeMultipart multiParts;
		/*
		 * mail.mime.charset String The default charset to be used by JavaMail. If not set (the normal case), the standard J2SE file.encoding System
		 * property is used. This allows applications to specify a default character set for sending messages that's different than the character set
		 * used for files stored on the system. This is common on Japanese systems. Windows was defaulting to Content-Type: text/html; charset=Cp1252
		 * which was screwing up viewing on some Outlook/Exchange servers. System.setProperty("mail.mime.charset", "iso-8859-1"); or
		 * properties.setProperty("mail.mime.charset", "iso-8859-1");
		 */
		
		final Properties properties =  new Properties(System.getProperties());
		properties.setProperty("mail.mime.charset", "utf-8");
		properties.put("mail.smtp.host", mailMessage.getMailHost());
		properties.put("mail.smtp.localhost", mailMessage.getMailHost());
		properties.put("mail.smtp.port", mailMessage.getMailPort());

		final Session session = Session.getDefaultInstance(properties, null);

		MimeMessage message = null;
		try {
			InternetAddress iaFrom = new InternetAddress(mailMessage.getAddressFrom());
			if (mailMessage.getFromName() != null && mailMessage.getFromName().length() > 0) {
				iaFrom.setPersonal(mailMessage.getFromName());
			}

			message = new MimeMessage(session);
			
			setReturnPath(mailMessage, message);
			
			message.setHeader("Content-Type", "multipart/mixed");
			message.setFrom(iaFrom);
			message.setSubject(mailMessage.getSubject());

			if (isBothBodiesDefined(mailMessage)) {
				// sending both HTML & text versions
				multiParts = new MimeMultipart("alternative");
				MimeBodyPart htmlBodyPart = new MimeBodyPart();
				MimeBodyPart textBodyPart = new MimeBodyPart();
				textBodyPart.setText(mailMessage.getBody());
				multiParts.addBodyPart(textBodyPart);

				javax.activation.MimeType type = new javax.activation.MimeType();
				type.setPrimaryType("text");
				type.setSubType("plain");
				type.setParameter("charset", "utf-8");

				htmlBodyPart.setContent(mailMessage.getBodyHtml(), type.toString());
				htmlBodyPart.setHeader("Content-Type", "text/html; charset=utf-8");

				multiParts.addBodyPart(htmlBodyPart);
				message.setContent(multiParts);
			} else if (isTextBodyDefinedOnly(mailMessage)) {
				// only sending HTML
				multiParts = new MimeMultipart();
				MimeBodyPart htmlBodyPart = new MimeBodyPart();

				javax.activation.MimeType type = new javax.activation.MimeType();
				type.setPrimaryType("text");
				type.setSubType("plain");
				type.setParameter("charset", "utf-8");

				htmlBodyPart.setContent(mailMessage.getBodyHtml(), type.toString());

				multiParts.addBodyPart(htmlBodyPart);
				message.setContent(multiParts);
			}

			setAddressTo(message, mailMessage);

			setAddressCcToMessage(message, mailMessage);
			setAddressBccToMessage(message, mailMessage);

			message.saveChanges();

			Transport.send(message);

		} catch (MimeTypeParseException mpe) {
			throw new EpSystemException("Email.sendHtmlEmail: MimeTypeParseException", mpe);
		} catch (MessagingException mex) {
			// When this exception occurs, log it and do not re-throw an exception so
			// that the system continues to work despite not being able to send mail
			LOG.error("Failed to send email!", mex);
			logMessage(message);
		} catch (UnsupportedEncodingException usee) {
			throw new EpSystemException("Email.sendHtmlEmail: UnsupportedEncodingException", usee);
		}
	}

	/**
	 * Sets the returnPath property to a message.
	 */
	private void setReturnPath(final MailMessage mailMessage, final MimeMessage message) throws MessagingException {
		if (mailMessage.getReturnPath() != null) {
			message.setHeader("Return-Path", mailMessage.getReturnPath());
		}
	}

	/**
	 *
	 * @param mailMessage
	 * @return
	 */
	private boolean isTextBodyDefinedOnly(final MailMessage mailMessage) {
		return mailMessage.getBodyHtml() != null && mailMessage.getBody() == null;
	}

	/**
	 *
	 * @param mailMessage
	 * @return
	 */
	private boolean isBothBodiesDefined(final MailMessage mailMessage) {
		return mailMessage.getBodyHtml() != null && mailMessage.getBody() != null;
	}

	/**
	 * Sets the address field of a message.
	 */
	private void setAddressTo(final MimeMessage message, final MailMessage mailMessage) 
		throws UnsupportedEncodingException, MessagingException {
		
		final InternetAddress iaTo = new InternetAddress(mailMessage.getAddressTo());
		if (mailMessage.getToName() != null) {
			iaTo.setPersonal(mailMessage.getToName());
		}
		message.addRecipient(javax.mail.Message.RecipientType.TO, iaTo);

	}

	/**
	 * Sets the BCC field of a message.
	 */
	private void setAddressBccToMessage(final MimeMessage message, final MailMessage mailMessage) throws MessagingException {
		if (mailMessage.getAddressBCC() != null && mailMessage.getAddressBCC().length() > 0) {
			message.addRecipient(javax.mail.Message.RecipientType.BCC, new InternetAddress(mailMessage.getAddressBCC()));
		}
	}

	/**
	 * Sets the CC field of a message.
	 */
	private void setAddressCcToMessage(final MimeMessage message, final MailMessage mailMessage) throws MessagingException {
		if (mailMessage.getAddressCC() != null && mailMessage.getAddressCC().length() > 0) {
			message.addRecipient(javax.mail.Message.RecipientType.CC, new InternetAddress(mailMessage.getAddressCC()));
		}
	}

	private void sanityCheck(final MailMessage mailMessage) {
		if (mailMessage.getAddressTo() == null) {
			throw new EpSystemException("Email addressTo must be set before sending email.");
		}
		if (mailMessage.getAddressFrom() == null) {
			throw new EpSystemException("Email addressFrom must be set before sending email.");
		}
		if (mailMessage.getSubject() == null) {
			throw new EpSystemException("Email subject must be set before sending email.");
		}
		if (mailMessage.getMailHost() == null) {
			throw new EpSystemException("Mail host must be set before sending email.");
		}
	}
}
