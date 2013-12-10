/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview;

import com.elasticpath.domain.EpDomain;

/**
 * The object contains the localized display info for the range filter.
 */
public interface FilterDisplayInfo extends EpDomain {
	/**
	 * Get the display name.
	 *
	 * @return the displayName
	 */
	String getDisplayName();

	/**
	 * Set the display name.
	 *
	 * @param displayName the displayName to set
	 */
	void setDisplayName(final String displayName);

	/**
	 * Get the seo id.
	 *
	 * @return the seoId
	 */
	String getSeoId();

	/**
	 * Set the seo id.
	 *
	 * @param seoId the seoId to set
	 */
	void setSeoId(final String seoId);

	/**
	 * Get the seo name.
	 *
	 * @return the seoName
	 */
	String getSeoName();

	/**
	 * Set the seo name.
	 *
	 * @param seoName the seoName to set
	 */
	void setSeoName(final String seoName);

}
