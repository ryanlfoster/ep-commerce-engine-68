package com.elasticpath.domain.catalogview.search.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * Default Advanced Search Request implementation.
 */
public class AdvancedSearchRequestImpl extends AbstractSearchRequestImpl implements AdvancedSearchRequest {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private transient FilterFactory advancedSearchFilterFactory;

	private FilterFactory getAdvancedSearchFilterFactory() {
		if (advancedSearchFilterFactory == null) {
			advancedSearchFilterFactory = getBean("advancedSearchFilterFactory");
		}
		return advancedSearchFilterFactory;
	}
	
	@Override
	protected void sanityCheck() {
		if (CollectionUtils.isEmpty(getFilters())) {
			throw new EpDomainException("Not initialized!");
		}
	}

	/**
	 * Compares this search request with the given search request.
	 *
	 * @param searchRequest the search request to compare
	 * @return 0 if this search request and the given search request has same key words and filters.
	 *         <p>
	 *         1 if this search request and the given search request has same key words, but has more filters.
	 *         <p>
	 *         -1 if this search request and the given search request has same key words, but has unmatching filters.
	 *         <p>
	 * @throws CatalogViewRequestUnmatchException when this search requet and the given search request have different key words
	 */
	@Override
	public int compare(final CatalogViewRequest searchRequest) throws CatalogViewRequestUnmatchException { //NOPMD

		if (searchRequest == null) {
			throw new EpDomainException("The search requst to compare cannot be null.");
		}
		
		if (!(searchRequest instanceof AdvancedSearchRequest)) {
			throw new EpDomainException("The request must be a search request to compare :"  + searchRequest.getClass());
		}

		return super.compare(searchRequest);
	}
	
	@Override
	public String getQueryString() {
		sanityCheck();
		final StringBuffer sbf = new StringBuffer();

		for (String key : getQueryProperties().keySet()) {
			if (sbf.length() > 0) {
				sbf.append(WebConstants.SYMBOL_AND);
			}
			sbf.append(key).append(WebConstants.SYMBOL_EQUAL).append(getQueryProperties().get(key));
		}

		return sbf.toString();
	}
	
	@Override
	public Map<String, String> getQueryProperties() {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		
		if (0 != getCategoryUid()) {
			properties.put(WebConstants.REQUEST_CATEGORY_ID, String.valueOf(getCategoryUid()));
		}

		if (getFilters().size() > 0) {
			final StringBuffer sbf = new StringBuffer();
			for (Filter< ? > filter : getFilters()) {
				sbf.append(filter.getId()).append(WebConstants.SPACE);
			}
			
			properties.put(WebConstants.REQUEST_FILTERS, sbf.toString().trim());
		}
		
		// keep the old values so that we don't modify anything
		SortOrder tmpOrder = getSortOrder();
		SortBy tmpType = getSortType();

		// make the featured product sorter the default one
		if (getSortType() == null) {
			setSortOrder(SortOrder.DESCENDING);
			setSortType(StandardSortBy.FEATURED_CATEGORY);
		}
		properties.put(WebConstants.REQUEST_SORTER, getSortTypeOrderString());
		setSortOrder(tmpOrder);
		setSortType(tmpType);
		
		return properties;
	}

	@Override
	public void addFilter(final Filter< ? > filter) {
		getFilters().add(filter);
	}
	
	@Override
	public List<Filter< ? >> getAdvancedSearchFilters() {
		Filter< ? > separator = getBean(ContextIdNames.ADVANCED_SEARCH_FILTERED_NAV_SEPARATOR_FILTER);
		if (getFilters().contains(separator)) {
			List<Filter< ? >> advSearchFilters = new ArrayList<Filter< ? >>();
			for (Filter< ? > filter : getFilters()) {
				if (filter.equals(separator)) {
					break;					 
				}
				advSearchFilters.add(filter);
			}
			return advSearchFilters;
		}
		return getFilters();
	}

	@Override
	protected List<Filter< ? >> createFilters(final String[] filterids, final Store store) {
		List<Filter< ? >> result = new ArrayList<Filter< ? >>(filterids.length);
		boolean encounteredSeparator = false;

		for (int i = 0; i < filterids.length; ++i) {
			if (!StringUtils.isEmpty(filterids[i])) {
				Filter< ? > filter;

				// only filters filters before the separator are specific to advanced search
				if (encounteredSeparator) {
					filter = getFilterFactory().getFilter(filterids[i], store);
				} else {
					filter = getAdvancedSearchFilterFactory().getFilter(filterids[i], store);
					encounteredSeparator = filter instanceof AdvancedSearchFilteredNavSeparatorFilter;
				}

				result.add(filter);
			}
		}
		return result;
	}
}