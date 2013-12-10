/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.controller.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.SyncToolControllerType;
import com.elasticpath.tools.sync.client.controller.SyncToolController;
import com.elasticpath.tools.sync.client.controller.SyncToolControllerFactory;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Helpful factory for creating proper controllers depends on parameters, also it unmarshalls connection configuration files.
 * It is possible to remove this factory for better OCP, but is helpful if a number of controllers does not increase, or increases slowly.
 * It is possible to improve this factory by loading ConnectionConfiguration in constructor, 
 * but all parameters for factory methods should have been already checked.
 */
public class SyncToolControllerFactoryImpl implements SyncToolControllerFactory {
	
	private static final Logger LOG = Logger.getLogger(SyncToolControllerFactoryImpl.class);

	private SyncToolConfiguration syncToolConfiguration;
	
	private Map<String, SyncToolController> syncToolControllerBeans = new HashMap<String, SyncToolController>();
	
	/**
	 * Create FullController or FullAndSaveController, depends on rootPath (null or not).
	 * 
	 * @return FullController or FullAndSaveController instance.
	 */
	public SyncToolController createController() {
		if (getSyncToolConfiguration() == null) {
			throw new SyncToolConfigurationException("'syncToolConfiguration' value has not bean set on this bean");
		}
		SyncToolControllerType controllerType = getSyncToolConfiguration().getControllerType();
		SyncToolController controller = getSyncToolControllerBeans().get(controllerType.getName());
		
		LOG.info("Created controller: " + controller);
		
		if (controller == null) {
			throw new SyncToolConfigurationException("No controller found for " + controllerType); 
		}
		return controller;
	}
	
	/**
	 *
	 * @return the syncToolConfiguration
	 */
	protected SyncToolConfiguration getSyncToolConfiguration() {
		return syncToolConfiguration;
	}

	/**
	 *
	 * @return the syncToolControllerBeans
	 */
	public Map<String, SyncToolController> getSyncToolControllerBeans() {
		return syncToolControllerBeans;
	}

	/**
	 *
	 * @param syncToolControllerBeans the syncToolControllerBeans to set
	 */
	public void setSyncToolControllerBeans(final Map<String, SyncToolController> syncToolControllerBeans) {
		this.syncToolControllerBeans = syncToolControllerBeans;
	}

	/**
	 *
	 * @param syncToolConfiguration the syncToolConfiguration to set
	 */
	public void setSyncToolConfiguration(final SyncToolConfiguration syncToolConfiguration) {
		this.syncToolConfiguration = syncToolConfiguration;
	}


}
