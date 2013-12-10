/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalogview.browsing.impl;

import java.util.List;
import java.util.Locale;

import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Encapsulates the logic required to build a SEO url for a filter.
 */
@SuppressWarnings("PMD.AvoidStringBufferField")
public class FilterSeoUrl {
	private final StringBuffer pageFilters = new StringBuffer();
	
	private final StringBuffer pathFilters = new StringBuffer();
	
	private int pageNumber = -1;
	
	private final Locale locale;
	
	private final SortBy sortType;
	
	private final SortOrder sortOrder;
	
	private String fieldSeparator;


	/**
	 * Create an instance that filters will be added to later which will use
	 * the specified locale and sorter objects during the URL construction.
	 * 
	 * @param locale the locale the url fragments should be in
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param fieldSeparator the string used to separate the filename tokens.
	 */
	public FilterSeoUrl(final Locale locale, final SortBy sortType, final SortOrder sortOrder, final String fieldSeparator) {
		this.locale = locale;
		this.sortType = sortType;
		this.sortOrder = sortOrder;
		this.fieldSeparator = fieldSeparator;
	}	
	
	/**
	 * Create an instance that will use the specified locale
	 * and sorter objects during the URL construction
	 * and add all the specified filters (in the order provided).
	 * 
	 * @param locale the locale the url fragments should be in.
	 * @param filters the list of filters that should be included in this url,
	 *        more may be appended with {@link #addFilter(Filter)}.
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param fieldSeparator the string to use to separate the filename tokens.
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public FilterSeoUrl(
			final Locale locale, 
			final List<Filter< ? >> filters, 
			final SortBy sortType, 
			final SortOrder sortOrder,
			final String fieldSeparator) {
		this(locale, sortType, sortOrder, fieldSeparator);
		for (Filter< ? > filter : filters) {
			addFilter(filter);
		}
	}	
	
	
	/**
	 * Create an instance that will use the specified locale
	 * and sorter objects during the URL construction
	 * and add all the specified filters (in the order provided).
	 * 
	 * @param locale the locale the url fragments should be in.
	 * @param filters the list of filters that should be included in this url,
	 *        more may be appended with {@link #addFilter(Filter)}.
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param pageNumber the page number to include in the url.  This is
	 *        ignored if it is less than zero.
	 * @param fieldSeparator the string to use to separate the filename tokens.
	 */
	public FilterSeoUrl(final Locale locale, final List<Filter< ? >> filters, final SortBy sortType, final SortOrder sortOrder,
			final int pageNumber, final String fieldSeparator) {
		this(locale, filters, sortType, sortOrder, fieldSeparator);
		this.pageNumber = pageNumber;
	}
	
	/**
	 * Add a filter to the existing list of filters.  The filter will be
	 * represented in the correct place in the url, depending on it's type.
	 * 
	 * @param filter the filter to add to the url.
	 */
	public void addFilter(final Filter< ? > filter) {
		if (filter == null) {
			return;
		}

		if (pageFilters.length() != 0) {
			pageFilters.append(fieldSeparator);
		}
		pageFilters.append(filter.getSeoId());

		if (filter instanceof BrandFilter) {
			// Brand always comes at the start of the url
			pathFilters.insert(0, filter.getSeoName(locale) + '/');
		} else {
			// All other filters get appended at the end of the url
			pathFilters.append(filter.getSeoName(locale)).append('/');
		}
	}
	
	/**
	 * Return a string representation of the url with the current set of
	 * filters that have been added.
	 * 
	 * @return an seo representing the current state fo the object.
	 */
	public String asString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(pathFilters);
		buffer.append(pageFilters);
		appendPageNumberFragment(buffer);
		buffer.append(SeoConstants.SUFFIX);
		appendSorterExtension(buffer);
		return buffer.toString();
	}
	
	/**
	 * Returns the sorter extension to use in the URL, the default implementation
	 * is FeaturedProductSorterImpl and if this is selected then it isn't shown in
	 * the url.  i.e. a blank sorter extension means the Feature Products sorter
	 * will be used.
	 * @return
	 */
	private void appendSorterExtension(final StringBuffer buffer) {
		if (sortType != null && sortType != StandardSortBy.FEATURED_CATEGORY) {
			buffer.append('?').append(WebConstants.REQUEST_SORTER).append('=').append(
					SortUtility.constructSortTypeOrderString(sortType, sortOrder));
		}
	}
	
	
	private void appendPageNumberFragment(final StringBuffer buffer) {
		if (pageNumber < 0) {
			return;
		}
		buffer.append(fieldSeparator).append(SeoConstants.PAGE_NUMBER_PREFIX).append(pageNumber);
	}
	
	/**
	 * Set the field separator - used to encode the filename part of the url
	 * so that the specific product or category can be identified.
	 * 
	 * @param fieldSeparator the string to separate the filename part of the
	 *        url with.
	 */
	public void setFieldSeparator(final String fieldSeparator) {
		if (fieldSeparator == null || "".equals(fieldSeparator)) {
			throw new IllegalArgumentException("SEO field separator cannot be null or empty string");
		}
		this.fieldSeparator = fieldSeparator;
	}
}