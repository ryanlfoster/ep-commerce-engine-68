package com.elasticpath.service.shoppingcart.dao.impl;

import java.util.List;

import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ShoppingItemLoadTuner;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.impl.WishListImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.shoppingcart.dao.WishListDao;

/**
 * The wish list dao implementation class.
 */
public class WishListDaoImpl implements WishListDao {

	private PersistenceEngine persistenceEngine;
	private ProductSkuLoadTuner productSkuLoadTuner;
	private ProductLoadTuner productLoadTuner;
	private CategoryLoadTuner categoryLoadTuner;
	private ShoppingItemLoadTuner shoppingItemLoadTuner;
	private FetchPlanHelper fetchPlanHelper;

	@Override
	public WishList findByShopper(final Shopper shopper) {
		configureLoadTuners();
		
		PersistenceEngine persister = getPersistenceEngine();
		Object[] params = new Object[] { shopper.getUidPk()};
        List<WishList> results = persister.retrieveByNamedQuery("WISHLIST_BY_SHOPPING_CONTEXT", params);
        
		WishList wishList = null;
		if (!results.isEmpty()) {
			wishList = results.get(0);
		}
		fetchPlanHelper.clearFetchPlan();
		return wishList;
	}
	
	@Override
	public WishList get(final long uid) throws EpServiceException {
		configureLoadTuners();
		WishList wishList = this.getPersistenceEngine().load(WishListImpl.class, uid);
		fetchPlanHelper.clearFetchPlan();
		return wishList;
	}

	@Override
	public void remove(final WishList wishList) {
		if (wishList != null) {
			this.getPersistenceEngine().delete(wishList);
		}
	}
	
	@Override
	public WishList saveOrUpdate(final WishList wishList) {
		return getPersistenceEngine().saveOrUpdate(wishList);
	}

	/**
	 * Get product sku load tuner. 
	 * 
	 * @return the product sku load tuner
	 */
	public ProductSkuLoadTuner getProductSkuLoadTuner() {
		return productSkuLoadTuner;
	}

	/**
	 * Set product sku load tuner. 
	 * 
	 * @param productSkuLoadTuner the product sku load tuner
	 */
	public void setProductSkuLoadTuner(final ProductSkuLoadTuner productSkuLoadTuner) {
		this.productSkuLoadTuner = productSkuLoadTuner;
	}

	/**
	 * Get product load tuner. 
	 * 
	 * @return the product load tuner
	 */
	public ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	/**
	 * Set product load tuner. 
	 * 
	 * @param productLoadTuner the product load tuner
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	/**
	 * Get category load tuner.
	 * 
	 * @return the category load tuner
	 */
	public CategoryLoadTuner getCategoryLoadTuner() {
		return categoryLoadTuner;
	}

	/**
	 * Set category load tuner.
	 * 
	 * @param categoryLoadTuner the category load tuner
	 */
	public void setCategoryLoadTuner(final CategoryLoadTuner categoryLoadTuner) {
		this.categoryLoadTuner = categoryLoadTuner;
	}

	/**
	 * Get shopping item load tuner.
	 * 
	 * @return the shopping item load tuner
	 */
	public ShoppingItemLoadTuner getShoppingItemLoadTuner() {
		return shoppingItemLoadTuner;
	}

	/**
	 * Set shopping item load tuner.
	 * 
	 * @param shoppingItemLoadTuner the shopping item load tuner
	 */
	public void setShoppingItemLoadTuner(final ShoppingItemLoadTuner shoppingItemLoadTuner) {
		this.shoppingItemLoadTuner = shoppingItemLoadTuner;
	}

	/**
	 * Get fetch plan helper. 
	 * 
	 * @return the fetch plan helper
	 */
	public FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	/**
	 * Set fetch plan helper. 
	 * 
	 * @param fetchPlanHelper the fetch plan helper
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}


	/**
	 * Configure the load tuners. 
	 */	
	protected void configureLoadTuners() {
		fetchPlanHelper.configureLoadTuner(productLoadTuner);
		fetchPlanHelper.configureLoadTuner(productSkuLoadTuner);
		fetchPlanHelper.configureLoadTuner(categoryLoadTuner);
		fetchPlanHelper.configureLoadTuner(shoppingItemLoadTuner);
	}

	/**
	 * Get the persistence Engine.
	 * 
	 * @return the persistence engine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Set the persistence Engine.
	 * 
	 * @param persistenceEngine the persistence engine
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	@Override
    public int deleteEmptyWishListsByShopperUids(final List<Long> shopperUids) {
        if (shopperUids == null) {
            throw new EpServiceException("shopperUids must not be null");
        }
	    if (shopperUids.isEmpty()) {
	    	return 0;
	    }
        
        return persistenceEngine.executeNamedQueryWithList("DELETE_EMPTY_WISHLISTS_BY_SHOPPER_UID", "list", shopperUids);
    }
	
	@Override
    public int deleteAllWishListsByShopperUids(final List<Long> shopperUids) {
        if (shopperUids == null) {
            throw new EpServiceException("shopperUids must not be null");
        }
	    if (shopperUids.isEmpty()) {
	    	return 0;
	    }

	    return persistenceEngine.executeNamedQueryWithList("DELETE_ALL_WISHLISTS_BY_SHOPPER_UID", "list", shopperUids);
    }
}
