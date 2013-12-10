package com.elasticpath.domain.catalogview;

import java.util.List;

import com.elasticpath.domain.EpDomain;

/**
 * Represents a list of catalog view results that a user has performed. The catalog view results are stored as a stack. The catalog view requests are
 * getting more and more specific from the bottom to the top.
 * <p>
 * E.g.
 * <p>
 * Top => Search Result of request : keywords "digital camera", belongs to canon category, price is between 250 - 300
 * <p>
 * 2nd => Search Result of request : keywords "digital camera", belongs to canon category, price is less than 500 <br/>
 * <p>
 * 3rd => Search Result of request : keywords "digital camera", belongs to canon category <br/>Bottom => Search Result of request : keywords "digital
 * camera"
 */
public interface CatalogViewResultHistory extends EpDomain {
	/**
	 * Add the given catalog view request and returns a catalog view result. If we can find similear catalog view request in the history stack,
	 * products in the returned catalog view result will be populated so you don't need to load them again.
	 *
	 * @param request the catalog view request to add
	 * @return a catalog view result of the given catalog view request
	 */
	CatalogViewResult addRequest(CatalogViewRequest request);

	/**
	 * Returns the size of catalog view results.
	 *
	 * @return the size of catalog view results
	 */
	int size();

	/**
	 * Returns the last catalog view result.
	 *
	 * @return the last catalog view result
	 */
	CatalogViewResult getLastResult();

	/**
	 * Returns the catalog view results as a <code>List</code>. The most recent catalog view result is at the tail.
	 *
	 * @return the catalog view result as a <code>List</code>.
	 */
	List<CatalogViewResult> getResultList();
}
