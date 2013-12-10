package com.elasticpath.domain.order;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.persistence.api.Persistable;

/**
 * <code>OrderLock</code> represents an order's lock. 
 */
public interface OrderLock extends Persistable {
	/**
	 * @return the order
	 */
	Order getOrder();

	/**
	 * @param order the order to set
	 */
	void setOrder(Order order);

	/**
	 * @return the cmUser
	 */
	CmUser getCmUser();

	/**
	 * @param cmUser the cmUser to set
	 */
	void setCmUser(CmUser cmUser);
	
	/**
	 * Get the date in milliseconds that this order lock was created on.
	 * 
	 * @return the created date
	 */
	long getCreatedDate();

	/**
	 * Set the date in milliseconds that the order lock is created.
	 * 
	 * @param createdDate the start date
	 */
	void setCreatedDate(final long createdDate);
}
