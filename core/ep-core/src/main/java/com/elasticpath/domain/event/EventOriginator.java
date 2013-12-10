/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.event;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Customer;

/**
 * Represents who popup the system events.
 *
 */
public interface EventOriginator extends EpDomain {

	/**
	 * Get the event originator type.
	 * @return the type
	 */
	EventOriginatorType getType();

	/**
	 * Set the event originator type.
	 * @param type the type to set
	 */
	void setType(final EventOriginatorType type);

	/**
	 * Get the cmUser, null if not related to the cmUser.
	 * @return the cmUser
	 */
	CmUser getCmUser();

	/**
	 * Set the cmUser, null if not related to the cmUser.
	 *
	 * @param cmUser the cmUser to set
	 */
	void setCmUser(final CmUser cmUser);

	/**
	 * Get the customer, null if not related to the customer.
	 *
	 * @return the customer
	 */
	Customer getCustomer();

	/**
	 * Set the customer, null if not related to the customer.
	 *
	 * @param customer the customer to set
	 */
	void setCustomer(final Customer customer);

}
