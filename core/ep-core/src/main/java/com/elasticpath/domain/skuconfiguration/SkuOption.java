/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.skuconfiguration;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents a SKU option that can be configured.
 */
@SuppressWarnings("PMD.CloneMethodMustImplementCloneable")
public interface SkuOption extends Entity, Cloneable {

	/**
	 * The key for the frequency sku option key.
	 * */
	String FREQUENCY_OPTION_KEY = "Frequency";

	/**
	 * Get the key of this SKU option (e.g. Color).
	 *
	 * @return the SKU option name.
	 */
	String getOptionKey();

	/**
	 * Set the key of this SKU option.
	 *
	 * @param optionKey the key of the option (e.g. Color).
	 */
	void setOptionKey(final String optionKey);

	/**
	 * Get the available values for this SKU option.
	 *
	 * @return a collection of <code>SkuValue</code>s
	 */
	Collection<SkuOptionValue> getOptionValues();

	/**
	 * Sets the available values for this SKU option.
	 *
	 * @param optionValues a set of <code>SkuOptionValue</code>s
	 */
	void setOptionValues(Set<SkuOptionValue> optionValues);

	/**
	 * Add an option value to the set of available values.
	 *
	 * @param optionValue an <code>OptionValue</code>
	 */
	void addOptionValue(final SkuOptionValue optionValue);

	/**
	 * Set the option value that is to appear by default if no option has yet been selected.
	 * Note that the default option value is computed based on the default SKU and is not persisted
	 *
	 * @param defaultOptionValue the default <code>SkuOptionValue</code>
	 */
	void setDefaultOptionValue(SkuOptionValue defaultOptionValue);

	/**
	 * Get the option value that has been designated the default value if no option has yet been selected.
	 * Note that the default option value is computed based on the default SKU and is not persisted
	 *
	 * @return the default <code>SkuOptionValue</code>
	 */
	SkuOptionValue getDefaultOptionValue();

	/**
	 * Create a shallow of this <code>SkuOption</code>.
	 *
	 * @return a shallow copy
	 * @throws CloneNotSupportedException if the object cannot be cloned
	 */
	Object clone() throws CloneNotSupportedException;

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 *
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();

	/**
	 * Returns <code>true</code> if this <code>SkuOption</code> contains the given value code.
	 *
	 * @param valueCode the sku option value code
	 * @return <code>true</code> if this <code>SkuOption</code> contains the given value code
	 */
	boolean contains(String valueCode);

	/**
	 * Returns the corresponding <code>SkuOptionValue</code> of the given value code.
	 *
	 * @param valueCode the sku option value code
	 * @return the corresponding <code>SkuOptionValue</code> of the given value code
	 */
	SkuOptionValue getOptionValue(String valueCode);

	/**
	 * Get the catalog that this sku option belongs to.
	 * @return the catalog
	 */
	Catalog getCatalog();

	/**
	 * Set the catalog that this sku option belongs to.
	 * @param catalog the catalog to set
	 */
	void setCatalog(final Catalog catalog);
	
	/**
	 * Get the max ordering of the optionValues.
	 * @return the max ordering
	 */
	int getMaxOrdering();

	/**
	 * Get the minimal ordering of the optionValues.
	 * @return the minimal ordering value
	 */
	int getMinOrdering();

	/**
	 * Check if the optionValueKey exists.
	 * @param optionValueKey the option value key to be checked
	 * @return true if exist, otherwise false
	 */
	boolean isValueKeyExist(final String optionValueKey);	
	
	/**
	 * Gets the display name of this SkuOption for the given locale,
	 * falling back to the SkuOption's Catalog's default locale
	 * if both requested and required, otherwise returns null
	 * if the display name doesn't exist for the given locale.
	 * @param locale the locale for which the display name should be returned
	 * @param fallback true if the SkuOption's Catalog's default locale should
	 * be used as a fallback, false if there is no fallback
	 * @return the display name in the given locale, or in the containing Catalog's
	 * default locale if fallback is requested and required, or null if no 
	 * display name is found
	 */
	String getDisplayName(Locale locale, boolean fallback);
	
	/**
	 * Sets the display name of this SkuOption.
	 * 
	 * @param name the display name
	 * @param locale the dependent display name or null if none
	 */
	void setDisplayName(String name, Locale locale);

	/**
	 * Removes a SKU option value from the list.
	 * 
	 * @param optionValueKey the SKU option value key
	 */
	void removeOptionValue(String optionValueKey);

}
