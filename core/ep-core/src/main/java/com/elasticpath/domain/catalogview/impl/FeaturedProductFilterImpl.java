package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.catalogview.FeaturedProductFilter;

/**
 * Special filter designed to filter out only those products that are featured. Not intended for
 * displayable use.
 */
public class FeaturedProductFilterImpl extends AbstractFilterImpl<FeaturedProductFilter> implements FeaturedProductFilter {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private Long categoryUid;

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
	public int compareTo(final FeaturedProductFilter obj) {
		return 0;
	}

	/**
	 * Returns the category UID of the category to get featured products for (the root category).
	 * 
	 * @return the category UID of the category to get featured product for
	 */
	public Long getCategoryUid() {
		return categoryUid;
	}

	/**
	 * Sets the category UID of the category to get featured products for (the root category).
	 * 
	 * @param categoryUid the category UID of the category to get featured product for
	 */
	public void setCategoryUid(final Long categoryUid) {
		this.categoryUid = categoryUid;
	}

	@Override
	public String getSeoId() {
		return getCategoryUid().toString();
	}

	@Override
	public void initialize(final Map<String, Object> properties) {
		this.categoryUid = (Long) properties.get("categoryUid");
	}

	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		Map<String, Object> tokenMap = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(filterIdStr)) {
			tokenMap.put("categoryUid", Long.parseLong(filterIdStr));
		}
		return tokenMap;
	}
}
