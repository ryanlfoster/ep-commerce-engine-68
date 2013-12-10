/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc;

import java.util.Locale;
import java.util.Set;

import com.elasticpath.persistence.dao.LocalePropertyLoaderAware;

/**
 * Represents a set of countries and provides geography-related functionality such as finding the subcountries of a
 * particular country. A subcountry is a region of a country such as a province, state, or territory.
 */
public interface Geography extends LocalePropertyLoaderAware {

	/** Sub-country properties should have the following prefix. */
	String SUB_COUNTRY_PROPERTY_PREFIX = "subcountry";

	/**
	 * @return list of all country codes
	 */
	Set<String> getCountryCodes();

	/**
	 * Gets the list of subcountry codes for the given {@code countryCode} or an empty list if there aren't any
	 * sub-countries for the country.
	 * 
	 * @param countryCode country code
	 * @return list of sub-country codes
	 */
	Set<String> getSubCountryCodes(String countryCode);

	/**
	 * Gets a countries display name from the given code for the default {@link Locale} or {@code null} if we don't know
	 * about the country code.
	 * 
	 * @param code country code
	 * @return display name
	 * @see #getCountryDisplayName(String, Locale)
	 */
	String getCountryDisplayName(String code);

	/**
	 * Gets a countries display name from the given code for the given {@link Locale} or {@code null} if we don't know
	 * about the country code.
	 * 
	 * @param code country code
	 * @param locale locale to get a name for
	 * @return display name
	 */
	String getCountryDisplayName(String code, Locale locale);

	/**
	 * Gets a sub-countries display name from the given {@code countryCode} and {@code subcountryCode} for the default
	 * {@link Locale} or {@code null} if we don't know about the country code.
	 * 
	 * @param countryCode country code
	 * @param subcountryCode sub-country code
	 * @return display name
	 * @see #getCountryDisplayName(String, Locale)
	 */
	String getSubCountryDisplayName(String countryCode, String subcountryCode);

	/**
	 * Gets a sub-countries display name from the given {@code countryCode} and {@code subcountryCode} for the given
	 * {@link Locale} or {@code null} if we don't know about the country code.
	 * 
	 * @param countryCode country code
	 * @param subcountryCode sub-country code
	 * @param locale locale to get a name for
	 * @return display name
	 */
	String getSubCountryDisplayName(String countryCode, String subcountryCode, Locale locale);
}