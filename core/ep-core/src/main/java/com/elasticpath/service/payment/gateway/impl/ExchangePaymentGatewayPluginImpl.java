package com.elasticpath.service.payment.gateway.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.PreAuthorizeCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.spi.AbstractPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.service.payment.PaymentServiceException;

/**
 * Implementation of the exchange payment gateway which allows customers to purchase using the value of exchanged goods.
 */
public class ExchangePaymentGatewayPluginImpl extends AbstractPaymentGatewayPluginSPI implements PreAuthorizeCapability {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final String GATEWAY_TYPE = "paymentGatewayExchange";

	private static final String EXCHANGE_AUTH_CODE_PREFIX = "AuthCode";

	private static final String EXCHANGE_REFERENCE_ID_PREFIX = "RefId";

	private final Random randomGenerator = new Random(System.currentTimeMillis());

	@Override
	public String getPluginType() {
		return GATEWAY_TYPE;
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.RETURN_AND_EXCHANGE;
	}

	@Override
	public AuthorizationTransactionResponse preAuthorize(final AuthorizationTransactionRequest authorizationTransactionRequest,
			final AddressDto billingAddress, final OrderShipmentDto shipment) {
		// BigDecimal values have to be compared with compareTo() method
		// even though on the SUN JDK =! works on IBM's one it doesn't
		Money money = authorizationTransactionRequest.getMoney();
		if (BigDecimal.ONE.compareTo(money.getAmount()) != 0) {
			throw new PaymentServiceException("Exchange order can only be authorized for $1. Passed amount: " + money.getAmount());
		}
		return createAuthorizationResponse(getReferenceId(), getAuthCode(), null, null, money);
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		return new ArrayList<String>();
	}

	private String getAuthCode() {
		return EXCHANGE_AUTH_CODE_PREFIX + randomGenerator.nextInt();
	}

	private String getReferenceId() {
		return EXCHANGE_REFERENCE_ID_PREFIX + randomGenerator.nextInt();
	}
}
