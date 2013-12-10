/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.UrlUtility;

/**
 * Default implementation of {@link UrlUtility}.
 */
public class UrlUtilityImpl implements UrlUtility {
	
	private static final Logger LOG = Logger.getLogger(UrlUtilityImpl.class);
	
	private String encoding;

	/**
	 * Encodes a text to a URL user friendly fashion.
	 * 
	 * @param text the string to encode 
	 * @param locale the locale the text is in
	 * @return an encoded string
	 */
	public String encodeText2UrlFriendly(final String text, final Locale locale) {
		final String encodedString = encodeString(text.trim());
		
		// make the string lower case in accordance with the SEO rules
		return encodedString.toLowerCase(locale);
	}

	/**
	 *
	 */
	private String encodeString(final String str) {
		String encodedString;
		try {
			// create the URLCodec every time this method is invoked 
			// to avoid synchronization problems
			encodedString = new URLCodec().encode(str, this.encoding);
		} catch (UnsupportedEncodingException exc) {
			encodedString = str;
		}
		// replace all plus signs, that were put in place of spaces by the encoder, with dashes
		return encodedString.replaceAll("[\\+]+", "-");
	}

	/**
	 * Encodes a GUID to a URL user friendly fashion.
	 * 
	 * @param text the string to encode 
	 * @return an encoded string
	 */
	public String encodeGuid2UrlFriendly(final String text) {
		final String encodedString = encodeString(text);
		
		return encodedString;
	}

	/**
	 * Returns an unescaped URL which in this case means decoding all
	 * the encoded characters in the string. It uses specific encoding
	 * and relies that the string was encoded with this type of encoding.
	 *
	 * @param url the URL string
	 * @return an unescaped URL (e.g. 'b%C3%BCcher' will be decoded as 'bucher' with an umlat ontop of the u)
	 */
	public String decodeUrl2Text(final String url) {
		String decodedName;
		try {
			// create the URLCodec every time this method is invoked 
			// to avoid synchronization problems
			decodedName = new URLCodec().decode(url, this.encoding);
		} catch (UnsupportedEncodingException exc) {
			decodedName = url;
		} catch (DecoderException exc) {
			decodedName = url;
		}
		return decodedName;
	}
	
	/**
	 * Sets the default character encoding used to encode/decode 
	 * character strings.
	 * 
	 * @param encoding the encoding name (e.g. UTF-8)
	 */
	public void setCharacterEncoding(final String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Gets the locale from a URL with a specified contextPath.
	 * 
	 * @param url the URL starting with '/contextPath'
	 * @param contextPath the context path of this URL
	 * @return the Locale or null if none is specified
	 */
	public Locale getLocaleFromUrl(final String url, final String contextPath) {
		Pattern pattern = Pattern.compile("^" + contextPath + "/(.*?)/.*");
		Matcher matcher = pattern.matcher(url);
		if (matcher.matches()) {
			String match1 = matcher.group(1);
			match1 = formatLocaleStringForParsing(match1);
			try {
				formatLocaleStringForParsing(match1);
				return LocaleUtils.toLocale(match1);
			} catch (IllegalArgumentException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(match1 + " is not a locale");
				}
				return null;
			}
		}
		return null;
	}

	/**
	 * Format the locale string to one acceptable by LocalUtils, which has strict requirements on the country code case.
	 * 
	 * @param locale locale string
	 * @return formatted string with upper case country code if one exists
	 */
	private String formatLocaleStringForParsing(final String locale) {
		int separator = locale.indexOf('_');
		if (separator > 0) {
			return locale.substring(0, separator).toLowerCase() + locale.substring(separator).toUpperCase();
		}
		return locale;
	}
}
