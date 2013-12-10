package com.elasticpath.domain.asset;

import java.util.Locale;

/**
 * An {@link ImageMap} with absolute paths.
 */
public interface ImageMapWithAbsolutePath extends ImageMap {

	/**
	 * Gets the image absolute path.
	 *
	 * @param key the key
	 * @param locale the locale
	 * @return the image path
	 */
	String getImageAbsolutePath(String key, Locale locale);

	
}
