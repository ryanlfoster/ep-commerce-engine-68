/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

import java.util.Locale;


/**
 * A utility class used to encode/decode URL strings into a
 * SEO friendly way.
 */
public interface UrlUtility {

	/**
	 * Returns a decoded URL string. 
	 *
	 * @param url the URL string
	 * @return a decoded URL
	 */
	String decodeUrl2Text(String url);

	/**
	 * Encodes a GUID into a URL friendly string.
	 * GUIDs in language other than English will be encoded with 
	 * unicode symbols.
	 * 
	 * @param guid a String representing the GUID
	 * @return an encoded string
	 */
	String encodeGuid2UrlFriendly(String guid);

	/**
	 * Gets the locale from a URL with a specified contextPath.
	 * 
	 * @param url the URL starting with '/contextPath'
	 * @param contextPath the context path of this URL
	 * @return the Locale or null if none is specified
	 */
	Locale getLocaleFromUrl(String url, String contextPath);
}
