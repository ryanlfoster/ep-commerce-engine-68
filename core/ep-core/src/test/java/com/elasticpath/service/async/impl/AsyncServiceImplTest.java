package com.elasticpath.service.async.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.log4j.Logger;

import com.elasticpath.service.async.AsyncService;
import com.elasticpath.service.async.impl.AsyncServiceImpl.AsyncRejectedExecutionHandelerImpl;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test InitialSyncServiceImpl.
 */
public class AsyncServiceImplTest {

	private static final String SETTING_PATH_QUEUE_SIZE = "COMMERCE/SYSTEM/ASYNC/ThreadQueueSize";

	private static final String SETTING_PATH_THREAD_POOL_CORE_SIZE = "COMMERCE/SYSTEM/ASYNC/ThreadPoolCoreSize";

	private static final String SETTING_PATH_THREAD_POOL_MAX_POOL_SIZE = "COMMERCE/SYSTEM/ASYNC/ThreadMaxPoolSize";

	private static final String SETTING_PATH_THREAD_POOL_KEEP_ALIVE_TIME = "COMMERCE/SYSTEM/ASYNC/ThreadKeepAlive";

	private static final String SETTING_PATH_THREAD_POOL_MONITOR_SLEEP = "COMMERCE/SYSTEM/ASYNC/ThreadPoolMonitorSleep";

	private static final String THREAD_QUEUE_SIZE = "100";

	private static final String THREAD_POOL_CORE_SIZE = "50";

	private static final String THREAD_POOL_MAX_SIZE = "100";

	private static final String THREAD_POOL_KEEP_ALIVE = "10000";

	private static final String THREAD_POOL_MONITOR_SLEEP = "5000";

	@Rule
	public final JUnitRuleMockery mockery = new JUnitRuleMockery();

	private AsyncService asyncService;

	private SettingsReader settingsReader;

	private static final Logger LOG = Logger.getLogger(AsyncServiceImplTest.class);

	/**
	 * Set up the test.
	 */
	@Before
	public void setUp() {
		settingsReader = mockery.mock(SettingsReader.class);

		final SettingValue queueSize = mockery.mock(SettingValue.class, SETTING_PATH_QUEUE_SIZE);
		final SettingValue poolCoreSize = mockery.mock(SettingValue.class, SETTING_PATH_THREAD_POOL_CORE_SIZE);
		final SettingValue maxPoolSize = mockery.mock(SettingValue.class, SETTING_PATH_THREAD_POOL_MAX_POOL_SIZE);
		final SettingValue keepAlive = mockery.mock(SettingValue.class, SETTING_PATH_THREAD_POOL_KEEP_ALIVE_TIME);
		final SettingValue monitorSleep = mockery.mock(SettingValue.class, SETTING_PATH_THREAD_POOL_MONITOR_SLEEP);

		mockery.checking(new Expectations() {
			{
				allowing(settingsReader).getSettingValue(SETTING_PATH_QUEUE_SIZE);
				will(returnValue(queueSize));
				allowing(queueSize).getValue();
				will(returnValue(THREAD_QUEUE_SIZE));

				allowing(settingsReader).getSettingValue(SETTING_PATH_THREAD_POOL_CORE_SIZE);
				will(returnValue(poolCoreSize));
				allowing(poolCoreSize).getValue();
				will(returnValue(THREAD_POOL_CORE_SIZE));

				allowing(settingsReader).getSettingValue(SETTING_PATH_THREAD_POOL_MAX_POOL_SIZE);
				will(returnValue(maxPoolSize));
				allowing(maxPoolSize).getValue();
				will(returnValue(THREAD_POOL_MAX_SIZE));

				allowing(settingsReader).getSettingValue(SETTING_PATH_THREAD_POOL_KEEP_ALIVE_TIME);
				will(returnValue(keepAlive));
				allowing(keepAlive).getValue();
				will(returnValue(THREAD_POOL_KEEP_ALIVE));

				allowing(settingsReader).getSettingValue(SETTING_PATH_THREAD_POOL_MONITOR_SLEEP);
				will(returnValue(monitorSleep));
				allowing(monitorSleep).getValue();
				will(returnValue(THREAD_POOL_MONITOR_SLEEP));

			}
		});
		asyncService = new AsyncServiceImpl(settingsReader);
	}

	/**
	 * Clean up after test.
	 */
	@After
	public void tearDown() {
		asyncService.shutdown();
	}

	/**
	 * Test for execution of one thread.
	 *
	 * @throws Exception EpServiceException
	 */
	@Test
	public void testRunningOneTask() throws Exception {

		// No tasks added, test queue is empty
		assertEquals(0, asyncService.getActiveCount());

		// Create all tasks will wait for the barrier call
		CyclicBarrier runningBarrier = new CyclicBarrier(2);
		CyclicBarrier completeBarrier = new CyclicBarrier(2);

		// Test adding one task
		TestThread task1 = new TestThread(runningBarrier, completeBarrier);
		asyncService.addTask(task1);
		runningBarrier.await();

		// Check we have exactly one task into the pool
		assertEquals(1, asyncService.getActiveCount());

		// Allow the task to continue
		completeBarrier.await();
	}

	/**
	 * Test for execution of max number of threads.
	 *
	 * @throws Exception EpServiceException
	 */
	@Test
	public void testRunningMaxNumberOfTasks() throws Exception {
		int maxNumberOfThreads = Integer.parseInt(THREAD_POOL_MAX_SIZE);
		int maxQueueSize = Integer.parseInt(THREAD_QUEUE_SIZE);
		// Schedule enough threads to fill both pool and queue
		int numberOfTasks = maxNumberOfThreads + maxQueueSize;
		CyclicBarrier runBarrier = new CyclicBarrier(maxNumberOfThreads + 1);
		CyclicBarrier completeBarrier = new CyclicBarrier(maxNumberOfThreads + 1);
		scheduleTasks(numberOfTasks, runBarrier, completeBarrier);
		runBarrier.await();

		// There should not be more threads than the maximum number of threads allowed
		assertEquals(maxNumberOfThreads, asyncService.getActiveCount());
		completeBarrier.await();
	}

	/**
	 * Test for rejection of one thread.
	 *
	 * @throws Exception EpServiceException
	 */
	@Test
	public void testRejectOne() throws Exception {
		int maxNumberOfThreads = Integer.parseInt(THREAD_POOL_MAX_SIZE);
		int maxQueueSize = Integer.parseInt(THREAD_QUEUE_SIZE);

		CyclicBarrier runBarrier = new CyclicBarrier(maxNumberOfThreads + 1);
		CyclicBarrier completeBarrier = new CyclicBarrier(maxNumberOfThreads + 1);

		// Cause one rejection
		scheduleTasks(maxNumberOfThreads + maxQueueSize + 1, runBarrier, completeBarrier);
		AsyncRejectedExecutionHandelerImpl rejectedHandler = (AsyncRejectedExecutionHandelerImpl) asyncService.getRejectedExecutionHandler();
		runBarrier.await();

		// One thread should have been rejected
		assertEquals(rejectedHandler.getRejectedCount(), 1);

		completeBarrier.await();
	}

	private void scheduleTasks(final int numberOfTasks, final CyclicBarrier runBarrier, final CyclicBarrier completeBarrier) throws Exception {
		// No tasks added, test queue is empty
		assertEquals(0, asyncService.getActiveCount());
		List<Runnable> tasks = new ArrayList<Runnable>();

		for (int i = 0; i < numberOfTasks; i++) {
			TestThread task = new TestThread(runBarrier, completeBarrier);
			tasks.add(task);
			asyncService.addTask(task);
		}
	}

	/**
	 * Test thread class to simulate a Runnable class.
	 */
	private class TestThread implements Runnable {
		private final CyclicBarrier runBarrier;

		private final CyclicBarrier completeBarrier;

		/**
		 * Getter for run barrier.
		 *
		 * @return the runBarrier
		 */
		public CyclicBarrier getRunBarrier() {
			return runBarrier;
		}

		/**
		 * Getter for complete barrier.
		 *
		 * @return the completeBarrier
		 */
		public CyclicBarrier getCompleteBarrier() {
			return completeBarrier;
		}

		/**
		 * Constructor with appropriate cyclic barriers.
		 *
		 * @param runBarrier Cyclic barrier for running
		 * @param completeBarrier Cyclic barrier for completion
		 */
		TestThread(final CyclicBarrier runBarrier, final CyclicBarrier completeBarrier) {
			this.runBarrier = runBarrier;
			this.completeBarrier = completeBarrier;
		}

		/**
		 * Execution method for Test Thread.
		 */
		public void run() {
			try {
				runBarrier.await();
				completeBarrier.await();
			} catch (InterruptedException e) {
				LOG.error(e);
			} catch (BrokenBarrierException e) {
				LOG.error(e);
			}
		}
	}
}
