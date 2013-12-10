package com.elasticpath.domain.order;

/**
 * Represents order return status. 
 */
public enum OrderReturnStatus {
	
	/**
	 * The <code>OrderReturnStatus</code> instance for a awaiting stock return status.
	 */
	AWAITING_STOCK_RETURN("OrderReturnStatus_AwaitingStockReturn"),

	/**
	 * The <code>OrderReturnStatus</code> instance for a awaiting completion status.
	 */
	AWAITING_COMPLETION("OrderReturnStatus_AwaitingCompletion"),
	
	/**
	 * The <code>OrderReturnStatus</code> instance for a cancelled status.
	 */
	CANCELLED("OrderReturnStatus_Cancelled"),

	/**
	 * The <code>OrderReturnStatus</code> instance for a completed status.
	 */
	COMPLETED("OrderReturnStatus_Completed");
	
	private String propertyKey = "";

	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	OrderReturnStatus(final String propertyKey) {
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
