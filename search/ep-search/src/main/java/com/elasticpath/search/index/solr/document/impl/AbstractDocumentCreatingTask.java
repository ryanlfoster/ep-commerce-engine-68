package com.elasticpath.search.index.solr.document.impl;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.Assert;

import com.elasticpath.search.index.pipeline.DocumentCreatingTask;

/**
 * The {@code AbstractSolrInputDocumentCreator} provides a number of Solr-specific plumbing methods which should likely just be utility methods. This
 * class extends it and provides the common code that all {@code DocumentCreatingTask} will have. Concrete implementations need only honour
 * {@code #createDocument()}, fetching the original entity through {@code #getEntity()}, which will never be null if {@code #createDocument()} is
 * called (despite what our tests check for).
 * 
 * @param <ENTITY> An entity as loaded from the loading stage of the pipeline.
 */
public abstract class AbstractDocumentCreatingTask<ENTITY> extends AbstractSolrInputDocumentCreator<ENTITY, SolrInputDocument> implements
		DocumentCreatingTask<ENTITY, SolrInputDocument> {

	private ENTITY entity;

	@Override
	public void run() {

		Assert.notNull(getNextStage(), "Next stage in pipeline must be set.");

		if (getEntity() == null) {
			return;
		}

		getPipelinePerformance().addCount("createdocument:docs_in", 1);
		final SolrInputDocument solrInputDocument = createDocument();
		getNextStage().send(solrInputDocument);
		getPipelinePerformance().addCount("createdocument:docs_out", 1);
	}

	/**
	 * Implement in concrete classes. Use {@code #getEntity()} to fetch the entity in question. You can safely return null if needed.
	 * 
	 * @return You can return <b>null</b> if needed.
	 */
	abstract SolrInputDocument createDocument();

	@Override
	public void setEntity(final ENTITY payload) {
		this.entity = payload;
	}

	/**
	 * Used by concrete implementations.
	 * 
	 * @return The entity of which a document should be created. Calls from {@code #createDocument()} can safely assume it does not return null.
	 */
	protected ENTITY getEntity() {
		return this.entity;
	}

}
