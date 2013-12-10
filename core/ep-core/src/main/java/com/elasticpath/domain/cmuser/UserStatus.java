package com.elasticpath.domain.cmuser;

/**
 * Cm user status.
 */
public enum UserStatus {
	/** enabled. */
	ENABLED("active"),
	/** disabled. */
	DISABLED("disabled"),
	/** locked. */
	LOCKED("locked");
	
	private String propertyKey = "";

	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	UserStatus(final String propertyKey) {
		this.propertyKey = propertyKey;
	}

	/**
	 * Get the property key.
	 * 
	 * @return the property key
	 */
	public String getPropertyKey() {
		return this.propertyKey;
	}
}