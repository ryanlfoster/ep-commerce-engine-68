/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.payment.PayPalExpressPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.exception.EpWebException;
import com.elasticpath.sfweb.service.WebCustomerSessionService;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;

/**
 * Spring MVC controller for continuing checkout process after returning from PayPal EC Shortcut.
 */
public class PayPalShortcutReturnControllerImpl extends AbstractEpControllerImpl {

	private static final Logger LOG = Logger.getLogger(PayPalShortcutReturnControllerImpl.class);

	private String standardShippingView;

	private String standardNoShippingView;

	private CustomerService customerService;

	private CheckoutService checkoutService;

	private WebCustomerSessionService webCustomerSessionService;

	private Geography geography;

	/**
	 * @param request the current <code>HttpServletRequest</code>
	 * @param response the <code>HttpServletResponse</code>
	 * @return the <code>ModelAndView</code> instance for the page to be displayed.
	 * @throws Exception if anything goes wrong
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final PayPalExpressSession payPalSession = (PayPalExpressSession) request.getSession().getAttribute(
				WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);

		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		
		validatePayPalExpressUsage(payPalSession, shoppingCart);

		final PayPalExpressPaymentGateway payPalExpressPayment = (PayPalExpressPaymentGateway) getRequestHelper().getStoreConfig().getStore()
				.getPaymentGatewayMap().get(PaymentType.PAYPAL_EXPRESS);

		final Map<String, String> customerDetails = payPalExpressPayment.getExpressCheckoutDetails(payPalSession.getToken());

		final String payPalAccountEmailId = customerDetails.get("EMAIL");
		payPalSession.setEmailId(payPalAccountEmailId);
		request.getSession().setAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION, payPalSession);

		if (!customerSession.isSignedIn()) {
			final Customer anonymousCustomer = createValidatedCustomer(customerDetails, payPalAccountEmailId);
			handleAnonymousSignIn(request, response, anonymousCustomer);
		}
		
		// set shipping address to the one returned from getECDetails (if shoppingCart requires shipping)
		if (shoppingCart.requiresShipping()) {
			final CustomerAddress shippingAddress = createShippingAddress(shoppingCart, customerDetails);

			final Customer customer = updateCustomerWithAddresses(customerSession.getShopper().getCustomer(), shippingAddress);
			customerSession.getShopper().setCustomer(customer);

			// Update the shopping cart with the already persisted address so that it has the uidPk with the proper value
			shoppingCart.setShippingAddress(customer.getPreferredShippingAddress());

			checkoutService.retrieveShippingOption(shoppingCart);
			checkoutService.calculateTaxAndBeforeTaxValue(shoppingCart);
		}

		String nextView;
		if (shoppingCart.requiresShipping()) {
			nextView = this.standardShippingView;
		} else {
			nextView = this.standardNoShippingView;
		}

		return new ModelAndView(nextView);
	}

	/**
	 * Validate PayPal Express usage to ensure correctness in process continuation.
	 * 
	 * @param payPalSession the PayPal Session
	 * @param shoppingCart the shopping cart
	 */
	private void validatePayPalExpressUsage(final PayPalExpressSession payPalSession, final ShoppingCart shoppingCart) {
		if (payPalSession == null) {
			throw new EpWebException("Session has timed out.");
		}
		
		// handle the case where a person has tried to bypass PayPal Express button suppression
		if (shoppingCart.hasRecurringPricedShoppingItems()) {
			payPalSession.clearSessionInformation();
			throw new EpWebException("Attempt to use PayPal Express on reoccurring cart items.");
		}
	}

	/**
	 * @param customer
	 * @param shippingAddress
	 * @return
	 */
	private Customer updateCustomerWithAddresses(final Customer customer, final CustomerAddress shippingAddress) {
		customer.addAddress(shippingAddress);
		customer.setPreferredBillingAddress(shippingAddress);
		customer.setPreferredShippingAddress(shippingAddress);

		return customerService.update(customer);
	}

	/**
	 * Delegate customer sign in to {@link WebCustomerSessionService#handleAnonymousSignIn} and set security context in spring security.
	 * 
	 * @param request
	 * @param response
	 * @param customer
	 */
	private void handleAnonymousSignIn(final HttpServletRequest request, final HttpServletResponse response, final Customer anonymousCustomer) {
		// update the customer session in session shopping cart
		final HttpServletFacadeFactory facadeFactory = getBean("httpServletFacadeFactory");
		final HttpServletRequestResponseFacade requestResponseFacade = facadeFactory.createRequestResponseFacade(request, response);

		webCustomerSessionService.handleGuestSignIn(requestResponseFacade, anonymousCustomer);

		// Add the Authentication object into the SecurityContextHolder so that Spring Security knows the customer is signed in
		Collection<? extends GrantedAuthority> authorities = anonymousCustomer.getAuthorities();
		List<GrantedAuthority> authoritiesList = new ArrayList<GrantedAuthority>(authorities.size());
		authoritiesList.addAll(authorities);
        final AnonymousAuthenticationToken authResult = new AnonymousAuthenticationToken("anonymousCustomer", 
                anonymousCustomer, authoritiesList);
        SecurityContextHolder.getContext().setAuthentication(authResult);
	}

	/**
	 * Create a validated customer.
	 * 
	 * @param customerDetails
	 * @param payPalAccountEmailId
	 * @return
	 */
	private Customer createValidatedCustomer(final Map<String, String> customerDetails, final String payPalAccountEmailId) {
		final Customer customer = getBean(ContextIdNames.CUSTOMER);
		customer.setAnonymous(true);
		customer.setEmail(payPalAccountEmailId);
		customer.setFirstName(customerDetails.get("FIRSTNAME"));
		customer.setLastName(customerDetails.get("LASTNAME"));
		customer.setStoreCode(getRequestHelper().getStoreConfig().getStoreCode());

		customerService.validateNewCustomer(customer);

		// Anonymous customers are currently persisted before checkout so that their addresses can be
		// assigned UIDPKs, which is necessary for manipulating addresses during checkout.
		customerService.add(customer);
		return customer;
	}

	/**
	 * Form a new shipping address out of the customer details provided by Paypal.
	 */
	private CustomerAddress createShippingAddress(final ShoppingCart shoppingCart, final Map<String, String> customerDetails) {
		final CustomerAddress shippingAddress = getBean(ContextIdNames.CUSTOMER_ADDRESS);
		final String[] names = customerDetails.get("SHIPTONAME").split(" ", 2);
		shippingAddress.setFirstName(names[0]);
		if (names.length > 1) {
			shippingAddress.setLastName(names[1]);
		}
		shippingAddress.setStreet1(customerDetails.get("SHIPTOSTREET"));
		shippingAddress.setStreet2(customerDetails.get("SHIPTOSTREET2"));
		shippingAddress.setCity(customerDetails.get("SHIPTOCITY"));
		shippingAddress.setCountry(customerDetails.get("SHIPTOCOUNTRYCODE"));
		final Set<String> subCountryCodes = geography.getSubCountryCodes(shippingAddress.getCountry());
		String shipToSubCountry = customerDetails.get("SHIPTOSTATE");

		// Need to convert the State/Province we get from PayPal back to a standard two-letter code
		for (String subCountryCode : subCountryCodes) {
			if (StringUtils.equals(
					geography.getSubCountryDisplayName(shippingAddress.getCountry(), subCountryCode, shoppingCart.getLocale()),
					shipToSubCountry)) {
				LOG.debug("Found sub-coutry code: " + subCountryCode);
				shipToSubCountry = subCountryCode;
				break;
			}
		}
		shippingAddress.setSubCountry(shipToSubCountry);
		shippingAddress.setZipOrPostalCode(customerDetails.get("SHIPTOZIP"));

		return shippingAddress;
	}

	/**
	 * Sets the name of the view upon successfully creating the order.
	 * 
	 * @param standardShippingView name of the view
	 */
	public final void setStandardShippingView(final String standardShippingView) {
		this.standardShippingView = standardShippingView;
	}

	/**
	 * Set the customer service.
	 * 
	 * @param customerService the customer service to set.
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Set the checkout service.
	 * 
	 * @param checkoutService the checkout service to set.
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	/**
	 * Set the web customer session service.
	 * 
	 * @param webCustomerSessionService the customer session service to set.
	 */
	public void setWebCustomerSessionService(final WebCustomerSessionService webCustomerSessionService) {
		this.webCustomerSessionService = webCustomerSessionService;
	}

	/**
	 * Sets the <code>Geography</code> instance.
	 * 
	 * @param geography the <code>Geography</code> instance
	 */
	public void setGeography(final Geography geography) {
		this.geography = geography;
	}

	/**
	 * @param standardNoShippingView the standardNoShippingView to set
	 */
	public void setStandardNoShippingView(final String standardNoShippingView) {
		this.standardNoShippingView = standardNoShippingView;
	}
}
