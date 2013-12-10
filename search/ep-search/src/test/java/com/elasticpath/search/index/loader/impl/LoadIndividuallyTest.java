package com.elasticpath.search.index.loader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;

/**
 * The {@code AbstractEntityLoader} has special retry logic for breaking batch loads up on failure. This tests that.
 */
public class LoadIndividuallyTest {

	/**
	 * Five is right out.
	 */
	static final long BAD_NUMBER = 5;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private AbstractEntityLoader<String> faultyLoader;

	@SuppressWarnings("unchecked")
	private final IndexingStage<String, SolrInputDocument> nextStage = context.mock(IndexingStage.class);

	/**
	 * This entity loader will explode if given the uid of 5.
	 */
	class FaultyEntityLoader extends AbstractEntityLoader<String> {

		@Override
		Collection<String> loadBatch() {
			final List<String> stuff = new ArrayList<String>();
			for (final Long uid : getUidsToLoad()) {
				if (uid == BAD_NUMBER) {
					throw new EpServiceException(BAD_NUMBER + " is right out.");
				}
				stuff.add("the number " + uid);
			}
			return stuff;
		}
	}

	/**
	 * Create a faulty loader.
	 */
	@Before
	public void setupLoader() {
		faultyLoader = new FaultyEntityLoader();
		faultyLoader.setNextStage(nextStage);
		faultyLoader.setPipelinePerformance(new PipelinePerformanceImpl());
	}

	/**
	 * Test regular loading with no BAD_NUMBER.
	 */
	@Test
	public void testNormalBatchLoad() {
		final Set<Long> uids = new HashSet<Long>();

		// CHECKSTYLE:OFF -- there's nothing magical about these numbers.
		uids.add(1L);
		uids.add(2L);
		uids.add(3L);
		// CHECKSTYLE:ON -- there was nothing magical about these numbers.

		context.checking(new Expectations() {
			{
				exactly(uids.size()).of(nextStage).send(with(any(String.class)));
			}
		});

		faultyLoader.setBatch(uids);
		faultyLoader.run();
	}

	/**
	 * Now lets test it with BAD_NUMBER which will throw an exception in the loading above.
	 */
	@Test
	public void testFaultyBatchLoad() {
		final Set<Long> uids = new HashSet<Long>();

		// CHECKSTYLE:OFF -- there's nothing magical about these numbers.
		uids.add(1L);
		uids.add(2L);
		uids.add(3L);
		uids.add(4L);
		uids.add(BAD_NUMBER); // except this one.
		uids.add(6L);
		// CHECKSTYLE:ON -- there was nothing magical about these numbers.

		context.checking(new Expectations() {
			{
				exactly(uids.size() - 1).of(nextStage).send(with(any(String.class)));
			}
		});

		faultyLoader.setBatch(uids);
		faultyLoader.run();

	}
}
