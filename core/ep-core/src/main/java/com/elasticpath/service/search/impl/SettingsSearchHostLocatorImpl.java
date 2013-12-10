package com.elasticpath.service.search.impl;

import com.elasticpath.service.search.SearchHostLocator;
import com.elasticpath.settings.SettingsReader;

/**
 * Implementation of SearchHostLocator that retrieves the search host location from the settings service.
 * If deployed in a multiple search server/cluster configuration, a master search host URL should be
 * defined in the settings service with a context of 'master'. On the master search server, this class should
 * be configured so that 'requiresMaster' is true so it will use the master search host URL for index writing.
 */
public class SettingsSearchHostLocatorImpl implements SearchHostLocator {
	
	private SettingsReader settingsReader;

	/** The property name of the value that holds the SOLR server URL. */
	private static final String SEARCH_SERVER_PROPERTY_KEY = "COMMERCE/SYSTEM/SEARCH/searchHost";

	/** 
	 * Context for obtaining the master search server URL. 
	 * This URL is optionally available depending on if search servers are deployed in a clustered configuration. 
	 **/
	private static final String MASTER_SEARCH_SERVER_CONTEXT = "master";
	
	/** Context for obtaining the default search server URL. This is used in every case except for the
	 * master search server if in a clustered configuration. */
	private static final String DEFAULT_SEARCH_SERVER_CONTEXT = "default";
	
	private boolean requiresMaster;
	
	/**
	 * Obtain the URL of the search host stored in the setting service.
	 * If multiple search servers are deployed in a cluster, a setting value with the context of 'master'
	 * should be set to define a different server URL for the master search server that writes to the indexes.
	 * 
	 * @return the URL string of the search host.
	 */
	public String getSearchHostLocation() {
		if (getRequiresMaster()) {
			return settingsReader.getSettingValue(SEARCH_SERVER_PROPERTY_KEY, MASTER_SEARCH_SERVER_CONTEXT).getValue();
		}
		return settingsReader.getSettingValue(SEARCH_SERVER_PROPERTY_KEY, DEFAULT_SEARCH_SERVER_CONTEXT).getValue();
	}
	
	/**
	 * Get the settings reader.
	 * 
	 * @return the <code>SettingsReader</code>
	 */
	public SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the settings service.
	 * 
	 * @param settingsReader the settingsService to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Set whether the master search server is required.
	 * A master search server URL is available optionally depending on if 
	 * search servers are deployed in a clustered configuration.
	 * 
	 * @param requiresMaster true if getting master server responsible for indexing in a cluster.
	 */
	public void setRequiresMaster(final boolean requiresMaster) {
		this.requiresMaster = requiresMaster;
	}

	/**
	 * @return true if the master server is required
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getRequiresMaster() {
		return requiresMaster;
	}

}
