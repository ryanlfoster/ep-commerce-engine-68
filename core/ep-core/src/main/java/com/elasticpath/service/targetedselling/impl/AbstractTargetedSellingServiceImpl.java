/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.targetedselling.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.targetedselling.TargetedSellingService;

/**
 * AbstractServiceImpl abstract implementations of Service interface.
 *
 * @param <T>
 */
public abstract class AbstractTargetedSellingServiceImpl<T extends Entity> extends AbstractEpPersistenceServiceImpl
	implements TargetedSellingService<T> {

	private static final String UNSUPPORTED_MESSAGE =
			"This method in the current calling context is unsupported. Subclasses must override this method.";

	/**
	 * @return Query name for find all operation.
	 */
	protected String getFindAllQueryName() {
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	/**
	 * @return Query name for find by name operation.
	 */
	protected String getQueryFindByName() {
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	/**
	 * @return Query name for find by name via like operation.
	 */
	protected String getQueryFindByNameLike() {
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	/**
	 * Get a persistent instance with the given id.
	 *
	 * @param uid the persistent instance uid
	 * @return the persistent instance if exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 * @deprecated Use an associated findByGuid method instead of getObject
	 */
	@Override
	@Deprecated
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpServiceException("Operation getObject by uid not supported");
	}

	@Override
	public T add(final T object) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().save(object);
		return object;
	}

	@Override
	public void remove(final T object) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(object);
	}

	@Override
	public T saveOrUpdate(final T dynamicContent) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(dynamicContent);
	}

	@Override
	public T findByName(final String name) throws EpServiceException {
		sanityCheck();
		if (name == null) {
			throw new EpServiceException("Cannot retrieve content with null name.");
		}

		// @TODO: findByNameLike() escapes name, but this method doesn't. There is no JIRA attached to the checkin to explain why.
		final List<T> results = getPersistenceEngine().retrieveByNamedQuery(getQueryFindByName(), name);
		T result = null;
		if (results.size() == 1) {
			result = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate objects with same name exist -- " + name);
		}
		return result;
	}

	@Override
	public List<T> findByNameLike(final String string) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(getQueryFindByNameLike(), "%" + escapeSpecialCharacters(string) + "%");
	}

	@Override
	public List<T> findAll() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery(getFindAllQueryName());
	}

	/**
	 * Currently this algorithm escapes only \ and [ since it is required for use with named queries.
	 *
	 * @param rawString unescaped raw string.
	 * @return string with escaped special characters.
	 */
	protected String escapeSpecialCharacters(final String rawString) {
		if (StringUtils.isNotBlank(rawString)) {
			return rawString.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\[", "\\\\[");
		}
		return rawString;
	}
}
