package com.elasticpath.common.dto.paymentgateway;

import java.util.Properties;
import java.util.Set;

import com.elasticpath.common.dto.PropertyDTO;
import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;
import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Assembler to go between PaymentGateway entity and PaymentGatewayDTOs. Note: This assembler does some magic involving a PlaceholderPaymentGateway
 * (see the comment on the inner class).
 */
public class PaymentGatewayDtoAssembler extends AbstractDtoAssembler<PaymentGatewayDTO, PaymentGateway> {

	/**
	 * The import/export tool needs a copy of the domain object when performing insert of new domain entities, however we're not sure at this point
	 * which instance of PaymentGateway we're actually creating. We're relying on the import code not actually performing any operations on this
	 * fresh PaymentGateway. Instead, the PaymentGatewayDtoAssembler will replace the PaymentGateway with the correct subclass in its assembleDomain
	 * method.
	 */
	// CHECKSTYLE:OFF
	final class PlaceholderPaymentGateway extends PaymentGatewayImpl {

		private static final long serialVersionUID = 0L;

		private static final String ERROR_MESSAGE = "You cannot use the placeholder payment gateway for performing operations.";

		public PaymentType getPaymentType() {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}

		public String getType() {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}

		public void preAuthorize(final OrderPayment payment, final Address billingAddress) {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}
		
		public void capture(final OrderPayment payment) {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}

		public void sale(final OrderPayment payment, final Address billingAddress) {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}

		public void voidCaptureOrCredit(final OrderPayment payment) {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}

		public void reversePreAuthorization(final OrderPayment payment) {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}

		@Override
		protected Set<String> getDefaultPropertyKeys() {
			throw new UnsupportedOperationException(ERROR_MESSAGE);
		}
	};

	// CHECKSTYLE:ON
	
	private PaymentGatewayFactory paymentGatewayFactory;

	/**
	 * Callers are warned that this method returns a <code>PlaceholderPaymentGateway</code> in lieu of an actual PaymentGateway implementation.
	 * 
	 * @return a placeholder paymentgateway, it will throw exceptions if accessed.
	 */
	@Override
	public PaymentGateway getDomainInstance() {
		return new PlaceholderPaymentGateway();
	}

	@Override
	public PaymentGatewayDTO getDtoInstance() {
		return new PaymentGatewayDTO();
	}

	@Override
	public void assembleDto(final PaymentGateway source, final PaymentGatewayDTO target) {
		target.setName(source.getName());
		target.setType(source.getType());

		for (String key : source.getPropertiesMap().keySet()) {
			PaymentGatewayProperty prop = source.getPropertiesMap().get(key);
			target.getProperties().add(new PropertyDTO(prop.getKey(), prop.getValue()));
		}
	}

	/**
	 * Populates the <code>target</code> domain entity from the <code>source</code> DTO.
	 * 
	 * @param source The DTO to get data from
	 * @param target The domain entity to populate
	 */
	@Override
	public void assembleDomain(final PaymentGatewayDTO source, final PaymentGateway target) {
		target.setName(source.getName());
		target.setType(source.getType());
		
		Properties properties = new Properties();
		for (PropertyDTO prop : source.getProperties()) {
			properties.setProperty(prop.getPropertyKey(), prop.getValue());
		}
		
		target.mergeProperties(properties);
	}

	/**
	 * Returns a new domain entity assembled from the <code>source</code> DTO. 
	 * 
	 * @param source The DTO to get data from
	 * @return a new domain entity assebled from the DTO
	 */
	@Override
	public PaymentGateway assembleDomain(final PaymentGatewayDTO source) {

		// Create a new PaymentGateway instance of the source's type and populate it from the source
		PaymentGateway target = paymentGatewayFactory.getPaymentGateway(source.getType());
		assembleDomain(source, target);
		
		return target; 

	}

	public void setPaymentGatewayFactory(final PaymentGatewayFactory factory) {
		this.paymentGatewayFactory = factory;
	}

}
