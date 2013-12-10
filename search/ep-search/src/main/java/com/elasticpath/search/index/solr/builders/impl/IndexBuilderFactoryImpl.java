package com.elasticpath.search.index.solr.builders.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.search.index.solr.builders.IndexBuilder;
import com.elasticpath.search.index.solr.builders.IndexBuilderFactory;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.IndexType;

/**
 * Represents default implementation of <code>IndexBuilderFactory</code> interface. 
 */
public class IndexBuilderFactoryImpl implements IndexBuilderFactory {
	
	private Map<IndexType, IndexBuilder> indexBuilderMap;

	@Override
	public IndexBuilder getIndexBuilder(final IndexType indexType) {
		final IndexBuilder indexBuilder = indexBuilderMap.get(indexType);
		if (indexBuilder == null) {
			throw new EpServiceException("index builder with index type: " + indexType + " does not exist");
		}
		return indexBuilder;
	}
	
	/**
	 * Sets the index builder map.
	 * 
	 * @param indexBuilderMap the index builder map
	 */
	public void setIndexBuilderMap(final Map<String, IndexBuilder> indexBuilderMap) {
		this.indexBuilderMap = new HashMap<IndexType, IndexBuilder>();
		for (Entry<String, IndexBuilder> entry : indexBuilderMap.entrySet()) {
			this.indexBuilderMap.put(IndexType.findFromName(entry.getKey()), entry.getValue());
		}
	}
}
