/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping;

import java.util.List;

import com.elasticpath.domain.EpDomain;

/**
 * A Region represents a geographical definition of an area.
 * For now, it is composed of a country and a list of subcountries.
 */
public interface Region extends EpDomain {
	/**
	 * Get the region's country code (ISO country code).
	 *
	 * @return the region's country code.
	 */
	String getCountryCode();

	/**
	 * Set the region's country code (ISO country code).
	 *
	 * @param countryCode the region's country code.
	 */
	void setCountryCode(final String countryCode);

	/**
	 * Get the region's subcountry code list.
	 *
	 * @return the region's subcountry code list.
	 */
	List<String> getSubCountryCodeList();

	/**
	 * Set the region's subcountry code list.
	 *
	 * @param subCountryCodeList the region's subcountry code list.
	 */
	void setSubCountryCodeList(final List<String> subCountryCodeList);

	/**
	 * Merge the given additionalSubCountryCodeList into the existing subCountryCodeList of this <code>Region</code> instance.
	 *
	 * @param additionalSubCountryCodeList - the additional subCountryCodeList to be merged in.
	 */
	void mergeSubCountryCodeList(final List<String> additionalSubCountryCodeList);

	/**
	 * Return the String representation of this <code>Region</code>.
	 *
	 * @return string representation of the region.
	 */
	String toString();

	/**
	 * Return the <code>Region</code> from parsing the given string representation.
	 *
	 * @param regionStr - the String representation of the <code>Region</code>
	 * @return Region - the instance of <code>Region</code>
	 */
	Region fromString(final String regionStr);
}
