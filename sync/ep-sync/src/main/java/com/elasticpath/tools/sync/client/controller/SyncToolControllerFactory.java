/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.tools.sync.client.controller;

/**
 * A factory responsible for creating a controller.
 */
public interface SyncToolControllerFactory {

	/**
	 * Creates the controller to drive the synchronization.
	 * 
	 * @return the synchronization controller
	 */
	SyncToolController createController();
}
