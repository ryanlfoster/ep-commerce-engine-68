package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.catalogview.DisplayableFilter;

/**
 * Limits results to a those displayable for a specified store.
 */
public class DisplayableFilterImpl extends AbstractFilterImpl<DisplayableFilter> implements DisplayableFilter {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String storeCode;

	/**
	 * Returns the empty string.
	 * 
	 * @param locale a locale
	 * @return the empty string
	 */
	public String getDisplayName(final Locale locale) {
		return "";
	}

	/**
	 * Returns the empty string.
	 * 
	 * @param locale a locale
	 * @return the empty string
	 */
	public String getSeoName(final Locale locale) {
		return "";
	}

	/**
	 * Returns zero.
	 * 
	 * @param obj an object
	 * @return zero
	 */
	public int compareTo(final DisplayableFilter obj) {
		return 0;
	}
	
	/**
	 * @return the code of the store for which to get displayable products
	 */
	@Override
	public String getStoreCode() {
		return this.storeCode;
	}
	
	/**
	 * Set the Code of the store for which to get displayable products.
	 *
	 * @param storeCode the Code of the store for which to get displayable products
	 */
	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * No SEO Id used for this filter.
	 * 
	 * @return an empty string
	 */
	public String getSeoId() {
		return StringUtils.EMPTY;
	}

	@Override
	public void initialize(final Map<String, Object> properties) {
		// no initialization required for this filter.
	}

	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		return new HashMap<String, Object>();
	}

}
