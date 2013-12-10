/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.skuconfiguration;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.Orderable;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents an available option value for a SKU option. Example option values include red,
 * green, small, large, etc.
 */
public interface SkuOptionValue extends Entity, Orderable {

	/**
	 * Get the key corresponding to this option value.
	 *
	 * @return the key for this option value
	 */
	String getOptionValueKey();

	/**
	 * Set the key corresponding to this option value.
	 *
	 * @param optionValueKey the key for this option
	 */
	void setOptionValueKey(final String optionValueKey);

	/**
	 * Get the path to the image corresponding to this option value.
	 *
	 * @return the path to the image
	 */
	String getImage();

	/**
	 * Set the path to the image corresponding to this option value.
	 *
	 * @param image the path to the image corresponding to this option value
	 */
	void setImage(final String image);


	/**
	 * Get the order in which this SKU option value should appear.
	 *
	 * @return the order number
	 */
	int getOrdering();

	/**
	 * Set the order in which this SKU option value should appear.
	 *
	 * @param order the ordering number
	 */
	void setOrdering(final int order);

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 *
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();

	/**
	 * Set the <code>LocalizedProperties</code>.
	 *
	 * @param properties the <code>LocalizedProperties</code>
	 */
	void setLocalizedProperties(final LocalizedProperties properties);

	/**
	 * Get the localized properties.
	 *
	 * @return the localized properties object
	 */
	Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap();

	/**
	 * Gets the display name of this option value for the
	 * given locale, falling back to the default locale if required.
	 * @param locale the Locale
	 * @return the display name
	 * @deprecated Use getDisplayName(locale, fallback)
	 */
	@Deprecated
	String getDisplayName(Locale locale);

	/**
	 * Get the localized display name in the given locale,
	 * falling back to the display name for the locale in the associated Master
	 * Catalog's default locale if requested.
	 *
	 * @param locale the locale in which to return the display name
	 * @param fallback if true, will fallback to the display name in the associated Master
	 * Catalog's default locale if required.
	 * @return the display name, or null if not found
	 */
	String getDisplayName(Locale locale, boolean fallback);

	/**
	 * Sets the display name of this option value.
	 * @param locale the Locale
	 * @param displayName locale dependent display name
	 */
	void setDisplayName(Locale locale, String displayName);

	/**
	 * Sets the associated SkuOption. This should only be used to maintain the bidirectional relationship.
	 * @param skuOption the SkuOption
	 */
	void setSkuOption(final SkuOption skuOption);

	/**
	 * Gets the associated SkuOption.
	 * @return the SkuOption containing this SkuOptionValue
	 */
	SkuOption getSkuOption();

}
