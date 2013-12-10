package com.elasticpath.domain.misc;

import java.util.Map;


/**
 * Holds configuration data about searching. This interface should <i>only</i> be used
 * when debugging/loading <code>SearchConfig</code>.
 */
public interface SearchConfigInternal extends SearchConfig {
	/**
	 * Gets the map of boost values for this configuration. The map holds a field name to a float
	 * boost value map.
	 * 
	 * @return the boost map for searches
	 */
	Map<String, Float> getBoostValues();
	
	/**
	 * Sets the map of boost values for this configuration.
	 *  
	 * @param boostValues the map of values
	 */
	void setBoostValues(Map<String, Float> boostValues);
	
	/**
	 * @param host URL string for the search server
	 */
	void setSearchHost(final String host);
}
