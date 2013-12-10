package com.elasticpath.ql.parser;

/**
 * <code>EpQuery</code>'s fetch type.
 */
public enum FetchType {
	/** UID. */
	UID("UID"), 
	
	/** UID. */
	GUID("GUID");
	private String type;

	private FetchType(final String type) {
		this.type = type;
	}

	/**
	 * Returns String representation of the type.
	 * @return string value of type.
	 */
	public String getType() {
		return type;
	}
}
