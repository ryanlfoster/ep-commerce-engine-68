/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client;

/**
 * An interface holding information for the sync tool configuration parameters.
 */
public interface SyncToolConfiguration {

	
	/**
	 * Gets the adapter parameter.
	 * 
	 * @return the adapter parameter
	 */
	String getAdapterParameter();

	/**
	 * Gets the target configuration name.
	 * 
	 * @return the target config name
	 */
	String getTargetConfigName();

	/**
	 * Gets the source configuration name.
	 * 
	 * @return the source config name
	 */
	String getSourceConfigName();
	
	/**
	 * Gets the root path.
	 * 
	 * @return the root path
	 */
	String getRootPath();
	
	/**
	 * Gets the sub directory relevant to root path.
	 * 
	 * @return the sub-directory
	 */
	String getSubDir();
	
	/**
	 * Gets the requested controller type.
	 * 
	 * @return the controller type
	 */
	SyncToolControllerType getControllerType();

}
