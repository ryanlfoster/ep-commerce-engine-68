package com.elasticpath.domain.pricing;

import com.elasticpath.persistence.api.Persistable;

/**
 * Descriptor of a Price List, contains the metadata for a collection of base amounts.
 */
public interface PriceListDescriptor extends Persistable {
	
	/**
	 * @return the currency code for the price list
	 */
	String getCurrencyCode();

	/**
	 * Set the currency code for the price list.
	 *
	 * @param currencyCode currency code for the price list
	 */
	void setCurrencyCode(String currencyCode);

	/**
	 * @return description of the price list
	 */
	String getDescription();

	/**
	 * Set the description for the price list.
	 *
	 * @param description for the price list.
	 */
	void setDescription(String description);

	/**
	 * @return name of the price list
	 */
	String getName();

	/**
	 * Set the name of this price list.
	 * @param name the name of the price list
	 */
	void setName(String name);

	/**
	 * @return the GUID of the price list
	 */
	String getGuid();

	/**
	 * Set the guid of this price list.
	 * @param guid the guid of the price list
	 */
	void setGuid(String guid);
	
	/**
	 * @return whether or not the price list is hidden.
	 */
	boolean isHidden();
	
	/**
	 * Set whether or not the price list is hidden.
	 * @param hidden the boolean to set
	 */
	void setHidden(boolean hidden);
	
	
}