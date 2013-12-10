package com.elasticpath.domain.order;

/**
 * Represents order return type.
 */
public enum OrderReturnType {

	/**
	 * The <code>OrderReturnType</code> instance for "Return" type.
	 */ 
	RETURN("OrderReturnType_Return"),

	/**
	 * The <code>OrderReturnType</code> instance for "Exchange" type.
	 */
	EXCHANGE("OrderReturnType_Exchange");

	private static final String EMPTY_STRING = "";

	private String propertyKey = EMPTY_STRING;

	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	OrderReturnType(final String propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * Get the localization property key.
	 * 
	 * @return the localized property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}
}
