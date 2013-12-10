package com.elasticpath.service.search.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SolrIndexConstantFactory;

/**
 * Default implementation of <code>SolrIndexConstantFactory</code> interface.
 */
public class SolrIndexConstantFactoryImpl implements SolrIndexConstantFactory {
	
	private Map<IndexType, String> indexTypesMap;

	@Override
	public String getSolrIndexConstant(final IndexType indexType) {
		return indexTypesMap.get(indexType);
	}
	
	/**
	 * Sets the index types map.
	 * 
	 * @param indexTypesMap the index type map
	 */
	public void setIndexTypesMap(final Map<String, String> indexTypesMap) {
		this.indexTypesMap = new HashMap<IndexType, String>();
		for (Entry<String, String> entry : indexTypesMap.entrySet()) {
			this.indexTypesMap.put(IndexType.findFromName(entry.getKey()), entry.getValue());
		}
	}
}
