/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartCleanupService;

/**
 * Service to clean up shopping cart usage.<br>
 * <p>
 * <b>Note:</b> the method {@link #deleteShoppingCartsByGuid(List)} is contained within this service instead of the ShoppingCartService since this
 * class manages the dependent CartOrder objects as well, so that there are no residual effects by using it alone within the
 * ShoppingCartService without an ensuing call to remove the associated CartOrders. <br>
 * ShoppingCartService cannot call CartOrderService to remove these items since this would cause a cyclical dependency.
 * </p>
 */
public class ShoppingCartCleanupServiceImpl implements ShoppingCartCleanupService {

	private static final Logger LOG = Logger.getLogger(ShoppingCartCleanupServiceImpl.class);

	private PersistenceEngine persistenceEngine;

	private CartOrderService cartOrderService;

	@Override
	public int deleteAbandonedShoppingCarts(final Date removalDate, final int maxResults) {
		if (removalDate == null) {
			throw new EpServiceException("removalDate must be supplied.");
		}

		int result = 0;
		List<String> abandonedCartGuids = findAbandonedShoppingCartGuids(removalDate, maxResults);
		if (!abandonedCartGuids.isEmpty()) {
			LOG.debug(String.format("The following shopping carts are removal candidates: %s", abandonedCartGuids));
			result = abandonedCartGuids.size();
			getCartOrderService().removeIfExistsByShoppingCartGuids(abandonedCartGuids);
			deleteShoppingCartsByGuid(abandonedCartGuids);
		}

		return result;
	}

	private int deleteShoppingCartsByGuid(final List<String> shoppingCartGuids) {
		return getPersistenceEngine().executeNamedQueryWithList("SHOPPING_CART_DELETE_BY_GUID", "list", shoppingCartGuids);
	}

	private List<String> findAbandonedShoppingCartGuids(final Date removalDate, final int maxResults) {
		return getPersistenceEngine().<String> retrieveByNamedQuery("FIND_SHOPPING_CART_GUIDS_LAST_MODIFIED_BEFORE_DATE",
				new Object[] { removalDate },
				0,
				maxResults);
	}

	/**
	 * Get the persistence Engine.
	 *
	 * @return the persistence engine
	 */
	protected PersistenceEngine getPersistenceEngine() {
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

	/**
	 * Gets the cart order service.
	 *
	 * @return the cart order service
	 */
	protected CartOrderService getCartOrderService() {
		return cartOrderService;
	}

	/**
	 * Sets the cart order service.
	 *
	 * @param cartOrderService the new cart order service
	 */
	public void setCartOrderService(final CartOrderService cartOrderService) {
		this.cartOrderService = cartOrderService;
	}
}
