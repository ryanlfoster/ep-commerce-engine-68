package com.elasticpath.search.index.loader.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.service.rules.RuleService;

/**
 * Test {@link BatchRuleLoader}.
 */
public class BatchRuleLoaderTest {
	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final RuleService ruleService = context.mock(RuleService.class);

	@SuppressWarnings("unchecked")
	private final IndexingStage<Rule, ?> nextStage = context.mock(IndexingStage.class);

	private final PipelinePerformance pipelinePerformance = new PipelinePerformanceImpl();

	private BatchRuleLoader batchRuleLoader;

	private List<Rule> rules;

	/**
	 * How the {@code BatchRuleLoader} actually loads rules is internal to it, and should be tested in integration tests. We override the
	 * {@code #loadBatch()} method to return data assigned to it.
	 */
	class TestBatchRuleLoader extends BatchRuleLoader {

		@Override
		public Collection<Rule> loadBatch() {
			return rules;
		}

	}

	/**
	 * Initialize the services on the {@link BatchRuleLoader}.
	 */
	@Before
	public void setUpBatchRuleLoader() {
		rules = new ArrayList<Rule>();
		batchRuleLoader = new TestBatchRuleLoader();
		batchRuleLoader.setRuleService(ruleService);
		batchRuleLoader.setPipelinePerformance(pipelinePerformance);
	}

	/**
	 * Test that loading a set of rules works.
	 */
	@Test
	public void testLoadingValidList() {

		final Rule firstRule = context.mock(Rule.class, "first");
		final Rule secondRule = context.mock(Rule.class, "second");
		rules.add(firstRule);
		rules.add(secondRule);

		context.checking(new Expectations() {
			{
				exactly(2).of(nextStage).send(with(any(Rule.class)));

			}
		});

		batchRuleLoader.setBatch(createUidsToLoad());
		batchRuleLoader.setNextStage(nextStage);
		batchRuleLoader.run();
	}

	/**
	 * Test sending an empty set of {@link Rule} uids to load returns an empty list.
	 */
	@Test
	public void testLoadingNoRules() {
		batchRuleLoader.setBatch(new HashSet<Long>());
		batchRuleLoader.setNextStage(nextStage);

		context.checking(new Expectations() {
			{
				never(nextStage).send(with(any(Rule.class)));

			}
		});

		batchRuleLoader.run();
	}

	/**
	 * Test that trying to load a set of {@link Rule}s without setting the next stage fails.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLoadingInvalidNextStage() {
		batchRuleLoader.setBatch(createUidsToLoad());
		batchRuleLoader.setNextStage(null);
		batchRuleLoader.run();
	}

	private Set<Long> createUidsToLoad() {
		final Set<Long> uidsToLoad = new HashSet<Long>();
		uidsToLoad.add(Long.valueOf(1));
		return uidsToLoad;
	}

}
