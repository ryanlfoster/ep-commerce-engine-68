/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.contentspace;

import java.util.Map;

/**
 * A repository class that will contain all content wrappers in the system.
 */
public interface ContentWrapperRepository {

	/**
	 * Returns all content wrappers within the repository.
	 *
	 * @return all wrapper Id's mapped to content wrapper objects, null if there are none
	 */
	Map<String, ContentWrapper> getContentWrappers();
	
	/**
	 * Returns all content wrappers within the repository.
	 * @param forceReload refresh content wrapper repository 
	 * before return content wrappers if true.
	 * @return all wrapper Id's mapped to content wrapper objects, null if there are none
	 */
	Map<String, ContentWrapper> getContentWrappers(boolean forceReload);
	

	/**
	 * Finds a specific content wrapper with the specified Id.
	 *
	 * @param contentWrapperId the content wrapper Id to be searched for
	 * @return the content wrapper with the Id, or null if it was not found
	 */
	ContentWrapper findContentWrapperById(final String contentWrapperId);
	
}
