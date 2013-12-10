/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.audit.impl;

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

import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Defines a change transaction. This is the <b>business</b> transaction that
 * surrounds a collection of change operations.
 */
@Entity
@Table(name = ChangeTransactionImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ChangeTransactionImpl extends AbstractPersistableImpl implements ChangeTransaction {

	private static final long serialVersionUID = 7773480506609685066L;

	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TCHANGETRANSACTION";

	private long uidPk;

	private String transactionId;

	private Date changeDate;

	/**
	 * Gets the unique identifier for this domain model object.
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS",
					pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
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
	 * Get an identifier for the transaction.
	 *
	 * @return the transactionId
	 */
	@Basic
	@Column(name = "TRANSACTION_ID")
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * Set the transaction identifier.
	 *
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}
	
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CHANGE_DATE", nullable = false)
	public Date getChangeDate() {
		return changeDate;
	}

	@Override
	public void setChangeDate(final Date changeDate) {
		this.changeDate = changeDate;
	}
}

