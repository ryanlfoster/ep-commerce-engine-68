package com.elasticpath.tools.sync.job.transaction.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.impl.TransactionJobDescriptorEntryImpl;

/**
 * Tests TrasnactionGroupByCommandAndTypeIterator and TrasnactionGroupByTypeIterator classes.
 */
public class TrasnactionGroupByCommandAndTypeIteratorTest {
	
	/**
	 * Tests hasNextElement() method.
	 */
	@Test
	public void testHasNextElement() {
		TrasnactionGroupByCommandAndTypeIterator iterator = new TrasnactionGroupByCommandAndTypeIterator();
		iterator.initialize(Collections.<TransactionJobDescriptorEntry>emptyList(), null);
		Assert.assertFalse(iterator.hasNextElement());
		
		List<TransactionJobDescriptorEntry> list = new ArrayList<TransactionJobDescriptorEntry>();
		list.add(new TransactionJobDescriptorEntryImpl());
		list.add(new TransactionJobDescriptorEntryImpl());
		iterator.initialize(list, null);
		Assert.assertTrue(iterator.hasNextElement());
	}
	
	/**
	 * Tests TrasnactionGroupByCommandAndTypeIterator.addToresult() method.
	 */
	@Test
	public void testAddToResultCommandAndType() {
		TrasnactionGroupByCommandAndTypeIterator iterator = new TrasnactionGroupByCommandAndTypeIterator();
		
		TransactionJobDescriptorEntry firstDescriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		TransactionJobDescriptorEntry descriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		firstDescriptorEntry1.setCommand(Command.UPDATE);
		firstDescriptorEntry1.setType(ProductImpl.class);
		
		descriptorEntry1.setCommand(Command.UPDATE);
		descriptorEntry1.setType(ProductImpl.class);
		Assert.assertTrue(iterator.addToResult(firstDescriptorEntry1, descriptorEntry1));

		TransactionJobDescriptorEntry firstDescriptorEntry2 = new TransactionJobDescriptorEntryImpl();
		TransactionJobDescriptorEntry descriptorEntry2 = new TransactionJobDescriptorEntryImpl();

		firstDescriptorEntry2.setCommand(Command.UPDATE);
		firstDescriptorEntry2.setType(ProductImpl.class);
		
		descriptorEntry2.setCommand(Command.REMOVE);
		descriptorEntry2.setType(ProductImpl.class);
		Assert.assertFalse(iterator.addToResult(firstDescriptorEntry2, descriptorEntry2));
		
		TransactionJobDescriptorEntry firstDescriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		TransactionJobDescriptorEntry descriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		
		firstDescriptorEntry3.setCommand(Command.UPDATE);
		firstDescriptorEntry3.setType(ProductImpl.class);
		
		descriptorEntry3.setCommand(Command.UPDATE);
		descriptorEntry3.setType(CatalogImpl.class);
		Assert.assertFalse(iterator.addToResult(firstDescriptorEntry3, descriptorEntry3));
	}
	
	/**
	 * Tests TrasnactionGroupByTypeIterator.addToresult() method.
	 */
	@Test
	public void testAddToResultType() {
		TrasnactionGroupByTypeIterator iterator = new TrasnactionGroupByTypeIterator();
		
		TransactionJobDescriptorEntry firstDescriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		TransactionJobDescriptorEntry descriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		firstDescriptorEntry1.setCommand(Command.UPDATE);
		firstDescriptorEntry1.setType(ProductImpl.class);
		
		descriptorEntry1.setCommand(Command.UPDATE);
		descriptorEntry1.setType(ProductImpl.class);
		Assert.assertTrue(iterator.addToResult(firstDescriptorEntry1, descriptorEntry1));

		TransactionJobDescriptorEntry firstDescriptorEntry2 = new TransactionJobDescriptorEntryImpl();
		TransactionJobDescriptorEntry descriptorEntry2 = new TransactionJobDescriptorEntryImpl();

		firstDescriptorEntry2.setCommand(Command.UPDATE);
		firstDescriptorEntry2.setType(ProductImpl.class);
		
		descriptorEntry2.setCommand(Command.REMOVE);
		descriptorEntry2.setType(ProductImpl.class);
		Assert.assertTrue(iterator.addToResult(firstDescriptorEntry2, descriptorEntry2));
		
		TransactionJobDescriptorEntry firstDescriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		TransactionJobDescriptorEntry descriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		
		firstDescriptorEntry3.setCommand(Command.UPDATE);
		firstDescriptorEntry3.setType(ProductImpl.class);
		
		descriptorEntry3.setCommand(Command.UPDATE);
		descriptorEntry3.setType(CatalogImpl.class);
		Assert.assertFalse(iterator.addToResult(firstDescriptorEntry3, descriptorEntry3));
	}

	
	/**
	 * Tests TrasnactionGroupByCommandAndTypeIterator.nextElement() method.
	 */
	@Test
	public void testNextElementCommandAndType() {
		TrasnactionGroupByCommandAndTypeIterator iterator = new TrasnactionGroupByCommandAndTypeIterator();
		
		final TransactionJobDescriptorEntryImpl descriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry1.setCommand(Command.REMOVE);
		descriptorEntry1.setGuid("dd1");
		descriptorEntry1.setType(ProductImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry2 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry2.setCommand(Command.REMOVE);
		descriptorEntry2.setGuid("dd2");
		descriptorEntry2.setType(ProductImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry3.setCommand(Command.UPDATE);
		descriptorEntry3.setGuid("dd3");
		descriptorEntry3.setType(ProductImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry4 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry4.setCommand(Command.UPDATE);
		descriptorEntry4.setGuid("dd4");
		descriptorEntry4.setType(CategoryImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry5 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry5.setCommand(Command.UPDATE);
		descriptorEntry5.setGuid("dd5");
		descriptorEntry5.setType(CategoryImpl.class);

		List<TransactionJobDescriptorEntry> entryList = new ArrayList<TransactionJobDescriptorEntry>();
		entryList.add(descriptorEntry1);
		entryList.add(descriptorEntry2);
		entryList.add(descriptorEntry3);
		entryList.add(descriptorEntry4);
		entryList.add(descriptorEntry5);
		
		iterator.initialize(entryList, null);
		Assert.assertTrue(iterator.hasNextElement());
		
		final List<TransactionJobDescriptorEntry> nextElement1 = iterator.nextElement();
		Assert.assertEquals(2, nextElement1.size());
		
		Assert.assertEquals(descriptorEntry1, nextElement1.get(0));
		Assert.assertEquals(descriptorEntry2, nextElement1.get(1));
		Assert.assertTrue(iterator.hasNextElement());
		
		final List<TransactionJobDescriptorEntry> nextElement2 = iterator.nextElement();
		Assert.assertEquals(1, nextElement2.size());
		
		Assert.assertEquals(descriptorEntry3, nextElement2.get(0));
		Assert.assertTrue(iterator.hasNextElement());
		
		final List<TransactionJobDescriptorEntry> nextElement3 = iterator.nextElement();
		Assert.assertEquals(2, nextElement3.size());
		
		Assert.assertEquals(descriptorEntry4, nextElement3.get(0));
		Assert.assertEquals(descriptorEntry5, nextElement3.get(1));
		Assert.assertFalse(iterator.hasNextElement());

	}
	
	/**
	 * Tests TrasnactionGroupByTypeIterator.nextElement() method.
	 */
	@Test
	public void testNextElementType() {
		TrasnactionGroupByTypeIterator iterator = new TrasnactionGroupByTypeIterator();
		
		final TransactionJobDescriptorEntryImpl descriptorEntry1 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry1.setCommand(Command.REMOVE);
		descriptorEntry1.setGuid("dd1");
		descriptorEntry1.setType(ProductImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry2 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry2.setCommand(Command.REMOVE);
		descriptorEntry2.setGuid("dd2");
		descriptorEntry2.setType(ProductImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry3 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry3.setCommand(Command.UPDATE);
		descriptorEntry3.setGuid("dd3");
		descriptorEntry3.setType(ProductImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry4 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry4.setCommand(Command.UPDATE);
		descriptorEntry4.setGuid("dd4");
		descriptorEntry4.setType(CategoryImpl.class);

		final TransactionJobDescriptorEntryImpl descriptorEntry5 = new TransactionJobDescriptorEntryImpl();
		descriptorEntry5.setCommand(Command.UPDATE);
		descriptorEntry5.setGuid("dd5");
		descriptorEntry5.setType(CategoryImpl.class);

		List<TransactionJobDescriptorEntry> entryList = new ArrayList<TransactionJobDescriptorEntry>();
		entryList.add(descriptorEntry1);
		entryList.add(descriptorEntry2);
		entryList.add(descriptorEntry3);
		entryList.add(descriptorEntry4);
		entryList.add(descriptorEntry5);
		
		iterator.initialize(entryList, null);
		Assert.assertTrue(iterator.hasNextElement());
		
		final List<TransactionJobDescriptorEntry> nextElement1 = iterator.nextElement();
		Assert.assertEquals(2 + 1, nextElement1.size());
		
		Assert.assertEquals(descriptorEntry1, nextElement1.get(0));
		Assert.assertEquals(descriptorEntry2, nextElement1.get(1));
		Assert.assertEquals(descriptorEntry3, nextElement1.get(2));
		Assert.assertTrue(iterator.hasNextElement());
				
		final List<TransactionJobDescriptorEntry> nextElement2 = iterator.nextElement();
		Assert.assertEquals(2, nextElement2.size());
		
		Assert.assertEquals(descriptorEntry4, nextElement2.get(0));
		Assert.assertEquals(descriptorEntry5, nextElement2.get(1));
		Assert.assertFalse(iterator.hasNextElement());

	}

}
