/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;

import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>SalesTaxCode</code>.
 */
@Entity
@Table(name = TaxCodeImpl.TABLE_NAME)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.STORE_FOR_EDIT, attributes = {
				@FetchAttribute(name = "guid")
		})
})
@DataCache(enabled = true)
public class TaxCodeImpl extends AbstractEntityImpl implements TaxCode {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTAXCODE";
	
	private String code;
	
	private long uidPk;

	private String guid;

	/**
	 * Return the sales tax code.
	 * 
	 * @return the sales tax code.
	 */
	@Basic
	@Column(name = "CODE")
	public String getCode() {
		return this.code;
	}

	/**
	 * Set the sales tax code.
	 * 
	 * @param code - the sales tax code.
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * String representation of the object.
	 * 
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return getCode();
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
	
	/**
	 * Returns <code>true</code> if this object equals to the given object.
	 *
	 * @param obj the given object
	 * @return <code>true</code> if this object equals to the given object
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TaxCode)) {
			return false;
		}

		return this.getGuid().equals(((TaxCode) obj).getGuid());
	}

	/**
	 * Generate the hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		return getGuid().hashCode();
	}
}
