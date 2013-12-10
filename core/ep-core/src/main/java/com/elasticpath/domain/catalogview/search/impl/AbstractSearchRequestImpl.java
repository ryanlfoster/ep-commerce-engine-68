package com.elasticpath.domain.catalogview.search.impl;

import java.util.Iterator;
import java.util.List;

import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.impl.AbstractCatalogViewRequestImpl;

/**
 * An abstract class for search request.
 *
 */
public abstract class AbstractSearchRequestImpl extends AbstractCatalogViewRequestImpl {

	private static final long serialVersionUID = -9054494343154842042L;

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
	 * @throws CatalogViewRequestUnmatchException when this search request and the given search request have different key words
	 */
	public int compare(final CatalogViewRequest searchRequest) throws CatalogViewRequestUnmatchException {		
		if (getCategoryUid() != searchRequest.getCategoryUid()) {
			throw new CatalogViewRequestUnmatchException("Category Uid is different");
		}
		
		return compareFilters(searchRequest);
	}
	
	/**
	 *	Compares the filters to see if it matches the filters in <code>searchRequest</code>.
	 *	@param searchRequest The search request to compare against
	 *	@return 0 if the the filters match, -1 if <code>searchRequest</code> has more filters, 
	 *	and 1 otherwise.
	 */
	protected int compareFilters(final CatalogViewRequest searchRequest) {
		final List<Filter< ? >> filters = searchRequest.getFilters();
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
	 * Returns the query string.
	 *
	 * @return the query string.
	 */
	public abstract String getQueryString();

}
