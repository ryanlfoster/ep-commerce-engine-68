/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test cases for {@link AbstractAggregationObjectProvider}.
 */
public class AbstractAggregationObjectProviderTest {

	private static final String ITEM2 = "item2";

	private static final String ITEM1 = "item1";

	private AbstractAggregationObjectProvider<String> aggregateObjectProvider;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@SuppressWarnings("unchecked")
	private final Iterable<String> objectProvider1 = context.mock(Iterable.class, "iterable1");

	@SuppressWarnings("unchecked")
	private final Iterable<String> objectProvider2 = context.mock(Iterable.class, "iterable2");

	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		aggregateObjectProvider = new AbstractAggregationObjectProvider<String>() {

			@Override
			protected List<Iterable<String>> getAllProviders() {
				List<Iterable<String>> objectProviders = new ArrayList<Iterable<String>>();
				objectProviders.add(objectProvider1);
				objectProviders.add(objectProvider2);
				return objectProviders;
			}

		};
	}

	/**
	 * Tests that the aggregation of iterators works as expected 
	 * and in the same order they are supposed to work.
	 */
	@Test
	public void testIteratorHappyCase() {
		context.checking(new Expectations() { {
			oneOf(objectProvider1).iterator();
			will(returnValue(Arrays.asList(ITEM1).iterator()));

			oneOf(objectProvider2).iterator();
			will(returnValue(Arrays.asList(ITEM2).iterator()));
		} });
		Iterator<String> iter = aggregateObjectProvider.iterator();
		assertEquals("Item1 should be on first place", ITEM1, iter.next());
		assertEquals("Item2 should be on second place", ITEM2, iter.next());
		assertFalse("Exctly two elements were expected and no more", iter.hasNext());
	}
	
	/**
	 * Expects that if no elements are available an exception will be thrown.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testIteratorNoProviders() {
		context.checking(new Expectations() { {
			oneOf(objectProvider1).iterator();
			will(returnValue(Arrays.<String>asList().iterator()));

			oneOf(objectProvider2).iterator();
			will(returnValue(Arrays.<String>asList().iterator()));
		} });
		Iterator<String> iter = aggregateObjectProvider.iterator();
		iter.next(); // this should throw exception
		
	}

}
