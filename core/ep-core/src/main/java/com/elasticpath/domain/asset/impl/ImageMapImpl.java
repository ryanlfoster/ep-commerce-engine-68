package com.elasticpath.domain.asset.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.asset.MutableImageMap;

/**
 * Maps an image key and locale to an image path. 
 */
public class ImageMapImpl implements MutableImageMap {
	private static final long serialVersionUID = 1L;

	private final Map<Pair<Locale, String>, String> localizedImages = new HashMap<Pair<Locale, String>, String>();
	
	@Override
	public Set<String> getImageKeys() {
		Set<String> keys = new HashSet<String>();
		for (Pair<Locale, String> imageKey : localizedImages.keySet()) {
			keys.add(imageKey.getSecond());
		}
		return Collections.unmodifiableSet(keys);
	}

	@Override
	public Set<String> getImageKeys(final Locale locale) {
		Set<String> keys = new HashSet<String>();
		for (Pair<Locale, String> imageKey : localizedImages.keySet()) {
			if (compatibleLocales(locale, imageKey.getFirst())) {
				keys.add(imageKey.getSecond());
			}
		}
		return Collections.unmodifiableSet(keys);
	}


	/**
	 * Find the image using the most specific key for the given locale, trying the full locale (language, country, variant) before falling 
	 * back to just the language and country, then finally to just the language.
	 * 
	 * @param key the key
	 * @param locale the locale
	 * @return the image path
	 */
	@Override
	public String getImagePath(final String key, final Locale locale) {
		Locale newLocale = locale;
		String imagePath = localizedImages.get(Pair.of(newLocale, key));
		while (imagePath == null && canBeBroaden(newLocale)) {
			newLocale = broadenLocale(newLocale);
			imagePath = localizedImages.get(Pair.of(newLocale, key));
		}
		return imagePath;
	}

	private Locale broadenLocale(final Locale locale) {
		if (locale == null) {
			return null;
		}
		if (StringUtils.isEmpty(locale.getVariant())) {
			if (StringUtils.isEmpty(locale.getCountry())) {
				return null;
			}
			return new Locale(locale.getLanguage());
		}
		return new Locale(locale.getLanguage(), locale.getCountry());
	}

	private boolean canBeBroaden(final Locale locale) {
		return locale != null;
	}
	
	private boolean compatibleLocales(final Locale localeToCheck, final Locale baseLocale) {
		return baseLocale == null
				|| baseLocale.equals(localeToCheck)
				|| baseLocale.equals(broadenLocale(localeToCheck))
				|| baseLocale.equals(broadenLocale(broadenLocale(localeToCheck)));
	}

	@Override
	public boolean hasImages(final Locale locale) {
		for (Pair<Locale, String> imageKey : localizedImages.keySet()) {
			if (imageKey.getFirst() == null || imageKey.getFirst().equals(locale)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addImage(final String key, final String path, final Locale locale) {
		localizedImages.put(Pair.of(locale, key), path);
	}

	@Override
	public void addImage(final String key, final String path) {
		addImage(key, path, null);
	}


}
