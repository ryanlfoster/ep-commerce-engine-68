package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide catalog product view service.
 */
public interface ProductViewService {
	/**
	 * Returns the StoreProduct with the given product code. Return null if no
	 * matching product exists. You can give a product load tuner to fine
	 * control what data get populated of the returned product.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the
	 * returned product.
	 * 
	 * @param productId
	 *            the product id. This can be a guid (product code) or a uid.
	 *            The id will be assumed to be a GUID first.
	 * @param loadTuner the product load tuner
	 * @param shoppingCart
	 *            the shopping cart, give <code>null</code> if you don't have it.
	 * @return the product if a product with the given code exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 * @deprecated Call {@link #getProduct(String, StoreProductLoadTuner, Store)} instead.
	 */
	@Deprecated
	StoreProduct getProduct(String productId, StoreProductLoadTuner loadTuner, ShoppingCart shoppingCart) throws EpServiceException;
	
	/**
	 * Returns the StoreProduct with the given product code. Return null if no
	 * matching product exists. You can give a product load tuner to fine
	 * control what data get populated of the returned product.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the
	 * returned product.
	 * 
	 * @param productCode the product code.
	 * @param loadTuner the product load tuner
	 * @param store the store
	 * @return the product if a product with the given code exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	StoreProduct getProduct(String productCode, StoreProductLoadTuner loadTuner, Store store) throws EpServiceException;
	
	/**
	 * Gets the product's parent folder with fetched fields which by default are omitted (like parent folder).  
	 * 
	 * @param product the product which category has to be returned
	 * @return the product's parent category
	 */
	Category getProductCategory(Product product);

}
