package com.elasticpath.sfweb.ajax.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides geography related services for ajax.
 */
public interface GeographyController {
	/**
	 * Returns a map of all subcountries within a particular country. If no locale is matched default values are returned.
	 *
	 * @param countryCode ISO two letter country code (ex: US)
	 * @param request the request object containing the shopping cart
	 * @return map containing keys and localized values of all available subcountries
	 */
	Map<String, String> getSubCountries(final String countryCode, final HttpServletRequest request);
}
