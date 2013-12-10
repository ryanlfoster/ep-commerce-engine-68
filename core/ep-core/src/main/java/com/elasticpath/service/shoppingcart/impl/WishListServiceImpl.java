/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.service.shoppingcart.dao.WishListDao;
import com.elasticpath.service.store.StoreService;

/** Service for customer wishlist persistence. */
public class WishListServiceImpl implements WishListService {

	private BeanFactory beanFactory;

	private WishListDao wishListDao;

	private PriceLookupFacade priceLookupFacade;

	private StoreService storeService;

	@Override
	public WishList createWishList(final Shopper shopper) {
		final WishList wishList = beanFactory.getBean(ContextIdNames.WISH_LIST);
		wishList.initialize();
		wishList.setShopper(shopper);
		return wishList;
	}

	@Override
	public WishList get(final long uid) {
		return wishListDao.get(uid);
	}

	@Override
	public WishList save(final WishList wishList) {
		if (wishList != null) {
			return wishListDao.saveOrUpdate(wishList);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
     *
     * This remove is null safe and only removes persisted wishlists.
	 */
	public void remove(final WishList wishList) {
		if (wishList != null && wishList.isPersisted()) {
			wishListDao.remove(wishList);
		}
	}

	@Override
	public WishList findOrCreateWishListByShopper(final Shopper shopper) {
		if (shopper == null) {
			return null;
		}

		WishList wishList = getWishListDao().findByShopper(shopper);

		if (wishList == null) {
			wishList = createWishList(shopper);
		}

		return wishList;
	}

    @Override
	public WishList findOrCreateWishListWithPrice(final CustomerSession customerSession) {
        // TODO: Intent is to just use shopper instead of customer session, but did not
        // want to cascade changes in the priceLookUpFacade at this time.
		final Shopper shopper = customerSession.getShopper();
		final WishList wishList = findOrCreateWishListByShopper(shopper);
		final Store store = storeService.findStoreWithCode(shopper.getStoreCode());
		for (ShoppingItem shoppingItem : wishList.getAllItems()) {
			final Price price = priceLookupFacade.getPromotedPriceForSku(shoppingItem.getProductSku(), store, shopper, new HashSet<Long>());
			if (price != null) {
				PricingScheme pricingScheme = price.getPricingScheme();
				Set<Integer> minQuantities =  pricingScheme.getPriceTiersMinQuantities();
				int quantity = minQuantities.iterator().next();
				shoppingItem.setPrice(quantity, price);
			}
		}

		return wishList;
	}

    @Override
    public int deleteEmptyWishListsByShopperUids(final List<Long> shopperUids) {
        return wishListDao.deleteEmptyWishListsByShopperUids(shopperUids);
    }

    @Override
    public int deleteAllWishListsByShopperUids(final List<Long> shopperUids) {
        return wishListDao.deleteAllWishListsByShopperUids(shopperUids);
    }

    // Setters/Getters for spring.
    // ---------------------------

	/**
	 * Get the bean factory.
	 *
	 * @return the bean factory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * set the bean factory.
	 *
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Get the wish list dao.
	 *
	 * @return the wish list dao
	 */
	public WishListDao getWishListDao() {
		return wishListDao;
	}

	/**
	 * Set the wish list dao.
	 *
	 * @param wishListDao the wish list dao
	 */
	public void setWishListDao(final WishListDao wishListDao) {
		this.wishListDao = wishListDao;
	}

	/**
	 * Set the price look up facade.
	 *
	 * @param priceLookupFacade the price look up facade instance
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * Gets the store service.
	 *
	 * @return the store service
	 */
	protected StoreService getStoreService() {
		return storeService;
	}

	/**
	 * Sets the store service.
	 *
	 * @param storeService the new store service
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
}
