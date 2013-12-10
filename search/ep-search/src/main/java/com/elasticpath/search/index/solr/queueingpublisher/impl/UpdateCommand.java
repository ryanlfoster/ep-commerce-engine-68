package com.elasticpath.search.index.solr.queueingpublisher.impl;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;

/**
 * Update document command for the {@code QueueingSolrDocumentPublisher}.
 */
public class UpdateCommand implements SolrPublishCommand {

	private final SolrInputDocument document;

	/**
	 * Constructor, sets the document to update.
	 * 
	 * @param document the document to update.
	 */
	public UpdateCommand(final SolrInputDocument document) {
		this.document = document;
	}

	/**
	 * Applies the update to the specified SolrServer server.
	 * 
	 * @param server the specified {@link SolrServer}.
	 * @throws SolrServerException on failure.
	 * @throws IOException on failure.
	 */
	public void apply(final SolrServer server) throws SolrServerException, IOException {
		server.add(document);
	}
}