package com.elasticpath.importexport.common.util;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * <code>MessageResolver</code> resolves messages with the given code and parameterizes them with the given parameters. 
 */
public class MessageResolverImpl implements MessageResolver {
	private MessageSource messageSource;

	private Locale locale = Locale.US;

	/**
	 * Sets messageSource.
	 * 
	 * @param messageSource messageSource
	 */
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public String resolve(final Message message) {
		return messageSource.getMessage(message.getCode(), message.getParams(), locale);
	}

	/**
	 * Sets resolver's locale.
	 * 
	 * @param locale resolver's locale
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}
}
