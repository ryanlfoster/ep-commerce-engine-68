/**
 * 
 */
package com.elasticpath.sfweb.ajax.bean;

import java.util.Locale;

import com.elasticpath.domain.shipping.ShippingServiceLevel;

/**
 * This bean is used for wrapping <code>ShippingServiceLevel</code> objects into localized versions for dwr.
 */
public interface ShippingServiceLevelBean extends ShippingServiceLevel {

	/**
	 * Convenience method for getting the shipping service level's localized display name.
	 * 
	 * @return the localized shipping service level name
	 */
	String getDisplayName();

	/**
	 * @return the locale
	 */
	Locale getLocale();

	/**
	 * @param locale the locale to set
	 */
	void setLocale(final Locale locale);
}
