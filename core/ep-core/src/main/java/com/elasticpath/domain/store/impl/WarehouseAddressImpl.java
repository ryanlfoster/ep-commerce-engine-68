/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.store.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation of <code>WarehouseAddress</code>.
 */
@Entity
@Table(name = WarehouseAddressImpl.TABLE_NAME)
@DataCache(enabled = true)
public class WarehouseAddressImpl extends AbstractPersistableImpl implements WarehouseAddress {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String city;

	private String country;

	private String subCountry;

	private String street1;

	private String street2;

	private String zipOrPostalCode;

	private long uidPk;

	/** Default string length of database strings. */
	private static final int DEFAULT_STRINGLENGTH = 200;

	/** String length of the zipOrPostalCode field. */
	private static final int ZIP_OR_POSTAL_CODE_STRINGLENGTH = 50;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TWAREHOUSEADDRESS";

	/**
	 * Gets the city of this <code>WarehouseAddress</code>.
	 * 
	 * @return the city of this warehouse
	 */
	@Basic(optional = false)
	@Column(name = "CITY", length = DEFAULT_STRINGLENGTH)
	public String getCity() {
		return city;
	}

	/**
	 * Sets the city of this <code>WarehouseAddress</code>.
	 * 
	 * @param city the city of this <code>WarehouseAddress</code>
	 */
	public void setCity(final String city) {
		this.city = city;
	}

	/**
	 * Gets the country of this <code>WarehouseAddress</code>.
	 * 
	 * @return the country of this <code>WarehouseAddress</code>
	 */
	@Basic(optional = false)
	@Column(name = "COUNTRY", length = DEFAULT_STRINGLENGTH)
	public String getCountry() {
		return country;
	}

	/**
	 * Sets the country of this <code>WarehouseAddress</code>.
	 * 
	 * @param country the country of this <code>WarehouseAddress</code>.
	 */
	public void setCountry(final String country) {
		this.country = country;
	}

	/**
	 * Gets the sub-country of this <code>WarehouseAddress</code>.
	 * 
	 * @return the sub-country of this <code>WarehouseAddress</code>
	 */
	@Basic
	@Column(name = "SUB_COUNTRY", length = DEFAULT_STRINGLENGTH)
	public String getSubCountry() {
		return subCountry;
	}

	/**
	 * Sets the sub-country of this <code>WarehouseAddress</code>.
	 * 
	 * @param subCountry the sub-country of this <code>WarehouseAddress</code>.
	 */
	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	/**
	 * Gets the first part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @return the first part of the street address of this <code>WarehouseAddress</code>.
	 */
	@Basic(optional = false)
	@Column(name = "STREET_1", length = DEFAULT_STRINGLENGTH)
	public String getStreet1() {
		return street1;
	}

	/**
	 * Sets the first part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @param street1 the first part of the street address of this <code>WarehouseAddress</code>.
	 */
	public void setStreet1(final String street1) {
		this.street1 = street1;
	}

	/**
	 * Gets the second part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @return the second part of the street address of this <code>WarehouseAddress</code>.
	 */
	@Column(name = "STREET_2", length = DEFAULT_STRINGLENGTH)
	public String getStreet2() {
		return street2;
	}

	/**
	 * Sets the second part of the street address of this <code>WarehouseAddress</code>.
	 * 
	 * @param street2 the second part of the street address of this <code>WarehouseAddress</code>.
	 */
	public void setStreet2(final String street2) {
		this.street2 = street2;
	}

	/**
	 * Gets the zip or postal code of this <code>WarehouseAddress</code>.
	 * 
	 * @return the zip or postal code of this <code>WarehouseAddress</code>.
	 */
	@Basic(optional = false)
	@Column(name = "ZIP_POSTAL_CODE", length = ZIP_OR_POSTAL_CODE_STRINGLENGTH)
	public String getZipOrPostalCode() {
		return zipOrPostalCode;
	}

	/**
	 * Sets the zip or postal code of this <code>WarehouseAddress</code>.
	 * 
	 * @param zipOrPostalCode the zip or postal code of this <code>WarehouseAddress</code>.
	 */
	public void setZipOrPostalCode(final String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}
