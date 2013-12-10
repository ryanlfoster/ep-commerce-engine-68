package com.elasticpath.sfweb.controller.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PayPalExpressSession;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.FrequencyAndRecurringPriceFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.sfweb.controller.BillingAndReviewFormBeanFactory;
import com.elasticpath.sfweb.formbean.BillingAndReviewFormBean;

/**
 * Default implementation of {@link BillingAndReviewFormBeanFactory}.
 */
public class BillingAndReviewFormBeanFactoryImpl extends ShoppingItemFormBeanContainerFactoryImpl implements BillingAndReviewFormBeanFactory {

	private CheckoutService checkoutService;

	@Override
	public BillingAndReviewFormBean createBillingAndReviewFormBean(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		final Customer customer = customerSession.getShopper().getCustomer();
		final PayPalExpressSession payPalSession = (PayPalExpressSession) request.getSession().getAttribute(
				WebConstants.PAYPAL_EXPRESS_CHECKOUT_SESSION);

		// Shipping and taxes
		checkoutService.calculateTaxAndBeforeTaxValue(shoppingCart);

		// Addresses
		final BillingAndReviewFormBean billingAndReviewFormBean = getBeanFactory().getBean(ContextIdNames.BILLING_AND_REVIEW_FORM_BEAN);
		billingAndReviewFormBean.setShippingAddress(shoppingCart.getShippingAddress());
		billingAndReviewFormBean.setBillingAddress(shoppingCart.getBillingAddress());

		final CreditCardPaymentGateway creditCardPaymentGateway = getCreditCardPaymentGateway();

		final Store store = getRequestHelper().getStoreConfig().getStore();

		final List<String> creditCardTypeList = getCreditCardTypeList(store);
		billingAndReviewFormBean.setCardTypes(creditCardTypeList);

		if (!store.isCreditCardCvv2Enabled()) {
			// Set valid code to avoid validation error when cvv2 is not in use.
			billingAndReviewFormBean.getOrderPaymentFormBean().setCvv2Code("000");
		}
		billingAndReviewFormBean.setValidateCvv2(
				creditCardPaymentGateway != null && creditCardPaymentGateway.isCvv2ValidationEnabled());
		billingAndReviewFormBean.setExistingCreditCards(customer.getCreditCards());

		// reset the credit card security code
		for (final CustomerCreditCard currCreditCard : billingAndReviewFormBean.getExistingCreditCards()) {
			if (store.isCreditCardCvv2Enabled()) {
				// clean the cvv2, customer need to input it each time.
				currCreditCard.setSecurityCode("");
			} else {
				// Set valid code to avoid validation error when cvv2 is not in
				// use.
				currCreditCard.setSecurityCode("000");
			}
		}

		if (payPalSession == null && customer.getCreditCards().size() > 0) {
			billingAndReviewFormBean.setSelectedPaymentOption(BillingAndReviewFormBean.PAYMENT_OPTION_EXISTING_CREDIT_CARD);
		} else if (payPalSession != null) {
			billingAndReviewFormBean.setSelectedPaymentOption(BillingAndReviewFormBean.PAYMENT_OPTION_PAYPAL_EXPRESS);
		}

		billingAndReviewFormBean.setCustomer(customer);

		if (getPaymentGateway(PaymentType.PAYPAL_EXPRESS) == null) {
			billingAndReviewFormBean.setPayPalEnabled(false);
		} else {
			billingAndReviewFormBean.setPayPalEnabled(true);
		}

		mapCartItemsToFormBeans(request, billingAndReviewFormBean);

		billingAndReviewFormBean.setLocale(shoppingCart.getLocale());

		billingAndReviewFormBean.setFrequencyMap(new FrequencyAndRecurringPriceFactory().getFrequencyMap(shoppingCart.getCartItems()));

		return billingAndReviewFormBean;
	}

	private CreditCardPaymentGateway getCreditCardPaymentGateway() {
		return (CreditCardPaymentGateway) getPaymentGateway(PaymentType.CREDITCARD);
	}

	/**
	 * @param store
	 * @return
	 */
	private List<String> getCreditCardTypeList(final Store store) {
		final List<String> creditCardTypeList = new LinkedList<String>();
		final Set<CreditCardType> cardTypes = store.getCreditCardTypes();
		for (CreditCardType card : cardTypes) {
			creditCardTypeList.add(card.getCreditCardType());
		}
		return creditCardTypeList;
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
	 * Gets a payment gateway by {@link PaymentType}.
	 *
	 * @param paymentType the payment type
	 * @return the payment gateway instance or null if not found
	 */
	protected PaymentGateway getPaymentGateway(final PaymentType paymentType) {
		final StoreConfig storeConfig = getRequestHelper().getStoreConfig();
		final Store store = storeConfig.getStore();
		return store.getPaymentGatewayMap().get(paymentType);
	}
}
