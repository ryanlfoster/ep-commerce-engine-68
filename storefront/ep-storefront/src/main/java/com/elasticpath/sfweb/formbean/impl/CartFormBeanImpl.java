package com.elasticpath.sfweb.formbean.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.elasticpath.sfweb.formbean.CartFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Form bean for collecting information from the ShoppingCartController.
 * Note that only quantity is required at present because shipping estimation and
 * code application use different mechanisms.
 */
public class CartFormBeanImpl implements CartFormBean {

	private static final long serialVersionUID = 1L;

	// We use a LazyList as ShoppingItemConfigController and productMacros.vm get the associations
	// directly from the product which doesn't give us a chance to be able to create the FormBean in the
	// same order. Therefore, when AddToCartController gets the submit it's easiest just to create
	// the entries on demand.

	@SuppressWarnings("unchecked")
	private final List<ShoppingItemFormBean> cartItems = LazyList.decorate(
			new ArrayList<ShoppingItemFormBean>(), FactoryUtils.instantiateFactory(ShoppingItemFormBeanImpl.class));

	@Override
	public List<ShoppingItemFormBean> getCartItems() {
		return cartItems;
	}

	@Override
	public ShoppingItemFormBean getRootItem() {
		return cartItems.get(0);
	}

	@Override
	public List<ShoppingItemFormBean> getAssociatedItems() {
		final List<ShoppingItemFormBean> associatedItems = new ArrayList<ShoppingItemFormBean>();
		for (final ShoppingItemFormBean formBean : cartItems.subList(1, cartItems.size())) {
			if (!formBean.isDependent()) {
				associatedItems.add(formBean);
			}
		}
		return associatedItems;
	}

	@Override
	public List<ShoppingItemFormBean> getDependentItems() {
		final List<ShoppingItemFormBean> dependentItems = new ArrayList<ShoppingItemFormBean>();
		for (final ShoppingItemFormBean formBean : cartItems.subList(1, cartItems.size())) {
			if (formBean.isDependent()) {
				dependentItems.add(formBean);
			}
		}
		return dependentItems;
	}

	@Override
	public void addShoppingItemFormBean(
			final ShoppingItemFormBean shoppingItemFormBean) {
		cartItems.add(shoppingItemFormBean);
	}

	/**
	 * @return the shopping items
	 */
	protected List<ShoppingItemFormBean> getShoppingItems() {
		return cartItems;
	}

}
