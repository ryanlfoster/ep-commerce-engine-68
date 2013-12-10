package com.elasticpath.domain.catalogview.sitemap;

import com.elasticpath.domain.catalogview.CatalogViewRequest;

/**
 * Represents a request for a certain section of the sitemap.
 */	
public interface SitemapRequest extends CatalogViewRequest {

	/**
	 * Returns the maximum return number specified.
	 *
	 * @return the maximum return number
	 */
	int getMaxReturnNumber();

	/**
	 * Sets the maximum return number.
	 *
	 * @param maxReturnNumber the max return number to set
	 */
	void setMaxReturnNumber(final int maxReturnNumber);

	/**
	 * Returns the brand uid specified in the request.
	 * 
	 * @return the brand uid
	 */
	long getBrandUid();

	/**
	 * Sets the brand uid.
	 * 
	 * @param brandUid the brand uid to set
	 */
	void setBrandUid(final long brandUid);

}