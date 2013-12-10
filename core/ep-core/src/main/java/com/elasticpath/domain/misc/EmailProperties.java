package com.elasticpath.domain.misc;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.EpDomain;

/**
 * Represents properties used to construct an email.
 */
public interface EmailProperties extends EpDomain {

	/**
	 * Gets the store code for the <code>EmailProperties</code>. If no store code is set (is
	 * <code>null</code>), global templates will be used rather than store specific templates.
	 *
	 * @return the store code for the <code>EmailProperties</code>
	 */
	String getStoreCode();

	/**
	 * Sets the store code for the <code>EmailProperties</code>. If no store code is set (is
	 * <code>null</code>), global templates will be used rather than store specific templates.
	 *
	 * @param storeCode the store code for the <code>EmailProperties</code>
	 */
	void setStoreCode(final String storeCode);

	/**
	 * Gets the recipient address for the <code>EmailProperties</code>.
	 *
	 * @return the recipient address for the <code>EmailProperties</code>
	 */
	String getRecipientAddress();

	/**
	 * Sets the recipient address for the <code>EmailProperties</code>.
	 *
	 * @param recipientAddress the recipient address for the <code>EmailProperties</code>
	 */
	void setRecipientAddress(final String recipientAddress);

	/**
	 * Gets path and filename of the text template used for rendering an email. The filename
	 * should not include the <code>.vm</code> extension.
	 *
	 * @return the path and filename of the text template used for rendering an email
	 */
	String getTextTemplate();

	/**
	 * Sets path and filename of the text template used for rendering an email. The filename
	 * should not include the <code>.vm</code> extension.
	 *
	 * @param textTemplate the path and filename of the text template used for rendering an email
	 */
	void setTextTemplate(final String textTemplate);

	/**
	 * Gets path and filename of the HTML template used for rendering an email. The filename
	 * should not include the <code>.vm</code> extension.
	 *
	 * @return the path and filename of the HTML template used for rendering an email
	 */
	String getHtmlTemplate();

	/**
	 * Gets path and filename of the HTML template used for rendering an email. The filename
	 * should not include the <code>.vm</code> extension.
	 *
	 * @param htmlTemplate the path and filename of the HTML template used for rendering an email
	 */
	void setHtmlTemplate(final String htmlTemplate);

	/**
	 * Gets the template resource for the <code>EmailProperties</code>.
	 *
	 * @return the template resources for the <code>EmailProperties</code>
	 */
	Map<String, Object> getTemplateResources();

	/**
	 * Sets the template resource for the <code>EmailProperties</code>.
	 *
	 * @param templateResources the template resources for the <code>EmailProperties</code>
	 */
	void setTemplateResources(final Map<String, Object> templateResources);

	/**
	 * Gets the whether the email should be sent as text only.
	 *
	 * @return whether the email should be sent as text only
	 */
	Boolean isTextOnly();

	/**
	 * Sets if text template should be used.
	 *
	 * @param textOnly whether to use text only template.
	 */
	void setTextOnly(final Boolean textOnly);

	/**
	 * Gets the <code>Locale</code> used for messages for this email.
	 * @return the emailLocale the <code>Locale</code> which will be used to get messages
	 */
	Locale getEmailLocale();

	/**
	 * Sets the <code>Locale</code> used for messages for this email.
	 * @param emailLocale the <code>Locale</code> to set
	 */
	void setEmailLocale(Locale emailLocale);

	/**
	 * Get the key from the properties files that will have the email subject for each supported <code>Locale</code>.
	 * @return the localeDependentSubjectKey - the code for the message
	 */
	String getLocaleDependentSubjectKey();

	/**
	 * Set the key from the properties files that will have the email subject for each supported <code>Locale</code>.
	 * @param localeDependentSubjectKey the the code for the message to set
	 */
	void setLocaleDependentSubjectKey(String localeDependentSubjectKey);

	/**
	 * Get the text that will be used for the Subject of the email in case we don't have a language specific message.
	 * @return the defaultSubject a text to use a subject
	 */
	String getDefaultSubject();

	/**
	 *  set the text that will be used for the Subject of the email in case we don't have a language specific message.
	 * @param defaultSubject the text to use a subject to set
	 */
	void setDefaultSubject(String defaultSubject);

}
