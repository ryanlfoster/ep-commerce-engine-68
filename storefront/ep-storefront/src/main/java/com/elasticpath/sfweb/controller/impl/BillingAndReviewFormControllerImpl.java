/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.UserStatusInactiveException;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressSession;
import com.elasticpath.domain.payment.PayerAuthenticationSession;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.impl.PayPalExpressSessionImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.AmountLimitExceededException;
import com.elasticpath.plugin.payment.exceptions.CardDeclinedException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.exceptions.InsufficientGiftCertificateBalanceException;
import com.elasticpath.plugin.payment.exceptions.InvalidAddressException;
import com.elasticpath.plugin.payment.exceptions.InvalidCVV2Exception;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.sfweb.controller.BillingAndReviewFormBeanFactory;
import com.elasticpath.sfweb.formbean.BillingAndReviewFormBean;
import com.elasticpath.sfweb.formbean.OrderPaymentFormBean;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.listeners.ShoppingCartEventListener;

/**
 * The Spring MVC controller for specifying billing information and reviewing an order.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class BillingAndReviewFormControllerImpl extends AbstractEpFormController {

	private static final String CHECKOUT_PAYMENT_PROCESSING_ERROR = "checkout.paymentProcessingError";

	private static final String CHECKOUT_USER_INACTIVE_ERROR = "checkout.userInactiveError";

	private static final String CHECKOUT_CARD_EXPIRED_ERROR = "checkout.cardExpiredError";

	private static final String CHECKOUT_CARD_PRE_AUTH_ERROR = "checkout.cardPreAuthError";

	private static final String ERRORS_INSUFFICIENT_INVENTORY = "errors.insufficientInventory";

	private static final String ERRORS_PAYER_AUTHENTICATION_ERROR = "errors.payerAuthenticationError";

	private static final Logger LOG = Logger.getLogger(BillingAndReviewFormControllerImpl.class);

	private static final String STORE_CUSTOMER_CREDIT_CARD_PATH = "COMMERCE/SYSTEM/storeCustomerCreditCards";

	private CheckoutService checkoutService;

	private String continueShoppingPage;

	private String returnURL;

	private String termURL;

	private String enrollmentURL;

	private SettingsReader settingsReader;

	private ShoppingItemAssembler shoppingItemAssembler;

	private BillingAndReviewFormBeanFactory billingAndReviewFormBeanFactory;

	private ProductInventoryShoppingService productInventoryShoppingService;

	private final List<ShoppingCartEventListener> shoppingCartEventListeners = new ArrayList<ShoppingCartEventListener>();

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
		LOG.debug("BillingAndReviewFormController: entering 'onSubmit' method...");

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		final BillingAndReviewFormBean billingAndReviewFormBean = (BillingAndReviewFormBean) command;
		final PayPalExpressSession payPalSession = (PayPalExpressSession) request.getSession().getAttribute(
				WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);

		final OrderPayment orderPayment = createOrderPayment(billingAndReviewFormBean.getOrderPaymentFormBean());
		final Map<String, Object> model = new HashMap<String, Object>();

		ModelAndView resultView = new ModelAndView(getSuccessView(), model);

		try {
			// Customer is using Gift Certificates and total balance is less than the total amount hence no Credit Card needed
			if (BigDecimal.ZERO.compareTo(shoppingCart.getTotal()) == 0 && !shoppingCart.getAppliedGiftCertificates().isEmpty()) {
				resultView = handlePaymentWithGiftCertificates(shoppingCart, orderPayment, request, billingAndReviewFormBean);
			} else if (billingAndReviewFormBean.getSelectedPaymentOption().equals(BillingAndReviewFormBean.PAYMENT_OPTION_PAYPAL_EXPRESS)) {
				// Proceed with next step of PayPal checkout

				if (payPalSession == null || payPalSession.getToken() == null) {
					// mark paypal express checkout
					resultView = handlePayPalExpressPayment(request, shoppingCart);
				} else {
					// shortcut paypal express checkout
					handleShortCutExpressPayment(shoppingCart, orderPayment, payPalSession.getToken(), request);
					// We're finishing a Paypal Express Short Cut Payment.
					// Clear sessions
					request.getSession().removeAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);
				}
			} else if (billingAndReviewFormBean.getSelectedPaymentOption().equals(BillingAndReviewFormBean.PAYMENT_OPTION_EXISTING_CREDIT_CARD)) {
				// Customer will pay with an existing card
				resultView = handlePaymentWithExistingCreditCard(shoppingCart, orderPayment, billingAndReviewFormBean, request);
			} else {
				// Customer will pay with new credit card info they've entered on the BillingAndReview page
				resultView = handlePaymentWithNewCreditCard(shoppingCart, orderPayment, billingAndReviewFormBean, request);
			}
		} catch (final InsufficientGiftCertificateBalanceException iie) {
			resultView = getErrorView("errors.insufficientGiftCertificateBalance", request, response, errors);
		} catch (final InsufficientInventoryException iie) {
			resultView = getErrorView(ERRORS_INSUFFICIENT_INVENTORY, request, response, errors);
		} catch (final CardDeclinedException cde) {
			resultView = getErrorView(CHECKOUT_CARD_PRE_AUTH_ERROR, request, response, errors);
		} catch (final CardExpiredException cee) {
			resultView = getErrorView(CHECKOUT_CARD_EXPIRED_ERROR, request, response, errors);
		} catch (final CardErrorException ceex) {
			resultView = getErrorView(CHECKOUT_CARD_PRE_AUTH_ERROR, request, response, errors);
		} catch (final InsufficientFundException isfex) {
			resultView = getErrorView(isfex.getClass().getName(), request, response, errors);
		} catch (final InvalidAddressException iaex) {
			resultView = getErrorView(iaex.getClass().getName(), request, response, errors);
		} catch (final AmountLimitExceededException alex) {
			resultView = getErrorView(alex.getClass().getName(), request, response, errors);
		} catch (final InvalidCVV2Exception ic2e) {
			resultView = getErrorView(ic2e.getClass().getName(), request, response, errors);
		} catch (final UserStatusInactiveException inactiveEx) {
			resultView = getErrorView(CHECKOUT_USER_INACTIVE_ERROR, request, response, errors);
		} catch (final EpServiceException epse) {
			resultView = getErrorView(CHECKOUT_PAYMENT_PROCESSING_ERROR, request, response, errors);
		}

		if (payPalSession != null) {
			// Clear sessions contents after checkout
			payPalSession.clearSessionInformation();
		}
		return resultView;
	}

	/**
	 * Handles payment with new credit card.
	 *
	 * @param shoppingCart the shopping cart
	 * @param orderPayment the {@link OrderPayment}
	 * @param billingAndReviewFormBean the form bean
	 * @param request the http request
	 * @return the next view
	 */
	protected ModelAndView handlePaymentWithNewCreditCard(final ShoppingCart shoppingCart, final OrderPayment orderPayment,
			final BillingAndReviewFormBean billingAndReviewFormBean, final HttpServletRequest request) {

		final CustomerSession customerSession = updateCustomerAndOrderPaymentForNewCreditCard(orderPayment, request, billingAndReviewFormBean);

		// Determine next page depending on whether the card is
		// enrolled for 3-D Secure
		String resultViewString;
		final PayerAuthenticationEnrollmentResult payerAuthEnrollmentValue = getCreditCardPaymentGateway().checkEnrollment(shoppingCart,
				orderPayment);

		if (payerAuthEnrollmentValue.is3DSecureEnrolled()) {
			resultViewString = getRedirectURL(request, shoppingCart, payerAuthEnrollmentValue);
			request.getSession().setAttribute(WebConstants.ORDER_PAYMENT_SESSION, orderPayment);
		} else {
			// !! THIS IS WHERE WE DO CHECKOUT!
			final CheckoutResults result = checkoutService.checkout(shoppingCart, orderPayment);
			request.getSession().setAttribute(WebConstants.CHECKOUT_RESULTS, result);
			notifyShoppingCartEventListeners(customerSession, request);
			resultViewString = getSuccessView();
		}
		
		return new ModelAndView(resultViewString);
	}

	/** */
	private CustomerSession updateCustomerAndOrderPaymentForNewCreditCard(final OrderPayment orderPayment,
							final HttpServletRequest request, final BillingAndReviewFormBean billingAndReviewFormBean) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();

		final boolean storeCustomerCreditCardsEnabled = settingsReader.getSettingValue(STORE_CUSTOMER_CREDIT_CARD_PATH).getBooleanValue();
		if (billingAndReviewFormBean.isSaveCreditCardForFutureUse()) {
			if (storeCustomerCreditCardsEnabled) {
				customer.addCreditCard(orderPayment.extractCreditCard());
			} else {
				LOG.error("An attempt to save a customer credit card for future use was made but the system is not currently configured to do so.");
			}
		}

		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		return customerSession;
	}

	/**
	 * Handles payment with an existing credit card.
	 *
	 * @param shoppingCart the shopping cart
	 * @param orderPayment the order payment
	 * @param billingAndReviewFormBean the form bean
	 * @param request the http request
	 * @return the next view
	 */
	protected ModelAndView handlePaymentWithExistingCreditCard(final ShoppingCart shoppingCart, final OrderPayment orderPayment,
			final BillingAndReviewFormBean billingAndReviewFormBean, final HttpServletRequest request) {

		final CustomerSession customerSession = updateCustomerAndOrderPaymentForExistingCreditCard(orderPayment, request, billingAndReviewFormBean);
		// Determine next page depending on whether the card is
		// enrolled for 3-D Secure
		String resultViewString;
		final PayerAuthenticationEnrollmentResult payerAuthEnrollmentResult = getCreditCardPaymentGateway().checkEnrollment(shoppingCart,
																																orderPayment);
		if (payerAuthEnrollmentResult.is3DSecureEnrolled()) {
			resultViewString = getRedirectURL(request, shoppingCart, payerAuthEnrollmentResult);
			request.getSession().setAttribute(WebConstants.ORDER_PAYMENT_SESSION, orderPayment);
		} else {
			final CheckoutResults result = checkoutService.checkout(shoppingCart, orderPayment);
			notifyShoppingCartEventListeners(customerSession, request);
			request.getSession().setAttribute(WebConstants.CHECKOUT_RESULTS, result);
			resultViewString = getSuccessView();
		}

		return new ModelAndView(resultViewString);
	}

	/** */
	private CustomerSession updateCustomerAndOrderPaymentForExistingCreditCard(final OrderPayment orderPayment, final HttpServletRequest request,
														final BillingAndReviewFormBean billingAndReviewFormBean) {
		final CustomerCreditCard selectedCreditCard = billingAndReviewFormBean.getSelectedExistingCreditCard();
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		selectedCreditCard.setDefaultCard(true);
		customer.updateCreditCard(selectedCreditCard);

		orderPayment.useCreditCard(selectedCreditCard);
		return customerSession;
	}

	private CreditCardPaymentGateway getCreditCardPaymentGateway() {
		return (CreditCardPaymentGateway) getPaymentGateway(PaymentType.CREDITCARD);
	}

	private String getRedirectURL(final HttpServletRequest request, final ShoppingCart shoppingCart,
			final PayerAuthenticationEnrollmentResult payerAuthEnrollmentValue) {

		final String curRequestUrl = request.getRequestURL().toString();
		final String returnUrl = curRequestUrl.substring(0, curRequestUrl.lastIndexOf('/') + 1).concat(this.termURL);

		payerAuthEnrollmentValue.setTermURL(returnUrl);
		// MerchantData could be empty.
		payerAuthEnrollmentValue.setMerchantData(shoppingCart.getGuid());
		request.getSession().setAttribute(WebConstants.PAYER_AUTHENTICATION_ENROLLMENT_SESSION, payerAuthEnrollmentValue);

		final StringBuffer redirectURL = new StringBuffer("redirect:");
		redirectURL.append(this.enrollmentURL);

		return redirectURL.toString();
	}

	/**
	 * Sets up the Shopping Cart's OrderPayment for the checkout process when paying exclusively with gift certificates.
	 *
	 * @param shoppingCart the shopping cart
	 * @param orderPayment the order payment consisting of only gift certificates
	 * @param billingAndReviewFormBean the form bean
	 * @param request the servlet request object
	 * @return the next page the customer needs to go
	 */
	protected ModelAndView handlePaymentWithGiftCertificates(final ShoppingCart shoppingCart, final OrderPayment orderPayment,
			final HttpServletRequest request, final BillingAndReviewFormBean billingAndReviewFormBean) {

		ModelAndView resultView = new ModelAndView(getSuccessView());
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);

		// for recurring items we need to capture an other form of payment.
		if (shoppingCart.hasRecurringPricedShoppingItems()) {
			if (BillingAndReviewFormBean.PAYMENT_OPTION_EXISTING_CREDIT_CARD.equals(billingAndReviewFormBean.getSelectedPaymentOption())) {
				// Customer will pay with an existing card
				updateCustomerAndOrderPaymentForExistingCreditCard(orderPayment, request, billingAndReviewFormBean);
			} else {
				// Customer will pay with new credit card info they've entered on the BillingAndReview page
				updateCustomerAndOrderPaymentForNewCreditCard(orderPayment, request, billingAndReviewFormBean);
			}
			final PayerAuthenticationEnrollmentResult payerAuthEnrollmentValue = getCreditCardPaymentGateway().checkEnrollment(shoppingCart,
					orderPayment);
			if (payerAuthEnrollmentValue.is3DSecureEnrolled()) {
				request.getSession().setAttribute(WebConstants.ORDER_PAYMENT_SESSION, orderPayment);
				return new ModelAndView(getRedirectURL(request, shoppingCart, payerAuthEnrollmentValue));

			}
		} else {
			orderPayment.setPaymentMethod(PaymentType.GIFT_CERTIFICATE);
		}
		final CheckoutResults result = checkoutService.checkout(shoppingCart, orderPayment);
		notifyShoppingCartEventListeners(customerSession, request);
		request.getSession().setAttribute(WebConstants.CHECKOUT_RESULTS, result);

		return resultView;
	}

	/**
	 * Handles a finishing paypal express payment request. <br>
	 * This method does the checkout after a confirmation of completed payment has been received.
	 *
	 * @param shoppingCart the shopping card
	 * @param orderPayment the order payment
	 * @param token the paypal token
	 * @param request the http request
	 */
	protected void handleShortCutExpressPayment(final ShoppingCart shoppingCart, final OrderPayment orderPayment, final String token,
			final HttpServletRequest request) {
		orderPayment.setPaymentMethod(PaymentType.PAYPAL_EXPRESS);
		orderPayment.setGatewayToken(token);
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final CheckoutResults result = checkoutService.checkout(shoppingCart, orderPayment);
		notifyShoppingCartEventListeners(customerSession, request);
		request.getSession().setAttribute(WebConstants.CHECKOUT_RESULTS, result);
	}

	/**
	 * Creates a new {@link OrderPayment} object out of a order payment form bean.
	 *
	 * @param orderPaymentFormBean the order payment bean
	 * @return an OrderPayment instance
	 */
	protected OrderPayment createOrderPayment(final OrderPaymentFormBean orderPaymentFormBean) {
		final OrderPayment orderPayment = getBean(ContextIdNames.ORDER_PAYMENT);

		orderPayment.setCvv2Code(orderPaymentFormBean.getCvv2Code());
		orderPayment.setCardHolderName(orderPaymentFormBean.getCardHolderName());
		orderPayment.setCardType(orderPaymentFormBean.getCardType());
		orderPayment.setExpiryMonth(orderPaymentFormBean.getExpiryMonth());
		orderPayment.setExpiryYear(orderPaymentFormBean.getExpiryYear());
		orderPayment.setUnencryptedCardNumber(orderPaymentFormBean.getUnencryptedCardNumber());

		return orderPayment;
	}

	/**
	 * Handles a paypal express payment.
	 *
	 * @param request the http request
	 * @param shoppingCart the shopping cart
	 * @return the next view
	 */
	protected ModelAndView handlePayPalExpressPayment(final HttpServletRequest request, final ShoppingCart shoppingCart) {
		ModelAndView resultView;
		final PayPalExpressPaymentGateway payPalExpressPayment = (PayPalExpressPaymentGateway) getPaymentGateway(PaymentType.PAYPAL_EXPRESS);

		final String curRequestUrl = request.getRequestURL().toString();
		final String returnUrl = curRequestUrl.substring(0, curRequestUrl.lastIndexOf('/') + 1).concat(this.returnURL);
		final String tokenStr = payPalExpressPayment.setExpressMarkCheckout(shoppingCart, returnUrl, request.getRequestURL().toString());
		// save paypal token string into session
		request.getSession().setAttribute(WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION, new PayPalExpressSessionImpl(tokenStr));
		final StringBuffer urlSB = new StringBuffer("redirect:");
		urlSB.append(payPalExpressPayment.getPropertiesMap().get("paypalExpressCheckoutURL").getValue());
		urlSB.append(tokenStr);
		urlSB.append("&useraction=commit");
		resultView = new ModelAndView(urlSB.toString());
		return resultView;
	}

	/**
	 * Gets a payment gateway by {@link PaymentType}.
	 *
	 * @param paymentType the payment type
	 * @return the payment gateway instance or null if not found
	 */
	protected PaymentGateway getPaymentGateway(final PaymentType paymentType) {
		return getRequestHelper().getStoreConfig().getStore().getPaymentGatewayMap().get(paymentType);
	}

	/**
	 * Prepare the command object for the address add form.
	 *
	 * @param request the request
	 * @return the command object
	 */
	@Override
	public Object formBackingObject(final HttpServletRequest request) {
		return billingAndReviewFormBeanFactory.createBillingAndReviewFormBean(request);
	}

	/**
	 * Handle the initial request.
	 *
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		if (shoppingCart.requiresShipping() && shoppingCart.getShippingAddress() == null) {
			return new ModelAndView("redirect:" + "/shipping-address.ep");
		}

		final PayPalExpressSession payPalSession = (PayPalExpressSession) request.getSession().getAttribute(
				WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);
		final BindException errors = this.getErrorsForNewForm(request);

		PayerAuthenticationSession payerAuthenticationSession = (PayerAuthenticationSession) request.getSession().getAttribute(
				WebConstants.PAYER_AUTHENTICATION_SESSION);

		ModelAndView resultView = null;

		if (shoppingCart.getNumItems() == 0) {
			return new ModelAndView(this.continueShoppingPage);
		}

		if (payPalSession != null && payPalSession.getEmailId() == null) {
			/* paypal mark payment starts since mark payment does not have email id */
			resultView = getResultViewFromPayPalSessionStatus(request, response, payPalSession.getStatus(), errors);
			payPalSession.clearSessionInformation();
			payPalSession.setStatus(PayPalExpressSession.ALL_GOOD);
			return resultView;
		}

		if (payerAuthenticationSession != null && payerAuthenticationSession.getStatus() != PayerAuthenticationSession.ALL_GOOD) {
			resultView = getResultViewFromPayerAuthenticationStatus(request, response, errors, payerAuthenticationSession.getStatus());
			request.getSession().removeAttribute(WebConstants.PAYER_AUTHENTICATION_SESSION);
			return resultView;
		}

		return super.handleRequestInternal(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @param errors
	 * @param status
	 * @return
	 * @throws Exception
	 */
	private ModelAndView getResultViewFromPayerAuthenticationStatus(final HttpServletRequest request, final HttpServletResponse response,
			final BindException errors, final int status) throws Exception {
		ModelAndView resultView;
		switch (status) {
		case PayerAuthenticationSession.PAYER_AUTHENTICATE_INVALID:
			resultView = getErrorView(ERRORS_PAYER_AUTHENTICATION_ERROR, request, response, errors);
			break;
		case PayerAuthenticationSession.PAYMENT_GATEWAY_ERROR:
			resultView = getErrorView(CHECKOUT_PAYMENT_PROCESSING_ERROR, request, response, errors);
			break;
		case PayerAuthenticationSession.INSUFFICIENT_FUND_ERROR:
			resultView = getErrorView(InsufficientFundException.class.getName(), request, response, errors);
			break;
		case PayerAuthenticationSession.AMOUNT_LIMIT_EXCEEDED_ERROR:
			resultView = getErrorView(AmountLimitExceededException.class.getName(), request, response, errors);
			break;
		case PayerAuthenticationSession.USER_STATUS_INACTIVE_ERROR:
			resultView = getErrorView(CHECKOUT_USER_INACTIVE_ERROR, request, response, errors);
			break;
		case PayerAuthenticationSession.CARD_DECLINED_ERROR:
			resultView = getErrorView(CHECKOUT_CARD_PRE_AUTH_ERROR, request, response, errors);
			break;
		case PayerAuthenticationSession.CARD_EXPIRED_ERROR:
			resultView = getErrorView(CHECKOUT_CARD_EXPIRED_ERROR, request, response, errors);
			break;
		case PayerAuthenticationSession.CARD_ERROR:
			resultView = getErrorView(CHECKOUT_CARD_PRE_AUTH_ERROR, request, response, errors);
			break;
		case PayerAuthenticationSession.INVALID_ADDRESS_ERROR:
			resultView = getErrorView(InvalidAddressException.class.getName(), request, response, errors);
			break;
		case PayerAuthenticationSession.INVALID_CVV2_ERROR:
			resultView = getErrorView(InvalidCVV2Exception.class.getName(), request, response, errors);
			break;
		case PayerAuthenticationSession.INSUFFICIENT_INVENTORY_ERROR:
			resultView = getErrorView(ERRORS_INSUFFICIENT_INVENTORY, request, response, errors);
			break;
		default:
			resultView = super.handleRequestInternal(request, response);
			break;
		}
		return resultView;
	}

	/**
	 * @param request
	 * @param response
	 * @param payPalStatus
	 * @param errors
	 * @return
	 * @throws Exception
	 */
	private ModelAndView getResultViewFromPayPalSessionStatus(final HttpServletRequest request, final HttpServletResponse response,
			final int payPalStatus, final BindException errors) throws Exception {
		ModelAndView resultView;
		if (payPalStatus == PayPalExpressSession.INSUFFICIENT_INVENTORY_ERROR) {
			resultView = getErrorView(ERRORS_INSUFFICIENT_INVENTORY, request, response, errors);
		} else if (payPalStatus == PayPalExpressSession.INSUFFICIENT_FUND_ERROR) {
			resultView = getErrorView(InsufficientFundException.class.getName(), request, response, errors);
		} else if (payPalStatus == PayPalExpressSession.AMOUNT_LIMIT_EXCEEDED_ERROR) {
			resultView = getErrorView(AmountLimitExceededException.class.getName(), request, response, errors);
		} else if (payPalStatus == PayPalExpressSession.USER_STATUS_INACTIVE_ERROR) {
			resultView = getErrorView(CHECKOUT_USER_INACTIVE_ERROR, request, response, errors);
		} else if (payPalStatus == PayPalExpressSession.PAYMENT_GATEWAY_ERROR) {
			resultView = getErrorView(CHECKOUT_PAYMENT_PROCESSING_ERROR, request, response, errors);
		} else {
			resultView = super.handleRequestInternal(request, response);
		}
		return resultView;
	}

	/**
	 * Reference data used for the rendering of the underlying forms.
	 *
	 * @param request HttpServletRequest
	 * @return reference data used for rendering forms
	 * @throws Exception exception
	 */
	@Override
	protected Map<String, Object> referenceData(final HttpServletRequest request) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("saveCustomerCreditCards", settingsReader.getSettingValue(STORE_CUSTOMER_CREDIT_CARD_PATH).getBooleanValue());

		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		modelMap.put("availabilityMap", getAvailabilityMap(shoppingCart.getCartItems(), shoppingCart.getStore().getWarehouse(), shoppingCart
				.getStore()));
		modelMap.put("selectedShippingServiceLevel", shoppingCart.getSelectedShippingServiceLevel());
		modelMap.put("paymentRequired", isPaymentRequired(shoppingCart));
		modelMap.put("hasRecurringPricedItems", shoppingCart.hasRecurringPricedShoppingItems());
		return modelMap;
	}

	/**
	 * Check is payment is required.
	 * @param shoppingCart the shopping cart
	 * @return true if payment is required
	 */
	protected boolean isPaymentRequired(final ShoppingCart shoppingCart) {
		return shoppingCart.getTotal().floatValue() > 0 || shoppingCart.hasRecurringPricedShoppingItems();
	}

	/**
	 * @param shopingItems list of shopping items
	 * @param warehouse the warehouse to look up
	 * @param store the store to check inventory
	 * @return the availability of shopping items from allocation service
	 */
	protected Map<Long, SkuInventoryDetails> getAvailabilityMap(final List<ShoppingItem> shopingItems, final Warehouse warehouse, final Store store) {
		Map<Long, SkuInventoryDetails> availabilityMap = new HashMap<Long, SkuInventoryDetails>();
		for (ShoppingItem item : shopingItems) {

			ShoppingItemDto dto = getShoppingItemAssembler().assembleShoppingItemDtoFrom(item);
			SkuInventoryDetails skuInventoryDetails = productInventoryShoppingService.getSkuInventoryDetails(item.getProductSku(), store, dto);

			availabilityMap.put(item.getUidPk(), skuInventoryDetails);
		}
		return availabilityMap;
	}

	/**
	 * Sends events to {@link ShoppingCartEventListener}.
	 *
	 * @param customerSession the customer session
	 * @param request the {@link HttpServletRequest}
	 */
	protected void notifyShoppingCartEventListeners(final CustomerSession customerSession, final HttpServletRequest request) {
		final HttpServletFacadeFactory facadeFactory = getBean("httpServletFacadeFactory");
		final HttpServletRequestFacade requestFacade = facadeFactory.createRequestFacade(request);

		for (ShoppingCartEventListener listener : getShoppingCartEventListeners()) {
			listener.execute(customerSession, requestFacade);
		}
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
	 * Set the page to be redirected to when the cart is empty.
	 *
	 * @param continueShoppingPage the page to redirect to
	 */
	public void setContinueShoppingPage(final String continueShoppingPage) {
		this.continueShoppingPage = continueShoppingPage;
	}

	/**
	 * Set the page to be redirected to when paypal commits payment.
	 *
	 * @param returnURL the page to redirect to
	 */
	public void setReturnURL(final String returnURL) {
		this.returnURL = returnURL;
	}

	/**
	 * Set the page to be redirected to when payer authenticated for bank commits payment.
	 *
	 * @param termURL the page to redirect to
	 */
	public void setTermURL(final String termURL) {
		this.termURL = termURL;
	}

	/**
	 * Set the page to be redirected to when payer authenticated for bank commits payment.
	 *
	 * @param enrollmentURL the page to redirect to
	 */
	public void setEnrollmentURL(final String enrollmentURL) {
		this.enrollmentURL = enrollmentURL;
	}

	/**
	 * Get the settings reader to be used for retrieving settings.
	 *
	 * @return the settingsReader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the settings reader to be used for retrieving settings.
	 *
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * @param billingAndReviewFormBeanFactory The billing and review form bean factory to set.
	 */
	public void setBillingAndReviewFormBeanFactory(final BillingAndReviewFormBeanFactory billingAndReviewFormBeanFactory) {
		this.billingAndReviewFormBeanFactory = billingAndReviewFormBeanFactory;
	}

	/**
	 * @param shoppingItemAssembler the shoppingItemAssembler to set
	 */
	public void setShoppingItemAssembler(final ShoppingItemAssembler shoppingItemAssembler) {
		this.shoppingItemAssembler = shoppingItemAssembler;
	}

	/**
	 * @return the shoppingItemAssembler
	 */
	public ShoppingItemAssembler getShoppingItemAssembler() {
		return shoppingItemAssembler;
	}

	/**
	 * @param productInventoryShoppingService the {@link ProductInventoryShoppingService}
	 */
	public void setProductInventoryShoppingService(final ProductInventoryShoppingService productInventoryShoppingService) {
		this.productInventoryShoppingService = productInventoryShoppingService;
	}

	/**
	 * Set the bunch of {@link ShoppingCartEventListener} to reflect shopping catr changes into tag set.
	 *
	 * @param shoppingCartEventListeners set of shopping cart listeners.
	 */
	public void setShoppingCartEventListeners(final List<ShoppingCartEventListener> shoppingCartEventListeners) {
		this.shoppingCartEventListeners.addAll(shoppingCartEventListeners);
	}

	/**
	 * Add the {@link ShoppingCartEventListener} to reflect shopping catr changes into tag set.
	 *
	 * @param shoppingCartEventListener shopping cart listeners
	 */
	public void addShoppingCartEventListener(final ShoppingCartEventListener shoppingCartEventListener) {
		this.shoppingCartEventListeners.add(shoppingCartEventListener);
	}

	/**
	 * Get the set of configured event shopping listeners.
	 *
	 * @return set of configured event shopping listeners
	 */
	public List<ShoppingCartEventListener> getShoppingCartEventListeners() {
		return shoppingCartEventListeners;
	}

}
