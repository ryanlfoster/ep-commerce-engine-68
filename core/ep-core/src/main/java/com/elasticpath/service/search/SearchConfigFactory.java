/**
 * 
 */
package com.elasticpath.service.search;

import com.elasticpath.domain.misc.SearchConfig;

/**
 * Methods for getting a search configuration object.
 */
public interface SearchConfigFactory {

	/**
	 * Gets the search configuration for the given <code>accessKey</code>. If no configuration is found for the given access key, returns the
	 * default configuration.
	 * 
	 * @param accessKey the key used to access a particular search configuration
	 * @return a search configuration
	 */
	SearchConfig getSearchConfig(final String accessKey);
	
}
