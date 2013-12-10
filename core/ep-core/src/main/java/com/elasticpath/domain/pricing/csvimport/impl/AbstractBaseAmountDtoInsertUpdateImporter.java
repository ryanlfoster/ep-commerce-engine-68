package com.elasticpath.domain.pricing.csvimport.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.tools.ant.util.DateUtils;

import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.AbstractInsertUpdateImporter;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.changeset.ChangeSetService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * A base class for insert/update a BaseAmount.
 *
 */
public class AbstractBaseAmountDtoInsertUpdateImporter extends AbstractInsertUpdateImporter {

	private BaseAmountDtoAssembler assembler;
	private BaseAmountService baseAmountService;
	private ChangeSetService changeSetService;
	private Map<String, Object> persistenceListenerMetadataMap;
	private PriceListDescriptorService priceListDescriptorService;
	private TimeService timeService;

	/**
	 * Create default import fault object.
	 * @param baseAmountDto a failed base amount 
	 * @return ImportFault fault object
	 */
	protected ImportFault createImportFault(final BaseAmountDTO baseAmountDto) {
		final ImportFault fault = getBeanFactory().getBean(ContextIdNames.IMPORT_FAULT);
		fault.setLevel(ImportFault.ERROR);
		fault.setCode("import.csvFile.badRow.databaseError");
		if (baseAmountDto == null) {
			fault.setArgs(new Object[] {});
		} else {
			fault.setArgs(new Object[] {baseAmountDto.getObjectGuid(), baseAmountDto.getQuantity()});
		}
		return fault;
	}

	/**
	 * Assembles a {@link BaseAmount} domain object from a {@link BaseAmountDTO}.
	 * This implementation calls {@link #getAssembler()} so that it can use a {@link BaseAmountDtoAssembler}.
	 * @param dto the dto to assemble
	 * @return the domain object
	 */
	protected BaseAmount assembleDomainFromDto(final BaseAmountDTO dto) {
		return getAssembler().assembleDomain(dto);
	}

	/**
	 * Inserts or replaces the given BaseAmount for the PriceListDescriptor represented
	 * by the given GUID. Calls {@link #deleteIfExisting(BaseAmount, String)} and
	 * {@link #insert(BaseAmount, String)}.
	 * @param baseAmount the BaseAmount
	 */
	protected void insertOrReplace(final BaseAmount baseAmount) {
		deleteIfExisting(baseAmount);
		insert(baseAmount);
	}

	/**
	 * Deletes the given BaseAmount from the given PriceListDescriptor if it exists.
	 * Calls {@link BaseAmountDtoInsertUpdateImporterImpl#getBaseAmountFilter()} to do a search
	 * for the BaseAmount first.
	 * @param baseAmount the BaseAmount
	 */
	protected void deleteIfExisting(final BaseAmount baseAmount) {
		if (isBaseAmountExist(baseAmount)) {
			delete(baseAmount);
		}
	}

	/**
	 * Checks whether a BaseAmount matching the given baseAmount exists.
	 * 
	 * @param baseAmount the base amount to check
	 * @return true if the given BaseAmount exists
	 */
	protected boolean isBaseAmountExist(final BaseAmount baseAmount) {
		return getBaseAmountService().exists(baseAmount);
	}
	
	/**
	 * find the base amount. 
	 * 
	 * @param baseAmount the base amount which does not have guid.
	 * @return the base amount from database.
	 */
	protected BaseAmount findBaseAmount(final BaseAmount baseAmount) {
		BaseAmountFilter filter = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter.setObjectGuid(baseAmount.getObjectGuid());
		filter.setObjectType(baseAmount.getObjectType());
		filter.setQuantity(baseAmount.getQuantity());
		filter.setPriceListDescriptorGuid(baseAmount.getPriceListDescriptorGuid());
		Collection<BaseAmount> baseAmounts = getBaseAmountService().findBaseAmounts(filter);
		if (CollectionUtils.isNotEmpty(baseAmounts)) {
			return baseAmounts.iterator().next();
		}
		return null;
	}

	/**
	 * Inserts the given BaseAmount into the given PriceList.
	 * @param baseAmount the BaseAmount to insert
	 */
	protected void insert(final BaseAmount baseAmount) {
		addBaseAmountAndPriceListToChangeSet(baseAmount, ChangeSetMemberAction.ADD);
		getBaseAmountService().add(baseAmount);
	}
	
	/**
	 * Inserts the given BaseAmount into the given PriceList.
	 * @param baseAmount the BaseAmount to insert
	 */
	protected void update(final BaseAmount baseAmount) {
		addBaseAmountAndPriceListToChangeSet(baseAmount, ChangeSetMemberAction.EDIT);
		getBaseAmountService().updateWithoutLoad(baseAmount);
	}


	/**
	 * Deletes the given BaseAmount.
	 * @param baseAmount the BaseAmount to delete
	 */
	protected void delete(final BaseAmount baseAmount) {
		final BaseAmountFilter filter = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		
		filter.setObjectGuid(baseAmount.getObjectGuid());
		filter.setObjectType(baseAmount.getObjectType());
		filter.setQuantity(baseAmount.getQuantity());
		filter.setPriceListDescriptorGuid(baseAmount.getPriceListDescriptorGuid());
		
		final List<BaseAmount> baList = new ArrayList<BaseAmount>();
		baList.addAll(getBaseAmountService().findBaseAmounts(filter));
		
		if (baList.isEmpty()) {
			throw new EpServiceException("BaseAmount with object guid: " + baseAmount.getObjectGuid() + " couldn't be found for deletion!");
		}
			
		final BaseAmount originalBaseAmount = baList.get(0);

		addBaseAmountAndPriceListToChangeSet(originalBaseAmount, ChangeSetMemberAction.DELETE);

		getBaseAmountService().delete(originalBaseAmount);
	}

	/**
	 *
	 * @param baseAmount
	 */
	private void addBaseAmountAndPriceListToChangeSet(final BaseAmount baseAmount, final ChangeSetMemberAction action) {
		if (getChangeSetService().isChangeSetEnabled()) {
			Map<String, String> changeSetMetaDataMap = new HashMap<String, String>();
			changeSetMetaDataMap.put("addedByUserGuid", getChangeSetUserGuid());
			changeSetMetaDataMap.put("dateAdded", getCurrentDateString());
			
			if (action.equals(ChangeSetMemberAction.ADD) || action.equals(ChangeSetMemberAction.DELETE)) { 
				//No need to add price list to the change set if updating base amount.
				String priceListGuid = baseAmount.getPriceListDescriptorGuid();
				PriceListDescriptor priceList = priceListDescriptorService.findByGuid(priceListGuid);
				changeSetMetaDataMap.put("action", ChangeSetMemberAction.EDIT.getName());
				getChangeSetService().addObjectToChangeSet(getChangeSetGuid(), priceList, changeSetMetaDataMap);
			}
			
			changeSetMetaDataMap.put("action", action.getName());
			getChangeSetService().addObjectToChangeSet(getChangeSetGuid(), baseAmount, changeSetMetaDataMap);
		}
	}
	
	private String getCurrentDateString() {
		return DateUtils.format(getTimeService().getCurrentTime().getTime(), "yyyyMMddHHmmssSSS");
	}
	
	/**
	 * @return the time service
	 */
	public TimeService getTimeService() {
		if (timeService == null) {
			timeService = getBeanFactory().getBean(ContextIdNames.TIME_SERVICE);
		}
		return timeService;
	}

	/**
	 */
	private String getChangeSetGuid() {
		return (String) getPersistenceListenerMetadataMap().get("changeSetGuid");
	}
	
	/**
	 *
	 * @return the change set user GUID
	 */
	protected String getChangeSetUserGuid() {
		return (String) getPersistenceListenerMetadataMap().get("changeSetUserGuid");
	}

	/**
	 * @return the baseAmountService
	 */
	private BaseAmountService getBaseAmountService() {
		return baseAmountService;
	}

	/**
	 * @param baseAmountService the baseAmountService to set
	 */
	public void setBaseAmountService(final BaseAmountService baseAmountService) {
		this.baseAmountService = baseAmountService;
	}

	/**
	 * @return the assembler
	 */
	public BaseAmountDtoAssembler getAssembler() {
		return assembler;
	}

	/**
	 * @param assembler the assembler to set
	 */
	public void setAssembler(final BaseAmountDtoAssembler assembler) {
		this.assembler = assembler;
	}

	/**
	 *
	 * @return the changeSetService
	 */
	protected ChangeSetService getChangeSetService() {
		return changeSetService;
	}

	/**
	 *
	 * @param changeSetService the changeSetService to set
	 */
	public void setChangeSetService(final ChangeSetService changeSetService) {
		this.changeSetService = changeSetService;
	}

	/**
	 *
	 * @return the persistenceListenerMetadataMap
	 */
	protected Map<String, Object> getPersistenceListenerMetadataMap() {
		return persistenceListenerMetadataMap;
	}

	/**
	 *
	 * @param persistenceListenerMetadataMap the persistenceListenerMetadataMap to set
	 */
	public void setPersistenceListenerMetadataMap(final Map<String, Object> persistenceListenerMetadataMap) {
		this.persistenceListenerMetadataMap = persistenceListenerMetadataMap;
	}

	/**
	 *
	 * @return the priceListDescriptorService
	 */
	protected PriceListDescriptorService getPriceListDescriptorService() {
		return priceListDescriptorService;
	}

	/**
	 *
	 * @param priceListDescriptorService the priceListDescriptorService to set
	 */
	public void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

}
