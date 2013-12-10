package com.elasticpath.search.index.pipeline.impl;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.ObjectFactory;

import com.elasticpath.search.index.pipeline.IndexingTask;
import com.elasticpath.search.index.pipeline.UidFilteringTask;

/**
 * This stage is responsible for removing UIDs that don't need to be indexed from a large list of UIDs.
 *
 * @param <IN> a big list of uids.
 * @param <OUT> a smaller list of uids.
 */
public class UidFilteringStage<IN, OUT> extends AbstractIndexingStage<Collection<Long>, Set<Long>> {

	private ObjectFactory<UidFilteringTask<Collection<Long>, Set<Long>>> indexFilteringTaskFactory;

	/**
	 * {@inheritDoc}
	 */
	IndexingTask<Collection<Long>, Set<Long>> create(final Collection<Long> payload) {
		final UidFilteringTask<Collection<Long>, Set<Long>> groupingTask = getIndexFilteringTaskFactory().getObject();
		groupingTask.setUids(payload);
		return groupingTask;
	}

	/**
	 * @param indexFilteringTaskFactory the indexFilteringTaskFactory to set
	 */
	public void setIndexFilteringTaskFactory(final ObjectFactory<UidFilteringTask<Collection<Long>, Set<Long>>> indexFilteringTaskFactory) {
		this.indexFilteringTaskFactory = indexFilteringTaskFactory;
	}

	/**
	 * @return the indexFilteringTaskFactory
	 */
	public ObjectFactory<UidFilteringTask<Collection<Long>, Set<Long>>> getIndexFilteringTaskFactory() {
		return indexFilteringTaskFactory;
	}

}