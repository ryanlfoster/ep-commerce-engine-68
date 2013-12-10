/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/** Test cases for <code>WishListMessageImpl</code>. */
public class WishListMessageImplTest {

	private static final String TEST_STRING = "TestString";
	
	private WishListMessageImpl wishListMessage;

	@Before
	public void setUp() throws Exception {
		this.wishListMessage = new WishListMessageImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl.setRecipientEmails(String)'.
	 */
	@Test
	public void testGetSetRecipientEmails() {
		wishListMessage.setRecipientEmails(TEST_STRING);
		assertSame(TEST_STRING, wishListMessage.getRecipientEmails());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl.setSenderName(String)'.
	 */
	@Test
	public void testGetSetSenderName() {
		wishListMessage.setSenderName(TEST_STRING);
		assertSame(TEST_STRING, wishListMessage.getSenderName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.shoppingcart.impl.WishListMessageImpl.setMessage(String)'.
	 */
	@Test
	public void testGetSetMessage() {
		wishListMessage.setMessage(TEST_STRING);
		assertSame(TEST_STRING, wishListMessage.getMessage());
	}

}
