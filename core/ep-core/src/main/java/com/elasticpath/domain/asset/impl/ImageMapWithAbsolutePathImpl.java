package com.elasticpath.domain.asset.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.asset.MutableImageMapWithAbsolutePath;

/**
 * An @{link ImageMap} that includes absolute paths.
 */
public class ImageMapWithAbsolutePathImpl implements MutableImageMapWithAbsolutePath {
	private static final long serialVersionUID = 1L;

	private static final transient Logger LOG = Logger.getLogger(ImageMapWithAbsolutePathImpl.class);
	
	private ImageMap relativeImageMap;
	private URL absolutePathPrefix;
	
	/**
	 * Gets the image keys.
	 *
	 * @return the image keys
	 */
	@Override
	public Set<String> getImageKeys() {
		return relativeImageMap.getImageKeys();
	}

	/**
	 * Gets the image keys for the given locale.
	 *
	 * @param locale the locale
	 * @return the image keys
	 */
	@Override
	public Set<String> getImageKeys(final Locale locale) {
		return relativeImageMap.getImageKeys(locale);
	}

	/**
	 * Gets the image path.
	 *
	 * @param key the key
	 * @param locale the locale
	 * @return the image path
	 */
	@Override
	public String getImagePath(final String key, final Locale locale) {
		return relativeImageMap.getImagePath(key, locale);
	}

	/**
	 * Checks for images.
	 *
	 * @param locale the locale
	 * @return true, if successful
	 */
	@Override
	public boolean hasImages(final Locale locale) {
		return relativeImageMap.hasImages(locale);
	}

	/**
	 * Gets the image absolute path.
	 *
	 * @param key the key
	 * @param locale the locale
	 * @return the image absolute path
	 */
	@Override
	public String getImageAbsolutePath(final String key, final Locale locale) {
		String relativeImagePath = getImagePath(key, locale);
		try {
			URL absoluteUrl = new URL(absolutePathPrefix, relativeImagePath);
			return absoluteUrl.toExternalForm();
		} catch (MalformedURLException e) {
			LOG.error("Could not create absolute path from prefix " + absolutePathPrefix + " and path " + relativeImagePath, e);
		}
		return relativeImagePath;
	}

	@Override
	public void setPathPrefix(final URL prefix) {
		absolutePathPrefix = prefix;
	}

	@Override
	public void setRelativeImageMap(final ImageMap imageMap) {
		relativeImageMap = imageMap;
	}

}
