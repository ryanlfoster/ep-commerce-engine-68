package com.elasticpath.commons.util.email.impl;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.StoreMessageSource;
import com.elasticpath.commons.util.VelocityEngineInstanceFactory;
import com.elasticpath.commons.util.email.EmailComposer;
import com.elasticpath.commons.util.email.EmailContextFactory;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsReader;

/**
 * Default implementation of {@link EmailComposer}.
 */
public class EmailComposerImpl extends AbstractEpServiceImpl implements EmailComposer {

	private static final Logger LOG = Logger.getLogger(EmailComposerImpl.class);

	private static final String SETTING_GLOBAL_SENDER_ADDRESS = "COMMERCE/SYSTEM/EMAIL/emailGlobalSenderAddress";

	private static final String SETTING_GLOBAL_SENDER_NAME = "COMMERCE/SYSTEM/EMAIL/emailGlobalSenderName";

	private static final String BASE_EMAIL_TEMPLATE_DIR = "email"; // the email folder is hard-coded but the store code is added dynamically

	private SettingsReader settingsReader;

	private StoreService storeService;

	private VelocityEngineInstanceFactory velocityEngineInstanceFactory;
	private StoreMessageSource storeMessageSource;

	// We need to use the thread local implementation to set storecode on the active thread. Will be picked up by velocity and resource managers
	private ThreadLocalStorageImpl tlStoreConfig;
	private EmailContextFactory emailContextFactory;

	/**
	 * Sets the store service required for resolving the store code and sender from <code>EmailProperties</code>.
	 *
	 * @param storeService a store service instance
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @param settingsReader the instance of SettingsReader.
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Set the velocity engine factory.
	 *
	 * @param velocityEnginefactory the velocity engine factory
	 */
	public void setVelocityEngineFactory(final VelocityEngineInstanceFactory velocityEnginefactory) {
		this.velocityEngineInstanceFactory = velocityEnginefactory;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @throws EmailException
	 */
	public Email composeMessage(final EmailProperties emailProperties) throws EmailException {
		Store store = null;

		final boolean useGlobalTemplates = emailProperties.getStoreCode() == null;
		LOG.debug("Using global email templates: " + useGlobalTemplates);

		if (!useGlobalTemplates) {
			store = storeService.findStoreWithCode(emailProperties.getStoreCode());
			if (store == null) {
				throw new EpServiceException("Passed store code '" + emailProperties.getStoreCode() + "' does not exist.");
			}
		}

		Email email = composeEmailContent(emailProperties, store);

		email.setSubject(getEmailSubject(emailProperties));

		if (StringUtils.isNotBlank(emailProperties.getRecipientAddress())) {
			email.addTo(emailProperties.getRecipientAddress());
		}

		if (useGlobalTemplates) {
			email.setFrom(getEmailGlobalSenderAddress(), getEmailGlobalSenderName());
		} else {
			email.setFrom(store.getEmailSenderAddress(), store.getEmailSenderName());
		}

		return email;
	}

	/** Determine's the email Subject.
	 * If a key exists, will try to get the message for a specific locale for that key.
	 * @param emailProperties the values of the
	 * @return the email subject
	 */
	protected String getEmailSubject(final EmailProperties emailProperties) {
		String emailSubject = emailProperties.getDefaultSubject();
		if (emailProperties.getLocaleDependentSubjectKey() != null) {
			emailSubject = StringUtils.defaultString(storeMessageSource.getMessage(emailProperties.getStoreCode(),
					emailProperties.getLocaleDependentSubjectKey(), emailProperties.getEmailLocale()),  emailProperties.getDefaultSubject());
		}
		return emailSubject;
	}

	/**
	 * Determine whether an email with the given properties can only be sent as a TEXT email (rather than as an HTML email). This implementation
	 * consults the settings service to check the emailTextTemplateEnabled setting (if the setting is true then the email template is text-only).
	 *
	 * @param emailProperties the email's properties
	 * @return true if only a text email can be sent, false if the email can be sent as HTML
	 */
	boolean canOnlySendTextEmail(final EmailProperties emailProperties) {
		return (emailProperties.isTextOnly() != null && emailProperties.isTextOnly()) // necessary to preserve old logic in Properties object
				|| settingsReader.getSettingValue("COMMERCE/SYSTEM/EMAIL/emailTextTemplateEnabled").getBooleanValue();
	}


	/**
	 * Compose email content.
	 * @param emailProperties is the set of email properties
	 * @param store is the store to compose the email for
	 * @return the composed email
	 * @throws EmailException an email exception
	 */
	protected Email composeEmailContent(final EmailProperties emailProperties,
										final Store store) throws EmailException {

		// This storeconfig should be able to return the right store code given in the emailProperties
		tlStoreConfig.setStoreCode(emailProperties.getStoreCode());

		// make sure defaults are set
		final Map<String, Object> velocityTemplateResources = getEmailContextFactory().createVelocityContext(store, emailProperties);

		String baseTemplateDir = BASE_EMAIL_TEMPLATE_DIR + File.separator;

		String emailHtmlBody = "";
		String emailTxtBody = "";

		try {
			if (emailProperties.getHtmlTemplate() != null) {
				emailHtmlBody = mergeTemplateIntoString(baseTemplateDir + emailProperties.getHtmlTemplate(), velocityTemplateResources,
						emailProperties.getStoreCode());
			}
			if (emailProperties.getTextTemplate() != null) {
				emailTxtBody = mergeTemplateIntoString(baseTemplateDir + emailProperties.getTextTemplate(), velocityTemplateResources,
						emailProperties.getStoreCode());
			}
		} catch (VelocityException e) {
			throw new EpServiceException("Velocity error occurred while rendering [" + getEmailSubject(emailProperties) + "] email", e);
		}

		if ("".equals(emailHtmlBody) || canOnlySendTextEmail(emailProperties) && !"".equals(emailTxtBody)) {
			Email email = new SimpleEmail();

			if (store != null) {
				final String encoding = store.getContentEncoding();
				if (!StringUtils.isEmpty(encoding)) {
					email.setCharset(encoding);
				}
			}
			email.setMsg(emailTxtBody);
			return email;
		}

		HtmlEmail htmlEmail = new HtmlEmail();

		htmlEmail.setHtmlMsg(emailHtmlBody);

		// set the alternative message if HTML email is not supported
		htmlEmail.setTextMsg(emailTxtBody);

		return htmlEmail;
	}

	/**
	 * Generates a string from the given template by running it through the Velocity engine. This method serves as a hook to be overridden by JUnit
	 * code.
	 *
	 * @param txtTemplate the template file name without the extension
	 * @param velocityTemplateResources a map of model objects referenced in the template
	 * @param storeCode the store code to use when retrieving the velocity engine
	 * @return a string corresponding to the processed template
	 * @throws VelocityException on error
	 */
	protected String mergeTemplateIntoString(final String txtTemplate, final Map<String, Object> velocityTemplateResources, final String storeCode)
			throws VelocityException {
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngineInstanceFactory.getVelocityEngine(storeCode), txtTemplate + ".vm",
				velocityTemplateResources);
	}

	/**
	 * Get value of emailSenderName.
	 *
	 * @return the setting value
	 */
	protected String getEmailGlobalSenderName() {
		return getSystemSettingValue(SETTING_GLOBAL_SENDER_NAME);
	}

	/**
	 * This implementation calls {@link #getSystemSettingValue(String)}.
	 *
	 * @return the email address to be used as the "from" address for system emails.
	 */
	protected String getEmailGlobalSenderAddress() {
		return getSystemSettingValue(SETTING_GLOBAL_SENDER_ADDRESS);
	}

	/**
	 * Retrieves a setting value string with global context from the settings service.
	 *
	 * @param key the key to the setting value
	 * @return the setting value string
	 */
	String getSystemSettingValue(final String key) {
		return settingsReader.getSettingValue(key).getValue();
	}

	/**
	 * Set the store config object. Resource loading components used by the email service are using implementations that depend on a thread aware
	 * store config object, and therefore we force the implementation to be the ThreadLocal one.
	 *
	 * @param tlStoreConfig the threadlocalstoreconfig object
	 */
	public void setStoreConfig(final ThreadLocalStorageImpl tlStoreConfig) {
		this.tlStoreConfig = tlStoreConfig;
	}

	/**
	 * @param storeMessageSource the storeMessageSource to set
	 */
	public void setStoreMessageSource(final StoreMessageSource storeMessageSource) {
		this.storeMessageSource = storeMessageSource;
	}

	protected EmailContextFactory getEmailContextFactory() {
		return emailContextFactory;
	}

	public void setEmailContextFactory(final EmailContextFactory emailContextFactory) {
		this.emailContextFactory = emailContextFactory;
	}
}
