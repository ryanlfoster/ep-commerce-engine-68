/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import java.util.Map;

import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPrice;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sfweb.formbean.DeliveryOptionsFormBean;

/**
 * Form bean for creating new delivery options during the checkout.
 */
public class DeliveryOptionsFormBeanImpl extends EpFormBeanImpl implements DeliveryOptionsFormBean {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private ShoppingCart shoppingCart;
	private Map<Quantity, FrequencyAndRecurringPrice> frequencyMap;
	
	@Override
	public Map<Quantity, FrequencyAndRecurringPrice> getFrequencyMap() {
		return this.frequencyMap;
	}

	@Override
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}
	
	@Override
	public void setFrequencyMap(final Map<Quantity, FrequencyAndRecurringPrice> frequencyMap) {
		this.frequencyMap = frequencyMap;
	}
	
	@Override
	public void setShoppingCart(final ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
	
}
