package com.elasticpath.test;

import java.io.IOException;
import java.net.ServerSocket;

import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Controls a live smtp server.
 */
public class RealEmailFacade {
	/** Name of the environment variable to check for the mail port number. */
	public static final String EP_SMTP_PORT_ENV_VARIABLE = "EP_SMTP_PORT";
	/** Default mail port to use if {@link #EP_SMTP_PORT_ENV_VARIABLE} environment variable not provided. */
	public static final int DEFAULT_EP_SMTP_PORT = 2525;

	private static final Logger LOG = Logger.getLogger(RealEmailFacade.class);
	private static final String MAIL_HOST = "localhost";
	private static final String MAIL_HOST_SETTING = "COMMERCE/SYSTEM/EMAIL/mailHost";
	private static final String MAIL_PORT_SETTING = "COMMERCE/SYSTEM/EMAIL/mailPort";

	private GreenMail greenMail;
	private int mailPort = -1;
	
	@Autowired 
	private SettingsService settingsService;

	/**
	 * @return smtp port to use
	 */
	public int getMailPort() {
		if (mailPort > 0) {
			return mailPort;
		}

		int port = -1;
		// use the environment variable if present
		final String envVariable = System.getenv(EP_SMTP_PORT_ENV_VARIABLE);
		if (envVariable != null) {
			try {
				port = Integer.parseInt(envVariable);
			} catch (final NumberFormatException e) {
				LOG.warn(EP_SMTP_PORT_ENV_VARIABLE + " environment variable not an integer: " + envVariable, e);
			}
		}
		if (port < 0) {
			try {
				/*
				 * Can't use a random integer here because green mail just fails if its in use, using a socket here
				 * guarantees one if one is available, albeit at the cost of creating a temporary socket.
				 */
				ServerSocket socket = new ServerSocket(0);
				port = socket.getLocalPort();
				socket.close();
			} catch (IOException e) {
				LOG.warn("Error retrieving socket port (ignoring)", e);
				// ignore
				port = DEFAULT_EP_SMTP_PORT;
			}
		}

		LOG.info("Mail port configured as " + port);
		mailPort = port;
		return port;
	}

	/**
	 * Starts a smtp server ready for emails.
	 */
	public void startServer() {
		if (greenMail != null) {
			LOG.warn("The previous test did not shut down the server in tearDown. stopping the server now.");
			stopServer();
		}

		configureDatabase();

		int port = getMailPort();
		LOG.info("Initializing Fake mail server on port " + port);
		greenMail = new GreenMail(new ServerSetup(port, null, ServerSetup.PROTOCOL_SMTP));
		greenMail.start();
		LOG.info("Initializing Fake mail server...done");
	}

	/**
	 * Stops the smtp server.
	 */
	public void stopServer() {
		if (greenMail != null) {
			LOG.info("Stopping mail server");
			setEmailEnabled(false);
			greenMail.stop();
		}
		greenMail = null;
	}

	/**
	 * @return received {@link MimeMessage}s
	 */
	public MimeMessage[] getReceivedMessages() {
		if (greenMail != null) {
			return greenMail.getReceivedMessages();
		}
		return new MimeMessage[0];
	}

	private void configureDatabase() {
		final SettingDefinition mailHost = settingsService.getSettingDefinition(MAIL_HOST_SETTING);
		final SettingDefinition mailPort = settingsService.getSettingDefinition(MAIL_PORT_SETTING);
		mailHost.setDefaultValue(MAIL_HOST);
		mailPort.setDefaultValue(String.valueOf(getMailPort()));
		getSettingsService().updateSettingDefinition(mailHost);
		getSettingsService().updateSettingDefinition(mailPort);
		setEmailEnabled(true);
	}

	public void setEmailEnabled(final boolean enabled) {
		final SettingDefinition emailEnabled = getSettingsService().getSettingDefinition("COMMERCE/SYSTEM/emailEnabled");
		emailEnabled.setDefaultValue(String.valueOf(enabled));
		getSettingsService().updateSettingDefinition(emailEnabled);
	}

	protected SettingsService getSettingsService() {
		return settingsService;
	}

	public void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}
}
