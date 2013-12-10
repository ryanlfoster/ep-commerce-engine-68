package com.elasticpath.domain.catalogview.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.elasticpath.domain.catalogview.Breadcrumbs;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.CatalogViewRequest.Breadcrumb;
import com.elasticpath.domain.catalogview.impl.AbstractCatalogViewRequestImpl.BreadcrumbImpl;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;


/**
 * A list of navigational breadcrumbs to a specific catalog page, the result of
 * viewing the catalog using a set of filters.
 * 
 * Each crumb provides:
 * <ul>
 *   <li>it's display name</li>
 *   <li>a url to itself (to cut out any crumbs that follows it</li>
 *   <li>a url for the full the full set of filters without that filter.</li>
 * </ul>
 */
 public class BreadcrumbsImpl implements Breadcrumbs {
	
	private final List<Filter< ? >> filters;
	
	private final Locale locale;
	
	private final SortBy sortType;
	
	private final SortOrder sortOrder;
	
	private final SeoUrlBuilder urlBuilder;
	
	/**
	 * Creates an instance that will create breadcrumbs in the specified
	 * locale, with the urls using the specified sorter.  The crumbs
	 * themselves are derived from the filters passed in.
	 * 
	 * @param locale the locale the url fragments should be in (category names,
	 *        brand names, etc).
	 * @param sortType the type of sorting to perform
	 * @param sortOrder the order of the sorting
	 * @param filters the filters that need to be reflected in the breadcrumbs
	 *        in the order they should be applied.
	 * @param urlBuilder the urlbuilder to use to create the urls.
	 */
	public BreadcrumbsImpl(
			final Locale locale, 
			final SortBy sortType, 
			final SortOrder sortOrder, 
			final List<Filter< ? >> filters,
			final SeoUrlBuilder urlBuilder) {
		this.locale = locale;
		this.filters = filters;
		this.sortType = sortType;
		this.sortOrder = sortOrder;
		this.urlBuilder = urlBuilder;
	}
	
	/**
	 * Return all the breadcrumb objects in the order they should be displayed.
	 * 
	 * @return the breadcrumbs in the order they should be displayed.
	 */
	public List<Breadcrumb> asList() {
		List<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
		
		List<Filter< ? >> tempFilters = new ArrayList<Filter< ? >>();
		for (Filter< ? > filter : filters) {
			
			tempFilters.add(filter);
			
			final Breadcrumb filterSeoUrl = new BreadcrumbImpl();
			filterSeoUrl.setDisplayName(filter.getDisplayName(locale));
			filterSeoUrl.setUrlFramgent(urlBuilder.filterSeoUrl(locale, tempFilters, sortType, sortOrder, 1));
			filterSeoUrl.setUrlFragmentWithoutThisCrumb(createUrlWithoutFilter(filter));
			
			crumbs.add(filterSeoUrl);
		}
		
		
		return crumbs;
	}
	
	private String createUrlWithoutFilter(final Filter< ? > filterToExclude) {
		if (filters.get(0) == filterToExclude) {
			// Without this filter then there are no breadcrumbs.
			return "";
		}
		
		List<Filter < ? >> tempFilters = new ArrayList<Filter< ? >>();
		for (Filter< ? > filter : filters) {
			if (filter != filterToExclude) {
				tempFilters.add(filter);
			}
		}
			
		return urlBuilder.filterSeoUrl(locale, tempFilters, sortType, sortOrder, -1);
	}
	
}