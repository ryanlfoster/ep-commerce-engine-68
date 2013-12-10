package com.elasticpath.importexport.common.adapters.customer;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.elasticpath.common.dto.assembler.customer.CreditCardFilter;
import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.common.dto.customer.CustomerDtoAssembler;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.importexport.common.adapters.DtoAssemblerDelegatingAdapter;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.validation.service.ConstraintViolationsSummariser;

/**
 * Adapter for converting between {@link com.elasticpath.domain.customer.Customer} and 
 * {@link com.elasticpath.common.dto.customer.CustomerDTO} objects for importexport. 
 */
public class CustomerAdapter extends DtoAssemblerDelegatingAdapter<Customer, CustomerDTO> {
	
	private Validator validator;
	private ConstraintViolationsSummariser summariser;
	
	/**
	 * Sets the filtering style for credit cards.
	 * @param filter the {@link CreditCardFilter} to set.
	 */
	public void setCardFilteringStyle(final CreditCardFilter filter) {
		((CustomerDtoAssembler) getAssembler()).setCardFilter(filter);
	}

	@Override
	public Customer buildDomain(final CustomerDTO source, final Customer target) {
		Customer customer = super.buildDomain(source, target);
		Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
		if (violations.isEmpty()) {
			return customer;
		}
		throw new PopulationRollbackException("IE-30851", customer.getUserId(), getSummariser().summarise(violations));
	}
	
	public void setValidator(final Validator validator) {
		this.validator = validator;
	}

	protected Validator getValidator() {
		return validator;
	}

	public void setSummariser(final ConstraintViolationsSummariser summariser) {
		this.summariser = summariser;
	}

	protected ConstraintViolationsSummariser getSummariser() {
		return summariser;
	}
	
}