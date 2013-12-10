package com.elasticpath.domain.catalogview;

/**
 * Limits results to a those displayable for a specified store. This filter is not designed
 * to be displayed in the storefront.
 */
public interface DisplayableFilter extends Filter<DisplayableFilter> {
	
	/**
	 * @return the code of the store for which to get displayable products
	 */
	String getStoreCode();
	
	/**
	 * Set the Code of the store for which to get displayable products.
	 *
	 * @param storeCode the Code of the store for which to get displayable products
	 */
	void setStoreCode(String storeCode);
}
