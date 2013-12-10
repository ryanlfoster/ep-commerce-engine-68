package com.elasticpath.service.payment.gateway.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.CreditCardCapability;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.spi.AbstractPaymentGatewayPluginSPI;

/**
 * This is a placeholder plugin for unresolved payment gateway plugin implementations. 
 * If a payment gateway plugin is not found by the {@link com.elasticpath.domain.payment.PaymentGatewayFactory}, this placeholder is returned.   
 * Any methods not required for creation by the {@link com.elasticpath.domain.payment.PaymentGatewayFactory}
 * or for viewing a payment gateway in the CMClient throw an {@link UnresolvedPluginException} to ensure it is never mistakenly used in production.
 */
public class UnresolvablePaymentGatewayPluginImpl extends AbstractPaymentGatewayPluginSPI implements CreditCardCapability {
	
	/**
	 * Serial Version id.
	 */
	private static final long serialVersionUID = 3741668568088230418L;
	
	private final String unresolvableGatewayType;
	
	/**
	 * Default Constructor.
	 * 
	 * @param unresolvableGatewayType The unresolvable gateway type
	 */
	public UnresolvablePaymentGatewayPluginImpl(final String unresolvableGatewayType) {
		this.unresolvableGatewayType = unresolvableGatewayType;
	}

	@Override
	public String getPluginType() {
		return unresolvableGatewayType;
	}
	
	@Override
	public List<String> getSupportedCardTypes() {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		return new ArrayList<String>();
	}

	@Override
	public PaymentType getPaymentType() {
		return PaymentType.CREDITCARD;
	}

	@Override
	public void setConfigurationValues(final Map<String, String> configurationValues) {
		// these values will never be used, as there is no plugin implementation that uses them.
	}
	
	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public boolean isCvv2ValidationEnabled() {
		throw new UnresolvedPluginException(getErrorMessage());
	}

	@Override
	public void setValidateCvv2(final boolean validate) {
		throw new UnresolvedPluginException(getErrorMessage());
	}

	@Override
	public PayerAuthenticationEnrollmentResultDto checkEnrollment(final ShoppingCartDto shoppingCart, final OrderPaymentDto payment) {
		throw new UnresolvedPluginException(getErrorMessage());
	}

	@Override
	public boolean validateAuthentication(final OrderPaymentDto payment, final String paRes) {
		throw new UnresolvedPluginException(getErrorMessage());
	}

	private String getErrorMessage() {
		return String.format("Payment Gateway Plugin definition not found for type: %s", unresolvableGatewayType);
	}
	
	/**
	 * Exception thrown when a method is called on the {@link UnresolvablePaymentGatewayPluginImpl}.   
	 */
	public static class UnresolvedPluginException extends EpServiceException {
		private static final long serialVersionUID = 1L;

		/**
		 * Delegates to {@link EpServiceException}.
		 * @param message the message to include.
		 */
		public UnresolvedPluginException(final String message) {
			super(message);
		}
		
	}
}
