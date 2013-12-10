package com.elasticpath.search.solr;

import java.io.IOException;

import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

/**
 * Factory for creating price comparators for dealing with price list price
 * sorting.
 */
public class PriceComparatorSource extends FieldComparatorSource {

	private static final long serialVersionUID = 1L;

	@Override
	public FieldComparator newComparator(final String fieldname, final int numHits, final int sortPos, final boolean reversed) throws IOException {
		return new PriceListPriceScoreDocComparator(fieldname, numHits);
	}

}
