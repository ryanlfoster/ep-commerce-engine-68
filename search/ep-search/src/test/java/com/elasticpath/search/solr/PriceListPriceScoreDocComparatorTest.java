/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.search.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link PriceListPriceScoreDocComparator}.
 */
public class PriceListPriceScoreDocComparatorTest {

	private final Map<String, Directory> testDirectoryMap = new HashMap<String, Directory>();

	private static final String SIMPLE_PRICE_DIRECTORY = "simplePriceDirectory";

	private final Map<String, PriceListPriceScoreDocComparator> testComparatorMap = new HashMap<String, PriceListPriceScoreDocComparator>();
	
	private static final String BASIC_SORT_COMPARATOR = "basicSortComparator";

	/**
	 * Tests that generation of the price list field names works as expected 
	 * in the happy case of having an argument in the right format.
	 */
	@Test
	public void testGeneratePriorityOrderedPriceListFieldNames() {
		String fieldname = "pricesort-catalog1#PL1#PL2#PL3";
		List<String> result = new PriceListPriceScoreDocComparator(fieldname, 0).generatePriorityOrderedPriceListFieldNames(fieldname);

		assertEquals("price_catalog1_PL1", result.get(0));
		assertEquals("price_catalog1_PL2", result.get(1));
		assertEquals("price_catalog1_PL3", result.get(2));
		
		assertTrue("Totally 3 price fields are expected to be generated. Actual result: " + result, 
				CollectionUtils.isEqualCollection(Arrays.asList("price_catalog1_PL1", "price_catalog1_PL2", "price_catalog1_PL3"), result));
	}
	
	/**
	 * Tests that when the passed field name is not in the expected format an exception will be thrown.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGeneratePriorityOrderedPriceListFieldNamesWithWrongFieldName() {
		String fieldname = "pricesort-wrong";
		new PriceListPriceScoreDocComparator(fieldname, 0).generatePriorityOrderedPriceListFieldNames(fieldname);
	}
	
	/**
	 * Sets up a simple price directory (for use by various tests).
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Before
	public void setupSimplePriceDirectory() throws IOException {
		final String[][] data = new String[][] {
				// tracer, 	contents,		price_product_parse
				{   "A",   "x a",           "A"},
				{   "B",   "y a",           "B"},
				{   "C",   "x a b c",       "C"},
				{   "D",   "y a b c",       "D"},
				{   "E",   "x a b c d",     "E"}
		};
		
		// Most of this code is "borrowed" from Lucene's test code in 2.9.4 (/src/test/org/apache/lucene/search/TestSort.java).
		final RAMDirectory directory = new RAMDirectory();
		final IndexWriter writer = new IndexWriter(directory, new SimpleAnalyzer(), true, IndexWriter.MaxFieldLength.LIMITED);
		writer.setMaxBufferedDocs(2);
		final int magicNumber = 1000;
		writer.setMergeFactor(magicNumber);
		for (int i = 0; i < data.length; ++i) {
			final Document doc = new Document();
			doc.add(new Field("tracer", data[i][0], Field.Store.YES, Field.Index.NO));
			doc.add(new Field("contents", data[i][1], Field.Store.NO, Field.Index.ANALYZED));
			doc.add(new Field("price_product_parse", data[i][2], Field.Store.NO, Field.Index.ANALYZED));
			doc.setBoost(2);
			writer.addDocument(doc);
		}
		writer.close();
		
		testDirectoryMap.put(SIMPLE_PRICE_DIRECTORY, directory);
	}
	
	private PriceListPriceScoreDocComparator createNewBasicSortComparator(final Directory directory) throws IOException {
		final String fieldname = "pricesort-product#parse";
		final IndexReader reader = IndexReader.open(directory, true);

		final int directorySize = reader.maxDoc();
		final PriceListPriceScoreDocComparator comparator = new PriceListPriceScoreDocComparator(fieldname, directorySize);
		
		comparator.setNextReader(reader, 0);
		
		for (int i = 0; i < directorySize; i++) {
			comparator.copy(i, i);
		}
		return comparator;
	}
	
	private PriceListPriceScoreDocComparator getBasicSortComparator(final Directory directory) throws IOException {
		PriceListPriceScoreDocComparator comparator = testComparatorMap.get(BASIC_SORT_COMPARATOR);
		if (comparator == null) {
			comparator = createNewBasicSortComparator(directory);
			testComparatorMap.put(BASIC_SORT_COMPARATOR, comparator);
		}
		
		return comparator;
	}	
	
	/**
	 * Tests the case where slot1 comes before slot2.  Makes sure the implementation follows the contract.
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Test
	public void testCompareSlot1BeforeSlot2() throws IOException {
		final Directory directory = testDirectoryMap.get(SIMPLE_PRICE_DIRECTORY);
		final PriceListPriceScoreDocComparator comparator = getBasicSortComparator(directory);
		
		final int result = comparator.compare(1, 3);
		assertTrue(result < 0);
	}
	
	/**
	 * Tests the case where slot1 comes after slot2.  Makes sure the implementation follows the contract.
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Test
	public void testCompareSlot2BeforeSlot1() throws IOException {
		final Directory directory = testDirectoryMap.get(SIMPLE_PRICE_DIRECTORY);
		final PriceListPriceScoreDocComparator comparator = getBasicSortComparator(directory);
		
		final int result = comparator.compare(3, 2);
		assertTrue(result > 0);
	}
	
	/**
	 * Tests the case where slot1 is equal to slot2.  Makes sure the implementation follows the contract.
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Test
	public void testCompareSlot1SameAsSlot2() throws IOException {
		final Directory directory = testDirectoryMap.get(SIMPLE_PRICE_DIRECTORY);
		final PriceListPriceScoreDocComparator comparator = getBasicSortComparator(directory);
		
		final int result = comparator.compare(2, 2);
		assertTrue(result == 0);
	}
	
	/**
	 * Tests the case where doc comes after the bottom.  Makes sure the implementation follows the contract.
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Test
	public void testCompareBottomWhereDocIsAfterBottom() throws IOException {
		final Directory directory = testDirectoryMap.get(SIMPLE_PRICE_DIRECTORY);
		final PriceListPriceScoreDocComparator comparator = getBasicSortComparator(directory);
		
		comparator.setBottom(1);
		final int result = comparator.compareBottom(2);
		assertTrue(result < 0);
	}
	
	/**
	 * Tests the case where doc comes before the bottom.  Makes sure the implementation follows the contract.
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Test
	public void testCompareBottomWhereDocIsBeforeBottom() throws IOException {
		final Directory directory = testDirectoryMap.get(SIMPLE_PRICE_DIRECTORY);
		final PriceListPriceScoreDocComparator comparator = getBasicSortComparator(directory);

		final int magicNumber = 3;
		
		comparator.setBottom(magicNumber);
		final int result = comparator.compareBottom(2);
		assertTrue(result > 0);
	}
	
	/**
	 * Tests the case where doc is the bottom.  Makes sure the implementation follows the contract.
	 *
	 * @throws IOException throws this if there's a problem.
	 */
	@Test
	public void testCompareBottomWhereDocIsSameAsBottom() throws IOException {
		final Directory directory = testDirectoryMap.get(SIMPLE_PRICE_DIRECTORY);
		final PriceListPriceScoreDocComparator comparator = getBasicSortComparator(directory);
		
		comparator.setBottom(2);
		final int result = comparator.compareBottom(2);
		assertTrue(result == 0);
	}
	
	/**
	 * Does any tear downs required for these tests.
	 */
	@After
	public void tearDownSimplePriceDirectory() {
		// Don't really need to do anything here.
	}
}
