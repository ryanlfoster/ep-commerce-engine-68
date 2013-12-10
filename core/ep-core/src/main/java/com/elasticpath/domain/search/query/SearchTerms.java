package com.elasticpath.domain.search.query;

/**
 * Represents the input for a keyword search operation.
 */
public interface SearchTerms {
	/**
	 * @return the keywords
	 */
	String getKeywords();
	
	/**
	 * Set the keywords.
	 * @param keywords the keywords
	 */
	void setKeywords(String keywords);
}
