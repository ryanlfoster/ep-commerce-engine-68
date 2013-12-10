/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPriceFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.formbean.CheckoutAddressFormBean;

/**
 * The Spring MVC controller for specifying a new shipping or billing address.
 */
public class ShippingAddressFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(AddressFormControllerImpl.class);

	private CustomerService customerService;

	private CheckoutService checkoutService;

	/**
	 * Handle the address-add form submit.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @return return the view
	 */
	@Override
	public ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) {
		LOG.debug("CheckoutAddressFormController: entering 'onSubmit' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Customer customer = customerSession.getShopper().getCustomer();
		final CheckoutAddressFormBean shippingFormBean = (CheckoutAddressFormBean) command;
		boolean newAddress = false;

		// Get the shipping address
		CustomerAddress shippingAddress = getUserSelectedAddress(request, customer);
		if (shippingAddress == null) {
			// Customer entered a new shipping address
			shippingAddress = shippingFormBean.getNewAddress();
			newAddress = true;
		}

		// Update the shopping cart
		shoppingCart.setShippingAddress(shippingAddress);

		// Display an error message if no shipping service is provided for the selected address
		checkoutService.retrieveShippingOption(shoppingCart);
		if (shoppingCart.getShippingServiceLevelList().isEmpty()) {
			return getErrorView("errors.noServiceForShippingAddress", request, response, errors).addObject("noServiceErrorNewAddress",
					Boolean.toString(newAddress));
		}

		// Only add the new address if there is service for that address (checked above)
		if (newAddress) {
			customer.addAddress(shippingAddress);
		}

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
			customerSession.getShopper().setCustomer(updatedCustomer);
		}
		
		return new ModelAndView(getSuccessView());
	}

	/**
	 * Prevents validation of the form when the user is selecting an existing address from the list.
	 * 
	 * @param request the request
	 * @param command the command object to validate
	 * @return true if validation should be suppressed
	 */
	@Override
	public boolean suppressValidation(final HttpServletRequest request, final Object command) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		return getUserSelectedAddress(request, customer) != null;
	}

	/**
	 * Prepare the command object for the address add form.
	 * 
	 * @param request -the request
	 * @return the command object
	 */
	@Override
	public Object formBackingObject(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Customer customer = customerSession.getShopper().getCustomer();

		final CheckoutAddressFormBean shippingFormBean = getBean(
				ContextIdNames.CHECKOUT_ADDRESS_FORM_BEAN);

		final CustomerAddress newAddress = getBean(ContextIdNames.CUSTOMER_ADDRESS);
		shippingFormBean.setNewAddress(newAddress);
		shippingFormBean.getNewAddress().setCountry(shoppingCart.getStore().getCountry());
		if (customer != null) {
			shippingFormBean.getNewAddress().setFirstName(customer.getFirstName());
			shippingFormBean.getNewAddress().setLastName(customer.getLastName());
			shippingFormBean.setExistingAddresses(customer.getAddresses());

			// Pre-select the shipping address if one is available and no address has yet been selected
			if (customer.getPreferredShippingAddress() != null && shoppingCart.getShippingAddress() == null) {
				shoppingCart.setShippingAddress(customer.getPreferredShippingAddress());
			}

			// If no shipping address is available but there is an address record, select that address
			if (customer.getAddresses().size() > 0 && shoppingCart.getShippingAddress() == null) {
				shoppingCart.setShippingAddress(customer.getAddresses().iterator().next());
			}
		}

		shippingFormBean.setShippingAddress(true);
		shippingFormBean.setSelectedAddress(shoppingCart.getShippingAddress());
		shippingFormBean.setFrequencyMap(new FrequencyAndRecurringPriceFactory().getFrequencyMap(shoppingCart.getCartItems()));
		return shippingFormBean;
	}

	/**
	 * Set the customer service.
	 * 
	 * @param customerService the customer service.
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Finds the address selected by the user.
	 * 
	 * @param request the HTTP request
	 * @param customer the customer
	 * @return the selected address or null if the address was not found
	 */
	private CustomerAddress getUserSelectedAddress(final HttpServletRequest request, final Customer customer) {
		for (int addressIndex = 0; addressIndex < customer.getAddresses().size(); addressIndex++) {
			final String addressId = ServletRequestUtils.getStringParameter(request, "address", "");
			if (addressId != null && !"".equals(addressId)) {
				for (final CustomerAddress curAddress : customer.getAddresses()) {
					if (curAddress.getUidPk() == Integer.parseInt(addressId)) {
						return curAddress;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Set the checkout service.
	 * 
	 * @param checkoutService the customer service.
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}
}
