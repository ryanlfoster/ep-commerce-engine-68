package com.elasticpath.service.search.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.service.search.IndexType;

/**
 * The IndexBuildService delegates work to a SolrDocumentPublisher to actually perform the changes in Solr. Different publishers can be optimized in
 * different ways. The 6.3.1 functionality can be found in the SimpleSolrDocumentPublisher in the search project.
 */
public interface SolrDocumentPublisher {

	/**
	 * In production code, you must call this method after {@code #setSolrServer(SolrServer)} to let the SolrDocumentPublisher know it can setup any
	 * internal state it needs. Calling it multiple times is undefined.
	 */
	void start();

	/**
	 * In a controlled shutdown of the server, you must call this method to ensure the publisher shuts down any active resources.
	 */
	void shutdown();

	/**
	 * Use when adding or updating a Solr document. When this document is committed to the server is left up to the implementation you're using. That
	 * said, you must call flush() when you're done to ensure the publisher doesn't hold your documents in a queue.
	 * 
	 * @param indexType the index type of the input document
	 * @param document the document to add or update
	 * @throws InterruptedException usually from {@code BlockingQueue#put(Object)}
	 */
	void addUpdate(final IndexType indexType, final SolrInputDocument document) throws InterruptedException;

	/**
	 * Use when removing a particular document from the Solr server. Takes the uid of the entity you'd like to remove. When removal is done is
	 * implementation specific. That said, you must call flush() when you're done to ensure the publish doesn't hold on to your pending deletes.
	 * 
	 * @param indexType the index type of the document to remove
	 * @param uid the uid of the document
	 * @throws InterruptedException usually from {@code BlockingQueue#put(Object)}
	 */
	void deleteDocument(final IndexType indexType, final long uid) throws InterruptedException;

	/**
	 * While a SolrDocumentPublisher will decide when to flush, you must call flush when you are done updating/deleting a group of documents to
	 * ensure any pending changes are flushed. You must also do this for commit!
	 */
	void flush();

	/**
	 * While a SolrDocumentPublisher may decide when to commit, you must call commit when you've called your final flush and you want the Solr server
	 * to commit your documents.
	 */
	void commit();

	/**
	 * The SolrServer that this SolrDocumentPublisher is working for. Each SolrDocumentPublisher works for exactly one SolrServer.
	 * 
	 * @param solrServer the Solr server
	 */
	void setSolrServer(final SolrServer solrServer);

	/**
	 * Returns true if this SolrDocumentPublisher is currently busy publishing documents.
	 * 
	 * @return true if busy, false otherwise.
	 */
	boolean isBusy();

}
