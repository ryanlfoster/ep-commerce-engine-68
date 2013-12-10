package com.elasticpath.sfweb.controller.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.exception.UserStatusInactiveException;
import com.elasticpath.domain.catalog.InsufficientInventoryException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.CreditCardPaymentGateway;
import com.elasticpath.domain.payment.PayerAuthenticationException;
import com.elasticpath.domain.payment.PayerAuthenticationSession;
import com.elasticpath.domain.payment.impl.PayerAuthenticationSessionImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.AmountLimitExceededException;
import com.elasticpath.plugin.payment.exceptions.CardDeclinedException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.exceptions.InvalidAddressException;
import com.elasticpath.plugin.payment.exceptions.InvalidCVV2Exception;
import com.elasticpath.service.shoppingcart.CheckoutService;

/**
 * Spring controller for handling the response information from Access Control Server (ACS) after requesting payer authentication.
 */
public class PayerAuthenticationReturnController extends AbstractEpControllerImpl {

	private static final String PAYER_AUTHENTICATION_RESULT_PARAM = "PaRes";

	private static final Logger LOG = Logger.getLogger(CheckoutControllerImpl.class);

	private CheckoutService checkoutService;

	private String successView;

	private String returnView;

	/**
	 * Return the ModelAndView for the configured static view page.
	 * 
	 * @param request -the request
	 * @param response -the response
	 * @return - the ModleAndView instance for the static page.
	 * @throws Exception if anything goes wrong.
	 */
	@Override
	protected ModelAndView handleRequestInternal(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		LOG.debug("entering 'handleRequestInternal' method...");

		request.getSession().removeAttribute(WebConstants.PAYER_AUTHENTICATION_ENROLLMENT_SESSION);

		String nextView = this.returnView;
		try {
			final String payerAuthenticationResult = request.getParameter(PAYER_AUTHENTICATION_RESULT_PARAM);
			if (payerAuthenticationResult == null) {
				setErrorIntoSession(request, PayerAuthenticationSession.PAYMENT_GATEWAY_ERROR);
			} else {
				final CreditCardPaymentGateway creditCardPaymentGateway = (CreditCardPaymentGateway) getRequestHelper().getStoreConfig().getStore()
						.getPaymentGatewayMap().get(PaymentType.CREDITCARD);
				final OrderPayment orderPayment = (OrderPayment) request.getSession().getAttribute(WebConstants.ORDER_PAYMENT_SESSION);
				final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
				final Shopper shopper = customerSession.getShopper();
				final boolean validated = creditCardPaymentGateway.validateAuthentication(orderPayment, payerAuthenticationResult);
				if (validated) {
					final CheckoutResults result = checkoutService.checkout(shopper.getCurrentShoppingCart(), orderPayment);
					request.getSession().setAttribute(WebConstants.CHECKOUT_RESULTS, result);
					nextView = this.successView;
				} else {
					setErrorIntoSession(request, PayerAuthenticationSession.PAYER_AUTHENTICATE_INVALID);
				}

			}
		} catch (InsufficientInventoryException iie) {
			setErrorIntoSession(request, PayerAuthenticationSession.INSUFFICIENT_INVENTORY_ERROR);
		} catch (CardDeclinedException cde) {
			setErrorIntoSession(request, PayerAuthenticationSession.CARD_DECLINED_ERROR);
		} catch (CardExpiredException cee) {
			setErrorIntoSession(request, PayerAuthenticationSession.CARD_EXPIRED_ERROR);
		} catch (CardErrorException ce) {
			setErrorIntoSession(request, PayerAuthenticationSession.CARD_ERROR);
		} catch (InsufficientFundException ife) {
			setErrorIntoSession(request, PayerAuthenticationSession.INSUFFICIENT_FUND_ERROR);
		} catch (InvalidAddressException iae) {
			setErrorIntoSession(request, PayerAuthenticationSession.INVALID_ADDRESS_ERROR);
		} catch (AmountLimitExceededException alee) {
			setErrorIntoSession(request, PayerAuthenticationSession.AMOUNT_LIMIT_EXCEEDED_ERROR);
		} catch (InvalidCVV2Exception ice) {
			setErrorIntoSession(request, PayerAuthenticationSession.INVALID_CVV2_ERROR);
		} catch (PayerAuthenticationException payerE) {
			setErrorIntoSession(request, PayerAuthenticationSession.PAYER_AUTHENTICATE_INVALID);
		} catch (UserStatusInactiveException inactiveEx) {
			setErrorIntoSession(request, PayerAuthenticationSession.USER_STATUS_INACTIVE_ERROR);
		} catch (EpServiceException eSrvE) {
			setErrorIntoSession(request, PayerAuthenticationSession.PAYMENT_GATEWAY_ERROR);
		}
		return new ModelAndView(nextView);
	}

	private void setErrorIntoSession(final HttpServletRequest request, final int errorCode) {
		PayerAuthenticationSession payerAuthenticationSession = new PayerAuthenticationSessionImpl(errorCode);
		request.getSession().setAttribute(WebConstants.PAYER_AUTHENTICATION_SESSION, payerAuthenticationSession);
	}

	/**
	 * Sets the name of the view upon successfully validate.
	 * 
	 * @param successView name of the view
	 */
	public final void setSuccessView(final String successView) {
		this.successView = successView;
	}

	/**
	 * Sets the name of the view when an error has occured for validation.
	 * 
	 * @param returnView name of the view
	 */
	public final void setReturnView(final String returnView) {
		this.returnView = returnView;
	}

	/**
	 * Set the checkout service for processing a checkout.
	 * 
	 * @param checkoutService the checkout service
	 */
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

}
