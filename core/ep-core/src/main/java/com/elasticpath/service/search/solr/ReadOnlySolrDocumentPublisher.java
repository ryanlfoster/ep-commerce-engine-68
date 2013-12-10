package com.elasticpath.service.search.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.service.search.IndexType;

/**
 * We pass a {@code SolrManager} to our storefront project, and the contract of {@code SolrManager} indicates that it would allow storefront to
 * update the index, therefore we need to supply a {@code SolrDocumentPublisher} for storefront to use. Unfortunately all the code for updating
 * indexes is in the search project, so while a caller may feel they could use {@code SolrManager} on storefront to update indexes, this would likely
 * fail. So here we provide a read-only implementation of {@code SolrDocumentPublisher} for storefront such that if anyone attempts to update a
 * search index on storefront, they'll receive an error.
 */
public class ReadOnlySolrDocumentPublisher implements SolrDocumentPublisher {

	/**
	 * Unsupported stub method for updating a Solr server with a {@link SolrInputDocument}.
	 * 
	 * @param indexType the {@link IndexType} of the server to update.
	 * @param document the {@link SolrInputDocument} to update the server with.
	 * @throws UnsupportedOperationException if called.
	 */
	public void addUpdate(final IndexType indexType, final SolrInputDocument document) {
		throw new UnsupportedOperationException("Will not update document, you must specify a different SolrDocumentPublisher"
				+ " than the read-only one if you wish to update the index.");
	}

	/**
	 * Stub method for deleting a Solr server with a {@link SolrInputDocument}.
	 * 
	 * @param indexType the {@link IndexType} of the server to update.
	 * @param uid the domain object identifier to delete
	 * @throws UnsupportedOperationException if called.
	 */
	public void deleteDocument(final IndexType indexType, final long uid) {
		throw new UnsupportedOperationException("Will not delete document, you must specify a different SolrDocumentPublisher "
				+ "than the read-only one if you wish to update the index.");
	}

	/**
	 * Unsupported stub method for flushing a Solr server.
	 * 
	 * @throws UnsupportedOperationException if called.
	 */
	public void flush() {
		throw new UnsupportedOperationException(
				"Cannot flush, you must specify a different SolrDocumentPublisher than the read-only one if you wish to update the index.");
	}

	/**
	 * Unsupported stub method for commiting changes to a Solr server.
	 * 
	 * @throws UnsupportedOperationException if called.
	 */
	public void commit() {
		throw new UnsupportedOperationException(
				"Cannot commit, you must specify a different SolrDocumentPublisher than the read-only one if you wish to update the index.");
	}

	/**
	 * Sets the {@link SolrServer}.
	 * 
	 * @param solrServer the {@link SolrServer}.
	 */
	public void setSolrServer(final SolrServer solrServer) {
		// do not need the SolrServer for this.
	}

	@Override
	public void start() {
		// nothing to start
	}

	@Override
	public void shutdown() {
		// nothing to shutdown
	}

	@Override
	public boolean isBusy() {
		return false;
	}

}
