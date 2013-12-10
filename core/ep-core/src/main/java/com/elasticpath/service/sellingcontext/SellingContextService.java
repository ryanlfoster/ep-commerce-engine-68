/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.sellingcontext;

import java.util.List;

import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Provide selling context related business service.
 */
public interface SellingContextService extends EpPersistenceService {

	/**
	 * Get SellingContext by guid. 
	 * @param sellingContextGuid entity guid
	 * @return SellingContext entity
	 * @throws EpServiceException for any persistence errors
	 */
	SellingContext getByGuid(String sellingContextGuid) throws EpServiceException;
	
	/**
	 * Get list of SellingContext by named condition guid.
	 * @param namedConditionGuid given named condition guid.
	 * @return list of SellingContext entity
	 * @throws EpServiceException for any persistence errors 
	 */
	List<SellingContext> getByNamedConditionGuid(String namedConditionGuid);
	
	/**
	 * Save or update SellingContext entity.
	 * @param sellingContext entity for save or update
	 * @return result operation entity
	 * @throws EpServiceException for any persistence errors
	 */
	SellingContext saveOrUpdate(SellingContext sellingContext) throws EpServiceException;

	/**
	 * Try to remove SellingContext entity.
	 * @param sellingContext entity for remove
	 * @throws EpServiceException for any persistence errors
	 */
	void remove(SellingContext sellingContext) throws EpServiceException;
	
	/**
	 * Find all SellingContext entities.
	 * @return return result list
	 * @throws EpServiceException for any persistence errors
	 */
	List<SellingContext> findAll() throws EpServiceException;
}
