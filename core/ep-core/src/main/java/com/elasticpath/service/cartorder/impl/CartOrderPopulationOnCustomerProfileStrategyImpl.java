package com.elasticpath.service.cartorder.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.cartorder.CartOrderPopulationStrategy;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * This strategy creates a <code>CartOrder</code>. It fills <code>CartOrder</code>'s billingAddressGuid based on the customer's
 * default billing address, and its paymentMethodGuid property based on the customer's default credit card info.
 */
public class CartOrderPopulationOnCustomerProfileStrategyImpl implements CartOrderPopulationStrategy {

	/** The shopping cart service. */
	private ShoppingCartService shoppingCartService;

	/** The prototype bean factory. */
	private BeanFactory prototypeBeanFactory;

	/**
	 * Creates the <code>CartOrder</code> object, fills the billingAddressGuid property if the customer has a default billing address,
	 * the paymentMethodGuid if the customer has a preferred credit card.
	 *
	 * @param cartGuid the cart guid
	 * @return the cart order
	 */
	@Override
	public CartOrder createCartOrder(final String cartGuid) {
		CartOrder cartOrder = prototypeBeanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(cartGuid);

		ShoppingCart shoppingCart = shoppingCartService.findByGuid(cartGuid);
		Customer customer = shoppingCart.getShopper().getCustomer();

		// set the default billing address
		CustomerAddress billingAddress = customer.getPreferredBillingAddress();
		if (null != billingAddress) {
			cartOrder.setBillingAddressGuid(billingAddress.getGuid());
		}

		// set the default shipping address
		CustomerAddress shippingAddress = customer.getPreferredShippingAddress();
		if (null != shippingAddress) {
			cartOrder.setShippingAddressGuid(shippingAddress.getGuid());
		}

		cartOrder = populateDefaultPaymentMethodOnCartOrder(cartOrder, customer);

		return cartOrder;
	}

	private CartOrder populateDefaultPaymentMethodOnCartOrder(final CartOrder cartOrder, final Customer customer) {
		PaymentMethod defaultPaymentMethod = customer.getPaymentMethods().getDefault();
		
		if (defaultPaymentMethod == null) {
			CustomerCreditCard preferredCreditCard = customer.getPreferredCreditCard();
			cartOrder.setPaymentMethod(preferredCreditCard);
			if (preferredCreditCard != null) {
				cartOrder.setPaymentMethodGuid(preferredCreditCard.getGuid());
			}
		} else {
			cartOrder.setPaymentMethod(defaultPaymentMethod);
		}
		return cartOrder;
	}

	/**
	 * Gets the shopping cart service.
	 *
	 * @return the shopping cart service
	 */
	protected ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}

	/**
	 * Sets the shopping cart service.
	 *
	 * @param shoppingCartService the shopping cart service
	 */
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	/**
	 * Gets the prototype bean factory.
	 *
	 * @return the prototype bean factory
	 */
	protected BeanFactory getPrototypeBeanFactory() {
		return prototypeBeanFactory;
	}

	/**
	 * Sets the prototype bean factory.
	 *
	 * @param prototypeBeanFactory the prototype bean factory
	 */
	public void setPrototypeBeanFactory(final BeanFactory prototypeBeanFactory) {
		this.prototypeBeanFactory = prototypeBeanFactory;
	}

}
