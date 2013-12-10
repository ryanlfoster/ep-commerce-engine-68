/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.contentspace.impl;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperRepository;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.service.targetedselling.impl.AbstractTargetedSellingServiceImpl;

/**
 * Service for Dynamic Content.
 */
public class DynamicContentServiceImpl extends AbstractTargetedSellingServiceImpl<DynamicContent> implements DynamicContentService {

	private ContentWrapperRepository contentWrapperRepository;

	// SELECT dc FROM DynamicContentImpl dc WHERE dc.name = ?1
	private static final String QUERY_FIND_BY_NAME = "DYNAMIC_CONTENT_FIND_BY_NAME";

	// SELECT dc FROM DynamicContentImpl dc WHERE dc.name LIKE ?1
	private static final String QUERY_FIND_BY_NAME_LIKE = "DYNAMIC_CONTENT_FIND_BY_NAME_LIKE";

	// SELECT dc FROM DynamicContentImpl dc
	private static final String QUERY_FIND_ALL = "DYNAMIC_CONTENT_SELECT_ALL";

	@Override
	public DynamicContent add(final DynamicContent dynamicContent) throws EpServiceException {
		sanityCheck();
		if (exists(dynamicContent)) {
			throw new EpServiceException("Dynamic content with the name \"" + dynamicContent.getName() + "\" already exists");
		}
		getPersistenceEngine().save(dynamicContent);
		return dynamicContent;
	}

	@Override
	public void remove(final DynamicContent dynamicContent) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(dynamicContent);
	}

	@Override
	public DynamicContent saveOrUpdate(final DynamicContent dynamicContent) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(dynamicContent);
	}

	/**
	 * Find if the Dynamic Content exists.
	 *
	 * @param dynamicContent the dynamic content
	 * @return true, if successful
	 * @throws EpServiceException the EP service exception
	 */
	public boolean exists(final DynamicContent dynamicContent) throws EpServiceException {
		if (null == dynamicContent || null == dynamicContent.getName()) {
			return false;
		}
		final DynamicContent existingDynamicContent = findByName(dynamicContent.getName());
		boolean nameExists = false;
		if (existingDynamicContent != null && existingDynamicContent.getUidPk() != dynamicContent.getUidPk()) {
			nameExists = true;
		}
		return nameExists;
	}

	@Override
	public List<DynamicContent> findAll() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_SELECT_ALL");
	}

	@Override
	public DynamicContent findByGuid(final String guid) throws EpServiceException {
		sanityCheck();
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve content with null guid.");
		}

		final List<DynamicContent> results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_GUID",
				escapeSpecialCharacters(guid));
		DynamicContent result = null;
		if (results.size() == 1) {
			result = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate objects with same guid exist -- " + guid);
		}
		return result;
	}

	@Override
	public DynamicContent findByName(final String name) throws EpServiceException {
		sanityCheck();
		if (name == null) {
			throw new EpServiceException("Cannot retrieve content with null name.");
		}

		final List<DynamicContent> results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_NAME",
				escapeSpecialCharacters(name));
		DynamicContent dynamicContent = null;
		if (results.size() == 1) {
			dynamicContent = results.get(0);
		} else if (results.size() > 1) {
			throw new EpServiceException("Inconsistent data -- duplicate dynamic contents with same name exist -- " + name);
		}
		return dynamicContent;
	}

	@Override
	public List<DynamicContent> findByNameLike(final String string) throws EpServiceException {
		sanityCheck();
		final List<DynamicContent> results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_NAME_LIKE",
				"%" + escapeSpecialCharacters(string) + "%");
		return results;
	}

	@Override
	public List<DynamicContent> getAllByContentWrapperId(final String wrapperId) throws EpServiceException {
		sanityCheck();
		if (wrapperId == null) {
			throw new EpServiceException("Cannot retrieve null wrapperId.");
		}
		final List<DynamicContent> results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_SELECT_BY_WRAPPER_ID",
				escapeSpecialCharacters(wrapperId));
		return results;
	}

	@Override
	public ContentWrapperRepository getContentWrapperRepository() throws EpServiceException {
		if (contentWrapperRepository == null) {
			throw new EpServiceException("ContentWrapperRepository is not initialized...");
		}
		return contentWrapperRepository;
	}

	@Override
	public void setContentWrapperRepository(final ContentWrapperRepository contentWrapperRepository) {
		this.contentWrapperRepository = contentWrapperRepository;
	}

	@Override
	public Map<String, ContentWrapper> getContentWrappersMap() throws EpServiceException {
		return getContentWrappersMap(false);
	}

	@Override
	public Map<String, ContentWrapper> getContentWrappersMap(final boolean forceReload) throws EpServiceException {
		return contentWrapperRepository.getContentWrappers(forceReload);
	}

	@Override
	protected String getFindAllQueryName() {
		return QUERY_FIND_ALL;
	}

	@Override
	protected String getQueryFindByName() {
		return QUERY_FIND_BY_NAME;
	}

	@Override
	protected String getQueryFindByNameLike() {
		return QUERY_FIND_BY_NAME_LIKE;
	}
}
