/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.event.EventOriginatorType;

/**
 * Test cases for <code>OrderEventImpl</code>.
 */
public class OrderEventImplTest {
	private static final String NOTE_STRING = "This is a test";
	private static final String TITLE_STRING = "TestTitle";
	private static final Date TEST_DATE = new Date();

	private OrderEventImpl orderEventImpl;

	@Before
	public void setUp() throws Exception {
		orderEventImpl = new OrderEventImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderEventImpl.getCreatedDate()'.
	 */
	@Test
	public void testGetSetCreatedDate() {
		orderEventImpl.setCreatedDate(TEST_DATE);
		assertEquals(TEST_DATE, orderEventImpl.getCreatedDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderEventImpl.getCreatedBy()'.
	 */
	@Test
	public void testGetSetCreatedBy() {
		CmUser cmUser = new CmUserImpl();
		orderEventImpl.setCreatedBy(cmUser);
		assertSame(cmUser, orderEventImpl.getCreatedBy());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderEventImpl.getOriginatorType()'.
	 */
	@Test
	public void testGetSetOriginatorType() {
		orderEventImpl.setOriginatorType(EventOriginatorType.CMUSER);
		assertSame(EventOriginatorType.CMUSER, orderEventImpl.getOriginatorType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderEventImpl.getTitle()'.
	 */
	@Test
	public void testGetSetTitle() {
		orderEventImpl.setTitle(TITLE_STRING);
		assertSame(TITLE_STRING, orderEventImpl.getTitle());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.OrderEventImpl.getNote()'.
	 */
	@Test
	public void testGetSetNote() {
		orderEventImpl.setNote(NOTE_STRING);
		assertEquals(NOTE_STRING, orderEventImpl.getNote());
	}
}
