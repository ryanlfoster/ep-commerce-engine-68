/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.paymentgateways.cybersource.constants;

/**
 * Constant values for CyberSource request/response.
 */
public class CyberSourceRequestFields {
	/** Request id CyberSource field. */
	public static final String REQUEST_ID = "requestID";

	/** Signed fields CyberSource field. */
	public static final String SIGNED_FIELDS = "signedFields";

	/** Signed fields public signature CyberSource field. */
	public static final String SIGNED_FIELDS_PUBLIC_SIGNATURE = "signedDataPublicSignature";

	/** Payment option CyberSource field. */
	public static final String PAYMENT_OPTION = "paymentOption";

	/** Card type CyberSource field. */
	public static final String CARD_TYPE = "card_cardType";

	/** Card expiration month CyberSource field. */
	public static final String CARD_EXPIRATION_MONTH = "card_expirationMonth";

	/** Card expiration year CyberSource field. */
	public static final String CARD_EXPIRATION_YEAR = "card_expirationYear";

	/** Order amount CyberSource field. */
	public static final String ORDER_AMOUNT = "orderAmount";

	/** Authorization code CyberSource field. */
	public static final String AUTHORIZATION_CODE = "ccAuthReply_authorizationCode";

	/** Request token CyberSource field. */
	public static final String REQUEST_TOKEN = "orderPage_requestToken";

	/** Order currency CyberSource field. */
	public static final String ORDER_CURRENCY = "orderCurrency";

	/** Card number CyberSource field. */
	public static final String CARD_ACCOUNT_NUMBER = "card_accountNumber";

	/** Bill to first name CyberSource field. */
	public static final String BILL_TO_FIRSTNAME = "billTo_firstName";

	/** Bill to last name CyberSource field. */
	public static final String BILL_TO_LASTNAME = "billTo_lastName";

	/** Bill to street1 CyberSource field. */
	public static final String BILL_TO_STREET1 = "billTo_street1";

	/** Bill to city CyberSource field. */
	public static final String BILL_TO_CITY = "billTo_city";

	/** Bill to postal code CyberSource field. */
	public static final String BILL_TO_POSTALCODE = "billTo_postalCode";

	/** Bill to country CyberSource field. */
	public static final String BILL_TO_COUNTRY = "billTo_country";

	/** Bill to state CyberSource field. */
	public static final String BILL_TO_STATE = "billTo_state";

	/** Bill to email CyberSource field. */
	public static final String BILL_TO_EMAIL = "billTo_email";

	/** Bill to IP address CyberSource field. */
	public static final String BILL_TO_IPADDRESS = "billTo_ipAddress";
	
	/** Bill to purchase totals currency CyberSource field. */
	public static final String PURCHASE_TOTALS_CURRENCY = "purchaseTotals_currency";

	/** Bill to purchase grand total CyberSource field. */
	public static final String PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT = "purchaseTotals_grandTotalAmount";

	/** Bill to Merchant ID CyberSource field. */
	public static final String MERCHANT_ID = "merchantID";

	/** Bill to payer auth ACS CyberSource field. */
	public static final String PAYER_AUTH_ACS_URL = "payerAuthEnrollReply_acsURL";

	/** Bill to payer auth pareq CyberSource field. */
	public static final String PAYER_AUTH_PAREQ = "payerAuthEnrollReply_paReq";

	/** Bill to merchant reference code CyberSource field. */
	public static final String MERCHANT_REFERENCE_CODE = "merchantReferenceCode";

	/** Bill to field prefix CyberSource field. */
	public static final String FIELD_PREFIX = "input[name=item_";

	/** Bill to item prefix CyberSource field. */
	public static final String ITEM_PREFIX = "item_";

	/** Currency CyberSource field. */
	public static final Object CURRENCY = "currency";

	/** Card CV number CyberSource field. */
	public static final String CARD_CVNUMBER = "card_cvNumber";

	/** Transaction type CyberSource field. */
	public static final String ORDERPAGE_TRANSACTIONTYPE = "orderPage_transactionType";

	/** Purchase order date CyberSource field. */
	public static final String INVOICEHEADER_PURCHASEORDERDATE = "invoiceHeader_purchaserOrderDate";

	/** This is the field which indicates to CyberSource which URL to post back. */
	public static final String RECEIPT_RESPONSE_URL = "orderPage_receiptResponseURL";

	/** This is the field which indicates to CyberSource which Error URL to post back. */
	public static final String ERROR_RECEIPT_RESPONSE_URL = "orderPage_declineResponseURL";

	/** Public signature field. */
	public static final String ORDERPAGE_SIGNATUREPUBLIC = "orderPage_signaturePublic";

	/** Request ID for auth reversal service. */
	public static final String CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID = "ccAuthReversalService_authRequestID";

	/** Run auth reversal service flag. */
	public static final String CC_AUTH_REVERSAL_SERVICE_RUN = "ccAuthReversalService_run";

	/** Subscription frequency. */
	public static final String RECURRING_SUBSCRIPTION_INFO_FREQUENCY = "recurringSubscriptionInfo_frequency";

	/** Ignore AVS result flag. */
	public static final String BUSINESS_RULES_IGNORE_AVS_RESULT = "businessRules_ignoreAVSResult";

	/** Run subscription create service flag. */
	public static final String PAY_SUBSCRIPTION_CREATE_SERVICE_RUN = "paySubscriptionCreateService_run";

}
