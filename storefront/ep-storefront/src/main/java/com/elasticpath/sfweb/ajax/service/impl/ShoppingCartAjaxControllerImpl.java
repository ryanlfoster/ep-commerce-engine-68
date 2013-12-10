/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.ajax.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.ajax.service.ShoppingCartAjaxController;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * The default implementation of the ShoppingCartAjaxController.
 */
public class ShoppingCartAjaxControllerImpl implements ShoppingCartAjaxController {
	private SfRequestHelper requestHelper;

	private CheckoutService checkoutService;
	
	private Geography geography;

	private BeanFactory beanFactory;
	private MoneyFormatter moneyFormatter;

	/**
	 * Estimate the shipping options and taxes for the session shopping cart, based on the given countryCode, subCountryCode and zipOrPostalCode. <br>
	 * The session shopping cart should be updated afterwards.
	 * 
	 * @param countryCode - the selected country code for the shipping options and taxes estimation.
	 * @param subCountryCode - the selected subCountry code for the shipping options and taxes estimation.
	 * @param zipOrPostalCode - the zip or postal code for the shipping options and taxes estimation.
	 * @param cartItemQtyList - the list of cart item qty.
	 * @param request - the current HttpServletRequest.
	 * @return the update shopping cart.
	 */
	public ShoppingCart estimateShippingAndTaxes(final String countryCode, final String subCountryCode, final String zipOrPostalCode,
			final List<String> cartItemQtyList, final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		shoppingCart.setEstimateMode(true);

		final Address estimateAddress = getBeanFactory().getBean(ContextIdNames.CUSTOMER_ADDRESS);
		estimateAddress.setCountry(countryCode);
		estimateAddress.setSubCountry(subCountryCode);
		estimateAddress.setZipOrPostalCode(zipOrPostalCode);

		// Set up the cart shipping address or billing address.
		if (shoppingCart.requiresShipping()) {
			shoppingCart.setShippingAddress(estimateAddress);
		} else {
			shoppingCart.setBillingAddress(estimateAddress);
		}

		// Shipping and taxes calculation
		checkoutService.retrieveShippingOption(shoppingCart);
		checkoutService.calculateTaxAndBeforeTaxValue(shoppingCart);

		return shoppingCart;
	}

	/**
	 * Calculate shopping cart with the selected shipping service level.
	 * 
	 * @param selectedShippingServiceLevelUid - the uid of the selected shipping service level.
	 * @param request - the current HttpServletRequest.
	 * @return the update shopping cart.
	 */
	public ShoppingCart calculateForSelectedShippingServiceLevel(final long selectedShippingServiceLevelUid, final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		shoppingCart.setSelectedShippingServiceLevelUid(selectedShippingServiceLevelUid);
		checkoutService.calculateTaxAndBeforeTaxValue(shoppingCart);
		return shoppingCart;
	}

	/**
	 * Prepare to reset the address for shipping/taxed.
	 * 
	 * @param request - the current HttpServletRequest.
	 * @return the updated shopping cart.
	 */
	public ShoppingCart changeEstimationAddress(final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		shoppingCart.setEstimateMode(false);
		shoppingCart.clearEstimates();
		return shoppingCart;
	}

	/**
	 * Return the string representation of the address bits entered for shipping and tax estimation.
	 * 
	 * @param request - the current HttpServletRequest.
	 * @return the string representation of the address.
	 */
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public String getEstimateAddressStr(final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final StringBuffer addressStrBuf = new StringBuffer();
		if (shoppingCart.isEstimateMode()) {
			Address estimateAddress = shoppingCart.getShippingAddress();
			if (!shoppingCart.requiresShipping()) {
				estimateAddress = shoppingCart.getBillingAddress();
			}

			if (estimateAddress.getSubCountry() != null && estimateAddress.getSubCountry().length() > 0) {
				addressStrBuf.append(
						getGeography().getSubCountryDisplayName(estimateAddress.getCountry(), estimateAddress.getSubCountry(),
								shoppingCart.getLocale())).append(", ");
			}
			addressStrBuf.append(getGeography().getCountryDisplayName(estimateAddress.getCountry(), shoppingCart.getLocale()));

			if (estimateAddress.getZipOrPostalCode() != null && estimateAddress.getZipOrPostalCode().length() > 0) {
				addressStrBuf.append(", ").append(estimateAddress.getZipOrPostalCode());
			}
		}
		return addressStrBuf.toString();
	}

	/**
	 * Retrieve the shopping cart item prices.
	 * 
	 * @param request - the current HttpServletRequest.
	 * @return the list of cart item prices.
	 */
	public List<Money> getCartItemPrices(final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final List<Money> cartItemPrices = new ArrayList<Money>();
		for (final ShoppingItem curCartItem : shoppingCart.getCartItems()) {
			cartItemPrices.add(curCartItem.getTotal());
		}
		return cartItemPrices;
	}
	
	/**
	 * Retrieve the shopping cart item prices formatted for the cart's locale.
	 * 
	 * @param request - the current HttpServletRequest.
	 * @return the list of formatted cart item prices.
	 */
	public List<String> getCartItemPricesFormattedForLocale(final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final List<String> cartItemPricesFormatted = new ArrayList<String>();
		for (final ShoppingItem curCartItem : shoppingCart.getCartItems()) {
			cartItemPricesFormatted.add(getMoneyFormatter().formatCurrency(curCartItem.getTotal(), shoppingCart.getLocale()));
		}
		return cartItemPricesFormatted;
	}


	/**
	 * Set the request helper.
	 * 
	 * @param requestHelper the request helper.
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}

	/**
	 * Set the checkout service for processing a checkout.
	 * 
	 * @param checkoutService the checkout service
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	/**
	 * Method for retrieving the shopping cart through dwr.
	 * 
	 * @param request the current request (filled in automatically by dwr)
	 * @return the shopping cart object
	 */
	public ShoppingCart getCart(final HttpServletRequest request) {
		final CustomerSession customerSession = requestHelper.getCustomerSession(request);
		return customerSession.getShoppingCart();
	}
	
	/**
	 * @param geography Set the Geography object.
	 */
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}
	
	/**
	 * @return Gets the Geography object.
	 */
	protected Geography getGeography() {
		return geography;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setMoneyFormatter(final MoneyFormatter formatter) {
		this.moneyFormatter = formatter;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}
}
