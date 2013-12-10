package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.impl.TransactionJobDescriptorEntryImpl;

/**
 * Tests TransactionPerGivenQtyJobEntryIterator class.
 */
public class TransactionPerGivenQtyJobEntryIteratorTest {

	private TransactionPerGivenQtyJobEntryIterator entryIterator;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() {
		entryIterator = new TransactionPerGivenQtyJobEntryIterator();
	}

	/**
	 * Tests initialize() method.
	 */
	@Test
	public void testInitialize() {
		Assert.assertEquals(1, entryIterator.getOffset());
		entryIterator.initialize(null, Arrays.asList(("2")));
		Assert.assertEquals(2, entryIterator.getOffset());
	}

	/**
	 * Tests setOffset() method.
	 */
	@Test
	public void testSetOffset() {
		entryIterator.setOffset(2);
		Assert.assertEquals(2, entryIterator.getOffset());

		entryIterator.setOffset(-1);
		Assert.assertEquals(Integer.MAX_VALUE, entryIterator.getOffset());
	}

	/**
	 * Tests hasNextElement() method.
	 */
	@Test
	public void testHasNextElement() {
		List<TransactionJobDescriptorEntry> entries = new ArrayList<TransactionJobDescriptorEntry>();
		entries.add(new TransactionJobDescriptorEntryImpl());
		entries.add(new TransactionJobDescriptorEntryImpl());

		entryIterator.initialize(entries, Arrays.asList(("2")));
		Assert.assertTrue(entryIterator.hasNextElement());

		entryIterator.initialize(Collections.<TransactionJobDescriptorEntry>emptyList(), Arrays.asList(("2")));
		Assert.assertFalse(entryIterator.hasNextElement());
	}

	/**
	 * Tests nextElement() method.
	 */
	@Test
	public void testNextElement() {
		List<TransactionJobDescriptorEntry> entries = new ArrayList<TransactionJobDescriptorEntry>();
		final TransactionJobDescriptorEntryImpl jobDescriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		final TransactionJobDescriptorEntryImpl jobDescriptorEntry2 = new TransactionJobDescriptorEntryImpl();
		final TransactionJobDescriptorEntryImpl jobDescriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		entries.add(jobDescriptorEntry1);
		entries.add(jobDescriptorEntry2);
		entries.add(jobDescriptorEntry3);

		entryIterator.initialize(entries, Arrays.asList(("2")));
		Assert.assertTrue(entryIterator.hasNextElement());

		final List<TransactionJobDescriptorEntry> nextElement1 = entryIterator.nextElement();
		Assert.assertEquals(2, nextElement1.size());
		Assert.assertSame(jobDescriptorEntry1, nextElement1.get(0));
		Assert.assertSame(jobDescriptorEntry2, nextElement1.get(1));
		Assert.assertTrue(entryIterator.hasNextElement());

		final List<TransactionJobDescriptorEntry> nextElement2 = entryIterator.nextElement();
		Assert.assertEquals(1, nextElement2.size());
		Assert.assertSame(jobDescriptorEntry3, nextElement2.get(0));

		Assert.assertFalse(entryIterator.hasNextElement());
	}

}
