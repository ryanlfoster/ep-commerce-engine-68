/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.store;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>WarehouseAddress</code> represents an address of a <code>Warehouse</code>.
 */
public interface WarehouseAddress extends Persistable {

	/**
	 * Gets the city of this <code>WarehouseAddress</code>.
	 * 
	 * @return the city of this warehouse
	 */
	String getCity();

	/**
	 * Sets the city of this <code>WarehouseAddress</code>.
	 * 
	 * @param city the city of this <code>WarehouseAddress</code>
	 */
	void setCity(final String city);

	/**
	 * Gets the country of this <code>WarehouseAddress</code>.
	 * 
	 * @return the country of this <code>WarehouseAddress</code>
	 */
	String getCountry();

	/**
	 * Sets the country of this <code>WarehouseAddress</code>.
	 * 
	 * @param country the country of this <code>WarehouseAddress</code>.
	 */
	void setCountry(final String country);

	/**
	 * Gets the sub-country of this <code>WarehouseAddress</code>.
	 * 
	 * @return the sub-country of this <code>WarehouseAddress</code>
	 */
	String getSubCountry();

	/**
	 * Sets the sub-country of this <code>WarehouseAddress</code>.
	 * 
	 * @param subCountry the sub-country of this <code>WarehouseAddress</code>.
	 */
	void setSubCountry(final String subCountry);

	/**
	 * Gets the first part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @return the first part of the street address of this <code>WarehouseAddress</code>.
	 */
	String getStreet1();

	/**
	 * Sets the first part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @param street1 the first part of the street address of this <code>WarehouseAddress</code>.
	 */
	void setStreet1(final String street1);

	/**
	 * Gets the second part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @return the second part of the street address of this <code>WarehouseAddress</code>.
	 */
	String getStreet2();

	/**
	 * Sets the second part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @param street2 the second part of the street address of this <code>WarehouseAddress</code>.
	 */
	void setStreet2(final String street2);

	/**
	 * Gets the zip or postal code of this <code>WarehouseAddress</code>.
	 * 
	 * @return the zip or postal code of this <code>WarehouseAddress</code>.
	 */
	String getZipOrPostalCode();

	/**
	 * Sets the zip or postal code of this <code>WarehouseAddress</code>.
	 * 
	 * @param zipOrPostalCode the zip or postal code of this <code>WarehouseAddress</code>.
	 */
	void setZipOrPostalCode(final String zipOrPostalCode);
}
