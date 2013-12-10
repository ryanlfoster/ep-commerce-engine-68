package com.elasticpath.domain.catalogview;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for {@link RangeFilterType}.
 */
public class RangeFilterTypeTest {
	
	/**
	 * Test for {@link RangeFilterType#removeLowerBound()}.
	 */
	@Test
	public void testRemoveLowerBound() {
		assertEquals(RangeFilterType.ALL, RangeFilterType.ALL.removeLowerBound());
		assertEquals(RangeFilterType.ALL, RangeFilterType.MORE_THAN.removeLowerBound());
		assertEquals(RangeFilterType.LESS_THAN, RangeFilterType.BETWEEN.removeLowerBound());
		assertEquals(RangeFilterType.LESS_THAN, RangeFilterType.LESS_THAN.removeLowerBound());
	}
	
	/**
	 * Test for {@link RangeFilterType#removeUpperBound()}.
	 */
	@Test
	public void testRemoveUpperBound() {
		assertEquals(RangeFilterType.ALL, RangeFilterType.ALL.removeUpperBound());
		assertEquals(RangeFilterType.ALL, RangeFilterType.LESS_THAN.removeUpperBound());
		assertEquals(RangeFilterType.MORE_THAN, RangeFilterType.BETWEEN.removeUpperBound());
		assertEquals(RangeFilterType.MORE_THAN, RangeFilterType.MORE_THAN.removeUpperBound());
	}
	
	/**
	 * Test for {@link RangeFilterType#addLowerBound()}.
	 */
	@Test
	public void testAddLowerBound() {
		assertEquals(RangeFilterType.BETWEEN, RangeFilterType.BETWEEN.addLowerBound());
		assertEquals(RangeFilterType.BETWEEN, RangeFilterType.LESS_THAN.addLowerBound());
		assertEquals(RangeFilterType.MORE_THAN, RangeFilterType.ALL.addLowerBound());
		assertEquals(RangeFilterType.MORE_THAN, RangeFilterType.MORE_THAN.addLowerBound());
	}
	
	/**
	 * Test for {@link RangeFilterType#addUpperBound()}.
	 */
	@Test
	public void testAddUpperBound() {
		assertEquals(RangeFilterType.BETWEEN, RangeFilterType.BETWEEN.addUpperBound());
		assertEquals(RangeFilterType.BETWEEN, RangeFilterType.MORE_THAN.addUpperBound());
		assertEquals(RangeFilterType.LESS_THAN, RangeFilterType.ALL.addUpperBound());
		assertEquals(RangeFilterType.LESS_THAN, RangeFilterType.LESS_THAN.addUpperBound());
	}
}
