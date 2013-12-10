package com.elasticpath.sfweb.viewbean;

import java.util.Map;

/**
 * View bean for advanced search summary options display.
 */
public interface AdvancedSearchSummaryOptionsBean {

	/**
	 * Gets the brand string for display.
	 * @return String representation of the brands in the previous search.
	 */
	String getBrandString();
	
	/**
	 * Sets the brands string for display.
	 * @param brandString The brand string to display
	 */
	void setBrandString(String brandString);
	
	/**
	 * Gets the price string for display.
	 * @return The price string to display.
	 */
	String getPriceString();
	
	/**
	 * Sets the price string to display.
	 * @param priceString the price string to display.
	 */
	void setPriceString(String priceString);
	
	/**
	 * Gets the attribute map containing the attribute keys and its attribute value filter display name.
	 * @return A map of the attribute keys and its attribute value filter display name.
	 */
	Map<String, String> getAttributeMap();
	
	/**
	 * Sets the attribute map containing the attribute keys and its attribute value filter display name.
	 * @param attributeMap The map to set
	 */
	void setAttributeMap(Map<String, String> attributeMap);
}
