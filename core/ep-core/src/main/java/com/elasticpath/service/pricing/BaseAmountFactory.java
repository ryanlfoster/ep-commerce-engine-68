package com.elasticpath.service.pricing;

import java.math.BigDecimal;

import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;

/**
 * Factory for creating BaseAmount objects.
 */
public interface BaseAmountFactory {

	/**
	 * Factory method for creating BaseAmounts with specified guid.
	 * 
	 * Throws EpServiceException if validation error occurs.
	 * 
	 * @param guid identifier.
	 * @param objGuid target object GUID. Cannot be null or Empty.
	 * @param objType target object type. Cannot be null or Empty.
	 * @param qty quantity. Must be > 0. 
	 * @param list amount. Must be > 0.
	 * @param sale amount. Optional.
	 * @param descriptorGuid identifier of the price list descriptor. Cannot be null or Empty.
	 * 
	 * @return the newly created BaseAmount
	 * 
	 * @throws BaseAmountInvalidException base exception for quantity, sale price, list prices errors
	 */
	BaseAmount createBaseAmount(final String guid, final String objGuid, final String objType,
			final BigDecimal qty, final BigDecimal list,
			final BigDecimal sale, final String descriptorGuid)
			throws BaseAmountInvalidException;
	
	/**
	 * Factory method for creating an empty BaseAmount.
	 * @return BaseAmount object with no fields set.
	 */
	BaseAmount createBaseAmount();

}