/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.contentspace;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperRepository;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.targetedselling.TargetedSellingService;

/**
 * Service for {@link DynamicContent}.
 */
public interface DynamicContentService extends TargetedSellingService<DynamicContent> {

	/**
	 * Find {@link DynamicContent} by GUID.
	 *
	 * @param guid the guid
	 * @return the {@link DynamicContent}
	 * @throws EpServiceException - in case of any errors
	 */
	DynamicContent findByGuid(final String guid) throws EpServiceException;

	/**
	 * Find dynamic content objects with the given wrapperId.
	 *
	 * @param wrapperId the wrapper ID.
	 * @return a list of dynamic contents that matches the given wrapper ID, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	List<DynamicContent> getAllByContentWrapperId(final String wrapperId) throws EpServiceException;

	/**
	 * Gets injected ContentWrapperRepository. <br>
	 * Properties of contentWrapperRepository are used only for property injection of <code>ContentWrapperRepository</code>
	 *
	 * @return contentWrapperRepository - injected
	 * @throws EpServiceException - in case of any errors
	 */
	ContentWrapperRepository getContentWrapperRepository() throws EpServiceException;

	/**
	 * Sets ContentWrapperRepository (this is injected in Spring). <br>
	 * Properties of contentWrapperRepository are used only for property injection of <code>ContentWrapperRepository</code>
	 *
	 * @param contentWrapperRepository ContentWrapperRepository to be injected
	 */
	void setContentWrapperRepository(ContentWrapperRepository contentWrapperRepository);

	/**
	 * Gets map of content wrappers from ContentWrapperRepository.
	 *
	 * @return Map of content wrappers
	 * @throws EpServiceException - in case of any errors
	 */
	Map<String, ContentWrapper> getContentWrappersMap() throws EpServiceException;

	/**
	 * Gets map of content wrappers from ContentWrapperRepository.
	 *
	 * @param forceReload refresh content wrapper repository before return content wrappers if true.
	 * @return Map of content wrappers
	 * @throws EpServiceException - in case of any errors
	 */
	Map<String, ContentWrapper> getContentWrappersMap(boolean forceReload) throws EpServiceException;
}
