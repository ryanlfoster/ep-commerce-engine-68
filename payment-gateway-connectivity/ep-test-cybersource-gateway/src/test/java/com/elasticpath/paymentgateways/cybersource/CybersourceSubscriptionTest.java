/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.elasticpath.paymentgateways.cybersource.impl.CyberSourceSubscriberFactoryImpl;

/**
 * Series of test to make sure cybersource subcriptions are functional.
 */
public class CybersourceSubscriptionTest {

	private final CyberSourceSubscriberFactory cyberSourceSubscriberFactory = new CyberSourceSubscriberFactoryImpl();

	/**
	 * Test successfully subscribing against Cybersource.
	 */
	@Test
	public void testCybersourceSubscription() {
		String subscriberId = cyberSourceSubscriberFactory.createBillableSubscriber();
		
		assertNotNull("A subscriber Id should have been returned.", subscriberId);
	}
}
