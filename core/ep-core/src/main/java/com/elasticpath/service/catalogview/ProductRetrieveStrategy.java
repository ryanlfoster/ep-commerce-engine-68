package com.elasticpath.service.catalogview;

import java.util.Collection;
import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.service.EpService;

/**
 * Provide an interface of the strategy to retrieve a list of products.
 */
public interface ProductRetrieveStrategy extends EpService {
	
	/**
	 * Retrieve a <code>Product</code> with the given product uid. Return <code>null</code> if
	 * a product with the given uid doesn't exist.
	 * <p>
	 * 
	 * @param productUid a product uid
	 * @param productLoadTuner the product load tuner
	 * @return a <code>Product</code> with the given product uid.
	 */
	Product retrieveProduct(long productUid, final LoadTuner productLoadTuner);
	
	/**
	 * Retrieve a list of <code>Product</code> of the given product uids.
	 * 
	 * @param productUids a collection of product uids
	 * @param productLoadTuner the product load tuner
	 * @return a list of <code>Product</code>s.
	 */
	List<Product> retrieveProducts(Collection<Long> productUids, LoadTuner productLoadTuner);

	/**
	 * Retrieve a <code>Product</code> with the given product code. Return <code>null</code> if
	 * a product with the given code doesn't exist.
	 * <p>
	 * 
	 * @param productCode a product code
	 * @param productLoadTuner the product load tuner
	 * @return a <code>Product</code> with the given product code.
	 */
	Product retrieveProduct(final String productCode, final LoadTuner productLoadTuner);
}
