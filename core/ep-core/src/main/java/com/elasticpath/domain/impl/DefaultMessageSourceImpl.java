package com.elasticpath.domain.impl;

import java.util.Locale;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.elasticpath.commons.beanframework.MessageSource;

/**
 * Implementations can provide formatted localized messages.
 */
public class DefaultMessageSourceImpl implements MessageSource, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	/** 
	 * Sets the application context that will provide the messages.
	 * 
	 * @param applicationContext the application context used to create this bean.
	 */ 
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	
	/**
	 * Gets the localized message.
	 * 
	 * @param code - the message key.
	 * @param args - the args for the message if needed.
	 * @param defaultMessage - the default message to display if the given message key does not
	 *            exists.
	 * @param locale - the locale that message needs to be in.
	 * @return localized message string.
	 */
	public String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale) {
		return this.applicationContext.getMessage(code, args, defaultMessage, locale);
	}
}
