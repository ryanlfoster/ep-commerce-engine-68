package com.elasticpath.domain.advancedsearch;

/**
 * Represents visibility options for advanced search query.
 */
public enum QueryVisibility {
	
	/**
	 * Public visibility.
	 */
	PUBLIC("Public"),
	
	/**
	 * Private visibility.
	 */
	PRIVATE("Private");
	
	private String propertyKey = "";
	
	/**
	 * Constructor.
	 * 
	 * @param propertyKey the property key.
	 */
	QueryVisibility(final String propertyKey) {
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
