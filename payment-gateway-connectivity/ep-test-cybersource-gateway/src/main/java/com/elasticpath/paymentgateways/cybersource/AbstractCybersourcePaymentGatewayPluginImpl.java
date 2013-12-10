package com.elasticpath.paymentgateways.cybersource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.cybersource.ws.client.Client;
import com.cybersource.ws.client.ClientException;
import com.cybersource.ws.client.FaultException;
import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.RefundCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.VoidCaptureCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.dto.impl.MoneyImpl;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthValidationValueDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthenticationEnrollmentResultDtoImpl;
import com.elasticpath.plugin.payment.exceptions.AuthorizedAmountExceededException;
import com.elasticpath.plugin.payment.exceptions.CardDeclinedException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.spi.AbstractCreditCardPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Abstract Cybersource payment gateway implementation.
 *
 */
public abstract class AbstractCybersourcePaymentGatewayPluginImpl extends AbstractCreditCardPaymentGatewayPluginSPI
		implements CaptureCapability, RefundCapability, ReversePreAuthorizationCapability, VoidCaptureCapability {

	private static final String PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT = "purchaseTotals_grandTotalAmount";

	private static final String CARD_EXPIRATION_YEAR = "card_expirationYear";

	private static final String CARD_EXPIRATION_MONTH = "card_expirationMonth";

	private static final String CARD_ACCOUNT_NUMBER = "card_accountNumber";

	private static final String ITEM_PREFIX = "item_";

	/** Determines the number of digits in the random portion of the reference code. */
	private static final int RANDOM_NUMBER_MULTIPLIER = 1000;

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;

	private static final String CYBERSOURCE_REASON_CODE = "Cybersource Reason Code: ";

	private static final Logger LOG = Logger.getLogger(AbstractCybersourcePaymentGatewayPluginImpl.class);

	private static final String MERCHANT_REFERENCE_CODE = "merchantReferenceCode";

	private static final String DECISION = "decision";

	private static final String CYBERSOURCE_CLIENT_EXCEPTION = "Cybersource client exception: ";

	private static final String CYBERSOURCE_FAULT_EXCEPTION = "Cybersource fault exception: ";

	private static final String REASON_CODE = "reasonCode";

	private static final String REQUEST_ID = "requestID";

	private static final String REQUEST_TOKEN = "requestToken";

	private static final String MERCHANT_ID = "merchantID";

	private static final String TRUE = "true";

	private static final String CYBER_DECISION_ERROR = "ERROR";

	private static final String CYBER_DECISION_ACCEPT = "ACCEPT";

	private static final String CYBER_DECISION_REJECT = "REJECT";

	private static final String CYBER_CARD_CREDIT_EXCEEDED = "210";

	private static final String CYBER_GENERAL_DECLINE = "203";

	private static final String CYBER_INSUFFICIENT_FUNDS = "204";

	private static final String CYBER_CARD_EXPIRED = "202";

	private static final String CYBER_CV_CHECK_FAILED = "230";
	
	private static final String CYBER_AUTHORIZATION_AMOUNT_EXCEEDED = "235";
	
	private static final String KEYS_DIRECTORY = "keysDirectory";

	private static final String PURCHASE_TOTALS_CURRENCY = "purchaseTotals_currency";

	private static final String PAYER_AUTH_ENROLL = "475";

	private static final String REASON_CODE_SUCCESSFUL = "100";

	private static final String REASON_CODE_SYS_ERROR = "150";

	private static final String PAYER_AUTH_ACS_URL = "payerAuthEnrollReply_acsURL";

	private static final String PAYER_AUTH_PAREQ = "payerAuthEnrollReply_paReq";

	private static final String TYPE_OF_VISA = "001";

	private static final String TYPE_OF_MASTERCARD = "002";

	private static final String TYPE_OF_JCB = "007";

	private Map<String, String> supportedCardTypes;

	/**
	 * Creates an item request map to be used in the Cybersource transaction from item information.
	 * 
	 * @param number number of item.
	 * @param productName the product name
	 * @param productSku the product sku
	 * @param quantity the quantity of the sku
	 * @param taxAmount the tax amount
	 * @param unitPrice the unit price
	 * @return the request map representation of an item.
	 */
	protected Map<String, String> createItem(final int number, final String productName, final String productSku,
			final int quantity, final BigDecimal taxAmount, final BigDecimal unitPrice) {
		Map<String, String> itemMap = new HashMap<String, String>();

		itemMap.put(ITEM_PREFIX + number + "_productName", productName);
		itemMap.put(ITEM_PREFIX + number + "_productSKU", productSku);
		itemMap.put(ITEM_PREFIX + number + "_quantity", String.valueOf(quantity));

		if (taxAmount != null) {
			itemMap.put(ITEM_PREFIX + number + "_taxAmount", convertToString(taxAmount));
		}
		if (unitPrice != null) {
			itemMap.put(ITEM_PREFIX + number + "_unitPrice", convertToString(unitPrice));
		}

		return itemMap;
	}

	/**
	 * Sets the reference code for the given <code>OrderPayment</code>.
	 *
	 * @param payment the payment whose reference code is to be set
	 * @param request the map specifying the parameters of the Cybersource request
	 */
	protected void setRequestReferenceCode(final CardDetailsPaymentMethod payment, final Map<String, String> request) {
		if (payment.getReferenceId() == null || "".equals(payment.getReferenceId())) {
			request.put(MERCHANT_REFERENCE_CODE, this.generateReferenceCode());
		} else {
			request.put(MERCHANT_REFERENCE_CODE, payment.getReferenceId());
		}
	}

	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital"
	 * processor and authorizations cannot be reversed using the test server and card info because
	 * the auth codes are not valid (Cybersource).
	 *
	 * @param payment the payment that was previously pre-authorized
	 * @throws CardExpiredException if the card has expired
	 * @throws CardErrorException if there was an error processing the given information
	 * @throws PaymentGatewayException if the payment processing fails
	 */
	@Override
	public void reversePreAuthorization(final OrderPaymentDto payment) {
		final HashMap<String, String> request = new HashMap<String, String>();

		request.put("ccAuthReversalService_run", TRUE);

		request.put("ccAuthReversalService_authRequestID", payment.getAuthorizationCode());
		request.put("ccAuthReversalService_authRequestToken", payment.getRequestToken());
		request.put(PURCHASE_TOTALS_CURRENCY, payment.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, convertToString(payment.getAmount()));
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		request.put(REQUEST_ID, payment.getAuthorizationCode());
		request.put(REQUEST_TOKEN, payment.getRequestToken());

		setRequestReferenceCode(payment, request);

		Map<String, String> transactionReply = runTransaction(payment, request);
		
		updateOrderPaymentWithFollowOnRequestFields(payment, transactionReply);
	}


	@Override
	public void voidCaptureOrCredit(final OrderPaymentDto payment) {
		// Do not attempt void on sandbox because it will always fail with error code 246.
		// See http://stackoverflow.com/questions/12949395/void-transaction-attempt-for-cybersource-gives-error.
		if (getConfigurationValues().get("sendToProduction").equalsIgnoreCase("true")) {
			final HashMap<String, String> request = new HashMap<String, String>();

			request.put("voidService_run", TRUE);

			request.put("voidService_voidRequestID", payment.getAuthorizationCode());
			request.put("voidService_voidRequestToken", payment.getRequestToken());
			request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));

			setRequestReferenceCode(payment, request);
			request.put(REQUEST_ID, payment.getAuthorizationCode());
			request.put(REQUEST_TOKEN, payment.getRequestToken());

			Map<String, String> transactionReply = runTransaction(payment, request);
			
			updateOrderPaymentWithFollowOnRequestFields(payment, transactionReply);
		}
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest) {
		final HashMap<String, String> request = new HashMap<String, String>();
		
		request.put("ccCaptureService_run", TRUE);
		request.put("ccCaptureService_authRequestID", captureTransactionRequest.getAuthorizationCode());
		request.put("ccCaptureService_authRequestToken", captureTransactionRequest.getRequestToken());
		
		Money money = captureTransactionRequest.getMoney();
		request.put(PURCHASE_TOTALS_CURRENCY, money.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, convertToString(money.getAmount()));
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		request.put(MERCHANT_REFERENCE_CODE, captureTransactionRequest.getReferenceId());
		request.put(REQUEST_ID, captureTransactionRequest.getAuthorizationCode());
		request.put(REQUEST_TOKEN, captureTransactionRequest.getRequestToken());

		Map<String, String> captureReply = runTransaction(null, request);
		String captureAmount = captureReply.get("ccCaptureReply_amount");
		String captureCurrency = captureReply.get(PURCHASE_TOTALS_CURRENCY);
		String requestToken = captureReply.get(REQUEST_TOKEN);
		String authorizationCode = captureReply.get(REQUEST_ID);
		
		return createCaptureResponse(new MoneyImpl(new BigDecimal(captureAmount), captureCurrency), requestToken, authorizationCode, 
				captureTransactionRequest.getReferenceId());
	}


	private void addAttributeForPayerAuth(final CardDetailsPaymentMethod payment, final Map<String, String> request) {
		final PayerAuthValidationValueDto payerAuthValidationValue = payment.getPayerAuthValidationValueDto();
		if (payerAuthValidationValue != null && payerAuthValidationValue.isValidated()) {
			request.put("ccAuthService_xid", payerAuthValidationValue.getXID());
			request.put("ccAuthService_commerceIndicator", payerAuthValidationValue.getCommerceIndicator());
			request.put("ccAuthService_eci", payerAuthValidationValue.getECI());
			request.put("ccAuthService_cavv", payerAuthValidationValue.getCAVV());
			request.put("ucaf_authenticationData", payerAuthValidationValue.getAAV());
			request.put("ucaf_collectionIndicator", payerAuthValidationValue.getUcafCollectionIndicator());
		}
	}

	private void updateOrderPaymentWithFollowOnRequestFields(final OrderPaymentDto payment, final Map<String, String> transactionReply) {
		payment.setAuthorizationCode(transactionReply.get(REQUEST_ID));
		payment.setRequestToken(transactionReply.get(REQUEST_TOKEN));
	}

	/**
	 * Executes the transaction.
	 *
	 * @param payment the payment to be authorized, captured, etc.
	 * @param request the request parameters
	 * @return PaymentTransactionResponse the response from the gateway
	 */
	protected Map<String, String> runTransaction(final PaymentMethod payment, final Map<String, String> request) {
		Map<String, String> reply = null;
		
		try {
			//Add name-pairs for Payer Authentication
			if (payment instanceof CardDetailsPaymentMethod) {
				addAttributeForPayerAuth((CardDetailsPaymentMethod) payment, request);
			}

			displayMap("PAYMENT GATEWAY REQUEST:", request);

			// run transaction
			reply = runTransaction(request);

			displayMap("PAYMENT GATEWAY REPLY:", reply);

			final String decision = reply.get(DECISION);

			if (!decision.equalsIgnoreCase(CYBER_DECISION_ACCEPT)) {
				processRejection(reply);
			} 
		} catch (final ClientException e) {
			LOG.error(CYBERSOURCE_CLIENT_EXCEPTION + e.getInnerException());
			throw new PaymentGatewayException(CYBERSOURCE_CLIENT_EXCEPTION + e.getInnerException(), e);

		} catch (final FaultException e) {
			LOG.error(CYBERSOURCE_FAULT_EXCEPTION + e);
			throw new PaymentGatewayException(CYBERSOURCE_FAULT_EXCEPTION + e);
		}

		return reply;
	}

	/**
	 * Process a rejected transaction's reply map.
	 *
	 * @param reply the reply
	 * @throws CardDeclinedException if the card is declined
	 * @throws CardExpiredException if the card has expired
	 * @throws CardErrorException if there was an error processing the given information
	 */
	private void processRejection(final Map<String, String> reply) throws CardErrorException {
		/*
		 * When a card is rejected, the Payment Processor supplies its own reason for the
		 * rejection, in the ccAuthReply_processorResponse string. In addition, there is a reason
		 * code, which is likely supplied by CyberSource. The two numbers are likely to be
		 * different, and the payment processor's response string may not always be translated
		 * into the equivalent CyberSource reason code. However, we use the CyberSource reason
		 * code.
		 */
		final String decision = reply.get(DECISION);
		final String reason = reply.get(REASON_CODE);

		if (decision.equalsIgnoreCase(CYBER_DECISION_REJECT)) {
			final String exceptionMsg = "CYBERSOURCE_REASON_CODE:" + CYBERSOURCE_REASON_CODE + " reason: " + reason;
			if (reason.equalsIgnoreCase(CYBER_CARD_EXPIRED)) {
				LOG.info("CardExpiredException: " + exceptionMsg);
				throw new CardExpiredException(CYBERSOURCE_REASON_CODE + reason);
			} else if (reason.equalsIgnoreCase(CYBER_GENERAL_DECLINE) || reason.equalsIgnoreCase(CYBER_CV_CHECK_FAILED)) {
				LOG.info("CardDeclinedException: " + exceptionMsg);
				throw new CardDeclinedException(CYBERSOURCE_REASON_CODE + reason);
			} else if (reason.equalsIgnoreCase(CYBER_INSUFFICIENT_FUNDS) || reason.equalsIgnoreCase(CYBER_CARD_CREDIT_EXCEEDED)) {
				LOG.info("InsufficientFundException: " + exceptionMsg);
				throw new InsufficientFundException(CYBERSOURCE_REASON_CODE + reason);
			} else if (reason.equalsIgnoreCase(CYBER_AUTHORIZATION_AMOUNT_EXCEEDED)) {
				LOG.info("Authorized amount exceeded: " + exceptionMsg);
				throw new AuthorizedAmountExceededException(CYBER_AUTHORIZATION_AMOUNT_EXCEEDED + reason); 
			}

			LOG.info("CardErrorException: CYBERSOURCE_REASON_CODE:" + CYBERSOURCE_REASON_CODE + " reason: " + reason);
			throw new CardErrorException(CYBERSOURCE_REASON_CODE + reason);

		} else if (decision.equalsIgnoreCase(CYBER_DECISION_ERROR)) {

			LOG.error("PaymentGatewayException: CyberSource Processor Error");
			throw new PaymentGatewayException("CyberSource Processor Error");
		}
	}

	/**
	 * Runs the transaction on the Cybersource client. This method is used as a hook for JUnit
	 * testing.
	 *
	 * @param request a Map of request key value pairs
	 * @return a result Map of key value pairs
	 * @throws ClientException on error
	 * @throws FaultException on error
	 */
	@SuppressWarnings("unchecked")
	Map<String, String> runTransaction(final Map<String, String> request) throws ClientException, FaultException {
		final Properties props = buildProperties();
		final String fullPath = FilenameUtils.concat(getCertificatePathPrefix(), props.getProperty(KEYS_DIRECTORY));		
		props.setProperty(KEYS_DIRECTORY, fullPath);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Using certificate key directory: " + fullPath);
		}
		return Client.runTransaction(request, props);
	}

	private Properties buildProperties() {
		final Properties prop = new Properties();
		for (final Entry<String, String> entry : this.getConfigurationValues().entrySet()) {
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			prop.setProperty(entry.getKey(), value);
		}
		return prop;
	}

	/**
	 * Displays the contents of a request or response map for debugging.
	 *
	 * @param header Header text describing the transaction.
	 * @param parameterMap Map object to display containing request or response parameters.
	 */
	private void displayMap(final String header, final Map<String, String> parameterMap) {

		if (LOG.isDebugEnabled()) {
			LOG.debug(header);

			final StringBuffer outputBuffer = new StringBuffer();

			if (parameterMap != null && !parameterMap.isEmpty()) {
				for (final Map.Entry<String, String> element : parameterMap.entrySet()) {
					final String key = element.getKey();
					final String value = element.getValue();
					outputBuffer.append(key + "=" + value + "\n");
				}
			}

			LOG.debug(outputBuffer.toString());
		}
	}

	/**
	 * Returns a unique reference code for a payment transaction. The reference code is composed
	 * of a three-digit random number, followed by the current date/time in milliseconds. The
	 * three digit random number minimizes the possibility of duplicate reference codes if two are
	 * processed during the same millisecond.
	 *
	 * @return the unique reference code
	 */
	private String generateReferenceCode() {
		return String.valueOf((Math.random() * RANDOM_NUMBER_MULTIPLIER) + (new Date()).getTime());
	}

	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a List of card type strings (e.g. VISA) supported by this payment gateway.
	 */
	@Override
	public List<String> getSupportedCardTypes() {
		if (supportedCardTypes == null) {
			supportedCardTypes = this.getDefaultSupportedCardTypes();
		}
		return getDefaultSupportedCardTypesValues(supportedCardTypes);
	}

	/**
	 * Get the List of card type strings in the given supportedCardTypesMap Map.
	 *
	 * @param supportedCardTypesMap a Map of all code-card combinations supported by CyberSource.
	 * @return a List of card type strings supported by CyberSource.
	 */
	private List<String> getDefaultSupportedCardTypesValues(final Map<String, String> supportedCardTypesMap) {
		final List<String> supportedCardTypesList = new ArrayList<String>();
		for (final String cardType : supportedCardTypesMap.keySet()) {
			supportedCardTypesList.add(cardType);
		}
		return supportedCardTypesList;
	}

	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a Map of all code-card combinations supported by CyberSource
	 */
	protected Map<String, String> getDefaultSupportedCardTypes() {
		final Map<String, String> cardTypesMap = new HashMap<String, String>();
		cardTypesMap.put("Visa", "001");
		cardTypesMap.put("MasterCard", "002");
		cardTypesMap.put("Eurocard", "002");
		cardTypesMap.put("American Express", "003");
		cardTypesMap.put("Discover", "004");
		cardTypesMap.put("Diners Club", "005");
		cardTypesMap.put("Carte Blanche", "006");
		cardTypesMap.put("JCB", "007");
		cardTypesMap.put("EnRoute", "014");
		cardTypesMap.put("JAL", "021");
		//cardTypesMap.put("Maestro", "024");
		//cardTypesMap.put("Solo", "024");
		cardTypesMap.put("Delta", "031");
		cardTypesMap.put("Visa Electron", "033");
		cardTypesMap.put("Dankort", "034");
		cardTypesMap.put("Laser", "035");
		cardTypesMap.put("Carte Bleue", "036");
		cardTypesMap.put("Carta Si", "037");
		//cardTypesMap.put("Maestro", "042");

		return cardTypesMap;
	}

	/**
	 * Check the card account enrollment.
	 * @param shoppingCart the shoppingCart
	 * @param payment orderPayment.
	 * @return result of enrollment checking.
	 */
	@Override
	public PayerAuthenticationEnrollmentResultDto checkEnrollment(final ShoppingCartDto shoppingCart, final OrderPaymentDto payment) {
		final PayerAuthenticationEnrollmentResultDto payerAuthEnrollmentValue = new PayerAuthenticationEnrollmentResultDtoImpl();

		final HashMap<String, String> request = new HashMap<String, String>();
		request.put("payerAuthEnrollService_run", TRUE);
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));

		setRequestReferenceCode(payment, request);

		// Card Information
		final String cardTypeCode = this.getDefaultSupportedCardTypes().get(payment.getCardType());
		if (cardTypeCode != null) {
			request.put("card_cardType", cardTypeCode);
			if (cardTypeCode.equals(TYPE_OF_VISA) || cardTypeCode.equals(TYPE_OF_JCB)) {

				request.put("item_0_unitPrice",
						convertToString(shoppingCart.getTotalAmount()));
			}
		}

		request.put(CARD_ACCOUNT_NUMBER, payment.getUnencryptedCardNumber());
		request.put(CARD_EXPIRATION_MONTH, payment.getExpiryMonth());
		request.put(CARD_EXPIRATION_YEAR, payment.getExpiryYear());
		request.put(PURCHASE_TOTALS_CURRENCY, shoppingCart.getCurrencyCode());

		displayMap("CREDIT CARD 3-D SECURE ENROLLMENT REQUEST:", request);

		try {
			// run transaction
			final Map<String, String> reply = runTransaction(request);

			displayMap("CREDIT CARD 3-D SECURE ENROLLMENT REPLY:", reply);

			final String decision = reply.get(DECISION);
			final String reason = reply.get(REASON_CODE);

			if (decision.equals(CYBER_DECISION_REJECT) && reason.equals(PAYER_AUTH_ENROLL)) {
				payerAuthEnrollmentValue.setEnrolled(true);
				final String acsURL = reply.get(PAYER_AUTH_ACS_URL);
				payerAuthEnrollmentValue.setAcsURL(acsURL);
				final String pareq = reply.get(PAYER_AUTH_PAREQ);
				payerAuthEnrollmentValue.setPaREQ(pareq);
			}

		} catch (final ClientException e) {
			LOG.error(CYBERSOURCE_CLIENT_EXCEPTION + e.getInnerException());

		} catch (final FaultException e) {
			LOG.error(CYBERSOURCE_FAULT_EXCEPTION + e);

		}
		return payerAuthEnrollmentValue;
	}

	/**
	 * Validate the authentication.
	 * @param payment orderPayment.
	 * @param paRes from issuing bank.
	 * @return boolean successful value for validation.
	 */
	@Override
	public boolean validateAuthentication(final OrderPaymentDto payment, final String paRes) {

		boolean returnValue = false;

		final HashMap<String, String> request = new HashMap<String, String>();
		request.put("payerAuthValidateService_run", TRUE);
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));

		setRequestReferenceCode(payment, request);

		// Card Information
		final String cardTypeCode = this.getDefaultSupportedCardTypes().get(payment.getCardType());
		if (cardTypeCode != null) {
			request.put("card_cardType", cardTypeCode);
		}

		request.put(CARD_ACCOUNT_NUMBER, payment.getUnencryptedCardNumber());
		request.put(CARD_EXPIRATION_MONTH, payment.getExpiryMonth());
		request.put(CARD_EXPIRATION_YEAR, payment.getExpiryYear());
		request.put(PURCHASE_TOTALS_CURRENCY, payment.getCurrencyCode());

		request.put("payerAuthValidateService_signedPARes", paRes);

		displayMap("CREDIT CARD 3-D SECURE AUTHENTICATION REQUEST:", request);

		try {
			// run transaction
			final Map<String, String> reply = runTransaction(request);

			displayMap("CREDIT CARD 3-D SECURE AUTHENTICATION REPLY:", reply);

			final String reason = reply.get(REASON_CODE);

			if (reason.equals(REASON_CODE_SUCCESSFUL)) {
				final PayerAuthValidationValueDto payerAuthValidationValue = new PayerAuthValidationValueDtoImpl();

				final String commerceIndicator = reply.get("payerAuthValidateReply_commerceIndicator");
				payerAuthValidationValue.setCommerceIndicator(commerceIndicator);

				final String eCI = reply.get("payerAuthValidateReply_eci");
				payerAuthValidationValue.setECI(eCI);

				final String xID = reply.get("payerAuthValidateReply_xid");
				payerAuthValidationValue.setXID(xID);

				if (cardTypeCode.equals(TYPE_OF_VISA) || cardTypeCode.equals(TYPE_OF_JCB)) {
					final String cAVV = reply.get("payerAuthValidateReply_cavv");
					payerAuthValidationValue.setCAVV(cAVV);
				}

				if (cardTypeCode.equals(TYPE_OF_MASTERCARD)) {
					final String aAv = reply.get("payerAuthValidateReply_ucafAuthenticationData");
					payerAuthValidationValue.setAAV(aAv);

					final String ucafCollectionIndicator = reply.get("payerAuthValidateReply_ucafCollectionIndicator");
					payerAuthValidationValue.setUcafCollectionIndicator(ucafCollectionIndicator);
				}

				payerAuthValidationValue.setValidated(true);
				payment.setPayerAuthValidationValueDto(payerAuthValidationValue);
				returnValue = true;

			} else if (reason.equals(REASON_CODE_SYS_ERROR)) {
				throw new PaymentGatewayException("CyberSource Processor Error");
			}


		} catch (final ClientException e) {
			LOG.error(CYBERSOURCE_CLIENT_EXCEPTION + e.getInnerException());
			throw new PaymentGatewayException(CYBERSOURCE_CLIENT_EXCEPTION + e.getInnerException(), e);
		} catch (final FaultException e) {
			LOG.error(CYBERSOURCE_FAULT_EXCEPTION + e);
			throw new PaymentGatewayException(CYBERSOURCE_FAULT_EXCEPTION + e);
		}

		return returnValue;
	}

	/**
	 * Refunds a certain amount to a credit card.
	 *
	 * @param payment the order payment
	 * @param billingAddress the billing address
	 */
	@Override
	public void refund(final OrderPaymentDto payment, final AddressDto billingAddress) {
		final HashMap<String, String> request = new HashMap<String, String>();

		if (payment.getAuthorizationCode() == null) { // refund to a new credit card (stand-alone credit)
			if (billingAddress == null) {
				throw new PaymentGatewayException("Can not refund a stand-alone payment without billing address defined");
			}
			// Billing Address
			request.put("billTo_firstName", billingAddress.getFirstName());
			request.put("billTo_lastName", billingAddress.getLastName());
			request.put("billTo_street1", billingAddress.getStreet1());
			request.put("billTo_city", billingAddress.getCity());

			if (billingAddress.getSubCountry() != null) {
				request.put("billTo_state", billingAddress.getSubCountry());
			}

			request.put("billTo_postalCode", billingAddress.getZipOrPostalCode());
			request.put("billTo_country", billingAddress.getCountry());
			request.put("billTo_email", payment.getEmail());

			request.put(CARD_ACCOUNT_NUMBER, payment.getUnencryptedCardNumber());
			request.put(CARD_EXPIRATION_MONTH, payment.getExpiryMonth());
			request.put(CARD_EXPIRATION_YEAR, payment.getExpiryYear());
		} else { // refund to an already existing capture payment (follow-up credit)
			request.put("ccCreditService_captureRequestID", payment.getAuthorizationCode());
			request.put("ccCreditService_captureRequestToken", payment.getRequestToken());
		}

		request.put("ccCreditService_run", TRUE);
		request.put(PURCHASE_TOTALS_CURRENCY, payment.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, convertToString(payment.getAmount()));
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		setRequestReferenceCode(payment, request);
		request.put(REQUEST_ID, payment.getAuthorizationCode());
		request.put(REQUEST_TOKEN, payment.getRequestToken());

		Map<String, String> transactionReply = runTransaction(payment, request);
		
		updateOrderPaymentWithFollowOnRequestFields(payment, transactionReply);
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		final Collection<String> parameters = new ArrayList<String>();
		parameters.add(MERCHANT_ID);
		parameters.add(KEYS_DIRECTORY);
		parameters.add("targetAPIVersion");
		parameters.add("sendToProduction");
		parameters.add("logMaximumSize");
		parameters.add("enableLog");
		parameters.add("logDirectory");
		return parameters;
	}

}
