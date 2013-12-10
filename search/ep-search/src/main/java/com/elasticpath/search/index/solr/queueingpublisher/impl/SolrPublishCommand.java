package com.elasticpath.search.index.solr.queueingpublisher.impl;

import org.apache.solr.client.solrj.SolrServer;

/**
 * A {@link SolrPublishCommand} implements the Command Pattern for the {@link QueueingSolrDocumentPublisher}.
 */
public interface SolrPublishCommand {

	/**
	 * Apply can throw any exception it wants, with the exception of {@code InterruptedException} they will simply be logged. Throwing
	 * {@code InterruptedException} tells the working thread to shutdown.
	 * 
	 * @param server the Solr server.
	 * @throws Exception will be logged but the further commands will continue to be processed, unless it's a {@code InterruptedException}.
	 */
	void apply(SolrServer server) throws Exception;

}
