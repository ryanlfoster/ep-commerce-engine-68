package com.elasticpath.sfweb.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Retrieval strategy for retrieving a resource from the Assets folder.
 * e.g. retrieving by store specific location, with or without theme subfolders. 
 *
 */
public interface AssetResourceRetrievalStrategy {
	/**
	 * Resolve a resource URL according to implemented strategy.
	 * @param resourcePath the path to the resource
	 * @return a URL to the resource, could be null.
	 * @throws MalformedURLException if there is a problem creating the URL.
	 */
	URL resolveResource(final String resourcePath) throws MalformedURLException;
}
