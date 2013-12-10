/**
 * 
 */
package com.elasticpath.sfweb.ajax.bean;

/**
 * Autocompletion search result object.
 *
 */
public interface AutocompletionSearchResult {

	/** Get the product GUID. 
	 * @return guid 
	 */
	String getGuid();
	
	/**
	 * Get the product name.
	 * @return product name
	 */
	String getName();
	
	/**
	 * Get the Image URL string.
	 * @return image URL string
	 */
	String getImage();
	
	/**
	 * Get the product Url.
	 * @return the product Url
	 */
	String getUrl();
	
	/**
	 * Get the product description.
	 * @return description
	 */
	String getDescription();
	
	/**
	 * Get the price and currency symbol.
	 * 
	 * @return price and currency symbol if price found otherwise 
	 * empty string. Also empty string is returned when enable 
	 * price settings turned off.
	 *  
	 */
	String getPrice();
	
	
	/**
	 * @param guid the guid to set
	 */
	void setGuid(String guid);

	/**
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * @param image the imageUrl to set
	 */
	void setImage(String image);

	/**
	 * @param url the url to set
	 */
	void setUrl(String url);
	
	/**
	 * @param description the description to set.
	 */
	void setDescription(String description);
	
	/** 
	 * @param price to set.
	 */
	void setPrice(String price);
}
