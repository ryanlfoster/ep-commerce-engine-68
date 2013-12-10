package com.elasticpath.domain.catalogview.browsing.impl;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.impl.AbstractCatalogViewRequestImpl;
import com.elasticpath.domain.catalogview.impl.BreadcrumbsImpl;

/**
 * Represents a default implementation of <code>BrowsingRequest</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class BrowsingRequestImpl extends AbstractCatalogViewRequestImpl implements BrowsingRequest {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * <p>Creates and returns a query string.</p>
	 * <p>This implementation builds a query string consisting of the category ID followed by 
	 * the IDs of any filters to be applied, followed by a string representing the sort
	 * order (specifying the order in which the results should be returned, e.g. descending
	 * order by featured category).</p>
	 * <p>Calls {#link {@link #getCategoryUid()} for the categoryId portion of the query.</p>
	 * <p>Calls {@link #createFiltersQueryString()} for the filters portion of the query.</p>
	 * <p>Calls {@link #createSortingQueryString()} for the sorting portion of the query. 
	 * If a Sort Type (e.g. "sort by featured category") has not been set, the sort order returned
	 * will be "Descending", and the sort type returned will be "FeaturedCategory", but this
	 * instance's sort order will not be changed if set.</p>
	 *
	 * @return the query string.
	 * @throws EpDomainException if the categoryUid has not been set
	 */
	public String getQueryString() {
		final StringBuffer sbf = new StringBuffer();
		
		sbf.append(createCategoryQueryString());
		
		String filtersQueryString = createFiltersQueryString();
		if (!StringUtils.isBlank(filtersQueryString)) {
			sbf.append(WebConstants.SYMBOL_AND);
			sbf.append(createFiltersQueryString());
		}

		sbf.append(WebConstants.SYMBOL_AND);
		sbf.append(createSortingQueryString());
		
		return sbf.toString();
	}
	
	/**
	 * Creates the part of the query string that specifies the categoryUid.
	 * Calls {@link #getCategoryUid()}.
	 * @return the part of the query string that specifies the category UID.
	 * @throws EpDomainException if the category UID has not been set.
	 */
	String createCategoryQueryString() {
		sanityCheck();
		return new StringBuffer().append(WebConstants.REQUEST_CID).append(WebConstants.SYMBOL_EQUAL).append(this.getCategoryUid()).toString();
	}
	
	/**
	 * Creates the part of the query string that involves Filter IDs.
	 * Calls {@link #getFilters()} to retrieve the filters.
	 * @return the Filter IDs part of the query string
	 */
	String createFiltersQueryString() {
		StringBuffer sbf = new StringBuffer();
		List<String> filterIds = this.getFilterIdList();
		if (!filterIds.isEmpty()) {
			sbf.append(WebConstants.REQUEST_FILTERS).append(WebConstants.SYMBOL_EQUAL);
			sbf.append(StringUtils.join(filterIds, WebConstants.SYMBOL_PLUS));
		}
		return sbf.toString(); 
	}
	
	/**
	 * Creates a new empty request.
	 *
	 * @return a new empty request
	 */
	protected AbstractCatalogViewRequestImpl createRequest() {
		// We don't need to use ElasticPath.getBean() here.
		// An implementation class knows itself, hence it's OK to create a new instance directly.
		// By using this way, we can create a new browsing request quicker because all filters and sorter can be inherited.
		return new BrowsingRequestImpl();
	}

	/**
	 * Compares this search request with the given search request.
	 *
	 * @param browsingRequest the search request to compare
	 * @return 0 if this search request and the given search request has same key words and filters.
	 *         <p>
	 *         1 if this search request and the given search request has same key words, but has more filters.
	 *         <p>
	 *         -1 if this search request and the given search request has same key words, but has unmatching filters.
	 *         <p>
	 * @throws CatalogViewRequestUnmatchException when this search requet and the given search request have different key words
	 */
	public int compare(final CatalogViewRequest browsingRequest) throws CatalogViewRequestUnmatchException {
		if (browsingRequest == null) {
			throw new EpDomainException("The search requst to compare cannot be null.");
		}

		if (!(browsingRequest instanceof BrowsingRequest)) {
			throw new EpDomainException("The request must be a search request to compare :" + browsingRequest.getClass());
		}

		if (this.getCategoryUid() != (browsingRequest.getCategoryUid())) {
			throw new CatalogViewRequestUnmatchException("Key words is differenct.");
		}

		final List<Filter< ? >> filters = browsingRequest.getFilters();
		final Iterator<Filter< ? >> iterator1 = this.getFilters().iterator();
		final Iterator<Filter< ? >> iterator2 = filters.iterator();
		while (true) {
			if (!iterator1.hasNext() && !iterator2.hasNext()) {
				return 0;
			}
			if (!iterator1.hasNext()) {
				return -1;
			}
			if (!iterator2.hasNext()) {
				return 1;
			}

			final Filter< ? > filter1 = iterator1.next();
			final Filter< ? > filter2 = iterator2.next();
			if (filter1.equals(filter2)) {
				continue;
			}

			return -1;
		}
	}

	/**
	 * Do the sanity check.
	 */
	@Override
	protected void sanityCheck() {
		if (this.getCategoryUid() == 0) {
			throw new EpDomainException("Not initialized!");
		}
	}

	/**
	 * Returns the SEO(Search Engine Optimized) url of this request.
	 *
	 * @return the SEO url of this request.
	 */
	public String getSeoUrl() {
		return getSeoUrl(-1);
	}

	/**
	 * Returns the SEO(Search Engine Optimized) url of this request.
	 *
	 * @param pageNumber the page number
	 * @return the SEO(Search Engine Optimized) url of this request.
	 */
	public String getSeoUrl(final int pageNumber) {
		sanityCheck();
		
		SeoUrlBuilder urlBuilder = getSeoUrlBuilder();
		
		return urlBuilder.filterSeoUrl(getLocale(), getFilters(), getSortType(), getSortOrder(), pageNumber);
	}

	/**
	 * Composes and returns a  page's breadcrumbs (derived from the set of
	 * filters used to access this page).
	 * 
	 * For each filter a FilterSeoUrlImpl is provided which details:
	 * <ul>
	 *   <li>it's display name</li>
	 *   <li>a url for that specific filter (to cut out filter's below that one</li>
	 *   <li>a url for the full the full set of filters without that filter.</li>
	 * </ul>
	 * 
	 * The urls have the full path to the page (used by search engines) and the filename (used when requests come
	 * back into the system so we know what to display).
	 *
	 * @return a list of <code>FilterSeoUrl</code>s
	 */
	public List<Breadcrumb> getFilterSeoUrls() {
		SeoUrlBuilder urlBuilder = getSeoUrlBuilder();
		return new BreadcrumbsImpl(getLocale(), getSortType(), getSortOrder(), getFilters(), urlBuilder).asList();
	}

	/**
	 * Composes and returns a title for the browsing page. A title will follow this structure:<br>
	 * <tt>Brand Name</tt> in the correct langauge, only if it exists<br>
	 * <tt>Category SEO Title</tt> in the correct langauge<br>
	 * <tt>Price Filter</tt> with currency symbol, only show the lowest applied filter<br>
	 * e.g. Kodak - Digital Cameras - $120 - $140<br>
	 * e.g. Digital Cameras - $200 - $400<br>
	 * e.g. Digital Cameras<br>
	 *
	 * @param category the category
	 * @return a title for the browsing page
	 */
	public String getTitle(final Category category) {
		final Locale locale = this.getLocale();
		final List<Filter< ? >> filters = getFilters();

		String brandFilterName = null;
		String priceFilterName = null;

		Map<String, String> attributeMap = new LinkedHashMap<String, String>();

		for (int i = 0; i < filters.size(); i++) {
			final Filter< ? > filter = filters.get(i);
			// There should be only one brand filter in the filters list.
			if (filter instanceof BrandFilter) {
				brandFilterName = filter.getDisplayName(locale);
			} else if (filter instanceof PriceFilter) {
				// The last price filter is the lowest one applied.
				priceFilterName = filter.getDisplayName(locale);
			} else if (filter instanceof AttributeFilter) {
				// Trace all the attribute filters.
				attributeMap.put(((AttributeFilter< ? >) filter).getAttributeKey(), filter.getDisplayName(locale));
			}
		}

		final StringBuffer sbf = new StringBuffer();
		if (brandFilterName != null) {
			sbf.append(brandFilterName).append(" - ");
		}
		LocaleDependantFields dependantFields = category.getLocaleDependantFields(locale);
		if (dependantFields.getTitle() == null) {
			sbf.append(dependantFields.getDisplayName());
		} else {
			sbf.append(dependantFields.getTitle());
		}
		if (priceFilterName != null) {
			sbf.append(" - ").append(priceFilterName);
		}

		// Add all the attribute filters to the title.
		for (String attributeFilter : attributeMap.keySet()) {
			sbf.append(" - ").append(attributeMap.get(attributeFilter));
		}

		return sbf.toString();
	}

	/**
	 * Get seo url builder from the StoreSeoUrlBuilderFactory.
	 * @return seoUrlBuilder the store specific SeoUrlBuilder
	 */
	protected SeoUrlBuilder getSeoUrlBuilder() {
		StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory = getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
		return storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();		
	}	
}
