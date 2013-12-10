/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.event.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.impl.ElasticPathImpl;

/**
 * The helper on the <code>EventOriginator</code>.
 * Help to generate the event originator who makes the events.
 * 
 */
@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
public class EventOriginatorHelperImpl implements EventOriginatorHelper {
	private ElasticPath elasticPath;

	/**
	 * Inject the ElasticPath singleton.
	 * 
	 * @param elasticpath
	 *            the ElasticPath singleton.
	 */
	public void setElasticPath(final ElasticPath elasticpath) {
		this.elasticPath = elasticpath;
	}

	/**
	 * Get the ElasticPath singleton.
	 * 
	 * @return elasticpath the ElasticPath singleton.
	 */
	public ElasticPath getElasticPath() {
		return this.elasticPath;
	}


	/**
	 * Create the event originator with the cmUser.
	 * @param cmUser the cmUser who is the event originator.
	 * @return the event originator.
	 */
	public EventOriginator getCmUserOriginator(final CmUser cmUser) {
		EventOriginator originator = ElasticPathImpl.getInstance().getBean(ContextIdNames.EVENT_ORIGINATOR);
		originator.setType(EventOriginatorType.CMUSER);
		originator.setCmUser(cmUser);
		return originator;
	}

	/**
	 * Create the event originator with the web service user.
	 * @param wsUser the wsUser who is the event originator.
	 * @return the event originator.
	 */
	public EventOriginator getWsUserOriginator(final CmUser wsUser) {
		EventOriginator originator = ElasticPathImpl.getInstance().getBean(ContextIdNames.EVENT_ORIGINATOR);
		originator.setType(EventOriginatorType.WSUSER);
		originator.setCmUser(wsUser);
		return originator;
	}

	/**
	 * Create the event originator with the customer.
	 * @param customer the customer who is the event originator.
	 * @return the event originator.
	 */
	public EventOriginator getCustomerOriginator(final Customer customer) {
		EventOriginator originator = ElasticPathImpl.getInstance().getBean(ContextIdNames.EVENT_ORIGINATOR);
		originator.setType(EventOriginatorType.CUSTOMER);
		originator.setCustomer(customer);
		return originator;
	}

	/**
	 * Create the event originator which is the system.
	 * @return the event originator.
	 */
	public EventOriginator getSystemOriginator() {
		EventOriginator originator = ElasticPathImpl.getInstance().getBean(ContextIdNames.EVENT_ORIGINATOR);
		originator.setType(EventOriginatorType.SYSTEM);
		return originator;
	}
}
