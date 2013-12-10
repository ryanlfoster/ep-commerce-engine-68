package com.elasticpath.service.search;

import java.util.List;
import java.util.Map;

import com.elasticpath.persistence.api.EpPersistenceException;

/**
 * An interface that describes a way to access a spelling index to determine if a word exists, or
 * suggest words similar to a provided word.
 */
public interface SpellIndexSearcher {
	/**
	 * Generates a map of potential fixes to strings that were misspelled. The key is the string
	 * that could be misspelled where the list is the suggestions for that word only.
	 * 
	 * @param searchCriteria the keyword search criteria
	 * @return map of potential fixes to string that were misspelled
	 * @throws EpPersistenceException in case of any errors
	 */
	Map<String, List<String>> suggest(final SpellSuggestionSearchCriteria searchCriteria) throws EpPersistenceException;
}
