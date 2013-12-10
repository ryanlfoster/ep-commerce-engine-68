package com.elasticpath.validation.service.impl;

import javax.validation.ConstraintViolation;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.customer.CustomerProfile;

/**
 * Creates a summary of a set of constraint violations which are results of validating a customer object. 
 */
public class CustomerConstraintViolationsSummariser extends SimpleConstraintViolationsSummariser {
	
	private static final String CUSTOMER_PROFILE = "customerProfile.";

	@Override
	protected <T> Object getInvalidValue(final ConstraintViolation<T> violation) {
		if (isCustomerProfileAttribute(violation)) {
			CustomerProfile customerProfile = (CustomerProfile) super.getInvalidValue(violation);
			return customerProfile.getProfileValue(getCustomerProfileAttributeKey(violation));
		}
		return super.getInvalidValue(violation);
	}
	
	@Override
	protected <T> String getPropertyName(final ConstraintViolation<T> violation) {
		if (isCustomerProfileAttribute(violation)) {
			CustomerProfile customerProfile = (CustomerProfile) super.getInvalidValue(violation);
			AttributeValue attributeValue = customerProfile.getProfileValueMap().get(getCustomerProfileAttributeKey(violation));
			return attributeValue.getAttribute().getName();
		}
		return super.getPropertyName(violation);
	}
	
	
	/**
	 * Checks if the violation is related to a customer profile attribute.
	 *
	 * @param <T> the generic type
	 * @param violation the violation
	 * @return true, if is customer profile attribute
	 */
	protected <T> boolean isCustomerProfileAttribute(final ConstraintViolation<T> violation) {
		String propertyPath = String.valueOf(violation.getPropertyPath());
		return propertyPath.startsWith(CUSTOMER_PROFILE) && violation.getInvalidValue() instanceof CustomerProfile;
	}
	
	/**
	 * Gets the customer profile attribute key.
	 *
	 * @param <T> the generic type
	 * @param violation the violation
	 * @return the customer profile attribute key
	 */
	protected <T> String getCustomerProfileAttributeKey(final ConstraintViolation<T> violation) {
		String propertyPath = String.valueOf(violation.getPropertyPath());
		return propertyPath.substring(CUSTOMER_PROFILE.length());
	}
}
