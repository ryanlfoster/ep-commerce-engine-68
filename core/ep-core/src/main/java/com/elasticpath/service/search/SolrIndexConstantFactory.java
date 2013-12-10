package com.elasticpath.service.search;

/**
 * This interface provides methods for receiving solr index constant by given index type.
 */
public interface SolrIndexConstantFactory {
	
	/**
	 * Gets the solr index constant by given index type.
	 * 
	 * @param indexType the index type
	 * @return appropriate solr index constant
	 */
	String getSolrIndexConstant(final IndexType indexType);

}
