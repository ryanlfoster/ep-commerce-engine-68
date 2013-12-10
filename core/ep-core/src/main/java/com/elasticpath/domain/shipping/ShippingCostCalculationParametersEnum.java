package com.elasticpath.domain.shipping;

import java.util.HashMap;
import java.util.Map;

/**
 * This enum represents constants for parameter keys.
 */
public enum ShippingCostCalculationParametersEnum {
	/** Fixed price. */
	FIXED_PRICE("CalculationParameter_FixedPrice", true),

	/** Fix base. */
	FIXED_BASE("CalculationParameter_FixedBased", true),

	/** Percentage of order total. */
	PERCENTAGE_OF_ORDER_TOTOAL("CalculationParameter_PercentageOfTotalOrder", false),

	/** Percentage of order total. */
	COST_PER_UNIT_WEIGHT("CalculationParameter_CostPerUnitWeigh", true);

	private static final Map<String, ShippingCostCalculationParametersEnum> PARAMS_MAP = new HashMap<String, ShippingCostCalculationParametersEnum>();

	static {
		PARAMS_MAP.put(FIXED_PRICE.getKey(), FIXED_PRICE);
		PARAMS_MAP.put(FIXED_BASE.getKey(), FIXED_BASE);
		PARAMS_MAP.put(PERCENTAGE_OF_ORDER_TOTOAL.getKey(), PERCENTAGE_OF_ORDER_TOTOAL);
		PARAMS_MAP.put(COST_PER_UNIT_WEIGHT.getKey(), COST_PER_UNIT_WEIGHT);
	}

	private String key;

	private boolean currencyAware;

	/**
	 * Gets cost parameter enum by string key.
	 * 
	 * @param key parameter key
	 * @return <code>CalculationCostParameters</code> enum or null if key isn't associated with any enum.
	 */
	public static ShippingCostCalculationParametersEnum getCalculationCostParameter(final String key) {
		return PARAMS_MAP.get(key);
	}

	private ShippingCostCalculationParametersEnum(final String key, final boolean currencyAware) {
		this.key = key;
		this.currencyAware = currencyAware;
	}

	/**
	 * Gets key of the calculation parameter.
	 * 
	 * @return key of the calculation parameter
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Returns true if this calculation parameter is currency aware, false otherwise.
	 * 
	 * @return true if this calculation parameter is currency aware, false otherwise.
	 */
	public boolean isCurrencyAware() {
		return currencyAware;
	}

	/**
	 * Gets key of the calculation parameter.
	 * 
	 * @return key of the calculation parameter
	 */
	public String toString() {
		return getKey();
	}
}
