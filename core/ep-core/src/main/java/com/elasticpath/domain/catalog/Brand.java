/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.persistence.api.Entity;

/**
 * <code>Brand</code> represents a product manufacturer/brand information.
 */
public interface Brand extends Entity, CatalogObject {

	/**
	 * The name of localized property -- display name.
	 */
	String LOCALIZED_PROPERTY_DISPLAY_NAME = "brandDisplayName";

	/**
	 * Returns the url of the brand logo image.
	 * 
	 * @return the url of the brand logo image
	 */
	String getImageUrl();

	/**
	 * Sets the url of the brand logo image.
	 * 
	 * @param imageUrl the url of the brand logo image
	 */
	void setImageUrl(final String imageUrl);

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 * 
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();
	
	/**
	 * Sets the <code>LocalizedProperties</code>.
	 *
	 * @param localizedProperties the <code>LocalizedProperties</code>
	 */
	void setLocalizedProperties(final LocalizedProperties localizedProperties);

	
	/**
	 * Returns the brand code.
	 * 
	 * @return the brand code of the brand
	 */
	String getCode();

	/**
	 * Sets the brand code.
	 * 
	 * @param code the brand code of the brand
	 */
	void setCode(final String code);
	
	/**
	 * Get the localized display name for this Brand in the given locale,
	 * falling back to the display name for the locale in this Brand's Master
	 * Catalog's default locale.
	 * 
	 * @param locale the locale in which to return the display name
	 * @return the Brand's display name
	 * @deprecated use getDisplayName(Locale, boolean) instead.
	 */
	@Deprecated
	String getDisplayName(Locale locale);
	
	/**
	 * Get the localized display name for this Brand in the given locale,
	 * falling back to the display name for the locale in this Brand's Master
	 * Catalog's default locale if requested.
	 * 
	 * @param locale the locale in which to return the display name
	 * @param fallback if true, will fallback to the display name in the Brand's Catalog if required
	 * @return the Brand's display name
	 */
	String getDisplayName(Locale locale, boolean fallback);
	
	/**
	 * Get the localized properties map.
	 * @return the map
	 */
	Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap();

	/**
	 * Set the property map.
	 * @param localizedPropertiesMap the map to set
	 */
	void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap);


}