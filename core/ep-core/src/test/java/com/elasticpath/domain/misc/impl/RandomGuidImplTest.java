/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Test <code>RandomGuidImpl</code>.
 */
public class RandomGuidImplTest {

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.RandomGuidImpl.RandomGuidImpl()'.
	 */
	@Test
	public void testRandomGuidImpl() {
		RandomGuidImpl randomGuidImpl1 = new RandomGuidImpl();
		RandomGuidImpl randomGuidImpl2 = new RandomGuidImpl();
		assertFalse(randomGuidImpl1.toString().equals(randomGuidImpl2.toString()));
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.RandomGuidImpl.RandomGuidImpl(boolean)'.
	 */
	@Test
	public void testRandomGuidImplSecure() {
		RandomGuidImpl randomGuidImpl1 = new RandomGuidImpl(true);
		RandomGuidImpl randomGuidImpl2 = new RandomGuidImpl(true);
		assertFalse(randomGuidImpl1.toString().equals(randomGuidImpl2.toString()));
	}

}
