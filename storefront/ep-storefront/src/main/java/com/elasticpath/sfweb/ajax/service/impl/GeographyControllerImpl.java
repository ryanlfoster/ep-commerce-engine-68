package com.elasticpath.sfweb.ajax.service.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.commons.util.impl.VelocityGeographyHelperImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.ajax.service.GeographyController;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Provides geography related services for ajax.
 */
public class GeographyControllerImpl implements GeographyController {
	private SfRequestHelper requestHelper;

	private VelocityGeographyHelperImpl geographyHelper;
	private Geography geography;

	/**
	 * Returns a map of all subcountries within a particular country. If no locale is matched default values are returned.
	 * 
	 * @param countryCode ISO two letter country code (ex: US)
	 * @param request the request object containing the shopping cart
	 * @return map containing keys and localized values of all available subcountries
	 */
	public Map<String, String> getSubCountries(final String countryCode, final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		return geographyHelper.getSubCountriesWithDisplayName(countryCode, shoppingCart.getLocale());
	}

	/**
	 * Returns the request helper.
	 * 
	 * @return the request helper
	 */
	public SfRequestHelper getRequestHelper() {
		return requestHelper;
	}

	/**
	 * Sets the request helper.
	 * 
	 * @param requestHelper the request helper.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Sets the <code>Geography</code> instance.
	 * 
	 * @param geography the <code>Geography</code> instance
	 */
	public void setGeography(final Geography geography) {
		this.geography = geography;
		geographyHelper = new VelocityGeographyHelperImpl(geography);
	}

	/**
	 * Returns the <code>Geography</code> instance.
	 * 
	 * @return <code>Geography</code> instance
	 */
	public Geography getGeography() {
		return this.geography;
	}
}
