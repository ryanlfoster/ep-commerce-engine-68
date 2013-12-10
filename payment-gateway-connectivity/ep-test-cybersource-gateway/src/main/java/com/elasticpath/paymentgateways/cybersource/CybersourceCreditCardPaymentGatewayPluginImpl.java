package com.elasticpath.paymentgateways.cybersource;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields;
import com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.CardDetailsPaymentMethod;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;
import com.elasticpath.plugin.payment.dto.impl.MoneyImpl;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;

/**
 * Cybersource credit card payment gateway implementation.
 *
 */
public class CybersourceCreditCardPaymentGatewayPluginImpl extends AbstractCybersourcePaymentGatewayPluginImpl 
		implements PreAuthorizeCapability {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getPluginType() {
		return "paymentGatewayCybersource";
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.CREDITCARD;
	}
	
	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest, 
			final AddressDto billingAddress, final OrderShipmentDto shipment) {
		final HashMap<String, String> request = new HashMap<String, String>();
		
		CardDetailsPaymentMethod paymentMethod = (CardDetailsPaymentMethod) authorizationTransactionRequest.getPaymentMethod();
		Money money = authorizationTransactionRequest.getMoney();
		
		request.put("ccAuthService_run", "true");
		request.put("businessRules_ignoreAVSResult", "true");
		
		setRequestReferenceCode(paymentMethod, request);

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
		request.put("billTo_email", paymentMethod.getEmail());

		// Card Information
		final String cardTypeCode = getDefaultSupportedCardTypes().get(paymentMethod.getCardType());
		if (cardTypeCode != null) {
			request.put("card_cardType", cardTypeCode);
		}
		request.put(CyberSourceRequestFields.CARD_ACCOUNT_NUMBER, paymentMethod.getUnencryptedCardNumber());
		request.put(CyberSourceRequestFields.CARD_EXPIRATION_MONTH, paymentMethod.getExpiryMonth());
		request.put(CyberSourceRequestFields.CARD_EXPIRATION_YEAR, paymentMethod.getExpiryYear());
		request.put(CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY, money.getCurrencyCode());
		request.put(CyberSourceRequestFields.PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT,
				convertToString(money.getAmount()));
		if (this.isCvv2ValidationEnabled()) {
			request.put("card_cvNumber", paymentMethod.getCvv2Code());
		}

		if (shipment != null) {
			int counter = 0;
			for (final OrderSkuDto orderSku : shipment.getOrderSkuDtos()) {
				request.putAll(createItem(counter, orderSku.getDisplayName(), orderSku.getSkuCode(),
						orderSku.getQuantity(), orderSku.getTaxAmount(), orderSku.getUnitPrice()));
				counter++;
			}
			if (shipment.isPhysical()) {
				request.putAll(createItem(counter, shipment.getCarrier(), shipment.getServiceLevel(), 1,
						shipment.getShippingTax(), shipment.getShippingCost()));
			}

		}

		// The Client will get the merchantID from the CyberSource properties and insert it into
		// the request Map.
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
