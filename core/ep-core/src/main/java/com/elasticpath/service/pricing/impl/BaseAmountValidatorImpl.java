/**
 * 
 */
package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.elasticpath.domain.pricing.BaseAmount;

/**
 * BaseAmountValidatorImpl.
 *
 */
public class BaseAmountValidatorImpl implements Validator {

	private String errorInvalidGuid;
	private String errorInvalidObjectGuid;
	private String errorInvalidObjectType;
	private String errorInvalidPriceListDescriptor;
	private String errorInvalidQuantity;
	private String errorInvalidListPrice;
	private String errorInvalidSalePrice;
	private String errorSalePriceIsMoreThenListPrice;

	/** {@inheritDoc}*/
	public boolean supports(final Class<?> clazz) {
		return BaseAmount.class.equals(clazz);
	}

	/** {@inheritDoc}*/
	public void validate(final Object target, final Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "objectGuid", errorInvalidObjectGuid);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "guid", errorInvalidGuid);
		ValidationUtils.rejectIfEmpty(errors, "priceListDescriptorGuid", errorInvalidPriceListDescriptor);
		BaseAmount baseAmount = (BaseAmount) target;
		
		this.validateObjectType(baseAmount.getObjectType(), errors);
		this.validateQuantity(baseAmount.getQuantity(), errors);
		this.validateListPrice(baseAmount.getListValue(), errors);
		this.validateSalePrice(baseAmount.getSaleValue(), errors);
		this.validateSalePrice(baseAmount.getListValue(), baseAmount.getSaleValue(), errors);
	}

	@SuppressWarnings("PMD.PreserveStackTrace") // we are using the BigInteger interface to determine integerness 
	private void validateQuantity(final BigDecimal quantity, final Errors errors) {

		String field = "quantity";
		if (quantity == null) {
			addError(field, errorInvalidQuantity, errors);
		} else if (quantity.compareTo(BigDecimal.ONE) < 0) {
			addError(field, errorInvalidQuantity, errors);
		} else {
			try {
				quantity.toBigIntegerExact();
			} catch (ArithmeticException e) {
				addError(field, errorInvalidQuantity, errors);
			}
		}
	}

	private void addError(final String field, final String errorCode, final Errors errors) {
		errors.rejectValue(field, errorCode);
	}
		
	private void validateListPrice(final BigDecimal listPrice, final Errors errors) {
		if (!isListPriceCorrect(listPrice)) {
			addError("listValue", errorInvalidListPrice, errors);
		}
	}
	
	private void validateObjectType(final String objType, final Errors errors) {
		if (objType == null || !isCorrectObjType(objType)) {
			addError("objectType", errorInvalidObjectType, errors);
		}
	}

	private boolean isCorrectObjType(final String objType) {
		return "PRODUCT".equalsIgnoreCase(objType) || "SKU".equalsIgnoreCase(objType);
	}

	private boolean isListPriceCorrect(final BigDecimal listPrice) {
		return !(listPrice == null || listPrice.compareTo(BigDecimal.ZERO) < 0);
	}

	private void validateSalePrice(final BigDecimal salePrice, final Errors errors) {
		if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) < 0) {
			addError("saleValue", errorInvalidSalePrice, errors);
		}
	}
	private void validateSalePrice(final BigDecimal listPrice, final BigDecimal salePrice, final Errors errors) {
		if (salePrice == null) {
			return;
		}
		if (isListPriceCorrect(listPrice) && listPrice.compareTo(salePrice) < 0) {
			addError("saleValue", errorSalePriceIsMoreThenListPrice, errors);
		}
	}

	/**
	 * @param errorInvalidGuid the errorInvalidGuid to set
	 */
	public void setErrorInvalidGuid(final String errorInvalidGuid) {
		this.errorInvalidGuid = errorInvalidGuid;
	}

	/**
	 * @param errorInvalidPriceListDescriptor the errorInvalidPriceListDescriptor to set
	 */
	public void setErrorInvalidPriceListDescriptor(
			final String errorInvalidPriceListDescriptor) {
		this.errorInvalidPriceListDescriptor = errorInvalidPriceListDescriptor;
	}

	/**
	 * @param errorInvalidQuantity the errorInvalidQuantity to set
	 */
	public void setErrorInvalidQuantity(final String errorInvalidQuantity) {
		this.errorInvalidQuantity = errorInvalidQuantity;
	}

	/**
	 * @param errorInvalidListPrice the errorInvalidListPrice to set
	 */
	public void setErrorInvalidListPrice(final String errorInvalidListPrice) {
		this.errorInvalidListPrice = errorInvalidListPrice;
	}

	/**
	 * @param errorInvalidSalePrice the errorInvalidSalePrice to set
	 */
	public void setErrorInvalidSalePrice(final String errorInvalidSalePrice) {
		this.errorInvalidSalePrice = errorInvalidSalePrice;
	}

	/**
	 * @param errorSalePriceIsMoreThenListPrice the errorSalePriceIsMoreThenListPrice to set
	 */
	public void setErrorSalePriceIsMoreThenListPrice(
			final String errorSalePriceIsMoreThenListPrice) {
		this.errorSalePriceIsMoreThenListPrice = errorSalePriceIsMoreThenListPrice;
	}

	/**
	 * @param errorInvalidObjectGuid the errorInvalidObjectGuid to set
	 */
	public void setErrorInvalidObjectGuid(final String errorInvalidObjectGuid) {
		this.errorInvalidObjectGuid = errorInvalidObjectGuid;
	}

	/**
	 * @param errorInvalidObjectType the errorInvalidObjectType to set
	 */
	public void setErrorInvalidObjectType(final String errorInvalidObjectType) {
		this.errorInvalidObjectType = errorInvalidObjectType;
	}

}
