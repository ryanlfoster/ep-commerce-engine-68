/*
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.persistence.dao.impl;

import java.util.List;

import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.SearchTermsMementoDao;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Implementation of {@link SearchTermsMemento}.
 */
public class SearchTermsMementoDaoImpl extends AbstractEpServiceImpl implements SearchTermsMementoDao
{
	private PersistenceEngine persistenceEngine;

	@Override
	public boolean exists(final SearchTermsMemento.SearchTermsId mementoId) {
		List<Object> list = getPersistenceEngine().retrieveByNamedQuery("SEARCH_TERMS_EXIST", mementoId.getValue());
		return !list.isEmpty();
	}

	@Override
	public SearchTermsMemento find(final SearchTermsMemento.SearchTermsId mementoId) {
		List<Object> list = getPersistenceEngine().retrieveByNamedQuery("FIND_SEARCH_TERMS_BY_GUID", mementoId.getValue());
		if (list.isEmpty()) {
			return null;
		}
		return (SearchTermsMemento) list.get(0);
	}
	
	@Override
	public void remove(final SearchTermsMemento.SearchTermsId mementoId) {
	    getPersistenceEngine().executeNamedQuery("DELETE_SEARCH_TERMS_BY_GUID", mementoId.getValue());
	}

	@Override
	public void saveSearchTermsMemento(final SearchTermsMemento memento) {
		getPersistenceEngine().save(memento);
	}
	
	/**
	 * @return The PersistenceEngine.
	 */
	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}
	
	/**
	 * @param persistenceEngine The PersistenceEngine.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

}
