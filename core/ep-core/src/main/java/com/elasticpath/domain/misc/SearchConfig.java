package com.elasticpath.domain.misc;

import java.util.Set;


/**
 * Holds configuration data about searching.
 */
public interface SearchConfig {
	
	/** Default value for maximum results to return. */
	int MAX_RETURN_NUMBER_DEFAULT = 0;
	
	/** Default value for prefix length. */
	int PREFIX_LENGTH_DEFAULT = 0;
	
	/** Default value for minimum similarity. */
	float MINIMUM_SIMILARITY_DEFAULT = 0.75F;
	
	/** Default value for minimum results threshold. */
	int MINIMUM_RESULTS_THRESHOLD_DEFAULT = 10;
	
	/** Default value for maximum results threshold. */
	int MAXIMUM_RESULTS_THRESHOLD_DEFAULT = 10000;
	
	/** Default value for maximum suggestions per word. */
	int MAXIMUM_SUGGESTIONS_PER_WORD_DEFAULT = 3;
	
	/** Default value for accuracy. */
	float ACCURACY_DEFAULT = 1.0F;
	
	/** Default boost value. */
	float BOOST_DEFAULT = 1.0F;
	

	/**
	 * Returns the number of maximum results to return to the search client as the search result.
	 * 
	 * @return the number of maximum results to return to the search client as the search result
	 */
	int getMaxReturnNumber();
	
	/**
	 * Sets the number of maximum results to return to the search client as the search result.
	 * 
	 * @param number the number of maximum results to return, set it to 0 for no limit.
	 */
	void setMaxReturnNumber(final int number);
	
	/**
	 * Returns the minimum similarity fuzzy search setting, value should be between 0 and 1.
	 * 
	 * @return the minimum similarity fuzzy search setting, value should be between 0 and 1
	 */
	float getMinimumSimilarity();
	
	/**
	 * Sets the minimum similarity fuzzy search setting, value should be between 0 and 1.
	 * 
	 * @param minimumSimilarity the minimum similarity fuzzy search setting
	 */
	void setMinimumSimilarity(final float minimumSimilarity);
	
	/**
	 * Returns the fuzzy search setting for the length of common non-fuzzy prefix.
	 * 
	 * @return the length of common non-fuzzy prefix
	 */
	int getPrefixLength();
	
	/**
	 * Sets fuzzy search setting for the length of common non-fuzzy prefix.
	 * 
	 * @param prefixLength length of common non-fuzzy prefix
	 */
	void setPrefixLength(final int prefixLength);
	
	/**
	 * Returns the number of search results that, if not exceeded will trigger the generation of
	 * search suggestions.
	 * 
	 * @return the minimum results threshold
	 */
	int getMinimumResultsThreshold();

	/**
	 * Sets the number of search results that, if not exceeded will trigger the generation of
	 * search suggestions.
	 * 
	 * @param minimumResultsThreshold the minimum results threshold
	 */
	void setMinimumResultsThreshold(final int minimumResultsThreshold);
	

	/**
	 * Gets the number of search results that, if exceed, will trigger the generation of search
	 * suggestions.
	 * 
	 * @return the maximum results threshold
	 */
	int getMaximumResultsThreshold();

	/**
	 * Sets the number of search results that, if exceed, will trigger the generation of search
	 * suggestions.
	 * 
	 * @param maximumResultsThreshold the maximum results threshold
	 */
	void setMaximumResultsThreshold(final int maximumResultsThreshold);

	/**
	 * Returns the maximum number of suggestions per word that will be generated when generating
	 * search suggestions.
	 * 
	 * @return the maximum number of suggestion per word
	 */
	int getMaximumSuggestionsPerWord();

	/**
	 * Sets the maximum number of suggestions per word that will be generated when generating
	 * search suggestions.
	 * 
	 * @param maximumSuggestionsPerWord the maximum number of suggestions per word
	 */
	void setMaximumSuggestionsPerWord(final int maximumSuggestionsPerWord);
	
	/**
	 * Returns the degree of similarity that a word must have to the original word in order to be
	 * suggested.
	 * 
	 * @return the similarity
	 */
	float getAccuracy();

	/**
	 * Sets the degree of similarity that a word must have to the original word in order to be
	 * suggested.
	 * 
	 * @param accuracy the similarity
	 */
	void setAccuracy(final float accuracy);
	
	/**
	 * Gets the boost amount for the given value if it exists. If it doesn't returns the default.
	 *
	 * @param value the value to get the boost for
	 * @return a boost amount for the given value
	 */
	float getBoostValue(final String value);
	
	/**
	 * Gets the {@link Set} of attributes that are not to be searched upon.
	 * 
	 * @return {@link Set} of attributes that are not to be searched upon
	 */
	Set<String> getExclusiveAttributes();

	/**
	 * Set the {@link Set} of attributes that are not to be searched upon.
	 * 
	 * @param exclusiveAttributeList {@link Set} of attributes that are not to be searched upon
	 */
	void setExclusiveAttributes(final Set<String> exclusiveAttributeList);
	
	/**
	 * @return URL of the search host. May be different URLs within a cluster.
	 */
	String getSearchHost();
}
