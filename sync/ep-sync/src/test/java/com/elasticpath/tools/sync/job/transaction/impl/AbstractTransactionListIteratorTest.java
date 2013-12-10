package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Tests AbstractTransactionListIterator class.
 */
public class AbstractTransactionListIteratorTest {
	
	private AbstractTransactionListIterator iterator;
	
	/**
	 * Overrides command resolver under the test to inject mock syncService.
	 */
	@Before
	public void setUp() {
		iterator = new AbstractTransactionListIterator() {
			@Override
			protected boolean hasNextElement() {
				return false;
			}

			@Override
			protected List<TransactionJobDescriptorEntry> nextElement() {
				return null;
			}
		};
	}
	
	/**
	 * Tests hasNext() method.
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testHasNext1() {
		iterator.hasNext();
	}
	
	/**
	 * Tests hasNext() method.
	 */
	@Test
	public void testHasNext2() {
		iterator.initialize(Collections.<TransactionJobDescriptorEntry>emptyList(), null);
		Assert.assertFalse(iterator.hasNext());
	}
	
	/**
	 * Tests next() method.
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testNext1() {
		iterator.next();
	}
	
	/**
	 * Tests next() method.
	 */
	@Test
	public void testNext2() {
		iterator.initialize(Collections.<TransactionJobDescriptorEntry>emptyList(), null);
		Assert.assertNull(iterator.next());
	}
	
	/**
	 * Tests remove() method.
	 */
	@Test(expected = SyncToolRuntimeException.class)
	public void testRemove1() {
		iterator.remove();
	}
	
	/**
	 * Tests remove() method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRemove2() {
		iterator.initialize(Collections.<TransactionJobDescriptorEntry>emptyList(), null);
		iterator.remove();
	}
	
	/**
	 * Tests removeElement() method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveElement() {
		iterator.removeElement();
	}


}
