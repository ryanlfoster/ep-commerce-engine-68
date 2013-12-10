package com.elasticpath.test.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;

/**
 * Utility class for concurrency tests.
 */
public final class ConcurrencyTestUtils {
	private static final int THREAD_FINISH_POLL_INTERVAL = 66;

	private ConcurrencyTestUtils() {
		// static class
	}

	/**
	 * The default number of threads per test is guaranteed to be at least the maximum number of threads the running JVM
	 * can support at the time you call this method.
	 * 
	 * @return default number of threads
	 */
	public static int getDefaultNumberOfThreads() {
		// available processors may change at runtime, can't be a constant
		return Runtime.getRuntime().availableProcessors() + 1;
	}

	/**
	 * Executes a test with a given number of threads. This executes the work in {@link Runnable} and blocks until all
	 * threads have completed.
	 * 
	 * @param numberOfThreads the number of threads to run on
	 * @param timeout a timeout in case of deadlocks or a negative number for no timeout
	 * @param factory {@link RunnableFactory} to create {@link Runnable}s
	 * @return {@link Runnable} instances that were created-
	 * @param <T> type of {@link Runnable} to create
	 */
	public static <T extends Runnable> List<T> executeTestWithTimeout(final int numberOfThreads, final long timeout,
			final RunnableFactory<T> factory) {
		final CountDownLatch latch = new CountDownLatch(1);

		List<T> runnables = new ArrayList<T>();
		Thread[] threads = new Thread[numberOfThreads];
		for (int i = 0; i < threads.length; ++i) {
			final T runnable = factory.createRunnable();
			runnables.add(runnable);
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							latch.await();
							break;
						} catch (InterruptedException e) {
							// do nothing, we are still waiting
						}
					}
					runnable.run();
				}
			});
			threads[i].start();
		}

		latch.countDown(); // start the threads

		long systemTimeout;
		if (timeout <= 0) {
			systemTimeout = Long.MAX_VALUE;
		} else {
			systemTimeout = System.currentTimeMillis() + timeout;
		}

		while (true) {
			boolean anyAlive = false;
			for (int i = 0; i < threads.length; ++i) {
				anyAlive |= threads[i].isAlive();
			}
			if (!anyAlive) {
				break;
			}

			if (System.currentTimeMillis() > systemTimeout) {
				Assert.fail(String.format("Test timed out after %d ms", timeout));
			}

			try {
				Thread.sleep(THREAD_FINISH_POLL_INTERVAL);
			} catch (InterruptedException e) {
				// do nothing
			}
		}

		return runnables;
	}

	/**
	 * Executes a test with a given number of threads. This executes the work in {@link Runnable} and blocks until all
	 * threads have completed.
	 * 
	 * @param numberOfThreads the number of threads to run on
	 * @param factory {@link RunnableFactory} to create {@link Runnable}s
	 * @return {@link Runnable} instances that were created
	 * @param <T> type of {@link Runnable} to create
	 */
	public static <T extends Runnable> List<T> executeTest(final int numberOfThreads, final RunnableFactory<T> factory) {
		return executeTestWithTimeout(numberOfThreads, -1, factory);
	}

	/**
	 * Executes a test with the {@link #getDefaultNumberOfThreads default number of threads}.
	 * 
	 * @param runnable {@link Runnable} action of the test
	 * @return {@link Runnable} instances that were created
	 * @param <T> type of {@link Runnable} to create
	 */
	public static <T extends Runnable> List<T> executeTest(final RunnableFactory<T> runnable) {
		return executeTest(getDefaultNumberOfThreads(), runnable);
	}

	/** A factory class for the creation of {@link Runnable}s. */
	public interface RunnableFactory<T extends Runnable> {
		/**
		 * @return a new {@link Runnable}
		 */
		T createRunnable();
	}
}
