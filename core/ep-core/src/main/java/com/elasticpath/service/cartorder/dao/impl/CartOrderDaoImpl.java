/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.cartorder.dao.impl;

import java.util.List;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.cartorder.dao.CartOrderDao;

/**
 * The CartOrder DAO implementation class, CartOrder should not be used in versions of EP prior to 6.4.
 */
public class CartOrderDaoImpl extends AbstractDaoImpl implements CartOrderDao {

	@Override
	public CartOrder get(final long uid) throws EpServiceException {
		return getPersistenceEngine().load(CartOrderImpl.class, uid);
	}

	@Override
	public CartOrder findByGuid(final String guid) {
		List<CartOrder> result = getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_BY_GUID", guid);
		if (result.isEmpty()) {
			return null;
		} else if (result.size() == 1) {
			return result.get(0);
		}
		throw new EpServiceException("Inconsistent data -- duplicate GUIDs exist -- " + guid);
	}

	@Override
	public void remove(final CartOrder cartOrder) {
		getPersistenceEngine().delete(cartOrder);
	}

	@Override
	public CartOrder saveOrUpdate(final CartOrder cartOrder) {
		return getPersistenceEngine().saveOrUpdate(cartOrder);
	}

	@Override
	public CartOrder findByShoppingCartGuid(final String guid) {
		List<CartOrder> result = getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_BY_SHOPPING_CART_GUID", guid);
		if (result.isEmpty()) {
			return null;
		} else if (result.size() == 1) {
			return result.get(0);
		}
		throw new EpServiceException("Inconsistent data -- duplicate GUIDs exist -- " + guid);
	}

	@Override
	public void removeByShoppingCartGuid(final String cartGuid) {
	    getPersistenceEngine().executeNamedQuery("DELETE_CART_ORDER_BY_SHOPPING_CART_GUID", cartGuid);
	}

	@Override
	public int removeByShoppingCartGuids(final List<String> shoppingCartGuids) {
		return getPersistenceEngine().executeNamedQueryWithList("DELETE_CART_ORDERS_BY_SHOPPING_CART_GUIDS", "list", shoppingCartGuids);
	}

	@Override
	public List<String> findCartOrderGuidsByCustomerGuid(final String storeCode, final String customerGuid) {
		return getPersistenceEngine().retrieveByNamedQuery("CART_ORDER_GUIDS_BY_CUSTOMER_GUID", storeCode, customerGuid);
	}

}
