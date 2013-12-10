/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.customer.CustomerAddress;

/**
 * A Customer's address.
 */
@Entity
@Table (name = CustomerAddressImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerAddressImpl extends AbstractAddressImpl implements CustomerAddress {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TADDRESS";
	
	private long uidPk;

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
