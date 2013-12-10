/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.remote;

import java.util.Map;
import java.util.Properties;

import com.elasticpath.service.EpService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provides the contents of various configuration files as Strings.
 */
public interface ResourceRetrievalService extends EpService {


    /**
     * Returns Map of property files.
     * Maps filename -> (property-name -> property-value)
     * @return Map of property files.
     * @throws EpServiceException if something is wrong.
     */

	Map<String, Properties> getProperties() throws EpServiceException;

	/**
	 * Gets the entire search-config configuration file as a String.
	 *
	 * @return the search-config.xml configuration file
	 * @throws EpServiceException in case of any errors
	 */
	String getSearchConfig() throws EpServiceException;
}
