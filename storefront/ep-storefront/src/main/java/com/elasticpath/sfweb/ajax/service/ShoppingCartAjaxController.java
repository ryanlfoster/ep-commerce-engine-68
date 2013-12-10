package com.elasticpath.sfweb.ajax.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.util.SfRequestHelper;

/**
 * Provides shopping cart related services for ajax.
 */
public interface ShoppingCartAjaxController {
	/**
	 * Estimate the shipping options and taxes for the session shopping cart, based on the given countryCode, subCountryCode and zipOrPostalCode. The
	 * session shopping cart shoud be updated afterwards.
	 *
	 * @param countryCode - the selected country code for the shipping options and taxes estimation.
	 * @param subCountryCode - the selected subCountry code for the shipping options and taxes estimation.
	 * @param zipOrPostalCode - the zip or postal code for the shipping options and taxes estimation.
	 * @param cartItemQtyList - the list of cart item qty.
	 * @param request - the current HttpServletRequest.
	 * @return the updated shopping cart.
	 */
	ShoppingCart estimateShippingAndTaxes(final String countryCode, final String subCountryCode, final String zipOrPostalCode,
			final List<String> cartItemQtyList, final HttpServletRequest request);

	/**
	 * Calculate shopping cart with the selected shipping service level.
	 * @param selectedShippingServiceLevelUid - the uid of the selected shipping service level.
	 * @param request - the current HttpServletRequest.
	 * @return the updated shopping cart.
	 */
	ShoppingCart calculateForSelectedShippingServiceLevel(final long selectedShippingServiceLevelUid, final HttpServletRequest request);

	/**
	 * Prepare to reset the address for shipping/taxed.
	 * @param request - the current HttpServletRequest.
	 * @return the updated shopping cart.
	 */
	ShoppingCart changeEstimationAddress(final HttpServletRequest request);

	/**
	 * Return the string representation of the address bits entered for shipping and tax estimation.
	 * @param request - the current HttpServletRequest.
	 * @return the string representation of the address.
	 */
	String getEstimateAddressStr(final HttpServletRequest request);

	/**
	 * Retieve the INDEPENDENT shopping cart item prices.
	 * @param request - the current HttpServletRequest.
	 * @return the list of cart item prices.
	 */
	List<Money> getCartItemPrices(final HttpServletRequest request);
	
	/**
	 * Retrieve the INDEPENDENT shopping cart item prices formatted for the cart's locale.
	 * @param request - the current HttpServletRequest.
	 * @return the list of cart item prices.
	 */
	List<String> getCartItemPricesFormattedForLocale(final HttpServletRequest request);

	/**
	 * Method for retreiving the shopping cart through dwr.
	 * 
	 * @param request the current request (filled in automatically by dwr)
	 * @return the shopping cart object
	 */
	ShoppingCart getCart(final HttpServletRequest request);

	/**
	 * Set the request helper.
	 * 
	 * @param requestHelper the request helper.
	 */
	void setRequestHelper(final SfRequestHelper requestHelper);

	/**
	 * Set the checkout service for processing a checkout.
	 * 
	 * @param checkoutService the checkout service
	 */
	void setCheckoutService(final CheckoutService checkoutService);
	
}
