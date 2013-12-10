package com.elasticpath.service.search.solr;

/**
 * Factory for creating {@code SolrDocumentPublisher}. Uses Spring's ServiceLocator so no Spring leaks into the classes.
 */
public interface SolrDocumentPublisherFactory {

	/**
	 * Create a {@code SolrDocumentPublisher}. After creation you must call
	 * {@code SolrDocumentPublisher#setSolrServer(org.apache.solr.client.solrj.SolrServer)} to assign a SolrServer to it.
	 * 
	 * @return a fresh SolrDocumentPublisher
	 */
	SolrDocumentPublisher createSolrDocumentPublisher();
}
