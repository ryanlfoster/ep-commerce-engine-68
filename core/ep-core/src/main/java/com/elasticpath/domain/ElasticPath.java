/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.domain;

import java.util.Locale;
import java.util.Map;

/**
 * <code>ElasticPath</code> provides configuration and context information to a number of clients across the system.
 */
public interface ElasticPath {
	/**
	 * Get the bean with the given id from spring application context.
	 *
	 * @param <T> the type of the bean to return.
	 * @param name bean id
	 * @return instance of the bean with the given id.
	 */
	<T> T getBean(String name);

	/**
	 * Return the <code>Class</code> object currently registered with the
	 * specified <code>beanName</code>.
	 *
	 * @param <T> the type of the bean to return.
	 * @param beanName the name of the bean to get the class for.
	 * @return the class object if the bean is registered, null otherwise.
	 */
	<T> Class<T> getBeanImplClass(final String beanName);

	/**
	 * Gets the localized message.
	 *
	 * @param code - the message key.
	 * @param args - the args for the message if needed.
	 * @param defaultMessage - the default message to display if the given message key does not exist.
	 * @param locale - the locale that message needs to be in.
	 * @return localized message string.
	 * @deprecated {@link com.elasticpath.commons.beanframework.MessageSource#getMessage(String, Object[], String, Locale)}
	 * should be used for retrieving localized messages
	 */
	@Deprecated
	String getMessage(final String code, final Object[] args, final String defaultMessage, final Locale locale);

	/**
	 * Returns a map used by Spring/Velocity for checkboxes.
	 *
	 * @return a map containing {Boolean.TRUE -> ""}
	 */
	Map<Boolean, String> getBoolMap();

	/**
	 * Return the absolute WEB-INF directory path.
	 *
	 * @return the absolute WEB-INF directory path
	 */
	String getWebInfPath();

	/**
	 * Set the absolute path to the WEB-INF directory.
	 *
	 * @param webInfPath the absolute path
	 */
	void setWebInfPath(String webInfPath);
}
