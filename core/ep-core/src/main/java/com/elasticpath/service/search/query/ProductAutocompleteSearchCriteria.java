/**
 * 
 */
package com.elasticpath.service.search.query;

/**
 * A criteria for autocomplete search.
 *
 */
public class ProductAutocompleteSearchCriteria extends ProductSearchCriteria {

	private static final long serialVersionUID = 1L;

	private String searchText;

	/**
	 * Default constructor.
	 */
	public ProductAutocompleteSearchCriteria() {
		super();
	}

	/**
	 * @param searchText text for search
	 */
	public ProductAutocompleteSearchCriteria(final String searchText) {
		super();
		this.searchText = searchText;
	}

	/**
	 * @return the searchText
	 */
	public String getSearchText() {
		return searchText;
	}

	/**
	 * Set the search text.
	 * @param searchText the searchText to set
	 */
	public void setSearchText(final String searchText) {
		this.searchText = searchText;
	}

}
