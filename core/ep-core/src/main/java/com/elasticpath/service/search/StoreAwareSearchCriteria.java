/**
 * 
 */
package com.elasticpath.service.search;

import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Search Criteria that may be store-aware (e.g. Product searches) will implement this interface.
 */
public interface StoreAwareSearchCriteria extends SearchCriteria {

	/**
	 * @return the store code
	 */
	String getStoreCode();
	
	/**
	 * Set the store code. This implementation does not set the store UID.
	 * @param storeCode the store code
	 */
	void setStoreCode(final String storeCode);
	
}
