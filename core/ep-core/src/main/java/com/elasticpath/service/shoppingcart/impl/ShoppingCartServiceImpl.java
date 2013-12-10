/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ShoppingItemLoadTuner;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartMemento;
import com.elasticpath.domain.shoppingcart.ShoppingCartMementoHolder;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.store.StoreService;

/** Service for retrieving and saving Shopping Carts. */
public class ShoppingCartServiceImpl extends AbstractEpPersistenceServiceImpl implements ShoppingCartService {
	private static final Logger LOG = Logger.getLogger(ShoppingCartServiceImpl.class);

	/** OpenJPA Query Name. */
	protected static final String SHOPPING_CART_FIND_BY_GUID_EAGER = "SHOPPING_CART_FIND_BY_GUID_EAGER";
	/** OpenJPA Query Name. */
	protected static final String SHOPPING_CART_FIND_BY_SHOPPER_UID = "SHOPPING_CART_FIND_BY_SHOPPER_UID";

	private FetchPlanHelper fetchPlanHelper;
	private ProductSkuLoadTuner productSkuLoadTuner;
	private ProductLoadTuner productLoadTuner;
	private CategoryLoadTuner categoryLoadTuner;
	private ShoppingItemLoadTuner shoppingItemLoadTuner;
	private ShopperService shopperService;
	private StoreService storeService;
	private TimeService timeService;

	/**
	 * Updates the given shopping cart.
	 *
	 * @param shoppingCart the shopping cart to update
	 * @return the persisted instance of shoppingCart.
	 * @throws EpServiceException - in case of any errors, if there is an error
	 *         the cart will be in a state consistent with the database.
	 */
	@Override
	public ShoppingCart saveOrUpdate(final ShoppingCart shoppingCart) throws EpServiceException {
		verifyShoppingCartInterfaces(shoppingCart);

		return saveOrUpdate(shoppingCart, (ShoppingCartMementoHolder) shoppingCart);
	}

	/**
	 * Updates the given shopping cart.
	 *
	 * @param shoppingCart the shopping cart to update
	 * @param mementoContainer the shopping cart memento container interface
	 * @return the persisted instance of shoppingCart.
	 * @throws EpServiceException - in case of any errors, if there is an error
	 *         the cart will be in a state consistent with the database.
	 */
	protected ShoppingCart saveOrUpdate(final ShoppingCart shoppingCart, final ShoppingCartMementoHolder mementoContainer) {
		LOG.debug("saving shopping cart...");
		configureLoadTuners();
		try {
			final ShoppingCartMemento shoppingCartMemento = mementoContainer.getShoppingCartMemento();
			shoppingCartMemento.setStoreCode(shoppingCart.getStore().getCode());

			final ShoppingCartMemento updatedShoppingCartMemento = getPersistenceEngine().saveOrUpdate(shoppingCartMemento);
			mementoContainer.setShoppingCartMemento(updatedShoppingCartMemento);
			shoppingCart.setStore(storeService.findStoreWithCode(shoppingCartMemento.getStoreCode()));
		} catch (RuntimeException e) {
			// If the update fails then re-read the memento from the database,
			// the one we tried to write will be broken and no longer usable.
			if (mementoContainer.getShoppingCartMemento().isPersisted()) {
				final ShoppingCartMemento freshlyReadMemento = loadMemento(mementoContainer.getShoppingCartMemento().getUidPk());
				mementoContainer.setShoppingCartMemento(freshlyReadMemento);
			}
			throw e;
		}
		fetchPlanHelper.clearFetchPlan();
		shoppingCart.fireRules();
		return shoppingCart;
	}

	@Override
	public ShoppingCart saveIfNotPersisted(final ShoppingCart shoppingCart) throws EpServiceException {
		verifyShoppingCartInterfaces(shoppingCart);

		if (!((ShoppingCartMementoHolder) shoppingCart).getShoppingCartMemento().isPersisted()) {
			return saveOrUpdate(shoppingCart);
		}

		return shoppingCart;
	}

	private void verifyShoppingCartInterfaces(final ShoppingCart shoppingCart) {
		if (!(shoppingCart instanceof ShoppingCartMementoHolder)) {
			throw new EpServiceException("ShoppingCart class " + shoppingCart.getClass() + " must implements ShoppingCartMementoHolder");
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * This remove is null-safe and only removes a persistent ShoppingCart.
	 */
	@Override
	public void remove(final ShoppingCart shoppingCart) throws EpServiceException {
		verifyShoppingCartInterfaces(shoppingCart);

		if (shoppingCart != null && getShoppingCartMemento(shoppingCart).isPersisted()) {
			getPersistenceEngine().delete(getShoppingCartMemento(shoppingCart));
		}
	}

	/**
	 * Retrieves a specific memento by uid.
	 * @param uidPk the uid of the memento to load.
	 * @return the requested memento , or null if it cannot be found.
	 */
	private ShoppingCartMemento loadMemento(final long uidPk) {
		return getPersistentBeanFinder().load(ContextIdNames.SHOPPING_CART_MEMENTO, uidPk);
	}

	/**
	 * Returns the shopping cart memento associated with a given shopping cart.
	 *
	 * @param shoppingCart the shopping cart
	 * @return the memento, or null if it does not exist.
	 */
	protected ShoppingCartMemento getShoppingCartMemento(final ShoppingCart shoppingCart) {
		verifyShoppingCartInterfaces(shoppingCart);
		return ((ShoppingCartMementoHolder) shoppingCart).getShoppingCartMemento();
	}

	/**
	 * Sets the shopping cart memento associated with a given shopping cart.
	 *
	 * @param shoppingCart the shopping cart
	 * @param memento the memento
	 */
	protected void setShoppingCartMemento(final ShoppingCart shoppingCart, final ShoppingCartMemento memento) {
		verifyShoppingCartInterfaces(shoppingCart);
		((ShoppingCartMementoHolder) shoppingCart).setShoppingCartMemento(memento);
	}

	/**
	 * Retrieves a specific memento by guid.
	 * @param guid the guid of the memento to load.
	 * @return the requested memento , or null if it cannot be found.
	 */
	private ShoppingCartMemento loadShoppingCartMemento(final String guid) {
		final List<ShoppingCartMemento> carts = getPersistenceEngine().retrieveByNamedQuery(SHOPPING_CART_FIND_BY_GUID_EAGER, guid);
		if (carts.size() == 1) {
			return carts.get(0);
		}
		if (carts.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate guid:" + guid);
		}
		return null;
	}

	@Override
	public ShoppingCart findByGuid(final String guid) throws EpServiceException {
		configureLoadTuners();
		
		final ShoppingCartMemento shoppingCartMemento = loadShoppingCartMemento(guid);
		if (shoppingCartMemento == null) {
			return null;			
		}
		
		fetchPlanHelper.clearFetchPlan();

		return createShoppingCart(shoppingCartMemento);
	}

	/**
	 * Factory method which constructs a shopping cart from a memento.
	 *
	 * @param shoppingCartMemento the memento
	 * @return a shopping cart
	 */
	private ShoppingCart createShoppingCart(final ShoppingCartMemento shoppingCartMemento) {
		final long shopperUidFromMemento = shoppingCartMemento.getShopperUid();
		final Shopper shopperFromShoppingCartMemento = shopperService.get(shopperUidFromMemento);

		return createShoppingCart(shopperFromShoppingCartMemento, shoppingCartMemento);
	}

	private ShoppingCart createShoppingCart(final Shopper shopperFromShoppingCartMemento, final ShoppingCartMemento shoppingCartMemento) {
		final Store store = storeService.findStoreWithCode(shoppingCartMemento.getStoreCode());

		final ShoppingCart shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.setStore(store);
		setShoppingCartMemento(shoppingCart, shoppingCartMemento);
		shoppingCart.setShopper(shopperFromShoppingCartMemento);

		return shoppingCart;
	}

	@Override
	public ShoppingCart findOrCreateByShopper(final Shopper shopper) throws EpServiceException {
		configureLoadTuners();
		final List<ShoppingCartMemento> carts = getPersistenceEngine().retrieveByNamedQuery(SHOPPING_CART_FIND_BY_SHOPPER_UID, shopper.getUidPk());
		fetchPlanHelper.clearFetchPlan();

		if (carts.isEmpty()) {
			final ShoppingCart shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
			shoppingCart.setShopper(shopper);

			String storeCodeFromShopper = shopper.getStoreCode();
			Store storeFromShopper = storeService.findStoreWithCode(storeCodeFromShopper);
			shoppingCart.setStore(storeFromShopper);
			return shoppingCart;
		}

		return createShoppingCart(shopper, carts.get(0));
	}

	@Override
	public List<String> findByCustomerAndStore(final String customerGuid, final String storeCode) throws EpServiceException {
		return getPersistenceEngine().retrieveByNamedQuery("SHOPPING_CART_GUID_FIND_BY_CUSTOMER_AND_STORE", customerGuid, storeCode);
	}
	
	/**
	 * Generic get method for all persistent domain models.
	 *
	 * @param uid the persisted instance uid
	 * @return the persisted instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	@Override
	public Object getObject(final long uid) throws EpServiceException {
        ShoppingCart shoppingCart;
        if (uid <= 0) {
            shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
        } else {
            configureLoadTuners();
            final ShoppingCartMemento memento = loadMemento(uid);
            if (memento == null) {
            	shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
            } else {
                shoppingCart = createShoppingCart(memento);
			}
            fetchPlanHelper.clearFetchPlan();
        }

        return shoppingCart;
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

	@Override
	public void touch(final String cartGuid) {
		ShoppingCartMemento memento = loadShoppingCartMemento(cartGuid);
		if (memento != null) {
			memento.setLastModifiedDate(getTimeService().getCurrentTime());
		}

		getPersistenceEngine().saveOrUpdate(memento);
	}

	@Override
	public int deleteEmptyShoppingCartsByShopperUids(final List<Long> shopperUids) {
	    if (shopperUids == null) {
	        throw new EpServiceException("shopperUids must not be null");
	    }
	    if (shopperUids.isEmpty()) {
	    	return 0;
	    }
	    	
	    return getPersistenceEngine().executeNamedQueryWithList("DELETE_EMPTY_SHOPPING_CARTS_BY_SHOPPER_UID", "list", shopperUids);
	}
	
	@Override
	public int deleteAllShoppingCartsByShopperUids(final List<Long> shopperUids) {
	    if (shopperUids == null) {
	        throw new EpServiceException("shopperUids must not be null");
	    }
	    if (shopperUids.isEmpty()) {
	    	return 0;
	    }
	    
	    getPersistenceEngine().executeNamedQueryWithList("DELETE_ALL_CART_ORDERS_BY_SHOPPER_UID", "list", shopperUids);
	    return getPersistenceEngine().executeNamedQueryWithList("DELETE_ALL_SHOPPING_CARTS_BY_SHOPPER_UID", "list", shopperUids);
	}

	@Override
	public Date getShoppingCartLastModifiedDate(final String cartGuid) {
		final List<Date> dates = getPersistenceEngine().retrieveByNamedQuery("FIND_SHOPPING_CART_LAST_MODIFIED_DATE", cartGuid);
		if (dates.isEmpty()) {
			return null;
		}
		return dates.get(0);
	}
	
	@Override
	public boolean shoppingCartExists(final String cartGuid) {
		final List<String> guids = getPersistenceEngine().retrieveByNamedQuery("FIND_GUID_OF_SHOPPING_CART_WITH_MATCHING_GUID", cartGuid);
		
		return !guids.isEmpty();
	}

	@Override
	public boolean shoppingCartHasItems(final String cartGuid) {
		final List<String> guids = getPersistenceEngine().retrieveByNamedQuery("FIND_GUID_OF_SHOPPING_CART_WITH_MATCHING_GUID_AND_HAS_ITEMS",
				cartGuid);
		
		return !guids.isEmpty(); 
	}

	@Override
	public boolean shoppingCartExistsForStore(final String cartGuid, final String storeCode) {
		final List<String> guids = getPersistenceEngine().retrieveByNamedQuery("FIND_GUID_OF_SHOPPING_CART_WITH_MATCHING_GUID_AND_STORE_CODE",
				cartGuid, storeCode);
		
		return !guids.isEmpty(); 
	}
	
	@Override
	public boolean shoppingCartContainsBundles(final String cartGuid) {
		final List<String> guids = getPersistenceEngine().retrieveByNamedQuery("FIND_GUID_OF_SHOPPING_CART_WITH_MATCHING_GUID_AND_HAS_BUNDLE_ITEMS",
				cartGuid);
		return !guids.isEmpty();
	}

	@Override
	public boolean shoppingCartContainsShippableItems(final String cartGuid) {
		final List<String> guids = getPersistenceEngine().retrieveByNamedQuery(
				"FIND_GUID_OF_SHOPPING_CART_WITH_MATCHING_GUID_AND_HAS_SHIPPABLE_ITEMS", cartGuid);
		return !guids.isEmpty();
	}

	@Override
	public boolean isPersisted(final ShoppingCart shoppingCart) {
		verifyShoppingCartInterfaces(shoppingCart);
		return ((ShoppingCartMementoHolder) shoppingCart).getShoppingCartMemento().isPersisted();
	}

	// BEGIN - SPRING SETTERS
	
	/**
	 * Set the category load tuner through spring.
	 * @param categoryLoadTuner category load tuner
	 */
	public void setCategoryLoadTuner(final CategoryLoadTuner categoryLoadTuner) {
	    this.categoryLoadTuner = categoryLoadTuner;
	}

	/**
	 * Setter for spring.
	 *
	 * @param shoppingItemLoadTuner a shopping item load tuner
	 */
	public void setShoppingItemLoadTuner(final ShoppingItemLoadTuner shoppingItemLoadTuner) {
	    this.shoppingItemLoadTuner = shoppingItemLoadTuner;
	}

	/**
	 * Sets the <code>ProductLoadTuner</code> for populating all data.
	 *
	 * @param productLoadTuner the <code>ProductLoadTuner</code> for populating all data.
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
	    this.productLoadTuner = productLoadTuner;
	}
	
	/**
	 * Sets the <code>ProductSkuLoadTuner</code> for populating all data.
	 *
	 * @param productSkuLoadTuner the <code>ProductSkuLoadTuner</code> for populating all data.
	 */
	public void setProductSkuLoadTuner(final ProductSkuLoadTuner productSkuLoadTuner) {
	    this.productSkuLoadTuner = productSkuLoadTuner;
	}
	
	/**
	 * Set the fetch plan helper.
	 *
	 * @param fetchPlanHelper the fetchPlanHelper to set
	 */
	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
	    this.fetchPlanHelper = fetchPlanHelper;
	}
	
	/**
	 * @param shopperService the shopperService to set
	 */
	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

	/**
	 * @return the shopperService
	 */
	public ShopperService getShopperService() {
		return shopperService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	// END - SPRING SETTERS
}
