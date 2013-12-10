/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.sitemap.impl;

import com.elasticpath.domain.catalogview.browsing.impl.BrowsingRequestImpl;
import com.elasticpath.domain.catalogview.sitemap.SitemapRequest;

/**
 * Default implementation of <code>SitemapRequest</code>.
 */
public class SitemapRequestImpl extends BrowsingRequestImpl implements SitemapRequest {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private int maxReturnNumber;

	private long brandUid;

	/**
	 * Returns the maximum return number specified.
	 * 
	 * @return the maximum return number
	 */
	public int getMaxReturnNumber() {
		return maxReturnNumber;
	}

	/**
	 * Sets the maximum return number.
	 * 
	 * @param maxReturnNumber the max return number to set
	 */
	public void setMaxReturnNumber(final int maxReturnNumber) {
		this.maxReturnNumber = maxReturnNumber;
	}

	/**
	 * Returns the brand uid specified in the request.
	 * 
	 * @return the brand uid
	 */
	public long getBrandUid() {
		return brandUid;
	}

	/**
	 * Sets the brand uid.
	 * 
	 * @param brandUid the brand uid to set
	 */
	public void setBrandUid(final long brandUid) {
		this.brandUid = brandUid;
	}

}
