package com.elasticpath.paymentgateways.cybersource.constants;

/**
 * Constant values for Cybersource decision fields.
 */
public class CyberSourceResponseConstants {
	/** ACCEPT decision. */
	public static final String DECISION_ACCEPT = "ACCEPT";

	/** REJECT decision. */
	public static final String DECISION_REJECT = "REJECT";

	/** ERROR decision. */
	public static final String DECISION_ERROR = "ERROR";
	
	/** Cybersource client exception. */
	public static final String CLIENT_EXCEPTION = "Cybersource client exception: ";

	/** Cybersource fault exception. */
	public static final String FAULT_EXCEPTION = "Cybersource fault exception: ";

	/** Cybersource reason code. */
	public static final String CYBERSOURCE_REASON_CODE = "Cybersource Reason Code: ";
	
	/** Invalid data response code. **/
	public static final String INVALID_DATA_RESPONSE_CODE = "102";
	
	/** Card credit exceeded response code. **/
	public static final String CARD_CREDIT_EXCEEDED_RESPONSE_CODE = "210";
	
	/** General decline response code. **/
	public static final String GENERAL_DECLINE_RESPONSE_CODE = "203";
	
	/** Insufficient funds response code. **/
	public static final String INSUFFICIENT_FUNDS_RESPONSE_CODE = "204";
	
	/** Card expired response code. **/
	public static final String CARD_EXPIRED_RESPONSE_CODE = "202";
	
	/** CV check failed response code. **/
	public static final String CV_CHECK_FAILED_RESPONSE_CODE = "230";
	
	/** Decision CyberSource field. */
	public static final String DECISION = "decision";

	/** Reason code CyberSource field. */
	public static final String REASON_CODE = "reasonCode";
	
	/** Invalid field CyberSource field. */
	public static final String INVALID_FIELD = "invalidField_";
	
	/** Cybersource subscription reply id. **/
	public static final String SUBSCRIPTION_REPLY_ID = "paySubscriptionCreateReply_subscriptionID";
	
	/** Cybersource reply request token. */
	public static final String REQUEST_TOKEN = "requestToken";
	
	/** Cybersource reply request id. */
	public static final String REQUEST_ID = "requestID";
}
