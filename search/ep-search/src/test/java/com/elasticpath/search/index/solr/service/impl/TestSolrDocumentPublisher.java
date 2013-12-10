package com.elasticpath.search.index.solr.service.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrDocumentPublisher;

/**
 * A simple {@code SolrDocumentPublisher} which puts things into arrays and clears them when {@code #flush()} is called.
 */
public class TestSolrDocumentPublisher implements SolrDocumentPublisher {

	private final List<SolrInputDocument> updated = new LinkedList<SolrInputDocument>();

	private final List<Long> deleted = new LinkedList<Long>();

	@Override
	public void addUpdate(final IndexType indexType, final SolrInputDocument document) {
		updated.add(document);
	}

	@Override
	public void deleteDocument(final IndexType indexType, final long uid) {
		deleted.add(uid);
	}

	@Override
	public void flush() {
		updated.clear();
		deleted.clear();

	}

	@Override
	public void commit() {
		// does nothing.
	}

	@Override
	public void setSolrServer(final SolrServer solrServer) {
		// does nothing.
	}

	public List<SolrInputDocument> getUpdated() {
		return updated;
	}

	public List<Long> getDeleted() {
		return deleted;
	}

	@Override
	public void start() {
		// nothing to do

	}

	@Override
	public void shutdown() {
		// nothing to do
	}

	@Override
	public boolean isBusy() {
		return false;
	}

}

