package com.elasticpath.common.pricing.service;

import java.util.List;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.base.exception.EpServiceException;

/**
 * Interface for a BaseAmount update strategy. Implementors will decide how to handle
 * processing of a BaseAmountChangeSet.
 */
public interface BaseAmountUpdateStrategy {
	

	/**
	 * Process the change set by applying the base amount changes.
	 * Assembles all appropriate detached entities before processing them in a separate txn.
	 * This ensures updates always deal with db detached entities and JPA base auditing remains intact.
	 * Does not try to continue on exception.
	 * 
	 * @param changeSet BaseAmounts to be added/removed/updated
	 * @throws EpServiceException on any service errors
	 */
	void modifyBaseAmounts(ChangeSetObjects <BaseAmountDTO> changeSet) throws EpServiceException;
	
	/**
	 * Process the change set by applying the base amount changes in one transaction.
	 * Does not try to continue on exception.
	 * 
	 * Note: transactional by design.
	 * 
	 * @param removalList db detached BaseAmounts for removal
	 * @param addList new BaseAmounts for addition
	 * @param updateList db detached BaseAmounts for update
	 * @throws EpServiceException on any service errors
	 */
	void auditableProcessChanges(final List<BaseAmount> removalList, 
			final List<BaseAmount> addList, final List<BaseAmount> updateList) throws EpServiceException;

}
