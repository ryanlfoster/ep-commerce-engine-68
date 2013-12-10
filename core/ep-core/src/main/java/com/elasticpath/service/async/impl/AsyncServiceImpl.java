/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.service.async.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.async.AsyncService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Implementation of <code>AsyncService</code>.
 */
public class AsyncServiceImpl implements AsyncService {

	// This is the queue used for tasks waiting to be executed when the number of
	// maximum threads is already running in the pool
	private BlockingQueue<Runnable> tasksQueue;

	// Rejected threads (i.e. due to queue overflow for example) are handled by this class.
	private AsyncRejectedExecutionHandelerImpl rejectedExecutionHandler;

	// The pool implementation
	private ThreadPoolExecutor threadPoolExecutor;

	private SettingsReader settingsReader;

	private static final Logger LOG = Logger.getLogger(AsyncServiceImpl.class);

	// The queue size to be used for waiting tasks
	private static final String SETTING_PATH_QUEUE_SIZE = "COMMERCE/SYSTEM/ASYNC/ThreadQueueSize";

	// The number of worker threads the pool is starting with
	private static final String SETTING_PATH_THREAD_POOL_CORE_SIZE = "COMMERCE/SYSTEM/ASYNC/ThreadPoolCoreSize";

	// The maximum number of threads existing in the pool at any given time
	private static final String SETTING_PATH_THREAD_POOL_MAX_POOL_SIZE = "COMMERCE/SYSTEM/ASYNC/ThreadMaxPoolSize";

	// The number of milliseconds to keep the worker thread alive after it has finished
	private static final String SETTING_PATH_THREAD_POOL_KEEP_ALIVE_TIME = "COMMERCE/SYSTEM/ASYNC/ThreadKeepAlive";

	/**
	 * Constructor taking in a settings reader.
	 * 
	 * @param settingsReader settings for the async service
	 */
	public AsyncServiceImpl(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
		initialize();
	}

	@Override
	public void shutdown() {
		if (threadPoolExecutor != null) {
			threadPoolExecutor.shutdown();
		}
	}

	/**
	 * Initialize the sync service attributes.
	 */
	private void initialize() {

		// Get the max size of the queue
		try {
			int queueSize = Integer.parseInt(getSettingStringValue(SETTING_PATH_QUEUE_SIZE));
			this.tasksQueue = new ArrayBlockingQueue<Runnable>(queueSize);
		} catch (NumberFormatException ex) {
			throw new EpSystemException("Unable to initialize AsyncService", ex);
		}

		if (tasksQueue != null) {
			// Create AsyncRejectedHandler to deal with rejected tasks (i.e. queue overflow);
			rejectedExecutionHandler = new AsyncRejectedExecutionHandelerImpl();
			try {
				int coreSize = Integer.parseInt(getSettingStringValue(SETTING_PATH_THREAD_POOL_CORE_SIZE));
				int maxPoolSize = Integer.parseInt(getSettingStringValue(SETTING_PATH_THREAD_POOL_MAX_POOL_SIZE));
				int keepAliveTime = Integer.parseInt(getSettingStringValue(SETTING_PATH_THREAD_POOL_KEEP_ALIVE_TIME));
				this.threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, tasksQueue,
						rejectedExecutionHandler);

			} catch (NumberFormatException ex) {
				throw new EpSystemException("Unable to initialize AsyncService", ex);
			}
		}
	}

	@Override
	public void addTask(final Runnable task) {
		if (threadPoolExecutor != null) {
			threadPoolExecutor.execute(new Runnable() {
				public void run() {
					try {
						task.run();
						LOG.trace(String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, "
								+ "isTerminated: %s, rejectedCount: %d", threadPoolExecutor.getPoolSize(), threadPoolExecutor
								.getCorePoolSize(), threadPoolExecutor.getActiveCount(), threadPoolExecutor.getCompletedTaskCount(),
								threadPoolExecutor.getTaskCount(), threadPoolExecutor.isShutdown(), threadPoolExecutor.isTerminated(),
								rejectedExecutionHandler.getRejectedCount()));
					} catch (RuntimeException e) {
						LOG.warn(String.format("Execution of task %s failed!", task), e);
						throw e;
					}
				}
			});
		}
	}

	/**
	 * Get the setting value string for the given settings path.
	 * 
	 * @param path the setting path
	 * @return a string value of the setting
	 */
	protected final String getSettingStringValue(final String path) {
		SettingValue settingValue = getSettingsReader().getSettingValue(path);
		return settingValue.getValue();
	}

	/**
	 * Set the settings reader.
	 * 
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Get the settings reader.
	 * 
	 * @return the settingsReader
	 */
	public final SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * The custom {@link RejectedExecutionHandler} to handle the rejected tasks / {@link Runnable}.
	 */
	public class AsyncRejectedExecutionHandelerImpl implements RejectedExecutionHandler {
		private int rejected;

		/**
		 * Method called when a thread is rejected.
		 * 
		 * @param runnable Task to be run
		 * @param executor ThreadPool
		 */
		public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor executor) {
			LOG.error("Rejected execution of: " + runnable);
			rejected++;
		}

		/**
		 * Get the number of rejected threads.
		 * 
		 * @return the number of rejected threads
		 */
		public int getRejectedCount() {
			return rejected;
		}
	}

	/**
	 * @return the number of active threads
	 */
	public int getActiveCount() {
		return this.threadPoolExecutor.getActiveCount();
	}

	@Override
	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return this.rejectedExecutionHandler;
	}
}
