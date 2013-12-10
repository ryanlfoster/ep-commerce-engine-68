/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

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

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.customer.CustomerDeleted;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The default implementation of <code>Customer</code>.
 */
@Entity
@Table(name = CustomerDeletedImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerDeletedImpl extends AbstractPersistableImpl implements CustomerDeleted {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERDELETED";
	
	private long customerUid;

	private Date deletedDate;

	private long uidPk;

	/**
	 * Default constructor.
	 */
	public CustomerDeletedImpl() {
		super();
	}

	/**
	 * Returns the uid of the deleted customer.
	 * 
	 * @return the uid of the deleted customer
	 */
	@Basic
	@Column(name = "CUSTOMER_UID", nullable = false)
	public long getCustomerUid() {
		return customerUid;
	}

	/**
	 * Sets the uid of the deleted customer.
	 * 
	 * @param customerUid the uid of the deleted customer.
	 */
	public void setCustomerUid(final long customerUid) {
		this.customerUid = customerUid;
	}

	/**
	 * Returns the date when the customer was deleted.
	 * 
	 * @return the date when the customer was deleted
	 */
	@Basic
	@Column(name = "DELETED_DATE", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getDeletedDate() {
		return deletedDate;
	}

	/**
	 * Sets the date when the customer was deleted.
	 * 
	 * @param deletedDate the date when the customer was deleted
	 */
	public void setDeletedDate(final Date deletedDate) {
		if (deletedDate == null) {
			throw new EpDomainException("name cannot be set to null.");
		}
		this.deletedDate = deletedDate;
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
