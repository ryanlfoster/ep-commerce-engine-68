/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation;

/**
 * A FilteredNavigationConfigurationLoader is responsible for loading Filtered
 * Navigation Configuration (FNC) data for a particular Store from a persistent layer 
 * and providing a populated FNC object.
 */
public interface FilteredNavigationConfigurationLoader {

	/**
	 * Loads FilteredNavigationConfiguration (FNC) for a Store represented by the given Store Code.
	 * @param storeCode the code representing the Store for which FNC should be loaded
	 * @return the Filtered Navigation Configuration for the given store, or the default FNC if it cannot be found
	 * for the specified store. 
	 */
	FilteredNavigationConfiguration loadFilteredNavigationConfiguration(final String storeCode);
	
	/**
	 * Getter.
	 * The text used for separating values from price ranges or attribute ranges
	 * @return the inTokenSeparator
	 */
	String getSeparatorInToken();
}
