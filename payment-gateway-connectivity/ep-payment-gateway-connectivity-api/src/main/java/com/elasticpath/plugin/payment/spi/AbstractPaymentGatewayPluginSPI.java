package com.elasticpath.plugin.payment.spi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

import com.elasticpath.plugin.payment.PaymentGatewayPluginInvoker;
import com.elasticpath.plugin.payment.PaymentGatewayPluginManagement;
import com.elasticpath.plugin.payment.capabilities.PaymentGatewayCapability;
import com.elasticpath.plugin.payment.dto.Money;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.PaymentTransactionResponse;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionResponseImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionResponseImpl;
import com.elasticpath.plugin.payment.transaction.impl.PaymentTransactionResponseImpl;

/**
 * Service Provider Interface for extension classes implementing payment gateway plugins.
 */
public abstract class AbstractPaymentGatewayPluginSPI implements PaymentGatewayPluginManagement, 
	PaymentGatewayPluginInvoker, Serializable {
	
	@Override
	public <T extends PaymentGatewayCapability> T getCapability(final Class<T> capability) {
		if (capability.isAssignableFrom(this.getClass())) {
			return capability.cast(this);
		}
		return null;
	}

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

	private Map<String, String> configurationValues;

	private String certificatePathPrefix;

	@Override
	public void setCertificatePathPrefix(final String certificatePathPrefix) {
		this.certificatePathPrefix = certificatePathPrefix;
	}

	protected String getCertificatePathPrefix() {
		return certificatePathPrefix;
	}

	@Override
	public void setConfigurationValues(final Map<String, String> configurationValues) {
		final Set<String> keyObjs = configurationValues.keySet();
		for (final String key : keyObjs) {
			if (!getConfigurationParameters().contains(key)) {
				throw new IllegalArgumentException("Unexpected configuration item : " + key);
			}
		}

		this.configurationValues = configurationValues;
	}

	@Override
	public boolean isResolved() {
		return true;
	}
	
	public Map<String, String> getConfigurationValues() {
		return configurationValues;
	}

	/**
	 * Converts a BigDecimal to a string value.
	 *
	 * @param value the value to be converted
	 * @return the string representation
	 */
	protected String convertToString(final BigDecimal value) {
		return value.setScale(2, RoundingMode.UP).toString();
	}
	
	/**
	 * Creates the {@link AuthorizationTransactionResponse} from the gateway response fields return after an authorization transaction. 
	 *
	 * @param referenceId the reference id
	 * @param authorizationCode the authorization code
	 * @param requestToken the request token
	 * @param email the email
	 * @param money the money associated with the transaction
	 * @return the payment transaction response
	 */
	protected AuthorizationTransactionResponse createAuthorizationResponse(final String referenceId, final String authorizationCode, 
			final String requestToken, final String email, final Money money) {
		AuthorizationTransactionResponseImpl response = new AuthorizationTransactionResponseImpl();
		
		response.setReferenceId(referenceId);
		response.setAuthorizationCode(authorizationCode);
		response.setRequestToken(requestToken);
		response.setEmail(email);
		response.setMoney(money);
		
		return response;
		
	}
	
	/**
	 * Creates the {@link CaptureTransactionResponse} from the gateway response fields returned after a capture transaction.
	 *
	 * @param money the {@link Money} associated with the transaction
	 * @param requestToken the request token associated with the transaction
	 * @param requestId the request id returned to use for the follow on request
	 * @param referenceId the reference id returned from the capture for the follow on transaction
	 * 
	 * @return the {@link CaptureTransactionResponse}
	 */
	protected CaptureTransactionResponse createCaptureResponse(final Money money, final String requestToken, 
			final String requestId, final String referenceId) {
		CaptureTransactionResponse response = new CaptureTransactionResponseImpl();
		response.setRequestToken(requestToken);
		response.setMoney(money);
		response.setAuthorizationCode(requestId);
		response.setReferenceId(referenceId);
		
		return response;
	}
	
	/**
	 * Creates the {@link PaymentTransactionResponse} from the gateway response fields return after an authorization transaction. 
	 *
	 * @param referenceId the reference id
	 * @param authorizationCode the authorization code
	 * @param requestToken the request token
	 * @param email the email
	 * @return the payment transaction response
	 */
	protected PaymentTransactionResponse createResponse(final String referenceId, final String authorizationCode, 
			final String requestToken, final String email) {
		PaymentTransactionResponse response = new PaymentTransactionResponseImpl();
		
		response.setReferenceId(referenceId);
		response.setAuthorizationCode(authorizationCode);
		response.setRequestToken(requestToken);
		response.setEmail(email);
		
		return response;
		
	}
}
