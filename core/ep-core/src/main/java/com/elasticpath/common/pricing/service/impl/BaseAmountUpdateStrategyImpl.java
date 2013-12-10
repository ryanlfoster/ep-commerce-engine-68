package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountUpdateStrategy;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.BaseAmountService;

/**
 * Strategy for applying BaseAmount changes.
 */
public class BaseAmountUpdateStrategyImpl implements BaseAmountUpdateStrategy {

	private static final Logger LOG = Logger.getLogger(BaseAmountUpdateStrategyImpl.class);

	private BaseAmountDtoAssembler baDtoAssembler;

	private BaseAmountService baService;

	private BeanFactory beanFactory;

	/** processOperation CUD. */
	private enum ProcessOperation { CREATE, UPDATE, DELETE };

	/**
	 * Process the change set by applying the base amount changes in one transaction.
	 * Does not try to continue on exception.
	 *
	 * @param removalList db detached BaseAmounts for removal
	 * @param addList new BaseAmounts for addition
	 * @param updateList db detached BaseAmounts for update
	 * @throws EpServiceException on any service errors
	 */
	public void auditableProcessChanges(final List<BaseAmount> removalList,
			final List<BaseAmount> addList, final List<BaseAmount> updateList) throws EpServiceException {
		this.applyRemovals(removalList);
		this.applyAdditions(addList);
		this.applyUpdates(updateList);
	}

	/**
	 * Process the change set by applying the base amount changes.
	 * Assembles all appropriate detached entities before processing them in a separate txn.
	 * This ensures updates always deal with db detached entities and JPA base auditing remains intact.
	 * Does not try to continue on exception.
	 *
	 * @param changes BaseAmounts to be added/removed/updated
	 * @throws EpServiceException on any service errors
	 */

	public void modifyBaseAmounts(final ChangeSetObjects <BaseAmountDTO> changes) throws EpServiceException {
		List<BaseAmount> removalList = 	assemble(changes.getRemovalList(), ProcessOperation.DELETE);
		List<BaseAmount> addList = assemble(changes.getAdditionList(), ProcessOperation.CREATE);
		List<BaseAmount> updateList = assemble(changes.getUpdateList(), ProcessOperation.UPDATE);

		//this one should be transactional
		getBaseAmountUpdateStrategy().auditableProcessChanges(removalList, addList, updateList);
	}

	/*
	 * Assemble the domain object list depending on operation desired.
	 *
	 * N.B. this could be refactored into the assembler.  Calling assembleDomain
	 * 		for UPDATE/DELETE is not strictly necessary.
	 * For CREATE we use the assembler to create new BaseAmounts from the DTOs.
	 * for DELETE we just lookup the baseAmount and put it in the removal list.
	 * for UPDATE we lookup the baseAmount but then modify it according to List and Sale amounts.
	 */
	private List<BaseAmount> assemble(final List<BaseAmountDTO> dtoList, final ProcessOperation operation) {
		List<BaseAmount> baList = null;
		switch(operation) {
		case CREATE:
			baList = baDtoAssembler.assembleDomain(dtoList);
			break;
		case UPDATE:
			//designed fall through
		case DELETE:
			baList = new ArrayList<BaseAmount>();

			for (BaseAmount baseAmount : baDtoAssembler.assembleDomain(dtoList)) {
				if (StringUtils.isEmpty(baseAmount.getGuid())) {
					break;
				}
				BaseAmount baseAmountPersisted = baService.findByGuid(baseAmount.getGuid());
				if (null == baseAmountPersisted) {
					break;
				}

				switch(operation) {
				case UPDATE:
					baseAmountPersisted.setListValue(baseAmount.getListValue());
					baseAmountPersisted.setSaleValue(baseAmount.getSaleValue());
					break;
				default:
					break;
				}
				baList.add(baseAmountPersisted);
			}
			break;
		default:
			break;
		}
		return baList;
	}

	/**
	 * Save all additions.
	 * Throws EpServiceException on any errors.
	 *
	 * @param additions collection of BaseAmounts.
	 */
	protected void applyAdditions(final Collection<BaseAmount> additions) {
		LOG.debug("Adding" + additions);
		for (BaseAmount baseAmount : additions) {
			baService.add(baseAmount);
		}
	}

	/**
	 * Delete all removals.
	 * Throws EpServiceException on any errors.
	 *
	 * @param removes collection of BaseAmounts.
	 */
	protected void applyRemovals(final Collection<BaseAmount> removes) {
		LOG.debug("Removing" + removes);
		for (BaseAmount baseAmount : removes) {
			baService.delete(baseAmount);
		}
	}

	/**
	 * Apply all updates.
	 * Throws EpServiceException on any errors.
	 *
	 * @param updates the collection of BaseAmounts to update.
	 */
	protected void applyUpdates(final Collection<BaseAmount> updates) {
		LOG.debug("Updating" + updates);
		for (BaseAmount baseAmount : updates) {
			baService.updateWithoutLoad(baseAmount);
		}
	}

	/**
	 * Set the BaseAmountService to use for working with BaseAmounts.
	 * @param baService instance of BaseAmountService
	 */
	public void setBaseAmountService(final BaseAmountService baService) {
		this.baService = baService;
	}


	/**
	 * Set the DTO assembler for BaseAmounts.
	 *
	 * @param baDtoAssembler the assembler
	 */
	public void setBaseAmountDtoAssembler(final BaseAmountDtoAssembler baDtoAssembler) {
		this.baDtoAssembler = baDtoAssembler;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/*
	 * Allows us to call self by proxy.
	 * Enabling declaritive txns.
	 */
	private BaseAmountUpdateStrategy getBaseAmountUpdateStrategy() {
		return beanFactory.getBean(ContextIdNames.BASE_AMOUNT_UPDATE_STRATEGY);
	}


}