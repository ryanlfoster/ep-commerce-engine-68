/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.cartorder.CartOrderPopulationStrategy;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.cartorder.dao.CartOrderDao;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Implementation of CartOrderService, CartOrder should not be used in versions of EP prior to 6.4.
 */
public class CartOrderServiceImpl extends AbstractEpPersistenceServiceImpl implements CartOrderService {
	private CustomerAddressDao addressDao;
	private CartOrderDao cartOrderDao;
	private ShoppingCartService shoppingCartService;
	private TimeService timeService;
	private ShippingServiceLevelService shippingServiceLevelService;
	private CartOrderPopulationStrategy cartOrderPopulationStrategy;
	private CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer;

	protected CartOrderPopulationStrategy getCartOrderPopulationStrategy() {
		return cartOrderPopulationStrategy;
	}

	public void setCartOrderPopulationStrategy(
			final CartOrderPopulationStrategy cartOrderPopulationStrategy) {
		this.cartOrderPopulationStrategy = cartOrderPopulationStrategy;
	}

	@Override
	public CartOrder findByGuid(final String guid) {
		return postProcessing(getCartOrderDao().findByGuid(guid));
	}

	/**
	 * If the CartOrder with the given uidPk cannot be found then null is returned.
	 * If the ShoppingCart cannot be found then null is returned.
	 *
	 * @param uidPk The uidPk of the desired CartOrder.
	 * @return The CartOrder.
	 */
	@Override
	public CartOrder getObject(final long uidPk) {
		return postProcessing(getCartOrderDao().get(uidPk));
	}

	/**
	 * Hook to perform additional processing on a {@link CartOrder} before usage.
	 *
	 * @param cartOrder the cartOrder to be processed
	 * @return the processed CartOrder
	 */
	private CartOrder postProcessing(final CartOrder cartOrder) {
		if (cartOrder == null) {
			return null;
		}

		boolean cartOrderWasUpdated = getCartOrderShippingInformationSanitizer().sanitize(getStoreCodeForCartOrder(cartOrder.getGuid()), cartOrder);
		if (cartOrderWasUpdated) {
			return cartOrderDao.saveOrUpdate(cartOrder);
		}

		return cartOrder;
	}

	@Override
	public Address getBillingAddress(final CartOrder cartOrder) {
		return addressDao.findByGuid(cartOrder.getBillingAddressGuid());
	}

	@Override
	public Address getShippingAddress(final CartOrder cartOrder) {
		return addressDao.findByGuid(cartOrder.getShippingAddressGuid());
	}

	@Override
	public CartOrder findByShoppingCartGuid(final String guid) {
		return cartOrderDao.findByShoppingCartGuid(guid);
	}

	/**
	 * @return The ShoppingCartService.
	 */
	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	/**
	 * @return The TimeService.
	 */
	protected TimeService getTimeService() {
		return timeService;
	}

	@Override
	public void remove(final CartOrder cartOrder) {
		cartOrderDao.remove(cartOrder);
	}

	@Override
	public CartOrder saveOrUpdate(final CartOrder cartOrder) {
		touchShoppingCart(cartOrder);
		return cartOrderDao.saveOrUpdate(cartOrder);
	}

	@Override
	public boolean createIfNotExists(final String cartGuid) {
		boolean notExists = findByShoppingCartGuid(cartGuid) == null;
		if (notExists) {
			CartOrder cartOrder = cartOrderPopulationStrategy.createCartOrder(cartGuid);
			saveOrUpdate(cartOrder);
		}
		return notExists;
	}

	/**
	 * @return The CartOrderDao.
	 */
	private CartOrderDao getCartOrderDao() {
		return cartOrderDao;
	}

	/**
	 * @param cartOrderDao The CartOrderDao to set.
	 */
	public void setCartOrderDao(final CartOrderDao cartOrderDao) {
		this.cartOrderDao = cartOrderDao;
	}

	/**
	 * @param addressDao The CustomerAddressDao to set.
	 */
	public void setCustomerAddressDao(final CustomerAddressDao addressDao) {
		this.addressDao = addressDao;
	}

	/**
	 * @param shoppingCartService The ShoppingCartService to set.
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	public void setTimeService(final TimeService service) {
		timeService = service;
	}

	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	protected ShippingServiceLevelService getShippingServiceLevelService() {
		return shippingServiceLevelService;
	}

	@Override
	public ShoppingCart populateShoppingCartTransientFields(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		ShoppingCart populatedCart = populateAddressAndShippingFields(shoppingCart, cartOrder);
		populatedCart.calculateShoppingCartTaxAndBeforeTaxPrices();

		return populatedCart;
	}

	@Override
	public ShoppingCart populateAddressAndShippingFields(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		Address billingAddress = getBillingAddress(cartOrder);
		shoppingCart.setBillingAddress(billingAddress);
		Address shippingAddress = getShippingAddress(cartOrder);
		shoppingCart.setShippingAddress(shippingAddress);
		Store store = shoppingCart.getStore();
		List<ShippingServiceLevel> shippingServiceLevels =
			shippingServiceLevelService.retrieveShippingServiceLevel(store.getCode(), shippingAddress);

		if (CollectionUtils.isNotEmpty(shippingServiceLevels)) {

			String shippingServiceLevelGuidFromCartOrder = cartOrder.getShippingServiceLevelGuid();
			ShippingServiceLevel matchingShippingServiceLevel =
				getShippingServiceLevelMatchingGuid(shippingServiceLevels, shippingServiceLevelGuidFromCartOrder);

			if (matchingShippingServiceLevel != null) {
				shoppingCart.setShippingServiceLevelList(shippingServiceLevels);
				shoppingCart.setSelectedShippingServiceLevelUid(matchingShippingServiceLevel.getUidPk());
			}
		}

		return shoppingCart;
	}


	private ShippingServiceLevel getShippingServiceLevelMatchingGuid(final List<ShippingServiceLevel> shippingServiceLevels,
			final String shippingServiceLevelGuid) {
		for (ShippingServiceLevel currentShippingServiceLevel : shippingServiceLevels) {
			String currentShippingServiceLevelGuid = currentShippingServiceLevel.getGuid();
			if (currentShippingServiceLevelGuid.equals(shippingServiceLevelGuid)) {
				return currentShippingServiceLevel;
			}
		}
		return null;
	}

	@Override
	public void removeIfExistsByShoppingCart(final ShoppingCart shoppingCart) {
		cartOrderDao.removeByShoppingCartGuid(shoppingCart.getGuid());
	}

	/**
	 * When a CartOrder is changed the ShoppingCart should also be updated and therefore both the
	 * CartOrder and ShoppingCart are not considered abandoned after 60 days.
	 * This method exists only because CartOrder and ShoppingCart are not integrated yet.
	 *
	 * @param cartOrder The CartOrder to use to lookup the ShoppingCart.
	 */
	private void touchShoppingCart(final CartOrder cartOrder) {
		if (cartOrder != null && cartOrder.getShoppingCartGuid() != null) {
			getShoppingCartService().touch(cartOrder.getShoppingCartGuid());
		}
	}

	@Override
	public int removeIfExistsByShoppingCartGuids(final List<String> shoppingCartGuids) {
		return cartOrderDao.removeByShoppingCartGuids(shoppingCartGuids);
	}

	@Override
	public List<String> findCartOrderGuidsByCustomerGuid(final String storeCode, final String customerGuid) {
		return cartOrderDao.findCartOrderGuidsByCustomerGuid(storeCode, customerGuid);
	}

	@Override
	public String getStoreCodeForCartOrder(final String cartOrderGuid) {
		List<String> storeCodes = getPersistenceEngine().retrieveByNamedQuery("STORE_CODE_BY_CART_ORDER_GUID", cartOrderGuid);
		if (storeCodes.isEmpty()) {
			return null;
		}
		return storeCodes.get(0);
	}

	@Override
	public Date getCartOrderLastModifiedDate(final String cartOrderGuid) {
		List<Date> lastModifiedDates = getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_LAST_MODIFIED_DATE", cartOrderGuid);
		if (lastModifiedDates.isEmpty()) {
			return null;
		}
		return lastModifiedDates.get(0);
	}

	protected CartOrderShippingInformationSanitizer getCartOrderShippingInformationSanitizer() {
		return cartOrderShippingInformationSanitizer;
	}

	public void setCartOrderShippingInformationSanitizer(final CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer) {
		this.cartOrderShippingInformationSanitizer = cartOrderShippingInformationSanitizer;
	}

}
