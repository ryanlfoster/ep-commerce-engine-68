/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport;

/**
 * Interface defining the import job validation and running processes.
 */
public interface ImportJobProcessor {

	
	/**
	 * Validates the next import job in the queue.
	 */
	void launchImportJob();
}
