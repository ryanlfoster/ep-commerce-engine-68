package com.elasticpath.domain.cmuser.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;

/**
 * Test case for <code>UserPasswordHistoryItemImpl</code> checks that items are equal if and only if old passwords are equal.
 */
public class UserPasswordHistoryItemImplTest {
	
	private UserPasswordHistoryItem historyItem1;
	
	private UserPasswordHistoryItem historyItem2;

	/**
	 * Prepare two instances of user password history item.
	 * Date when passwords were expired are different for these two items
	 */
	@Before
	public void setUp() {
		final long oneDate = 1234L;
		final long anotherDate = 4321L;
		historyItem1 = new UserPasswordHistoryItemImpl();
		historyItem1.setExpirationDate(new Date(oneDate));
		historyItem2 = new UserPasswordHistoryItemImpl();
		historyItem2.setExpirationDate(new Date(anotherDate));
	}

	/**
	 * Check that two password history items with equal passwords are equal.
	 * Equal password history items must have the same hash code
	 */
	@Test
	public void testEquals() {
		final String password = "password1";
		historyItem1.setOldPassword(password);
		historyItem2.setOldPassword(password);
		assertEquals(historyItem1, historyItem2);
		assertEquals(historyItem1.hashCode(), historyItem2.hashCode());
	}

	/**
	 * Check that two items with the same password expiration date but different passwords aren't equal.
	 */
	@Test
	public void testNotEqual() {
		historyItem1.setExpirationDate(new Date());
		historyItem2.setExpirationDate(historyItem1.getExpirationDate());
		historyItem1.setOldPassword("password1");
		historyItem2.setOldPassword("password2");
		assertFalse(historyItem1.equals(historyItem2));
	}
}
