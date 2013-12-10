package com.elasticpath.service.catalogview;

/**
 * Paginates. 
 */
public interface PaginationService {

	/**
	 * Calculate last page number.
	 * @param numberOfResults are the number of results returned
	 * @param storeCode Store code.
	 * @return the last page number
	 */
	int getLastPageNumber(final int numberOfResults, final String storeCode);
	
	/**
	 * Number of items per page.
	 * @param storeCode Store code.
	 * @return number of items per page.
	 */
	int getNumberOfItemsPerPage(final String storeCode);
}
