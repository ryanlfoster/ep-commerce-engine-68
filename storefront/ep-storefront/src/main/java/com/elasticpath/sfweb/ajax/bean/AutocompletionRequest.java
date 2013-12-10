/**
 * 
 */
package com.elasticpath.sfweb.ajax.bean;


/**
 * A request for autocompletion service.
 * 
 *
 */
public interface AutocompletionRequest {

	/**
	 * @return the search text from user input.
	 */
	String getSearchText();
	
	/**
	 * Set the search text.
	 * @param searchText search string
	 */
	void setSearchText(String searchText);
	
	/**
	 * Returns the category uid specified in the catalog view request.
	 *
	 * @return the category uid
	 */
	String getCategoryUid();

	/**
	 * Sets the category uid.
	 *
	 * @param categoryUid the category uid to set
	 */
	void setCategoryUid(String categoryUid);
	
}
