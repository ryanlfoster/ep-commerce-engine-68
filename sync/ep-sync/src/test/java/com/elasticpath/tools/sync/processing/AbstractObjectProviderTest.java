/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link AbstractObjectProvider}.
 */
public class AbstractObjectProviderTest {

	private AbstractObjectProvider<String> objectProvider;

	private static final String[] ELEMENTS = { "element1", "element2" };
	
	/**
	 *
	 */
	@Before
	public void setUp() {
		objectProvider = new AbstractObjectProvider<String>() {

			@Override
			protected String getElement(final int index) {
				return ELEMENTS[index];
			}

			@Override
			protected int getSize() {
				return ELEMENTS.length;
			}
		};
	}

	/**
	 * Tests that the iterator returned by {@link AbstractObjectProvider} returns the test elements properly.
	 */
	@Test
	public void testIteratorHappyCase() {
		Iterator<String> iterator = objectProvider.iterator();
		assertEquals(ELEMENTS[0], iterator.next());
		assertEquals(ELEMENTS[1], iterator.next());
		assertFalse(iterator.hasNext());
	}

	/**
	 * Tests that an exception will be thrown if the limits of the iterator are not obeyed.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testNoSuchElementException() {
		Iterator<String> iterator = objectProvider.iterator();
		// two elements expected
		iterator.next();
		iterator.next();
		
		// next line throws an exception
		iterator.next();
	}
	
	/**
	 * Tests that an exception will be thrown if {@link Iterator#remove()} is used.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testIteratorRemove() {
		Iterator<String> iterator = objectProvider.iterator();
		
		iterator.remove();
	}

}
