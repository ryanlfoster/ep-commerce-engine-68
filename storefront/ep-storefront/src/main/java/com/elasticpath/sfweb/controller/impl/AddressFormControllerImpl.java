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
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.CustomerAddressFormBean;

/**
 * The Spring MVC controller for address-add page.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class AddressFormControllerImpl extends AbstractEpFormController {

	private static final Logger LOG = Logger.getLogger(AddressFormControllerImpl.class);

	private static final String DELETE_ADDRESS_PARAMETER = "deleteAddress";

	private CustomerService customerService;

	private String billingAddressView;

	private String shippingAddressView;

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
		LOG.debug("AddressFormController: entering 'onSubmit' method...");
		
		final CustomerAddressFormBean customerAddressFormBean = (CustomerAddressFormBean) command;
		final CustomerAddress address = customerAddressFormBean.getCustomerAddress();
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		final long addressUid = customerAddressFormBean.getAddressUidPk();

		if (request.getParameter(DELETE_ADDRESS_PARAMETER) == null) {
			if (addressUid == 0) { // add new address
				if (!customer.getAddresses().contains(address)) {
					customer.addAddress(address);
					if (customerAddressFormBean.isPreferredBillingAddress()) {
						customer.setPreferredBillingAddress(address);
					}
				}
			} else { // edit existing address
				CustomerAddress sessionAddress = customer.getAddressByUid(addressUid);
				if (!sessionAddress.equals(address)) {
					sessionAddress.copyFrom(address);
				}
				if (customerAddressFormBean.isPreferredBillingAddress()) {
					customer.setPreferredBillingAddress(sessionAddress);
				}
			}

		} else {
			// Delete the address
			customer.removeAddress(customer.getAddressByUid(addressUid));
		}
		if (customer.isPersisted()) {
			Customer updatedCustomer = customerService.update(customer);
			customerSession.getShopper().setCustomer(updatedCustomer);
		}

		final String isShippingAddressEdit = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_EDIT_SHIPPING_ADDRESS, "");
		final String isBillingAddressEdit = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_EDIT_BILLING_ADDRESS, "");

		if (!"".equals(isShippingAddressEdit)) { // NOPMD
			return new ModelAndView(shippingAddressView);
		} else if (!"".equals(isBillingAddressEdit)) { // NOPMD
			return new ModelAndView(billingAddressView);
		} else {
			return new ModelAndView(getSuccessView());
		}
	}

	/**
	 * Prepare the command object for the address add form.
	 * 
	 * @param request -the request
	 * @return the command object
	 */
	@Override
	@SuppressWarnings("PMD.CyclomaticComplexity")
	public Object formBackingObject(final HttpServletRequest request) {
		final CustomerAddressFormBean customerAddressFormBean = getBean(
				ContextIdNames.CUSTOMER_ADDRESS_FORM_BEAN);

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Customer customer = customerSession.getShopper().getCustomer();
		final String checkoutShipping = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_EDIT_SHIPPING_ADDRESS, "");
		final String checkoutBilling = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_EDIT_BILLING_ADDRESS, "");
		final long addressUid = ServletRequestUtils.getLongParameter(request, WebConstants.REQUEST_ADDRESS_ID, 0);

		final CustomerAddress address = getBean(ContextIdNames.CUSTOMER_ADDRESS);
		if (addressUid == 0 && "".equals(checkoutShipping) && "".equals(checkoutBilling)) {

			if (customer != null) {
				address.setFirstName(customer.getFirstName());
				address.setLastName(customer.getLastName());
			}
			// Set the default country of the new address
			address.setCountry(shoppingCart.getStore().getCountry());

		} else {
			final CustomerAddress sessionAddress = customer.getAddressByUid(addressUid);
			if (sessionAddress == null) {
				// Not a valid address for the customer
				throw new EpSfWebException("Address not found");
			}
			request.setAttribute(WebConstants.REQUEST_ADDRESS_ID, (new Long(addressUid)).toString());

			if (!"".equals(checkoutShipping)) {
				request.setAttribute(WebConstants.REQUEST_EDIT_SHIPPING_ADDRESS, checkoutShipping);
			}
			if (!"".equals(checkoutBilling)) {
				request.setAttribute(WebConstants.REQUEST_EDIT_BILLING_ADDRESS, checkoutBilling);
			}

			if (customer.getPreferredBillingAddress() != null && customer.getPreferredBillingAddress().getUidPk() == sessionAddress.getUidPk()) {
				customerAddressFormBean.setPreferredBillingAddress(true);
			} else {
				customerAddressFormBean.setPreferredBillingAddress(false);
			}
			// record the session Address uidpk to customerAddressFormBean
			customerAddressFormBean.setAddressUidPk(sessionAddress.getUidPk());

			// We can't use the original address object because spring will mark all the fields as dirty
			// when the form is submitted.
			address.copyFrom(sessionAddress);

		}

		customerAddressFormBean.setCustomerAddress(address);
		return customerAddressFormBean;
	}

	/**
	 * Set the request attributes after validation.
	 * 
	 * @param request -the request
	 * @param command -the command object
	 * @param errors - will be written back in case of any business error happens
	 * @throws Exception - Any exception
	 */
	@Override
	protected void onBindAndValidate(final HttpServletRequest request, final Object command, final BindException errors) throws Exception {

		final String checkoutShipping = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_EDIT_SHIPPING_ADDRESS, "");
		final String checkoutBilling = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_EDIT_BILLING_ADDRESS, "");
		final long addressUid = ServletRequestUtils.getLongParameter(request, WebConstants.REQUEST_ADDRESS_ID, 0);

		if (!"".equals(checkoutShipping)) {
			request.setAttribute(WebConstants.REQUEST_EDIT_SHIPPING_ADDRESS, checkoutShipping);
		}
		if (!"".equals(checkoutBilling)) {
			request.setAttribute(WebConstants.REQUEST_EDIT_BILLING_ADDRESS, checkoutBilling);
		}
		if (addressUid != 0L) {
			request.setAttribute(WebConstants.REQUEST_ADDRESS_ID, (new Long(addressUid)).toString());
		}

	}

	/**
	 * Set the customer service used to associate the address with a customer.
	 * 
	 * @param customerService the customer service.
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Set the view to return to when editing a billing address.
	 * 
	 * @param billingAddressView the billing address view
	 */
	public void setCheckoutBillingAddressView(final String billingAddressView) {
		this.billingAddressView = billingAddressView;
	}

	/**
	 * Set the view to return to when editing a shipping address.
	 * 
	 * @param shippingAddressView the shipping address view
	 */
	public void setCheckoutShippingAddressView(final String shippingAddressView) {
		this.shippingAddressView = shippingAddressView;
	}

}
