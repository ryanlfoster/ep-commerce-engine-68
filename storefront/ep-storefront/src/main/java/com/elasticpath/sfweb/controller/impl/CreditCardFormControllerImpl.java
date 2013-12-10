/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.controller.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.sfweb.EpSfWebException;
import com.elasticpath.sfweb.formbean.CreditCardFormBean;

/**
 * The Spring MVC controller for credit card editing page.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class CreditCardFormControllerImpl extends AbstractEpFormController {
	private static final Logger LOG = Logger.getLogger(CreditCardFormControllerImpl.class);

	private static final String DELETE_CREDIT_CARD_PARAMETER = "deleteCreditCard";

	private static final String IS_CHECKOUT_PARAMETER = "isCheckout";

	private CustomerService customerService;

	private SettingsReader settingsReader;

	private String checkoutView;

	private String errorView;

	private Utility utility;

	/**
	 * Handle the create-credit-card form submit.
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
		LOG.debug("CreditCardFormController: entering 'onSubmit' method...");

		final CreditCardFormBean creditCardFormBean = (CreditCardFormBean) command;
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		final long creditCardUid = creditCardFormBean.getCardUidPk();
		final CustomerCreditCard beanCard = createCreditCardFromBean(creditCardFormBean);
		CustomerCreditCard creditCard;

		if (creditCardUid == 0) { // Add new credit card
			/* The card number being passed in is unencrypted, so we need to encrypt it here */
			beanCard.encrypt();

			// Check if card already exists or not
			if (customer.getCreditCards().contains(beanCard)) {
				errors.reject("error.creditcard.exist");
				try {
					return super.showForm(request, response, errors);
				} catch (Exception e1) {
					throw new EpSfWebException("Caught an exception.", e1);
				}
			}
			creditCard = beanCard;

		} else { // Edit existing card
			creditCard = customer.getCreditCardByUid(creditCardUid);
			if (creditCard == null) {
				// Not a valid credit card for the customer
				throw new EpSfWebException("Credit Card not found");
			}
			// We can't change the card number, so copy this before comparing
			beanCard.setCardNumber(creditCard.getCardNumber());

			// If the default card has been set make sure we update it as it is not
			// covered by equals or copyFrom
			if (beanCard.isDefaultCard()) {
				creditCard.setDefaultCard(beanCard.isDefaultCard());
			}

			// If the cards have differences, copy the bean card to the session card
			if (!creditCard.equals(beanCard)) {
				creditCard.copyFrom(beanCard, false);
			}
		}

		if (request.getParameter(DELETE_CREDIT_CARD_PARAMETER) == null) {
			// Add or update
			if (creditCard.isPersisted()) {
				customer.updateCreditCard(creditCard);
			} else {
				customer.addCreditCard(creditCard);
			}
		} else {
			// Delete the credit card
			customer.removeCreditCard(creditCard);
		}

		final Customer updatedCustomer = customerService.update(customer);
		customerSession.getShopper().setCustomer(updatedCustomer);

		if (creditCardFormBean.isRequestFromCheckout()) {
			return new ModelAndView(this.checkoutView);
		}
		return new ModelAndView(getSuccessView());
	}

	/**
	 * Create a new credit card object from the form bean fields.
	 * 
	 * @param creditCardFormBean the form bean to get the info from
	 * @return a CustomerCreditCard object
	 */
	private CustomerCreditCard createCreditCardFromBean(final CreditCardFormBean creditCardFormBean) {
		final CustomerCreditCard creditCard = getBean(ContextIdNames.CUSTOMER_CREDIT_CARD);
		creditCard.setCardHolderName(creditCardFormBean.getCardHolderName());
		creditCard.setCardType(creditCardFormBean.getCardType());
		creditCard.setDefaultCard(creditCardFormBean.isDefaultCard());
		creditCard.setExpiryMonth(creditCardFormBean.getExpiryMonth());
		creditCard.setExpiryYear(creditCardFormBean.getExpiryYear());
		creditCard.setStartMonth(creditCardFormBean.getStartMonth());
		creditCard.setStartYear(creditCardFormBean.getStartYear());
		creditCard.setIssueNumber(creditCardFormBean.getIssueNumber());
		creditCard.setCardNumber(creditCardFormBean.getCardNumber());
		creditCard.setUidPk(creditCardFormBean.getCardUidPk());

		return creditCard;
	}

	/**
	 * Prepare the command object for the credit card add form.
	 * 
	 * @param request -the request
	 * @return the command object
	 */
	@Override
	public Object formBackingObject(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final Customer customer = customerSession.getShopper().getCustomer();
		final long creditCardUid = ServletRequestUtils.getLongParameter(request, WebConstants.REQUEST_CUSTOMER_CREDIT_CARD_ID, 0);
		CustomerCreditCard creditCard = null;
		final CreditCardFormBean creditCardFormBean = getBean(ContextIdNames.CREDIT_CARD_FORM_BEAN);
		creditCardFormBean.setCardUidPk(creditCardUid);

		if (creditCardUid == 0) {
			if (customer != null) {
				creditCardFormBean.setCardHolderName(customer.getFirstName() + " " + customer.getLastName());
			}
		} else {
			creditCard = customer.getCreditCardByUid(creditCardUid);
			if (creditCard == null) {
				// Not a valid credit card for the customer
				throw new EpSfWebException("Credit Card not found");
			}
			creditCardFormBean.setCardHolderName(creditCard.getCardHolderName());
			creditCardFormBean.setCardType(creditCard.getCardType());

			// Set fake card number to bypass validation.
			// The Masked card number will be displayed when editing an existing card.
			// The card number cannot be changed when editing a card.
			creditCardFormBean.setCardNumber("4111111111111111");
			creditCardFormBean.setMaskedCardNumber(creditCard.getMaskedCardNumber());
			creditCardFormBean.setExpiryMonth(creditCard.getExpiryMonth());
			creditCardFormBean.setExpiryYear(creditCard.getExpiryYear());
			creditCardFormBean.setStartMonth(creditCard.getStartMonth());
			creditCardFormBean.setStartYear(creditCard.getStartYear());
			creditCardFormBean.setIssueNumber(creditCard.getIssueNumber());
			creditCardFormBean.setDefaultCard(creditCard.isDefaultCard());

			request.setAttribute(WebConstants.REQUEST_CUSTOMER_CREDIT_CARD_ID, (new Long(creditCardUid)).toString());
		}

		final String isCheckout = ServletRequestUtils.getStringParameter(request, IS_CHECKOUT_PARAMETER, "");

		if (StringUtils.isNotBlank(isCheckout)) {
			creditCardFormBean.setRequestFromCheckout(true);
		}

		return creditCardFormBean;
	}

	/**
	 * @param request the current request
	 * @return the data model
	 */
	@Override
	protected Map<String, Map<String, String>> referenceData(final HttpServletRequest request) {
		final Map<String, Map<String, String>> model = new HashMap<String, Map<String, String>>();
		model.put("cardTypeMap", utility.getStoreCreditCardTypesMap(getRequestHelper().getStoreConfig().getStore().getCode()));
		model.put("yearMap", utility.getYearMap());
		model.put("monthMap", utility.getMonthMap());

		return model;
	}

	/**
	 * Override the default show form behaviour to check if storage of credit cards on the Customer account has been disabled. If so, redirect to an
	 * error page with a NOT FOUND error.
	 * 
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param errors validation errors holder
	 * @return the prepared form view, or null if handled directly
	 * @throws Exception in case of invalid state or argument
	 */
	@Override
	protected ModelAndView showForm(final HttpServletRequest request, final HttpServletResponse response, final BindException errors)
			throws Exception {
		final boolean showCC = settingsReader.getSettingValue("COMMERCE/SYSTEM/storeCustomerCreditCards").getBooleanValue();
		if (!showCC) {
			final Map<String, String> errorModel = new HashMap<String, String>();
			errorModel.put("errorKey", String.valueOf(HttpServletResponse.SC_NOT_FOUND));
			return new ModelAndView(errorView, errorModel);
		}
		return super.showForm(request, response, errors);
	}

	/**
	 * Set the customer service used to associate the credit card with a customer.
	 * 
	 * @param customerService the customer service.
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Set the view to be redirected to when editing a credit card from the checkout page.
	 * 
	 * @param checkoutView the view name
	 */
	public void setCheckoutView(final String checkoutView) {
		this.checkoutView = checkoutView;
	}

	/**
	 * Set the utility object.
	 * 
	 * @param utility the utility object.
	 */
	public void setUtility(final Utility utility) {
		this.utility = utility;
	}

	/**
	 * Get the settings reader.
	 * 
	 * @return the settingsReader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}

	/**
	 * Set the settings reader.
	 * 
	 * @param settingsReader the settingsReader to set
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	/**
	 * Set the view to be redirected to when an error occurs.
	 * 
	 * @param errorView the errorView to set
	 */
	public void setErrorView(final String errorView) {
		this.errorView = errorView;
	}

}
