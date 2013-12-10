package com.elasticpath.tools.sync.target.impl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for the Product Dao Replacer.
 *
 */
public class ProductDaoReplacerTest {

    private ProductDaoReplacer replacer;

    /**
     * Sets up the test.
     */
    @Before
    public void setUp() {
        replacer = new ProductDaoReplacer();
    }

	/**
     * Tests the reimplementation of the notification method.
     * @throws Throwable from ProductDaoReplacer.reimplement
     * @throws IllegalArgumentException in this test due to unexpected args[] param
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrongArgsForReimplementation() throws Throwable {
		final Long uidpk = 2L;

        final Object[] args = new Object[1];
		args[0] = uidpk;

        final Object obj = null;
        final Method method = null;

        replacer.reimplement(obj, method, args);

        assertEquals(1, replacer.getAffectedCategoryUids().size());
        assertEquals(uidpk, replacer.getAffectedCategoryUids().iterator().next());
    }

	/**
     * Tests the reimplementation of the notification method.
	 * @throws Throwable from ProductDaoReplacer.reimplement
     */
    @Test
    public void testReimplementAddsToInternalCollectionAndReturnsEmpty() throws Throwable {
		final Collection<Long> uidpks = new HashSet<Long>();
		uidpks.add(1L);
		uidpks.add(2L);

		final Object[] args = new Object[1];
		args[0] = uidpks;

		final Object obj = null;
		final Method method = null;

		replacer.reimplement(obj, method, args);

		assertEquals("Unexpected contents of category UIDs collection",
				uidpks, replacer.getAffectedCategoryUids());
	}

}