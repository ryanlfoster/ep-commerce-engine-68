package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.test.concurrent.ConcurrencyTestUtils;
import com.elasticpath.test.concurrent.ConcurrencyTestUtils.RunnableFactory;
import com.elasticpath.test.concurrent.RepeatableTestRunner;
import com.elasticpath.test.concurrent.Repeats;

/**
 * Tests {@link DefaultSolrManager} against concurrency issues. As such, this test may spontaneously pass when it should
 * fail, but we've tried to run the tests enough times to show the problem.
 */
@RunWith(RepeatableTestRunner.class)
public class DefaultSolrManagerConcurrencyTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setThreadingPolicy(new Synchroniser());
		}
	};

	/**
	 * This number was chosen at random to exercise the concurrency issue. A pre-determined number can be invalidated by
	 * code changes in {@link DefaultSolrManager} itself.
	 */
	private static final int NUM_TEST_REPEATS = 101;
	private SolrDocumentPublisherFactory solrDocumentPublisherFactory;
	private SearchConfigFactory searchConfigFactory;

	/** Test initialization. */
	@Before
	public void initialize() {
		solrDocumentPublisherFactory = context.mock(SolrDocumentPublisherFactory.class);
		searchConfigFactory = context.mock(SearchConfigFactory.class);

		context.checking(new Expectations() {
			{
				SearchConfig searchConfig = context.mock(SearchConfig.class);
				allowing(searchConfig).getSearchHost();
				will(returnValue("http://localhost/"));

				allowing(searchConfigFactory).getSearchConfig(with(any(String.class)));
				will(returnValue(searchConfig));
				allowing(searchConfigFactory);
				allowing(solrDocumentPublisherFactory);
			}
		});
	}

	/** Tests getting a server. */
	@Test
	@Repeats(times = NUM_TEST_REPEATS)
	public void getServer() {
		final DefaultSolrManager manager = createDefaultSolrManager();
		List<GetServerTestRunnable> runnables = ConcurrencyTestUtils.executeTest(new RunnableFactory<GetServerTestRunnable>() {
			@Override
			public GetServerTestRunnable createRunnable() {
				return new GetServerTestRunnable(manager);
			}
		});

		SolrServer firstServer = runnables.get(0).server;
		for (int i = 0; i < runnables.size(); ++i) {
			assertEquals(String.format("Test server[0] against server[%d] failed (all should be the same)", i), firstServer,
					runnables.get(i).server);
		}
	}

	/** {@link Runnable} for {@link DefaultSolrManagerConcurrencyTest#getServer()}. */
	private static class GetServerTestRunnable implements Runnable {
		private SolrServer server;
		private final SolrProvider provider;

		public GetServerTestRunnable(final SolrProvider provider) {
			this.provider = provider;
		}

		@Override
		public void run() {
			server = provider.getServer(IndexType.PRODUCT);
		}
	}

	private DefaultSolrManager createDefaultSolrManager() {
		final DefaultSolrManager manager = new DefaultSolrManager();
		manager.setSearchConfigFactory(searchConfigFactory);
		manager.setSolrDocumentPublisherFactory(solrDocumentPublisherFactory);
		return manager;
	}

	/** {@link Runnable} for {@link DefaultSolrManagerConcurrencyTest#getPublisherAndServer()}. */
	private static class GetDocumentPublisher implements Runnable {
		private final SolrManager manager;

		public GetDocumentPublisher(final SolrManager manager) {
			this.manager = manager;
		}

		@Override
		public void run() {
			manager.getDocumentPublisher(IndexType.PRODUCT);
		}
	}

	/**
	 * {@link DefaultSolrManager#getDocumentPublisher(IndexType)} and {@link DefaultSolrManager#getServer(IndexType)}
	 * may deadlock if they are using different locks.
	 */
	@Test
	@Repeats(times = NUM_TEST_REPEATS)
	public void getPublisherAndServer() {
		final int timeout = 750;
		final DefaultSolrManager manager = createDefaultSolrManager();
		List<Runnable> runnables = ConcurrencyTestUtils.executeTestWithTimeout(
				Math.max(ConcurrencyTestUtils.getDefaultNumberOfThreads(), 2), timeout, new RunnableFactory<Runnable>() {
					private int index = 0;

					@Override
					public Runnable createRunnable() {
						if ((++index % 2) == 0) {
							return new GetServerTestRunnable(manager);
						}
						return new GetDocumentPublisher(manager);
					}
				});

		List<GetServerTestRunnable> serverRunnables = new ArrayList<GetServerTestRunnable>();
		for (Runnable runnable : runnables) {
			if (runnable instanceof GetServerTestRunnable) {
				serverRunnables.add((GetServerTestRunnable) runnable);
			}
		}
		SolrServer firstServer = serverRunnables.get(0).server;
		for (int i = 0; i < serverRunnables.size(); ++i) {
			assertEquals(String.format("Test server[0] against server[%d] failed (all should be the same)", i), firstServer,
					serverRunnables.get(i).server);
		}
	}
}
