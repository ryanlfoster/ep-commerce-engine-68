package com.elasticpath.domain.catalogview.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;

/**
 * This class is a filter implementation on the dummy filter to separate Advanced Search Filters from Filtered Nav Filters.
 */
public class AdvancedSearchFilteredNavSeparatorFilterImpl 
	extends AbstractFilterImpl<AdvancedSearchFilteredNavSeparatorFilter> implements AdvancedSearchFilteredNavSeparatorFilter {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	@Override
	public String getDisplayName(final Locale locale) {
		return getSeoId();
	}
	
	/**
	 * Returns the hash code.
	 *
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}

	/**
	 * Returns <code>true</code> if this filter equals to the given object.
	 *
	 * @param object the object to compare
	 * @return <code>true</code> if this filter equals to the given object.
	 */
	@Override
	public boolean equals(final Object object) {
		if (object instanceof AdvancedSearchFilteredNavSeparatorFilter) {
			return true;
		}
		return false;
	}

	/**
	 * Compares this object with the specified object for ordering.
	 *
	 * @param other the given object
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 * @throws EpDomainException in case the given object is not a <code>BrandFilter</code>
	 */
	public int compareTo(final AdvancedSearchFilteredNavSeparatorFilter other) throws EpDomainException {
		if (equals(other)) {
			return 0;
		}
		return 1;
	}

	/**
	 * Returns the SEO url of the filter with the given locale. Currently the display name will be used as the seo url.
	 *
	 * @param locale the locale
	 * @return the SEO url of the filter with the given locale.
	 */
	public String getSeoName(final Locale locale) {
		return getDisplayName(locale);
	}

	/**
	 * Returns the SEO identifier of the filter with the given locale.
	 *
	 * @return the SEO identifier of the filter with the given locale.
	 */
	public String getSeoId() {
		return getId();
	}	
	
	
	@Override
	public void initialize(final Map<String, Object> properties) {
		setId(SeoConstants.SEPARATOR_BETWEEN_ADV_SEARCH_AND_FITERED_NAV_FILTERS);
	}	
	
	@Override
	public Map<String, Object> parseFilterString(final String filterIdStr) {
		return new HashMap<String, Object>();
	}
}
