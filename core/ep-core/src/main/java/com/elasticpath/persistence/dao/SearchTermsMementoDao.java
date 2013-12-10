/*
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.persistence.dao;

import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.service.EpService;

/**
 * Provides CRUD services on {@link SearchTermsMemento}.
 */
public interface SearchTermsMementoDao extends EpService {

	/**
	 * Determine if the SearchTermsMemento exists.
	 *
	 * @param mementoId The SearchTermsMemento's ID.
	 * @return True if the SearchTermsMemento exists, false otherwise.
	 */
	boolean exists(SearchTermsMemento.SearchTermsId mementoId);

	/**
	 * Find the SearchTermsMemento by its ID.
	 *
	 * @param mementoId The ID. Cannot be null.
	 * @return The SearchTermsMemento or null if it can't be found.
	 */
	SearchTermsMemento find(SearchTermsMemento.SearchTermsId mementoId);
	
	/**
	 * Removes the SearchTermsMemento by the given id.
	 * 
	 * @param mementoId The SearchTermsMemento id. Cannot be null.
	 */
	void remove(SearchTermsMemento.SearchTermsId mementoId);
	
	/**
	 * Save the given SearchTermsMemento.
	 * Cannot use this method to update an existing SearchTermsMemento because it is supposed to be immutable
	 * (and its guid is derived from its content).
	 *
	 * @param memento The SearchTermsMemento.
	 */
	void saveSearchTermsMemento(SearchTermsMemento memento);

}
