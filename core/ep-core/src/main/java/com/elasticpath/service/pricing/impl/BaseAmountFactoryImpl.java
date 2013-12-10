package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.pricing.BaseAmountFactory;

/**
 * Base implementation of the BaseAmountFactory.
 */
public class BaseAmountFactoryImpl implements BaseAmountFactory {

	private Validator validator;
	
	@Override
	public BaseAmount createBaseAmount(final String guid, final String objGuid, final String objType, final BigDecimal qty, 
			final BigDecimal list, final BigDecimal sale, final String descriptorGuid) 
			throws BaseAmountInvalidException {
		BaseAmountImpl result = new BaseAmountImpl(guid, objGuid, objType, qty, list, sale, descriptorGuid);
		result.initialize();
		if (StringUtils.isNotEmpty(objGuid)) {
			verifyBaseAmount(result);
			result.setQuantityScaleToInteger();
		}
		
		return result;
	}

	private void verifyBaseAmount(final BaseAmount baseAmount) {
		Errors errors = new BeanPropertyBindingResult(baseAmount, "BaseAmount");
		this.validator.validate(baseAmount, errors);
		if (errors.hasErrors()) {
			throw new BaseAmountInvalidException("BaseAmount validation error", errors); 
		}
	}
	
	@Override
	public BaseAmount createBaseAmount() {
		return new BaseAmountImpl();
	}

	/**
	 * Set validator for BaseAmount object. 
	 * @param validator the validator to set
	 */
	public void setValidator(final Validator validator) {		
		this.validator = validator;
	}



}
