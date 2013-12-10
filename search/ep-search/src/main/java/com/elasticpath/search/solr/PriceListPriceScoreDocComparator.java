package com.elasticpath.search.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.FieldComparator;

import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * This class needs a little bit of an explanation. 
 * 
 * First the facts:
 * 
 * We index on Solr several prices, one for each price list that exist in the system. If you open the product
 * index with Luke, you will see fields such as: price_SNAPITUS_SNAPITUP_USD, price_SNAPITUS_Seion_PL, 
 * price_SNAPITUK_SNAPITUP_GBP, etc.
 * 
 * That means that a set of products returned by any search should be shown to the user with prices coming
 * from different price lists. This fact make it impossible for Solr to sort a search result set by prices
 * because the price of a specific product is determined at runtime based on the store and the price lists 
 * the web user is entitled to use. So, how we can sort by price?
 * 
 * The solution:
 * 
 * We created a dynamic field in product.schema.xml called 'sortprice*'. You should note that:
 * 
 * 	1) this field is never populated on the ProductIndexBuilder.
 * 	2) this field is used in such a way that it has its own sorting class (this class: PriceListPriceScoreDocComparator)
 *
 * When StoreFront issues a search request to Solr and ask it to sort by price, the request looks like this:
 * 
 * http://demo.elasticpath.com:8080/searchserver/product/select?qt=dismax&q=canon&......
 * ...&sort=sortprice_SNAPITUPUS_SNAPITUP_USD%23price_SNAPITUS_Senior_PL+asc&start=40&rows=20&fl=objectUid
 * 
 * Note that the sorting is requested by the parameter "sort=sortprice_SNAPITUPUS_SNAPITUP_USD%23price_SNAPITUS_Senior_PL"
 * 
 * Solr identifies that the field starts with 'pricesort*' and then it uses this class to delegate the sort algorithm.
 * 
 * This class first parses the field name using the method generatePriorityOrderedPriceListFieldNames(....). The parsing breaks down the field name
 * into existing populated fields such as price_SNAPITUPUS_SNAPITUP_USD and price_SNAPITUS_Senior_PL.
 * 
 * When it comes to the method setNextReader(...), we calculate the price based on the different price lists and sort it accordingly with
 * the prices the user that issued this request is entitled to.
 * 
 * The final sort behaves like it is really sorting by a price field. (yeee!)
 * 
 * Ok, I know it sounds a little bit complicate and obscure. The first time I came across this thing, I wanted to change it to be simpler but soon 
 * I realized that is indeed a good "price sorting solution". If you think it can be smarter and simplified just bring it up in the next opportunity.
 * 
 */
class PriceListPriceScoreDocComparator extends FieldComparator {
	
	/**
	 * This constant must be the same as the one used in {@code IndexUtility#createPriceSortFieldName(String, String, List)} implementation.
	 */
	private static final String PRICE_LIST_SEPARATOR = "#";

	private static final Logger LOG = Logger.getLogger(PriceListPriceScoreDocComparator.class);
	
	private static final String MARKER_PREFIX = SolrIndexConstants.PRICE_SORT;

	private static final int MINIMUM_FIELD_LENGTH = 2;

	private static final String DEFAULT_PRICE_VALUE = "";

	private final List<String> fields;
	private final String[] values;
	private String[] currentReaderValues;
	private String bottom;
	
	/**
	 * Constructor for class.
	 *
	 * @param field - the field
	 * @param numhits - the no of hits for the field
	 */
	public PriceListPriceScoreDocComparator(final String field, final int numhits) {
			fields = generatePriorityOrderedPriceListFieldNames(field);
			values = new String[numhits];
 	}
	
	/**
	 * Generates a list of product price fields out of the given field name which follows the format.
	 * <p>pricesort_catalogGuid_priceListGuid1_priceListGuid2_..._priceListGuidN
	 *  
	 * @param fieldName the field name
	 * @return a list of price list fields in the following format:
	 * <li>price_catalogGuid_priceListGuid1
	 * <li>price_catalogGuid_priceListGuid2
	 * <br>...
	 * <li>price_catalogGuid_priceListGuidN
	 */
	protected final List<String> generatePriorityOrderedPriceListFieldNames(final String fieldName) {
 		String semanticPart = fieldName;
 		if (fieldName.startsWith(MARKER_PREFIX)) {
 			semanticPart = fieldName.substring(MARKER_PREFIX.length() + 1); // +1 for the underscore after the prefix
		}

		Set<String> priorityOrderedPriceListFieldNames = new LinkedHashSet<String>();
		String[] fieldNameComponents = semanticPart.split(PRICE_LIST_SEPARATOR);
		String catalogName = fieldNameComponents[0];
		
		for (int i = 1; i < fieldNameComponents.length; i++) {
			String singlePriceListFieldName = SolrIndexConstants.PRICE + "_" + catalogName + "_" + fieldNameComponents[i];
			priorityOrderedPriceListFieldNames.add(singlePriceListFieldName);
		}
		if (fieldNameComponents.length < MINIMUM_FIELD_LENGTH) {
			throw new IllegalArgumentException("Field name must follow the format 'pricesort_catalogGuid#pricelistGuid#pricelistGuid'"
					+ "and must contain exactly one catalog guid and at least one 'pricelistGuid'. Field name passed in: " + fieldName);
		}
		return new ArrayList<String>(priorityOrderedPriceListFieldNames);
	}

	@Override
	public int compare(final int slot1, final int slot2) {
		return values[slot1].compareTo(values[slot2]);
	}

	@Override
	public Comparable<?> value(final int slot) {
		return values[slot];
	}

	@Override
	public int compareBottom(final int doc) throws IOException {
		return bottom.compareTo(checkEmpty(currentReaderValues, doc));
	}

	@Override
	public void copy(final int slot, final int doc) throws IOException {
		values[slot] = checkEmpty(currentReaderValues, doc);
	}

	@Override
	public void setBottom(final int slot) {
		bottom = values[slot];
 	}

	/**
		 * Updates prices with the given {@link IndexReader}.
		 * 
		 * @param reader an {@link IndexReader}
		 * @param docBase docBase of {@link IndexReader}
		 * @throws IOException on error
	 	 */
	@Override
	public void setNextReader(final IndexReader reader, final int docBase) throws IOException {
		currentReaderValues = new String[reader.maxDoc()];
		for (String priceField : fields) {
			TermEnum enumerator = reader.terms(new Term(priceField, ""));
			TermDocs termDocs = reader.termDocs();

			try {
				if (enumerator.term() == null) {
					LOG.error("No terms in field: " + priceField);
					continue;
				}
				do {
					Term term = enumerator.term();
					if (!StringUtils.equals(term.field(), priceField)) {
						break;
					}
					termDocs.seek(enumerator);
					while (termDocs.next()) {
						// if value already assigned, price with higher priority already assigned
						if (currentReaderValues[termDocs.doc()] == null) {
							currentReaderValues[termDocs.doc()] = term.text();
						}
					}
				} while (enumerator.next()); // For each term (price list field, price)
			} finally {
				termDocs.close();
				enumerator.close();
			}
		}
	}
	
	private String checkEmpty(final String[] arr, final int index) {
		if (arr[index] == null) {
			return DEFAULT_PRICE_VALUE;
		}
		return arr[index];
	}
	
}