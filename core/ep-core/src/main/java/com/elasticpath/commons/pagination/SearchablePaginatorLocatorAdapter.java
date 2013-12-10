package com.elasticpath.commons.pagination;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides an interface to {@code PaginatorImpl} which presents a {@code SearchablePaginatorLocator}
 * as a {@code PaginatorLocator}.
 *
 * @param <T> The model class to use.
 */
public class SearchablePaginatorLocatorAdapter<T>  implements PaginatorLocator<T> {
	private List<SearchCriterion> searchCriteria = new ArrayList<SearchCriterion>();
	private SearchablePaginatorLocator<T> searchablePaginatorLocator;

	/**
	 * 
	 * @param searchCriteria The search criteria to set which modifies the coupons returned.
	 */
	public void setSearchCriteria(final List<SearchCriterion> searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	/**
	 * @return the searchCriteria
	 */
	protected List<SearchCriterion> getSearchCriteria() {
		return searchCriteria;
	}
	
	/**
	 * 
	 * @param paginatorLocator The searchable paginator locator to use.
	 */
	public void setSearchablePaginatorLocator(final SearchablePaginatorLocator<T> paginatorLocator) {
		this.searchablePaginatorLocator = paginatorLocator;
	}
	
	@Override
	public List<T> findItems(
			final Page<T> unpopulatedPage, final String objectId) {
		return searchablePaginatorLocator.findItems(unpopulatedPage, objectId, getSearchCriteria());
	}

	@Override
	public long getTotalItems(final String objectId) {
		return searchablePaginatorLocator.getTotalItems(getSearchCriteria(), objectId);
	}
}
