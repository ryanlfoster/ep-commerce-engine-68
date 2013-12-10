/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation;

import java.io.InputStream;

/**
 * Parses the persistent representation of the intelligent-browsing configuration.
 */
public interface FilteredNavigationConfigurationParser {

	/**
	 * Parses the given configuration document and uses it to populate the given configuration object.
	 * @param configurationStream the input stream containing the configuration
	 * @param configurationObject the configuration object to populate
	 */
	void parse(InputStream configurationStream, FilteredNavigationConfiguration configurationObject);
	
}
