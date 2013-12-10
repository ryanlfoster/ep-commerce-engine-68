/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource;

/**
 * Creates different types of CyberSource subscribers for use in testing.
 */
public interface CyberSourceSubscriberFactory {
	/**
	 * Creates a subscriber that can be billed.
	 * @return the ID of the subscriber that was created
	 */
	String createBillableSubscriber();
}
