package com.elasticpath.commons.util.email.impl;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import com.elasticpath.commons.util.email.EmailSender;
import com.elasticpath.settings.SettingsReader;

/**
 * Default implementation of {@link EmailSender}.
 */
public class EmailSenderImpl implements EmailSender {
	
	private static final String SETTING_MAIL_CHARSET = "UTF-8";

	private static final String SETTING_MAIL_HOST = "COMMERCE/SYSTEM/EMAIL/mailHost";
	
	private static final String SETTING_MAIL_PORT = "COMMERCE/SYSTEM/EMAIL/mailPort";
	
	private SettingsReader settingsReader;

	/**
	 * Sets the SettingsReader property.
	 * @param settingsReader SettingsReader instance.
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	@Override
	public String sendEmail(final Email email) throws EmailException {
		prepareSendEmail(email);
		return email.send();
	}

	/**
	 * set up the mailHost and authentication account.
	 * @param email Email to be sent.
	 */
	protected void prepareSendEmail(final Email email) {
		email.setHostName(getMailHost());
		email.setSmtpPort(getMailPort());				
		email.setCharset(SETTING_MAIL_CHARSET);
	}

	/**
	 * Get setting value of mailHost from setting framework.
	 * 
	 * @return the setting value
	 */
	protected String getMailHost() {
		return settingsReader.getSettingValue(SETTING_MAIL_HOST).getValue();
	}

	/**
	 * Get setting value of mail port from setting framework.
	 * 
	 * @return the setting value
	 */
	protected int getMailPort() {
		return Integer.parseInt(settingsReader.getSettingValue(SETTING_MAIL_PORT).getValue());
	}
}
