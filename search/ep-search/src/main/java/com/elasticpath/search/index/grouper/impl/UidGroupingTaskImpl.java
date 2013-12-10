package com.elasticpath.search.index.grouper.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.elasticpath.search.index.pipeline.AbstractIndexingTask;
import com.elasticpath.search.index.pipeline.UidGroupingTask;

/**
 * Batches a set of indices and passes each batch to the next stage.
 */
public class UidGroupingTaskImpl extends AbstractIndexingTask<Collection<Long>, Set<Long>> implements UidGroupingTask<Collection<Long>, Set<Long>> {

	private final Collection<Long> fullList = new ArrayList<Long>();

	/**
	 * Use a group size of 50 by default if none is specified.
	 */
	// CHECKSTYLE:OFF
	private int groupSize = 50;

	// CHECKSTYLE:ON

	@Override
	public void run() {
		validateConfiguration();
		getPipelinePerformance().addCount("grouper:uids_in", fullList.size());
		breakIntoSmallerGroups();
	}

	private void breakIntoSmallerGroups() {
		while (!fullList.isEmpty()) {
			Set<Long> group = getNextGroup();
			getPipelinePerformance().addCount("grouper:batches_out", 1);
			getNextStage().send(group);
		}
	}
	
	private Set<Long> getNextGroup() {
		int lastBatchIndex = Math.min(groupSize, fullList.size());
		Set<Long> batch = new HashSet<Long>();
		Iterator<Long> iterator = fullList.iterator();
		for (int idCount = 0; idCount < lastBatchIndex; ++idCount) {
			batch.add(iterator.next());
			iterator.remove();
		}
		getPipelinePerformance().addValue("grouper:batch_size", batch.size());
		return batch;
	}
	
	private void validateConfiguration() {
		if (getNextStage() == null) {
			throw new IllegalArgumentException("Next stage must be set.");
		}
	}

	@Override
	public void setUids(final Collection<Long> objectIdentifiers) {
		fullList.addAll(objectIdentifiers);
	}

	/**
	 * Sets the size of the groups that the indices will be broken into. Must be larger than zero.
	 * 
	 * @param groupSize the full list of uids to be broken up.
	 */
	public void setGroupSize(final int groupSize) {
		if (groupSize < 1) {
			throw new IllegalArgumentException("Index group size must be greater than zero.");
		}

		this.groupSize = groupSize;
	}

}
