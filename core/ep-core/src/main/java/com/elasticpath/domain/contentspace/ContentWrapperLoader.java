/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.contentspace;

import java.util.Map;


/**
 * A class for loading content wrappers.
 */
public interface ContentWrapperLoader {

	/**
	 * A method which loads content wrappers and returns a collection of successfully loaded
	 * wrappers.
	 * 
	 * @return a map of wrapper Ids to wrappers objects
	 */
	Map<String, ContentWrapper> loadContentWrappers();
}
