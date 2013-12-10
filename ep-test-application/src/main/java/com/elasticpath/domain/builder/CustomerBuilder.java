package com.elasticpath.domain.builder;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * A builder that builds {@link Customer}s for testing purposes.
 */
public class CustomerBuilder implements DomainObjectBuilder<Customer> {

	@Autowired
    private BeanFactory beanFactory;

    private String userId;
    private Locale preferredLocale;
    private String email;
    private String firstName;
    private String lastName;
    private String clearTextPassword;
    private Boolean anonymous;
    private Date creationDate;
    private String storeCode;
    private int status;
	private PaymentMethod[] paymentMethods = new PaymentMethod[] {};
	private PaymentMethod method;

	public CustomerBuilder withUserId(final String userId) {
        this.userId = userId;
        return this;
    }

    public CustomerBuilder withEmail(final String email) {
        this.email = email;
        return this;
    }

    public CustomerBuilder withPreferredLocale(final Locale preferredLocale) {
        this.preferredLocale = preferredLocale;
        return this;
    }

    public CustomerBuilder withFirstName(final String firstName) {
        this.firstName = firstName;
        return this;
    }

    public CustomerBuilder withLastName(final String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CustomerBuilder withAnonymous(final Boolean anonymous) {
        this.anonymous = anonymous;
        return this;
    }

    public CustomerBuilder withCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public CustomerBuilder withStoreCode(final String storeCode) {
        this.storeCode = storeCode;
        return this;
    }

    public CustomerBuilder withStatus(final int status) {
        this.status = status;
        return this;
    }

	public CustomerBuilder withTokens(final PaymentMethod... tokens) {
		this.paymentMethods = tokens;
    	return this;
    }

	public CustomerBuilder withDefaultToken(final PaymentMethod method) {
		this.method = method;
    	return this;
    }

    public CustomerBuilder withClearTextPassword(final String clearTextPassword) {
        this.clearTextPassword = clearTextPassword;
        return this;
    }

    @Override
    public Customer build() {
        Customer customer = beanFactory.getBean(ContextIdNames.CUSTOMER);
        customer.setUserId((String) ObjectUtils.defaultIfNull(userId, "john.smith@elasticpath.com"));
        customer.setPreferredLocale((Locale) ObjectUtils.defaultIfNull(preferredLocale, Locale.ENGLISH));
        customer.setEmail((String) ObjectUtils.defaultIfNull(email, "john.smith@elasticpath.com"));
        customer.setFirstName((String) ObjectUtils.defaultIfNull(firstName, "James"));
        customer.setLastName((String) ObjectUtils.defaultIfNull(lastName, "Bond"));
        customer.setCreationDate((Date) ObjectUtils.defaultIfNull(creationDate, new Date()));
        customer.setStatus((Integer) ObjectUtils.defaultIfNull(status, Customer.STATUS_ACTIVE));
        customer.setClearTextPassword((String) ObjectUtils.defaultIfNull(clearTextPassword, "password"));
        customer.setStoreCode((String) ObjectUtils.defaultIfNull(storeCode, "storeCode"));
        customer.setAnonymous((Boolean) ObjectUtils.defaultIfNull(anonymous, Boolean.FALSE));
        if (paymentMethods != null) {
        	customer.getPaymentMethods().addAll(Arrays.asList(paymentMethods));
        }
		customer.getPaymentMethods().setDefault(method);

        return customer;
    }
}
