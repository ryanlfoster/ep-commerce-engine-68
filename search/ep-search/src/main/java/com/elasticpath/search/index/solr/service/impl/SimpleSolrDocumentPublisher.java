package com.elasticpath.search.index.solr.service.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.search.index.solr.service.IndexBuildPolicy;
import com.elasticpath.search.index.solr.service.IndexBuildPolicyContext;
import com.elasticpath.search.index.solr.service.IndexBuildPolicyContextFactory;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrDocumentPublisher;
import com.elasticpath.service.search.solr.SolrManager;

/**
 * A {@code SolrDocumentPublisher} which attempts to mimic the implementation that previously existed before being refactored.
 */
public class SimpleSolrDocumentPublisher implements SolrDocumentPublisher {

	private SolrServer solrServer;

	private IndexBuildPolicy indexBuildPolicy;

	private PersistenceEngine persistenceEngine;

	private SolrManager solrManager;

	private IndexBuildPolicyContextFactory indexBuildPolicyContextFactory;

	private int operations = 0;

	private int docsAddedUpdated = 0;

	private final Collection<SolrInputDocument> documents = new LinkedList<SolrInputDocument>();

	@Override
	public void start() {
		// This publisher does not need to be started.
	}

	@Override
	public void shutdown() {
		// This publisher does not need to be shutdown.
	}

	@Override
	public void deleteDocument(final IndexType indexType, final long uid) {
		solrManager.deleteDocument(solrServer, uid);

		operations++;
		if (getIndexBuildPolicy().isCommitRequired(newIndexPolicyContext(indexType, operations, -1))) {
			commitChanges(solrServer);
		}
	}

	@Override
	public void addUpdate(final IndexType indexType, final SolrInputDocument document) {
		documents.add(document);

		++docsAddedUpdated;
		if (getIndexBuildPolicy().isAddDocumentsRequired(newIndexPolicyContext(indexType, operations, docsAddedUpdated))) {
			// Need to clear the session to release memory.
			getPersistenceEngine().clear();
			solrManager.addUpdateDocument(solrServer, documents);
			documents.clear();
		}

		operations++;
		if (getIndexBuildPolicy().isCommitRequired(newIndexPolicyContext(indexType, operations, docsAddedUpdated))) {
			commitChanges(solrServer);
		}
	}

	@Override
	public void flush() {
		if (!documents.isEmpty()) {
			solrManager.addUpdateDocument(solrServer, documents);
			documents.clear();
		}
		this.docsAddedUpdated = 0;
		this.operations = 0;
	}

	@Override
	public void commit() {
		commitChanges(solrServer);
	}

	/**
	 * Creates a new instance of {@link IndexBuildPolicyContext} and populates it with data.
	 * 
	 * @param indexType the index type
	 * @param operationsCount the count of operations performed up until now
	 * @param documentsAdded the count of documents added up until now
	 * @return the policy context
	 */
	protected IndexBuildPolicyContext newIndexPolicyContext(final IndexType indexType, final int operationsCount, final int documentsAdded) {

		IndexBuildPolicyContext policyContext = indexBuildPolicyContextFactory.createIndexBuildPolicyContext();
		policyContext.setIndexType(indexType);
		policyContext.setDocumentsAdded(documentsAdded);
		policyContext.setOperationsCount(operationsCount);

		return policyContext;
	}

	/**
	 * Commits the changes done to the Solr server.
	 * 
	 * @param solrServer the SOLR server
	 */
	protected void commitChanges(final SolrServer solrServer) {
		// never do optimisation on commit. A separate Quartz job does that on a
		// timely basis.
		solrManager.flushChanges(solrServer, false);
	}

	public boolean isBusy() {
		return !documents.isEmpty();
	}

	protected IndexBuildPolicy getIndexBuildPolicy() {
		return this.indexBuildPolicy;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	public SolrServer getSolrServer() {
		return solrServer;
	}

	public void setSolrServer(final SolrServer solrServer) {
		this.solrServer = solrServer;
	}

	public SolrManager getSolrManager() {
		return solrManager;
	}

	public void setSolrManager(final SolrManager solrManager) {
		this.solrManager = solrManager;
	}

	public IndexBuildPolicyContextFactory getIndexBuildPolicyContextFactory() {
		return indexBuildPolicyContextFactory;
	}

	public void setIndexBuildPolicyContextFactory(final IndexBuildPolicyContextFactory indexBuildPolicyContextFactory) {
		this.indexBuildPolicyContextFactory = indexBuildPolicyContextFactory;
	}

	public void setIndexBuildPolicy(final IndexBuildPolicy indexBuildPolicy) {
		this.indexBuildPolicy = indexBuildPolicy;
	}

}

