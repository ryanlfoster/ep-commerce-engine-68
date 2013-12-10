package com.elasticpath.domain.catalogview.impl;

import java.util.Locale;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;

/**
 * This is the default implementation of <code>FilterOption</code>.
 *
 * @param <T> the type of filter
 */
public class FilterOptionImpl<T extends Filter<T>> extends AbstractEpDomainImpl implements FilterOption<T> {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private int hitsNumber;

	private T filter;

	private String seoUrl;

	private String queryString;

	/**
	 * Returns the number of hits in the filter option.
	 *
	 * @return the number of hits in the filter option.
	 */
	public int getHitsNumber() {
		return this.hitsNumber;
	}

	/**
	 * Sets the number of hits in under the filter option.
	 *
	 * @param hitsNumber the hits number to set
	 */
	public void setHitsNumber(final int hitsNumber) {
		this.hitsNumber = hitsNumber;
	}

	/**
	 * Returns the display name of the given locale.
	 *
	 * @param locale the locale
	 * @return the display name of the given locale.
	 */
	public String getDisplayName(final Locale locale) {
		if (this.filter == null) {
			throw new EpDomainException("Not initialized correctly.");
		}
		return this.filter.getDisplayName(locale);
	}

	/**
	 * Returns the query string.
	 *
	 * @return the query string.
	 */
	public String getQueryString() {
		if (queryString == null) {
			throw new EpDomainException("Not initialized correctly.");
		}
		return queryString;
	}

	/**
	 * Sets the query string corresponding to this filter option.
	 *
	 * @param queryString the query string for this filter option.
	 */
	public void setQueryString(final String queryString) {
		this.queryString = queryString;
	}


	/**
	 * Sets the search filter.
	 *
	 * @param filter the search filter.
	 */
	public void setFilter(final T filter) {
		if (this.filter != null) {
			throw new EpDomainException("Search filter can only be set once.");
		}
		this.filter = filter;
	}

	/**
	 * Gets the search filter.
	 *
	 * @return the search filter
	 */
	public T getFilter() {
		return filter;
	}

	/**
	 * Sets the SEO(Search Engine Optimized) url for this filter option.
	 *
	 * @param seoUrl the seo url for this filter option.
	 */
	public void setSeoUrl(final String seoUrl) {
		this.seoUrl = seoUrl;
	}

	/**
	 * Returns the SEO(Search Engine Optimized) url for this filter option.
	 *
	 * @return the SEO(Search Engine Optimized) url for this filter option.
	 */
	public String getSeoUrl() {
		if (seoUrl == null) {
			return queryString;
		}
		return seoUrl;
	}
}
