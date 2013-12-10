package com.elasticpath.domain.builder.payment.gateway;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.DomainObjectBuilder;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.PaymentGatewayFactory;

public class PaymentGatewayBuilder implements DomainObjectBuilder<PaymentGateway> {
	@Autowired
	private PaymentGatewayFactory paymentGatewayFactory;  
	
	private String type;
	private String name;
	private Properties properties;
	
	public PaymentGatewayBuilder withType(final String type) {
		this.type = type;
		return this;
	}

	public PaymentGatewayBuilder withName(final String name) {
		this.name = name;
		return this;
	}

	public PaymentGatewayBuilder withProperties(final Properties properties) {
		this.properties = properties;
		return this;
	}
	
	public PaymentGateway build() {
		PaymentGateway paymentGateway = paymentGatewayFactory.getPaymentGateway(type);
		paymentGateway.setName(name);
		paymentGateway.setProperties(properties);
		return paymentGateway;
	}
}
