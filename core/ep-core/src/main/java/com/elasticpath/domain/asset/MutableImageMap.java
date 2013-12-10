package com.elasticpath.domain.asset;

import java.util.Locale;

/**
 * A mutable version of an {@link ImageMap}.
 */
public interface MutableImageMap extends ImageMap {

	/**
	 * Adds the image.
	 *
	 * @param key the key
	 * @param path the path
	 * @param locale the locale
	 */
	void addImage(String key, String path, Locale locale);
	
	/**
	 * Adds the image.
	 *
	 * @param key the key
	 * @param path the path
	 */
	void addImage(String key, String path);
	
}
