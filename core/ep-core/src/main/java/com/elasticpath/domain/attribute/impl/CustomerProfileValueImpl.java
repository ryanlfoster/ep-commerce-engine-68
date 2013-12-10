/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.attribute.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.attribute.CustomerProfileValue;

/**
 * Class required for JPA persistence mapping.
 */
@Entity
@Table(name = CustomerProfileValueImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerProfileValueImpl extends AbstractAttributeValueImpl implements CustomerProfileValue, DatabaseLastModifiedDate {
    
	/** Allocation size for JPA_GENERATED_KEYS id. */
    private static final int ALLOCATION_SIZE = 1000;
	
	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TCUSTOMERPROFILEVALUE";

	private static final long serialVersionUID = 5000000001L;

	private long uidPk;

	private Date lastModifiedDate;

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, 
					table = "JPA_GENERATED_KEYS", 
					pkColumnName = "ID", 
					valueColumnName = "LAST_VALUE", 
					pkColumnValue = TABLE_NAME, 
					allocationSize = ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * {@inheritDoc} <br>
	 * No need to define other fields as Override on equals tests class equivalence for symmetry.
	 */
	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc} <br>
	 * Equals is redefined in this case to handle object equivalence between siblings.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CustomerProfileValueImpl)) {
			return false;
		}
		return super.equals(obj);
	}

}
