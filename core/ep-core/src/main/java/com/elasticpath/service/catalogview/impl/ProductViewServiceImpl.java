package com.elasticpath.service.catalogview.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.ProductViewService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Represents a default implementation of <code>ProductViewService</code>.
 */
public class ProductViewServiceImpl extends AbstractEpServiceImpl implements ProductViewService {

	private StoreProductService storeProductService;

	private ProductService productService;

	private StoreConfig storeConfig;

	private FetchGroupLoadTuner categoryLoadTuner;

	private CategoryService categoryService;
	
	/**
	 * Returns the product with the given product code. Return null if no matching product exists. You can give a product load tuner to fine control
	 * what data get populated of the returned product.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the returned product.
	 * 
	 * @param productCode the product code.
	 * @param loadTuner the product load tuner
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it.
	 * @return the product if a product with the given code exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	public StoreProduct getProduct(
			final String productCode, final StoreProductLoadTuner loadTuner, final ShoppingCart shoppingCart) throws EpServiceException {
		return getProduct(productCode, loadTuner, shoppingCart.getStore());
	}
	
	@Override
	public StoreProduct getProduct(final String productCode, final StoreProductLoadTuner loadTuner, final Store store) throws EpServiceException {
		final long productUid = productService.findUidById(productCode);
		if (productUid == 0L) {
			return null;
		}

		return storeProductService.getProductForStore(productUid, store, loadTuner);
	}
	
	/**
	 * Gets the product's parent folder with fetched fields which by default are omitted (like parent folder).  
	 * 
	 * @param product the product which category has to be returned
	 * @return the product's parent category
	 */
	public Category getProductCategory(final Product product) {
		Catalog catalog = storeConfig.getStore().getCatalog();
		Category productCategory = product.getDefaultCategory(catalog);
		return categoryService.load(productCategory.getUidPk(), getCategoryLoadTuner());
	}
		
	private FetchGroupLoadTuner getCategoryLoadTuner() {
		if (categoryLoadTuner == null) {
			categoryLoadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			categoryLoadTuner.addFetchGroup(FetchGroupConstants.CATEGORY_BASIC,
					FetchGroupConstants.CATALOG_DEFAULTS, // need default locale
					FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH // needed for breadcrumb
			);
		}
		return categoryLoadTuner;
	}


	/**
	 * Sets the <code>StoreProductService</code>.
	 * 
	 * @param storeProductService the product retrieve strategy
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * Sets the product service.
	 * 
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Sets the category service.
	 * 
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	
	/**
	 * Sets the store configuration that provides context for the actions 
	 * of this service.
	 * 
	 * @param storeConfig the store configuration.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

}