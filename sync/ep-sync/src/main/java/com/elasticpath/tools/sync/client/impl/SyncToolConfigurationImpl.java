/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client.impl;

import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.client.SyncToolControllerType;

/**
 * The default implementation of a {@link SyncToolConfiguration}.
 */
public class SyncToolConfigurationImpl implements SyncToolConfiguration {

	private String adapterParameter;
	
	private SyncToolControllerType controllerType;
	
	private String rootPath;
	
	private String subDir;
	
	private String sourceConfigName;
	
	private String targetConfigName;

	/**
	 *
	 * @return the adapterParameter
	 */
	public String getAdapterParameter() {
		return adapterParameter;
	}

	/**
	 *
	 * @param adapterParameter the adapterParameter to set
	 */
	public void setAdapterParameter(final String adapterParameter) {
		this.adapterParameter = adapterParameter;
	}

	/**
	 *
	 * @return the controllerType
	 */
	public SyncToolControllerType getControllerType() {
		return controllerType;
	}

	/**
	 *
	 * @param controllerType the controllerType to set
	 */
	public void setControllerType(final SyncToolControllerType controllerType) {
		this.controllerType = controllerType;
	}

	/**
	 *
	 * @return the rootPath
	 */
	public String getRootPath() {
		return rootPath;
	}

	/**
	 *
	 * @param rootPath the rootPath to set
	 */
	public void setRootPath(final String rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 *
	 * @return the subDir
	 */
	public String getSubDir() {
		return subDir;
	}

	/**
	 *
	 * @param subDir the subDir to set
	 */
	public void setSubDir(final String subDir) {
		this.subDir = subDir;
	}

	/**
	 *
	 * @return the sourceConfigName
	 */
	public String getSourceConfigName() {
		return sourceConfigName;
	}

	/**
	 *
	 * @param sourceConfigName the sourceConfigName to set
	 */
	public void setSourceConfigName(final String sourceConfigName) {
		this.sourceConfigName = sourceConfigName;
	}

	/**
	 *
	 * @return the targetConfigName
	 */
	public String getTargetConfigName() {
		return targetConfigName;
	}

	/**
	 *
	 * @param targetConfigName the targetConfigName to set
	 */
	public void setTargetConfigName(final String targetConfigName) {
		this.targetConfigName = targetConfigName;
	}

}
