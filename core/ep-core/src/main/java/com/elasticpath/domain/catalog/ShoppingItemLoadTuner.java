/**
 * Copyrighted to Elastic Path Software Inc, 2009.
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Load tuner for shopping item.
 */
public interface ShoppingItemLoadTuner extends LoadTuner {

	/**
	 * @return whether to load dependent items recursively via FetchGroup.
	 */
	Boolean isLoadingRecursiveDependentItems();

	/**
	 * @return whether to load quantity.
	 */
	Boolean isLoadingQuantity();
	
	/**
	 * @return whether to load parent item.
	 */
	Boolean isLoadingParentItem();

	/**
	 * @return whether to load product SKU.
	 */
	Boolean isLoadingProductSku();

	/**
	 * @return whether to load default association quantity.
	 */
	Boolean isLoadingDefaultAssociationQuantity();
	
	/**
	 * @return whether to load dependent items.
	 */
	Boolean isLoadingDependentItems();

	/**
	 * @return whether to load price.
	 */
	Boolean isLoadingPrice();
	
	/**
	 * Sets the loading option for dependent items.
	 * 
	 * @param flag true/false
	 */
	void setLoadingDependentItems(Boolean flag);

	/**
	 * Sets the loading option for dependent items recursively via Fetch Group.
	 * 
	 * @param flag true/false
	 */
	void setLoadingRecursiveDependentItems(Boolean flag);
	
	/**
	 * Sets the loading option for default association quantity.
	 * 
	 * @param flag true/false
	 */
	void setLoadingDefaultAssociationQuantity(Boolean flag);
	
	/**
	 * Sets the loading option for parent item.
	 * 
	 * @param flag true/false
	 */
	void setLoadingParentItem(Boolean flag);

	/**
	 * Sets the loading option for product SKU.
	 * 
	 * @param flag true/false
	 */
	void setLoadingProductSku(Boolean flag);

	/**
	 * Sets the loading option for quantity.
	 * 
	 * @param flag true/false
	 */
	void setLoadingQuantity(Boolean flag);
	
	/**
	 * Sets the loading option for price.
	 * 
	 * @param flag true/false
	 */
	void setLoadingPrice(Boolean flag);
}
