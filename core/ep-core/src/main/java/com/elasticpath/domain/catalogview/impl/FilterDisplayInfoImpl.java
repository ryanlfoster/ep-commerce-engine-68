/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.impl;

import com.elasticpath.domain.catalogview.FilterDisplayInfo;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * The object which contains the display info for the range filter.
 * The display info is defined with the range value in filtered navigation configuration.
 */
public class FilterDisplayInfoImpl extends AbstractEpDomainImpl implements FilterDisplayInfo {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String displayName;

	private String seoName;

	private String seoId;

	/**
	 * Get the display name.
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display name.
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Get the seo id.
	 * @return the seoId
	 */
	public String getSeoId() {
		return seoId;
	}

	/**
	 * Set the seo id.
	 * @param seoId the seoId to set
	 */
	public void setSeoId(final String seoId) {
		this.seoId = seoId;
	}

	/**
	 * Get the seo name.
	 * @return the seoName
	 */
	public String getSeoName() {
		return seoName;
	}

	/**
	 * Set the seo name.
	 * @param seoName the seoName to set
	 */
	public void setSeoName(final String seoName) {
		this.seoName = seoName;
	}
}
