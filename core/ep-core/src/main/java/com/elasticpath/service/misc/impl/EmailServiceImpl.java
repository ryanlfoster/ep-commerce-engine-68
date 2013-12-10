package com.elasticpath.service.misc.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.email.EmailComposer;
import com.elasticpath.commons.util.email.EmailSender;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.misc.CustomerEmailPropertyHelper;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.notification.helper.EmailNotificationHelper;
import com.elasticpath.settings.SettingsReader;

/**
 * Email service responsible for sending storefront related and CM admin emails.
 */
@SuppressWarnings({ "PMD.NPathComplexity", "PMD.CyclomaticComplexity",
		"PMD.ExcessiveMethodLength" })
public class EmailServiceImpl extends AbstractEpServiceImpl implements
		EmailService {

	private static final Logger LOG = Logger.getLogger(EmailServiceImpl.class);

	private static final String SETTING_GLOGBAL_MAIL_ENABLED = "COMMERCE/SYSTEM/emailEnabled";

	private SettingsReader settingsReader;

	private EmailComposer emailComposer;

	private EmailSender emailSender;

	private EmailNotificationHelper emailNotificationHelper;

	private final AtomicBoolean disabledEmailWarningSent = new AtomicBoolean(false);

	/**
	 * @param emailComposer
	 *            the instance of EmailComposer.
	 */
	public void setEmailComposer(final EmailComposer emailComposer) {
		this.emailComposer = emailComposer;
	}

	/**
	 * @param emailSender
	 *            the instance of EmailSender.
	 */
	public void setEmailSender(final EmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * Sends a HTML or text email to a list of recipients using the provided
	 * email properties. Each recipients' email will be constructed with the
	 * same passed email properties (short the recipient address). The recipient
	 * address in the passed email properties will be overwritten and not used.
	 *
	 * @param toEmailList a list of recipient email addresses
	 * @param emailProperties the properties used to create the email
	 * @throws EpServiceException in case of any errors
	 * @deprecated This will be removed in the near future
	 */
	@Deprecated
	public void sendMail(final List<String> toEmailList,
			final EmailProperties emailProperties) throws EpServiceException {
		if (!isEmailEnabled()) {
			return;
		}

		try {
			Email email = emailComposer.composeMessage(emailProperties);

			for (String recipient : toEmailList) {
				// set an brand new "to list" to remove the previous email
				// address
				List<InternetAddress> toList = new ArrayList<InternetAddress>();
				toList.add(new InternetAddress(recipient));
				email.setTo(toList);
				emailSender.sendEmail(email);
			}
		} catch (Exception e) {
			throw new EpServiceException(e.getMessage(), e);
		}

	}

	/**
	 * Sends a HTML or text email to a recipient using the provided email
	 * properties. System sends email only when system settings
	 * COMMERCE/SYSTEM/emailEnabled is set to true, all other values will
	 * disable email sending. By default the COMMERCE/SYSTEM/emailEnabled is set
	 * to true.
	 *
	 * @param emailProperties the properties used to create the email
	 * @throws EpServiceException in case of any errors
	 * @deprecated This will be removed in the near future
	 */
	@Deprecated
	public void sendMail(final EmailProperties emailProperties) throws EpServiceException {
		if (!isEmailEnabled()) {
			return;
		}

		try {
			Email email = emailComposer.composeMessage(emailProperties);
			emailSender.sendEmail(email);
		} catch (Exception e) {
			throw new EpServiceException(e.getMessage(), e);
		}
	}

	/**
	 * Check whether system has email enabled. 
	 *
	 * @return true only if COMMERCE/SYSTEM/emailEnabled is set to true, all
	 *         other values or null will return false.
	 */
	protected boolean isEmailEnabled() {
		boolean emailEnabled = getSettingsReader().getSettingValue(SETTING_GLOGBAL_MAIL_ENABLED).getBooleanValue();
		if (!emailEnabled) {
			onEmailIsDisabled();
		}
		return emailEnabled;
	}

	/**
	 * Is called when the emails are disabled. This method will log a warning message the first time it is called.
	 */
	protected void onEmailIsDisabled() {
		if (disabledEmailWarningSent.compareAndSet(false, true)) {
			LOG.warn("Emails are disabled. See the setting at 'COMMERCE/SYSTEM/emailEnabled'.");
		}
	}

	/**
	 * Retrieves a setting value string with global context from the settings
	 * service.
	 *
	 * @param key
	 *            the key to the setting value
	 * @return the setting value string
	 */
	String getSystemSettingValue(final String key) {
		return getSettingsReader().getSettingValue(key).getValue();
	}

	/**
	 * @return the settingsReader
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set Email notifier helper.
	 *
	 * @param emailNotificationHelper
	 *            is the helper class
	 */
	public void setEmailNotificationHelper(
			final EmailNotificationHelper emailNotificationHelper) {
		this.emailNotificationHelper = emailNotificationHelper;
	}

	/**
	 * @param settingsReader
	 *            the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Send order confirmation email.
	 *
	 * @param orderNumber
	 *            is the order number
	 * @return boolean for whether the email was sent successfully or not
	 * @throws EmailException
	 *             an email exception
	 */
	public boolean sendOrderConfirmationEmail(final String orderNumber) throws EmailException {
		EmailProperties emailProperties = emailNotificationHelper.getOrderEmailProperties(orderNumber);
		return composeAndSendEmail(emailProperties);
	}

	/**
	 * Send gift certificate email.
	 *
	 * @param orderNumber is the order number
	 * @throws EmailException an email exception
	 * @return boolean value the result of sending email
	 */
	public boolean sendGiftCertificateEmails(final String orderNumber)
			throws EmailException {
		boolean result = true;
		List<EmailProperties> emailPropertiesList = emailNotificationHelper
				.getGiftCertificateEmailProperties(orderNumber);


		for (EmailProperties emailProperties : emailPropertiesList) {
			result &= composeAndSendEmail(emailProperties);
		}
		return result;
	}

	/**
	 * Send Order Shipped email.
	 *
	 * @param properties contains orderNumber and shipmentNumber
	 * @throws EmailException an email exception
	 * @return boolean value the result of sending email
	 */
	public boolean sendOrderShippedEmail(final Properties properties)
			throws EmailException {
		String orderNumber = properties.getProperty("orderNumber");
		String shipmentNumber = properties.getProperty("shipmentNumber");
		final EmailProperties emailProperties = emailNotificationHelper.getShipmentConfirmationEmailProperties(orderNumber, shipmentNumber);
		return composeAndSendEmail(emailProperties);
	}

	@Override
	public boolean sendOrderReturnEmail(final EmailProperties emailProperties) throws EmailException {
		return composeAndSendEmail(emailProperties);
	}

	@Override
	public boolean sendPasswordConfirmationEmail(final Customer customer) throws EmailException {
		// isEmailEnabled not used here since calling code is expecting
		// exceptions in this case.
		CustomerEmailPropertyHelper customerEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CUSTOMER);
		return composeAndSendEmail(customerEmailPropHelper.getPasswordConfirmationEmailProperties(customer));
	}

	@Override
	public boolean sendForgottenPasswordEmail(final Customer customer, final String newPassword) throws EmailException {
		// isEmailEnabled not used here since calling code is expecting
		// exceptions in this case.
		CustomerEmailPropertyHelper customerEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CUSTOMER);
		return composeAndSendEmail(customerEmailPropHelper.getForgottenPasswordEmailProperties(customer, newPassword));
	}

	@Override
	public boolean sendNewlyRegisteredCustomerEmail(final Customer customer, final String newPassword) throws EmailException {
		// isEmailEnabled not used here since calling code is expecting
		// exceptions in this case.
		CustomerEmailPropertyHelper customerEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CUSTOMER);
		return composeAndSendEmail(customerEmailPropHelper.getNewlyRegisteredCustomerEmailProperties(customer, newPassword));
	}

	@Override
	public boolean sendNewCustomerEmailConfirmation(final Customer customer) throws EmailException {
		// isEmailEnabled not used here since calling code is expecting
		// exceptions in this case.
		CustomerEmailPropertyHelper customerEmailPropHelper = getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CUSTOMER);
		return composeAndSendEmail(customerEmailPropHelper.getNewAccountEmailProperties(customer));
	}

	/**
	 * Composer and send email.
	 *
	 * @param emailProperties
	 *            email properties
	 * @throws EmailException
	 *             email exception
	 * @return the boolean value of result to send email
	 */
	protected boolean composeAndSendEmail(final EmailProperties emailProperties)
			throws EmailException {
		if (isEmailEnabled()) {
			boolean emailSent = false;
			try {
				Email email = emailComposer.composeMessage(emailProperties);
				String messageId = emailSender.sendEmail(email);

				LOG.debug("Successfully sent email with id: " + messageId);
				emailSent = true;

			} catch (EmailException ee) {
				// Need to catch, log, rethrow exception for handling by Mule
				LOG.error("EmailException occurred when sending an email");
				throw ee;
			}
			return emailSent;
		}
		return true;
	}

}