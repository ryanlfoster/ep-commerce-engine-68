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
import com.elasticpath.sfweb.formbean.CheckoutAddressFormBean;

/**
 * The Spring MVC controller for specifying a new shipping or billing address.
 */
public class BillingAddressFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(BillingAddressFormControllerImpl.class);

	private CustomerService customerService;

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
		LOG.debug("BillingAddressFormController: entering 'onSubmit' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Customer customer = customerSession.getShopper().getCustomer();
		final CheckoutAddressFormBean shippingFormBean = (CheckoutAddressFormBean) command;

		// Get the billing address
		CustomerAddress billingAddress = getUserSelectedAddress(request, customer);
		if (billingAddress == null) {
			// Customer entered a new billing address
			billingAddress = shippingFormBean.getNewAddress();
			customer.addAddress(billingAddress);
		}

		// Update the shopping cart
		shoppingCart.setBillingAddress(billingAddress);

		// Update the customer
		customer.setPreferredBillingAddress(billingAddress);
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

		final CheckoutAddressFormBean billingAddressFormBean = getBean(
				ContextIdNames.CHECKOUT_ADDRESS_FORM_BEAN);

		final CustomerAddress newAddress = getBean(ContextIdNames.CUSTOMER_ADDRESS);
		billingAddressFormBean.setNewAddress(newAddress);

		billingAddressFormBean.getNewAddress().setCountry(shoppingCart.getStore().getCountry());
		if (customer != null) {
			billingAddressFormBean.getNewAddress().setFirstName(customer.getFirstName());
			billingAddressFormBean.getNewAddress().setLastName(customer.getLastName());
			billingAddressFormBean.setExistingAddresses(customer.getAddresses());
		}
		billingAddressFormBean.setShippingAddress(false);
		billingAddressFormBean.setSelectedAddress(shoppingCart.getBillingAddress());
		billingAddressFormBean.setFrequencyMap(new FrequencyAndRecurringPriceFactory().getFrequencyMap(shoppingCart.getCartItems()));
		return billingAddressFormBean;
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
	 * Set the customer service.
	 * 
	 * @param customerService the customer service.
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

}
