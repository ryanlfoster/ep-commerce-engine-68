package com.elasticpath.search.index.solr.builders;

import com.elasticpath.service.search.IndexType;

/**
 * This interface represents the factory for index builders.
 */
public interface IndexBuilderFactory {
	
	/**
	 * Gets the index builder by given index type.
	 * 
	 * @param indexType the index type
	 * @return the index builder
	 */
	IndexBuilder getIndexBuilder(final IndexType indexType);

}
