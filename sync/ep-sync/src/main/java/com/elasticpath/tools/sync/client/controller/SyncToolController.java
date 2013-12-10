package com.elasticpath.tools.sync.client.controller;

import com.elasticpath.tools.sync.target.result.Summary;

/**
 * Serves to process synchronization.
 */
public interface SyncToolController {

	/**
	 * Processes synchronization.
	 * 
	 * @return Summary instance with success and error items
	 */
	Summary synchronize();

}
