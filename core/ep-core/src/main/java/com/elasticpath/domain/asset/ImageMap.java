package com.elasticpath.domain.asset;

import java.io.Serializable;
import java.util.Locale;
import java.util.Set;

/**
 * Maps an image key and locale to an image path.
 */
public interface ImageMap extends Serializable {
	
	/**
	 * Gets the image keys.
	 *
	 * @return the image keys
	 */
	Set<String> getImageKeys();
	
	/**
	 * Gets the image keys for the given locale.
	 *
	 * @param locale the locale
	 * @return the image keys
	 */
	Set<String> getImageKeys(Locale locale);
	
	/**
	 * Gets the image path.
	 *
	 * @param key the key
	 * @param locale the locale
	 * @return the image path
	 */
	String getImagePath(String key, Locale locale);
	
	/**
	 * Checks for images.
	 *
	 * @param locale the locale
	 * @return true, if successful
	 */
	boolean hasImages(Locale locale);

}
