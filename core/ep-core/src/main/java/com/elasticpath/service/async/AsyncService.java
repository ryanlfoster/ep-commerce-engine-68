package com.elasticpath.service.async;

import java.util.concurrent.RejectedExecutionHandler;

import com.elasticpath.settings.SettingsReader;

/**
 * <code>AsyncService</code> is abstract implementation of the base interface for a service providing interfaces for performing monitored.
 * asynchronous calls
 */
public interface AsyncService {

	/**
	 * Clean-up and shut-down.
	 */
	 void shutdown();

	/**
	 * Adds a task to the pool and attempts to execute it.
	 * 
	 * @param task to be executed
	 */
	void addTask(final Runnable task);

	/**
	 * Set the settings reader.
	 * 
	 * @param settingsReader settings reader for async service
	 */
	void setSettingsReader(final SettingsReader settingsReader);

	/**
	 * Get the settings reader.
	 * 
	 * @return the settingsReader
	 */
	SettingsReader getSettingsReader();

	/**
	 * Get the number of active threads.
	 * 
	 * @return the number of active threads
	 */
	int getActiveCount();
	
	/**
	 * Get the number of active threads.
	 * 
	 * @return the number of active threads
	 */
	RejectedExecutionHandler getRejectedExecutionHandler();
}
