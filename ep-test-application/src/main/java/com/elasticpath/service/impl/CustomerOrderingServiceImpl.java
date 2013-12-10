/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.impl;

import java.util.Locale;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.CustomerOrderingService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;

/**
 * Provides a high-level service for customer ordering controllers. FIXME: next comments should be removed: Actually this service is used only in FIT
 * tests. But all the methods should implement a logic of controllers exactly as it is done there.
 */
public class CustomerOrderingServiceImpl extends AbstractEpServiceImpl implements CustomerOrderingService {

	private ShoppingCartService shoppingCartService;

	private CheckoutService checkoutService;

	private CustomerService customerService;

	@Override
	public ShoppingCart updateShoppingCartLocale(final ShoppingCart shoppingCart, final Locale locale) {
		shoppingCart.setLocale(locale);
		return shoppingCartService.saveOrUpdate(shoppingCart);
	}

	@Override
	public void selectShippingAddress(final Shopper shopper, final CustomerAddress shippingAddress) {

		final Customer customer = shopper.getCustomer();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();
		
		// Update the shopping cart with shipping address
		shoppingCart.setShippingAddress(shippingAddress);

		// Throw the exception if no shipping service is provided for the selected address
		checkoutService.retrieveShippingOption(shoppingCart);
		if (shoppingCart.getShippingServiceLevelList().isEmpty()) {
			// TODO: throw specific exception from here if future SF controllers require.
			throw new EpServiceException("No shipping service level could be found for the shipping address " + shippingAddress);
		}

		// Address will actually be added just if it's not currently contained in the customer's addresses list. Safe operation.
		customer.addAddress(shippingAddress);

		// Set the billing address to the shipping address by default
		// if no billing address is available
		if (shoppingCart.getBillingAddress() == null) {
			if (customer.getPreferredBillingAddress() == null) {
				shoppingCart.setBillingAddress(shippingAddress);
				customer.setPreferredBillingAddress(shippingAddress);
			} else {
				shoppingCart.setBillingAddress(customer.getPreferredBillingAddress());
			}
		}

		// Update taxes and totals
		checkoutService.calculateTaxAndBeforeTaxValue(shoppingCart);

		// Update the customer
		customer.setPreferredShippingAddress(shippingAddress);
		if (customer.isPersisted()) {
			customerService.verifyCustomer(customer);
			Customer updatedCustomer = customerService.update(customer);
			shopper.setCustomer(updatedCustomer);
		}
	}

	@Override
	public void selectBillingAddress(final Shopper shopper, final CustomerAddress billingAddress) {
		Customer customer = shopper.getCustomer();
		
		// Address will actually be added just if it's not currently contained in the customer's addresses list. Safe operation.
		customer.addAddress(billingAddress);
		customer.setPreferredBillingAddress(billingAddress);
		
		if (customer.isPersisted()) {
			customerService.verifyCustomer(customer);
		}

		// Update the shopping cart
		shopper.getCurrentShoppingCart().setBillingAddress(billingAddress);
	}

	@Override
	public ShoppingCart selectShippingServiceLevel(final ShoppingCart shoppingCart, final long selectedSSLUid) {
		shoppingCart.setSelectedShippingServiceLevelUid(selectedSSLUid);
		return shoppingCart;
	}

	@Override
	public CheckoutResults checkout(final ShoppingCart shoppingCart, final OrderPayment orderPayment) {
		// TODO: Investigate: Why next line used in controller.
		// shoppingCart.setEstimateMode(false);
		return checkoutService.checkout(shoppingCart, orderPayment, false);
	}

	@Override
	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	@Override
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	@Override
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}
	
	/**
	 * Removes the cart item and its dependent items.
	 */
//	private void removeExistingCartItem(final ShoppingCart shoppingCart, final CartItem existingCartItem, final long cartItemId) {
//		shoppingCart.removeCartItem(cartItemId);
//		if (existingCartItem != null) {
//			shoppingCart.removeCartItem(existingCartItem.getUidPk());
//		}
//	}
}
