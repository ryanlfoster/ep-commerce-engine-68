/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.event;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Customer;

/**
 * The helper on the <code>EventOriginator</code>.
 * Help to generate the event originator who makes the events.
 * 
 */
public interface EventOriginatorHelper {

	/**
	 * Inject the ElasticPath singleton.
	 * 
	 * @param elasticpath the ElasticPath singleton.
	 */
	void setElasticPath(final ElasticPath elasticpath);

	/**
	 * Get the ElasticPath singleton.
	 * 
	 * @return elasticpath the ElasticPath singleton.
	 */
	ElasticPath getElasticPath();

	/**
	 * Create the event originator with the cmUser.
	 * @param cmUser the cmUser who is the event originator.
	 * @return the event originator.
	 */
	EventOriginator getCmUserOriginator(final CmUser cmUser);

	/**
	 * Create the event originator with the web service user.
	 * @param wsUser the wsUser who is the event originator.
	 * @return the event originator.
	 */
	EventOriginator getWsUserOriginator(final CmUser wsUser);

	/**
	 * Create the event originator with the customer.
	 * @param customer the customer who is the event originator.
	 * @return the event originator.
	 */
	EventOriginator getCustomerOriginator(final Customer customer);

	/**
	 * Create the event originator which is the system.
	 * @return the event originator.
	 */
	EventOriginator getSystemOriginator();

}
