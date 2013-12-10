package com.elasticpath.service.search.solr;

import org.apache.solr.client.solrj.SolrServer;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.search.IndexType;

/**
 * Methods for providing SOLR related objects.
 */
public interface SolrProvider {

	/**
	 * Gets the default server for a particular <code>IndexType</code>.
	 * 
	 * @param indexType an <code>IndexType</code> enumeration value.
	 * @return a SOLR server
	 * @throws EpPersistenceException in case of any errors
	 */
	SolrServer getServer(IndexType indexType) throws EpPersistenceException;

	/**
	 * Gets the configuration for the specified <code>IndexType</code>.
	 *
	 * @param indexType an <code>IndexType</code> enumeration value.
	 * @return a <code>SearchConfig</code>
	 */
	SearchConfig getSearchConfig(IndexType indexType);

}