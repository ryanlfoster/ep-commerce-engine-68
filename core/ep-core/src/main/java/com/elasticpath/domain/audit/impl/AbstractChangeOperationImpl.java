/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.audit.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.audit.ChangeOperation;
import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Abstract class for change operation.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = AbstractChangeOperationImpl.TABLE_NAME)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DataCache(enabled = false)
public class AbstractChangeOperationImpl extends AbstractPersistableImpl implements ChangeOperation {
	private static final long serialVersionUID = 1L;

	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TCHANGEOPERATION";

	private long uidPk;

	private ChangeTransaction changeTransaction;

	private int operationOrder;

	private String changeTypeName;

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
	 * Get the business transaction this operation belongs to.
	 *
	 * @return the changesetTransaction
	 */
	@ManyToOne(targetEntity = ChangeTransactionImpl.class)
	@JoinColumn(name = "CHANGE_TRANSACTION_UID")
	@ForeignKey
	public ChangeTransaction getChangeTransaction() {
		return changeTransaction;
	}

	/**
	 * Set the business transaction this operation belongs to.
	 *
	 * @param changeTransaction the changeTransaction to set
	 */
	public void setChangeTransaction(final ChangeTransaction changeTransaction) {
		this.changeTransaction = changeTransaction;
	}

	/**
	 * @return the order
	 */
	@Basic
	@Column(name = "OPERATION_ORDER")
	public int getOperationOrder() {
		return operationOrder;
	}

	/**
	 * @param operationOrder the order to set
	 */
	public void setOperationOrder(final int operationOrder) {
		this.operationOrder = operationOrder;
	}

	/**
	 * Get the change type.
	 *
	 * @return the <code>ChangeType</code>
	 */
	@Basic
	@Column(name = "CHANGE_TYPE", nullable = false)
	public String getChangeTypeName() {
		return changeTypeName;
	}

	/**
	 * Set the change type.
	 *
	 * @param changeTypeName the name of change type
	 */
	public void setChangeTypeName(final String changeTypeName) {
		this.changeTypeName = changeTypeName;
	}
	
	/**
	 * Get the change type.
	 *
	 * @return the <code>ChangeType</code>
	 */
	@Transient
	public ChangeType getChangeType() {
		return ChangeType.getChangeType(getChangeTypeName());
	}

	/**
	 * Set the change type.
	 *
	 * @param changeType the <code>ChangeType</code> to set
	 */
	public void setChangeType(final ChangeType changeType) {
		setChangeTypeName(changeType.getName());
	}
	
}
