/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The <code>OrderLock</code> implementation. Acts as a holder for the order and the locker user.
 */
@Entity
@Table(name = OrderLockImpl.TABLE_NAME)
@DataCache(enabled = false)
public class OrderLockImpl extends AbstractPersistableImpl implements OrderLock {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERLOCK";

	private long createdDate;

	private Order order;

	private CmUser cmUser;

	private long uidPk;

	/**
	 * @return the order
	 */
	@OneToOne(targetEntity = OrderImpl.class)
	@Column(name = "ORDER_UID")
	public Order getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(final Order order) {
		this.order = order;
	}

	/**
	 * @return the cmUser
	 */
	@ManyToOne(targetEntity = CmUserImpl.class)
	@Column(name = "USER_UID")
	public CmUser getCmUser() {
		return cmUser;
	}

	/**
	 * @param cmUser the cmUser to set
	 */
	public void setCmUser(final CmUser cmUser) {
		this.cmUser = cmUser;
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
	 * Get the date in milliseconds that this order lock was created on.
	 * 
	 * @return the created date
	 */
	@Basic
	@Column(name = "CREATED_DATE", nullable = false)
	public long getCreatedDate() {
		return createdDate;
	}

	/**
	 * Set the date in milliseconds that the order lock is created.
	 * 
	 * @param createdDate the start date
	 */
	public void setCreatedDate(final long createdDate) {
		this.createdDate = createdDate;
	}
}
