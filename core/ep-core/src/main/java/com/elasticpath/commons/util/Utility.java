/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.util.impl.LocalizedDateFormat;

/**
 * Provides utility methods used throughout the application.
 */
public interface Utility extends Serializable {

	/**
	 * Return the localized file path of the given file path. <br>
	 * Example: <br>
	 * When you are looking for "/dir/file.vm" with locale "en_CA" <br>
	 * &nbsp;If "/dir/file_en_CA.vm" exists, it will be returned; <br>
	 * &nbsp;If "/dir/file_en_CA.vm" does not exist, but "/dir/file_en.vm" exists, it will be returned; <br>
	 * &nbsp;If neither of the above files exist, "/dir/file.vm" will be returned;
	 *
	 * @param baseDir the base directory
	 * @param filePath the file path relative to the base directory
	 * @param fileExtension the file extension
	 * @param locale the locale
	 * @return the localized file path
	 */
	String getLocalizedFile(String baseDir, String filePath, String fileExtension, Locale locale);

	/**
	 * Returns an escaped name which is URL-friendly.
	 *
	 * @param name the original name
	 * @return a escaped name which is URL-friendly.
	 *
	 * @deprecated use {@link #escapeName2UrlFriendly(String, Locale)} or
	 * {@link #encodeGuid2UrlFriendly(String)} depending on the type of string
	 * to be encoded
	 */
	@Deprecated
	String escapeName2UrlFriendly(String name);

	/**
	 * Returns an escaped name which is URL-friendly.
	 *
	 * @param name the original name
	 * @param locale the {@link Locale} this name has
	 * @return a escaped name which is URL-friendly.
	 */
	String escapeName2UrlFriendly(String name, Locale locale);

	/**
	 * Returns the system-default date formatting string and locale.
	 * @return the system-default date formatting string and locale
	 */
	LocalizedDateFormat getDefaultLocalizedDateFormat();

	/**
	 * Returns <code>true</code> if the length of the given value &lt;= short text max length. Otherwise, <code>false</code>.
	 *
	 * @param value the value to check
	 * @return <code>true</code> if the length of the given value &lt;= short text max length. Otherwise, <code>false</code>.
	 */
	boolean checkShortTextMaxLength(String value);

	/**
	 * Returns <code>true</code> if the length of the given value &lt;= long text max length. Otherwise, <code>false</code>.
	 *
	 * @param value the value to check
	 * @return <code>true</code> if the length of the given value &lt;= long text max length. Otherwise, <code>false</code>.
	 */
	boolean checkLongTextMaxLength(String value);

	/**
	 * Returns <code>true</code> if the given string is a valid guid string. Otherwise, <code>false</code>
	 *
	 * @param string the string
	 * @return <code>true</code> if the given string is a valid guid string. Otherwise, <code>false</code>
	 */
	boolean isValidGuidStr(final String string);

	/**
	 * Returns <code>true</code> if the given zip postal code is valid. Otherwise, <code>false</code>.
	 *
	 * @param zipPostalCode the zip postal code.
	 * @return <code>true</code> if the given zip postal code is valid. Otherwise, <code>false</code>.
	 */
	boolean isValidZipPostalCode(final String zipPostalCode);

	/**
	 * Get a map of the numbers of months of the year for the Spring form input for credit card expiry.
	 *
	 * @return map of month digit strings
	 */
	Map<String, String> getMonthMap();

	/**
	 * Get a map of years for the Spring form input for credit card expiry.
	 *
	 * @return map of year strings
	 */
	Map<String, String> getYearMap();

	/**
	 * Get a map of the store's supported card types for display in Spring.
	 *
	 * @param storeCode the store's code
	 * @return the credit card type map
	 */
	Map <String, String> getStoreCreditCardTypesMap(String storeCode);

	/**
	 * Return all credit card types supported by any store.
	 *
	 * @deprecated instead call  <code>StoreService.findAllSupportedCreditCardTypes()</code>.
	 * @return map of all credit card types
	 */
	@Deprecated
	Map <String, String> getAllCreditCardTypesMap();

	/**
	 * Get a random string with the required length.
	 *
	 * @param length the length of the string to return
	 * @return a random string
	 */
	String getRandomStringWithLength(final int length);

}
