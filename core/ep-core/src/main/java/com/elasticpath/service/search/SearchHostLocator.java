package com.elasticpath.service.search;

/**
 * Interface that provides methods for receiving the location of the search host.
 */
public interface SearchHostLocator {

	/**
	 * @return string URL location of the search host.
	 */
	String getSearchHostLocation();
}
