package com.elasticpath.domain.shipping.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;

/**
 * Factory for shipping code tests.
 */
public final class ShippingCostTestDataFactory {

	private ShippingCostTestDataFactory() {
		// prevent external instantiation
	}
	
	/**
	 * @param params the shipping cost calculation parameters to wrap in the set.
	 * @return a set of shipping cost calculations.
	 */
	public static Set<ShippingCostCalculationParameter> someCalculationParams(final ShippingCostCalculationParameter ... params) {
		Set<ShippingCostCalculationParameter> paramSet = new HashSet<ShippingCostCalculationParameter>();
		paramSet.addAll(Arrays.asList(params));
		return paramSet;
	}

	/**
	 * @param paramEnum the key for the param.
	 * @param value the value for this param.
	 * @param currency the currency to create this param for.
	 * @return a shipping cost calculation method.
	 */
	public static ShippingCostCalculationParameter aCostCalculationParam(
			final ShippingCostCalculationParametersEnum paramEnum,
			final BigDecimal value, 
			final Currency currency) {
		ShippingCostCalculationParameter shippingCostCalculationParameter = aCostCalculationParam(paramEnum, value);
		shippingCostCalculationParameter.setCurrency(currency);
		return shippingCostCalculationParameter;
	}
	
	/**
	 * @param paramEnum the key for the param.
	 * @param value the value for this param.
	 * @return a shipping cost calculation method.
	 */
	public static ShippingCostCalculationParameter aCostCalculationParam(
			final ShippingCostCalculationParametersEnum paramEnum,
			final BigDecimal value) {
		ShippingCostCalculationParameter shippingCostCalculationParameter = new ShippingCostCalculationParameterImpl();
		shippingCostCalculationParameter.setKey(paramEnum.getKey());
		shippingCostCalculationParameter.setValue(value.toString());
		return shippingCostCalculationParameter;
	}
}
