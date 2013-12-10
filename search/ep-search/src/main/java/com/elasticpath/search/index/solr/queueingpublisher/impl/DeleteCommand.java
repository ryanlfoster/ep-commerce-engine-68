package com.elasticpath.search.index.solr.queueingpublisher.impl;

import org.apache.solr.client.solrj.SolrServer;

/**
 * Delete command for a Solr server.
 */
public class DeleteCommand implements SolrPublishCommand {

	private final String uid;

	/**
	 * Constructor, sets the domain object identifier to delete.
	 * 
	 * @param uid the domain object identifier to delete.
	 */
	public DeleteCommand(final long uid) {
		this.uid = String.valueOf(uid);
	}

	/**
	 * Applies the deletion to the specified SolrServer server.
	 * 
	 * @param server the specified {@link SolrServer}.
	 * @throws Exception on failure.
	 */
	public void apply(final SolrServer server) throws Exception {
		server.deleteById(uid);
	}

}
