package com.elasticpath.domain.misc.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.misc.SearchConfigInternal;

/**
 * Default implementation of <code>SearchConfig</code>.
 */
public class SearchConfigImpl implements SearchConfigInternal {

	private int maxReturnNumber = MAX_RETURN_NUMBER_DEFAULT;

	private int prefixLength = PREFIX_LENGTH_DEFAULT;

	private float minimumSimilarity = MINIMUM_SIMILARITY_DEFAULT;

	private int minimumResultsThreshold = MINIMUM_RESULTS_THRESHOLD_DEFAULT;

	private int maximumResultsThreshold = MAXIMUM_RESULTS_THRESHOLD_DEFAULT;

	private int maximumSuggestionsPerWord = MAXIMUM_SUGGESTIONS_PER_WORD_DEFAULT;

	private float accuracy = ACCURACY_DEFAULT;

	private Map<String, Float> boostValues;
	
	private Set<String> exclusiveAttributes;
	
	private String searchHost;
	
	/**
	 * Sets the number of maximum results to return to the search client as the search result.
	 * 
	 * @param number the number of maximum results to return, set it to 0 for no limit.
	 */
	public void setMaxReturnNumber(final int number) {
		this.maxReturnNumber = number;
	}

	/**
	 * Returns the number of maximum results to return to the search client as the search result.
	 * 
	 * @return the number of maximum results to return to the search client as the search result
	 */
	public int getMaxReturnNumber() {
		return maxReturnNumber;
	}

	/**
	 * Returns the minimum similarity fuzzy search setting, value should be between 0 and 1.
	 * 
	 * @return the minimum similarity fuzzy search setting, value should be between 0 and 1
	 */
	public float getMinimumSimilarity() {
		return minimumSimilarity;
	}

	/**
	 * Sets the minimum similarity fuzzy search setting, value should be between 0 and 1.
	 * 
	 * @param minimumSimilarity the minimum similarity fuzzy search setting
	 */
	public void setMinimumSimilarity(final float minimumSimilarity) {
		this.minimumSimilarity = minimumSimilarity;
	}

	/**
	 * Returns the fuzzy search setting for the length of common non-fuzzy prefix.
	 * 
	 * @return the length of common non-fuzzy prefix
	 */
	public int getPrefixLength() {
		return prefixLength;
	}

	/**
	 * Sets fuzzy search setting for the length of common non-fuzzy prefix.
	 * 
	 * @param prefixLength length of common non-fuzzy prefix
	 */
	public void setPrefixLength(final int prefixLength) {
		this.prefixLength = prefixLength;
	}

	/**
	 * Returns the number of search results that, if not exceeded will trigger the generation of
	 * search suggestions.
	 * 
	 * @return the minimum results threshold
	 */
	public int getMinimumResultsThreshold() {
		return minimumResultsThreshold;
	}

	/**
	 * Sets the number of search results that, if not exceeded will trigger the generation of
	 * search suggestions.
	 * 
	 * @param minimumResultsThreshold the minimum results threshold
	 */
	public void setMinimumResultsThreshold(final int minimumResultsThreshold) {
		this.minimumResultsThreshold = minimumResultsThreshold;
	}

	/**
	 * Gets the number of search results that, if exceed, will trigger the generation of search
	 * suggestions.
	 * 
	 * @return the maximum results threshold
	 */
	public int getMaximumResultsThreshold() {
		return maximumResultsThreshold;
	}

	/**
	 * Sets the number of search results that, if exceed, will trigger the generation of search
	 * suggestions.
	 * 
	 * @param maximumResultsThreshold the maximum results threshold
	 */
	public void setMaximumResultsThreshold(final int maximumResultsThreshold) {
		this.maximumResultsThreshold = maximumResultsThreshold;
	}

	/**
	 * Returns the maximum number of suggestions per word that will be generated when generating
	 * search suggestions.
	 * 
	 * @return the maximum number of suggestion per word
	 */
	public int getMaximumSuggestionsPerWord() {
		return maximumSuggestionsPerWord;
	}

	/**
	 * Sets the maximum number of suggestions per word that will be generated when generating
	 * search suggestions.
	 * 
	 * @param maximumSuggestionsPerWord the maximum number of suggestions per word
	 */
	public void setMaximumSuggestionsPerWord(final int maximumSuggestionsPerWord) {
		this.maximumSuggestionsPerWord = maximumSuggestionsPerWord;
	}

	/**
	 * Returns the degree of similarity that a word must have to the original word in order to be
	 * suggested.
	 * 
	 * @return the similarity
	 */
	public float getAccuracy() {
		return accuracy;
	}

	/**
	 * Sets the degree of similarity that a word must have to the original word in order to be
	 * suggested.
	 * 
	 * @param accuracy the similarity
	 */
	public void setAccuracy(final float accuracy) {
		this.accuracy = accuracy;
	}
	
	/**
	 * Gets the boost amount for the given value if it exists. If it doesn't returns the default.
	 *
	 * @param value the value to get the boost for
	 * @return a boost amount for the given value
	 */
	public float getBoostValue(final String value) {
		Float result = getBoostValues().get(value);
		if (result == null) {
			return BOOST_DEFAULT;
		}
		return result;
	}

	/**
	 * Gets the map of boost values for this configuration. The map holds a field name to a float
	 * boost value map.
	 * 
	 * @return the boost map for customer searches
	 */
	public Map<String, Float> getBoostValues() {
		if (boostValues == null) {
			boostValues = new HashMap<String, Float>();
		}
		return boostValues;
	}
	
	/**
	 * Gets the {@link Set} of attributes that are not to be searched upon.
	 * 
	 * @return {@link Set} of attributes that are not to be searched upon
	 */
	public Set<String> getExclusiveAttributes() {
		if (exclusiveAttributes == null) {
			exclusiveAttributes = new HashSet<String>();
		}
		return exclusiveAttributes;
	}

	/**
	 * Set the {@link Set} of attributes that are not to be searched upon.
	 * 
	 * @param exclusiveAttributeList {@link Set} of attributes that are not to be searched upon
	 */
	public void setExclusiveAttributes(final Set<String> exclusiveAttributeList) {
		this.exclusiveAttributes = exclusiveAttributeList;
	}

	/**
	 * Sets the map of boost values for this configuration.
	 *  
	 * @param boostValues the map of values
	 */
	public void setBoostValues(final Map<String, Float> boostValues) {
		this.boostValues = boostValues;
	}
	
	/**
	 * @return URL of the search host
	 */
	public String getSearchHost() {
		return searchHost;
	}

	/**
	 * @param host URL string for the search server
	 */
	public void setSearchHost(final String host) {
		this.searchHost = host;
	}
}
