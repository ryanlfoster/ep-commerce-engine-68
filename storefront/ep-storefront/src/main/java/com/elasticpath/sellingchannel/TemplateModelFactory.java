package com.elasticpath.sellingchannel;

import java.util.Map;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Warehouse;

/**
 * Creates the model hashmap to be used by the product templates to display information.
 */
public interface TemplateModelFactory {

	/**
	 * Creates the model map. This map can be returned from e.g. the referenceData() method
	 * of a Spring form controller.
	 *  
	 * @param shoppingCart The shopping cart to use.
	 * @param updatePage The name of the page to set on the ProductViewPage for update.
	 * @param cartItemId The id inside the page of this cart item.
	 * @param warehouse The warehouse to get inventory information from.
	 * @param catalog The catalog to use.
	 * @param product The product to get the model information about.
	 * @return The model map.
	 */
	Map<String, Object> createModel(ShoppingCart shoppingCart, String updatePage, Long cartItemId,
			Warehouse warehouse, Catalog catalog, StoreProduct product);
	
	/**
	 * Returns the name of the template associated with the current Product Type of the Product
	 * matched by {@code productCode}.
	 * 
	 * @param productCode The product code to look for.
	 * @param shoppingCart The shopping cart associated with the current customer.
	 * @return The web template name or null if not found.
	 */
	String getTemplateName(String productCode, ShoppingCart shoppingCart);
}
