package com.elasticpath.search.index.loader.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.elasticpath.search.index.pipeline.AbstractIndexingTask;
import com.elasticpath.search.index.pipeline.EntityLoadingTask;

/**
 * The common plumbing used by {@code EntityLoadingTask} which are generated for the {@code EntityLoadingStage}. This class handles collecting
 * performance statistics and calls out to the concrete implementation of {@code #loadBatch()}.
 * 
 * @param <ENTITY> The type of entity being loaded.
 */
public abstract class AbstractEntityLoader<ENTITY> extends AbstractIndexingTask<Set<Long>, ENTITY> implements EntityLoadingTask<Set<Long>, ENTITY> {

	private static final Logger LOG = Logger.getLogger(AbstractEntityLoader.class);

	private Set<Long> uidsToLoad;

	@Override
	public void run() {
		Assert.notNull(getNextStage(), "next stage not provided");
		Assert.notNull(uidsToLoad, "setBatch not called, or called with a null argument");

		getPipelinePerformance().addCount("loader:batch_in", 1);
		getPipelinePerformance().addCount("loader:entities_in", uidsToLoad.size());

		if (getUidsToLoad().isEmpty()) {
			return;
		}

		try {

			final long start = System.currentTimeMillis();
			final Collection<ENTITY> loadedEntities = loadBatch();
			getPipelinePerformance().addValue("loader:batch_load_time", System.currentTimeMillis() - start);
			getPipelinePerformance().addCount("loader:entities_loaded", loadedEntities.size());

			for (final ENTITY entity : loadedEntities) {
				getNextStage().send(entity);
				getPipelinePerformance().addCount("loader:entities_out", 1);
			}

		} catch (final Exception e) {

			getPipelinePerformance().addCount("loader:batch_loading_failures", 1);

			LOG.error(
					"Exception when loading batch (these uids will not be indexed and this exception is only logged) with uids " + getUidsToLoad(),
					e);

			handleEntitiesIndividually();
		}

	}

	/**
	 * If loading in a batch fails, this method will break the batch into units of 1 and attempt load each piece individually. This code attempts to
	 * make the most of a bad situation.
	 */
	protected void handleEntitiesIndividually() {

		final Set<Long> originalBatch = getUidsToLoad();
		final Set<Long> batchOfOne = new HashSet<Long>();

		/**
		 * We replace the batch of all the uids with our own which we'll only put one uid at a time.
		 */
		setBatch(batchOfOne);

		LOG.info("Loading " + originalBatch.size() + " entities individually");

		for (final Long uid : originalBatch) {

			try {

				batchOfOne.clear();
				batchOfOne.add(uid);

				final Collection<ENTITY> loadedEntity = loadBatch();
				getPipelinePerformance().addCount("loader:entities_loaded_individually", loadedEntity.size());

				for (final ENTITY entity : loadedEntity) {
					getNextStage().send(entity);
					getPipelinePerformance().addCount("loader:entities_out", 1);

				}
			} catch (final Exception e) {
				getPipelinePerformance().addCount("loader:individual_entity_loading_failures", 1);

				LOG.error("Could not load entity with uid " + uid + " for indexing, this entity will not be indexed. ", e);
			}

		}

		/** Put the original set of uids back. */
		setBatch(originalBatch);

	}

	/**
	 * Concrete implementations should fetch the entities provided by {@code #getUidsToLoad()} and return the resulting entities. This method will
	 * never be called when uids to load is empty or null.
	 * 
	 * @return one or more entities.
	 */
	abstract Collection<ENTITY> loadBatch();

	@Override
	public void setBatch(final Set<Long> payload) {
		this.uidsToLoad = payload;
	}

	/**
	 * Called by concrete implementations to get the set of uids to load.
	 * 
	 * @return as set by {@code #setBatch(Set)} by the {@code EntityLoadingStage}.
	 */
	protected Set<Long> getUidsToLoad() {
		return uidsToLoad;
	}
}
