/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.contentspace;

import java.util.List;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.targetedselling.TargetedSellingService;

/**
 * Service for {@link ContentSpace}.
 */
public interface ContentSpaceService extends TargetedSellingService<ContentSpace> {

	/**
	 * Find all entities.
	 *
	 * @return return result list
	 * @throws EpServiceException for any error
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<ContentSpace> findAll() throws EpServiceException;

	/**
	 * Find by GUID.
	 *
	 * @param guid the GUID
	 * @return the content space
	 * @throws EpServiceException the service exception
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	ContentSpace findByGuid(String guid) throws EpServiceException;

	/**
	 * Find entity by name.
	 *
	 * @param name entity name
	 * @return result entity
	 * @throws EpServiceException for any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	ContentSpace findByName(String name) throws EpServiceException;

	/**
	 * Find entity by name via like.
	 *
	 * @param token the token to match against entity names
	 * @return result entity
	 * @throws EpServiceException for any errors
	 * @deprecated Use the QueryService instead.
	 */
	@Deprecated
	List<ContentSpace> findByNameLike(String token) throws EpServiceException;

	/**
	 * List all {@link DynamicContentDelivery} for the given ContentSpace GUID.
	 *
	 * @param guid The {@link ContentSpace} GUID.
	 * @return A list of {@link DynamicContentDelivery}.
	 * @throws EpServiceException in case of any errors.
	 */
	List<DynamicContentDelivery> findUsesByDynamicContentDelivery(String guid) throws EpServiceException;

}
