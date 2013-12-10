package com.elasticpath.service.search.solr;

import java.util.Collection;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.search.IndexType;

/**
 * Service to handle SOLR object delegation.
 */
public interface SolrManager extends SolrProvider {

	/**
	 * Adds the given document to index.
	 * 
	 * @param server the server to operate on
	 * @param document the document to add into/update index
	 * @throws EpPersistenceException in case of any errors
	 */
	void addUpdateDocument(final SolrServer server,
			final SolrInputDocument document) throws EpPersistenceException;

	/**
	 * Adds the given collection of documents to the index.
	 * 
	 * @param server the server to operate on
	 * @param documents the collection of documents to add into/update the index
	 * @throws EpPersistenceException in case of any errors
	 */
	void addUpdateDocument(final SolrServer server,
			final Collection<SolrInputDocument> documents)
			throws EpPersistenceException;

	/**
	 * Deletes the document specified by the UID.
	 * 
	 * @param server the server to operate on
	 * @param uid the UID of the object
	 * @throws EpPersistenceException in case of any errors
	 */
	void deleteDocument(final SolrServer server, final long uid)
			throws EpPersistenceException;

	/**
	 * Flushes (writes) changes to the index.
	 * 
	 * @param server the server to operate on
	 * @param optimize whether to optimize the index changes when closing
	 * @throws EpPersistenceException in case of any errors
	 */
	void flushChanges(final SolrServer server,
			final boolean optimize) throws EpPersistenceException;

	/**
	 * Rebuilds the spelling index.
	 * 
	 * @param server the server to operate on
	 * @throws EpPersistenceException in case of any errors
	 */
	void rebuildSpelling(final SolrServer server) throws EpPersistenceException;

	/**
	 * Obtain the {@code SolrDocumentPublisher} which will be used to publish {@code SolrInputDocument}s
	 * to the appropriate Solr server.
	 * @param indexType {@link IndexType} of the publisher you want to obtain
	 * @return the associated publisher.
	 */
	SolrDocumentPublisher getDocumentPublisher(IndexType indexType);
}