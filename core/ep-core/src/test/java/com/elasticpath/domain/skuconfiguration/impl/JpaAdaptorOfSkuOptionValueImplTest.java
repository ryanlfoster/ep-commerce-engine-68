/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.skuconfiguration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Tests {@link JpaAdaptorOfSkuOptionValueImpl}.
 */
public class JpaAdaptorOfSkuOptionValueImplTest {

	private JpaAdaptorOfSkuOptionValueImpl adaptor;

	@Before
	public void setUp() {
		adaptor = new JpaAdaptorOfSkuOptionValueImpl();
	}
	
	/**
	 * Tests that hashCode() works for hash based collections.
	 */
	@Test
	public void testHashCode() {
		// test two objects with no data defined for them
		JpaAdaptorOfSkuOptionValueImpl adaptor2 = new JpaAdaptorOfSkuOptionValueImpl();
		Set<SkuOptionValue> set = new HashSet<SkuOptionValue>();
		set.add(adaptor);
		set.add(adaptor2);
		
		assertEquals(1, set.size());

		// checks if one of the objects have different value for one of the attributes
		adaptor.setOptionKey("key1");
		adaptor2.setOptionKey(null);
		set.clear();
		
		set.add(adaptor);
		set.add(adaptor2);
		
		assertTrue(set.contains(adaptor));
		assertTrue(set.contains(adaptor2));
		assertEquals(2, set.size());
		
		// make the two objects equal
		adaptor2.setOptionKey("key1");
		set.clear();

		set.add(adaptor);
		set.add(adaptor2);
		
		assertTrue(set.contains(adaptor));
		assertTrue(set.contains(adaptor2));
		assertEquals(1, set.size());
		
	}
	
	/**
	 * Tests that equals() behaves in the expected way.
	 */
	@Test
	public void testEquals() {
		// test two objects with no data defined for them
		JpaAdaptorOfSkuOptionValueImpl adaptor2 = new JpaAdaptorOfSkuOptionValueImpl();

		boolean equals = adaptor.equals(adaptor2);
		assertTrue(equals);
		
		adaptor2.setOptionKey("key1");
		
		equals = adaptor.equals(adaptor2);
		assertFalse(equals);

		equals = adaptor2.equals(adaptor);
		assertFalse(equals);
	}
}
