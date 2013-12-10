package com.elasticpath.search.index.pipeline.impl;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.search.index.solr.service.impl.QueueingSolrDocumentPublisher;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrDocumentPublisher;
import com.elasticpath.service.search.solr.SolrManager;

/**
 * This stage sends the created {@code SolrInputDocument} to the appropriate {@code SolrDocumentPublisher}. Since the {@code SolrDocumentPublisher}
 * already has a work queue, we do not do this in a separate thread and therefore do not need a {@code IndexingTask}.
 *
 * @param <IN> a {@code SolrInputDocument}
 * @param <OUT> the number of documents processed, in our case, always 1.
 */
public class DocumentPublishingStage<IN, OUT> implements IndexingStage<SolrInputDocument, Long> {

	private static final Logger LOG = Logger.getLogger(DocumentPublishingStage.class);

	private SolrManager solrManager;

	private IndexType indexType;

	private PipelinePerformance performance;

	private IndexingStage<Long, ?> nextStage;

	private SolrDocumentPublisher publisher;

	@Override
	public void send(final SolrInputDocument document) {
		getPipelinePerformance().addCount("publish:docs_in", 1);
		initPublisher();
		try {
			publisher.addUpdate(getIndexType(), document);
		} catch (final InterruptedException e) {
			LOG.warn("Interrupted while waiting for publishing queue to drain", e);
			return;
		}
		getPipelinePerformance().addCount("publish:docs_queued", 1);
		nextStage.send(1L);
	}

	@Override
	public boolean isBusy() {
		initPublisher();
		return publisher.isBusy();
	}

	private void initPublisher() {
		if (publisher == null) {
			solrManager.getServer(getIndexType());
			publisher = solrManager.getDocumentPublisher(getIndexType());
			if (publisher instanceof QueueingSolrDocumentPublisher) {
				((QueueingSolrDocumentPublisher) publisher).setPipelinePerformance(performance);
			}
		}
	}

	@Override
	public void setNextStage(final IndexingStage<Long, ?> nextStage) {
		this.nextStage = nextStage;
	}

	public IndexType getIndexType() {
		return indexType;
	}

	public void setIndexType(final IndexType indexType) {
		this.indexType = indexType;
	}

	public void setSolrManager(final SolrManager solrManager) {
		this.solrManager = solrManager;
	}

	public PipelinePerformance getPipelinePerformance() {
		return this.performance;
	}

	@Override
	public void setPipelinePerformance(final PipelinePerformance performance) {
		this.performance = performance;
	}

	protected void setDocumentPublisher(final SolrDocumentPublisher publisher) {
		this.publisher = publisher;
	}

}