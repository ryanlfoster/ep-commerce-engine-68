package com.elasticpath.paymentgateways.cybersource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields;
import com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.TokenAuthorizationCapability;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;
import com.elasticpath.plugin.payment.dto.impl.MoneyImpl;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * Cybersource tokenized payment gateway implementation.
 *
 */
public class CybersourceTokenPaymentGatewayPluginImpl extends AbstractCybersourcePaymentGatewayPluginImpl 
			implements TokenAuthorizationCapability {
	private static final long serialVersionUID = 1L;

	@Override
	public String getPluginType() {
		return "paymentGatewayCyberSourceToken";
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.PAYMENT_TOKEN;
	}
	
	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest) {
		final HashMap<String, String> request = new HashMap<String, String>();
		
		request.put("ccAuthService_run", "true");
		request.put("businessRules_ignoreAVSResult", "true");

		request.put(CyberSourceRequestFields.MERCHANT_ID, getConfigurationValues().get(CyberSourceRequestFields.MERCHANT_ID));
		request.put(CyberSourceRequestFields.MERCHANT_REFERENCE_CODE, authorizationTransactionRequest.getReferenceId());
		
		Money money = authorizationTransactionRequest.getMoney();
		request.put(CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY, money.getCurrencyCode());

		request.put(CyberSourceRequestFields.PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT,
				convertToString(money.getAmount()));
		
		TokenPaymentMethod paymentMethod = (TokenPaymentMethod) authorizationTransactionRequest.getPaymentMethod();
		request.put("recurringSubscriptionInfo_subscriptionID", paymentMethod.getValue());
		
		Map<String, String> transactionReply = runTransaction(paymentMethod, request);
		
		/*
		 * if the authorization was successful, obtain the request id and request token
		 * for the follow-on capture later.
		 */
		String authorizationCode = transactionReply.get(CyberSourceResponseConstants.REQUEST_ID);
		String requestToken = transactionReply.get(CyberSourceResponseConstants.REQUEST_TOKEN);
		
		String authorizationAmount = transactionReply.get("ccAuthReply_amount");
		String authorizationCurrrency = transactionReply.get("purchaseTotals_currency");
		
		return createAuthorizationResponse(null, authorizationCode, requestToken, null, new MoneyImpl(new BigDecimal(authorizationAmount), 
				authorizationCurrrency));
	}
}
