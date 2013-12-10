package com.elasticpath.domain.search;

import com.elasticpath.persistence.api.Persistable;

/**
 * A synonym used in a {@link SynonymGroup}.
 */
public interface Synonym extends Persistable, Comparable<Synonym> {

	/**
	 * Gets the synonym.
	 * 
	 * @return the synonym
	 */
	String getSynonym();

	/**
	 * Sets the synonym.
	 * 
	 * @param synonym the synonym
	 */
	void setSynonym(final String synonym);
}
