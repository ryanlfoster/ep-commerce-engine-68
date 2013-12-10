package com.elasticpath.service.search;

import java.util.Set;

import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Represents a <code>SearchCriteria</code> that has keywords.
 */
public interface SpellSuggestionSearchCriteria extends SearchCriteria {
	/**
	 * Gets the list of potential misspelled strings.
	 * 
	 * @return the list of potential misspelled strings
	 */
	Set<String> getPotentialMisspelledStrings();
}
