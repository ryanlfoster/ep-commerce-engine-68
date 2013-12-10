package com.elasticpath.common.dto.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.store.StoreAssociationService;
import com.elasticpath.service.store.StoreService;

/**
 * Assembler for {@link com.elasticpath.domain.store.Store} domain object and {@link com.elasticpath.common.dto.store.StoreDTO}.
 */
public class StoreAssociationDtoAssembler extends AbstractDtoAssembler<StoreAssociationDTO, Store> {

	private BeanFactory beanFactory;
	
	private StoreService storeService;
	
	private StoreAssociationService storeAssociationService;
	
	@Override
	public Store getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.STORE);
	}

	@Override
	public StoreAssociationDTO getDtoInstance() {
		return new StoreAssociationDTO();
	}

	@Override
	public void assembleDto(final Store source, final StoreAssociationDTO target) {
		String storeCode = source.getCode();
		target.setStoreCode(storeCode);
		Collection <String> directlyAssociatedStoreCodes = storeAssociationService.getDirectlyAssociatedStoreCodes(storeCode);
		List <String> directlyAssociatedStoreCodeList = new ArrayList<String>(directlyAssociatedStoreCodes);
		target.setAssociatedStoreCodes(directlyAssociatedStoreCodeList);		
	}

	@Override
	public void assembleDomain(final StoreAssociationDTO source, final Store target) {
		
		String targetStoreCode = target.getCode();
		String sourceStoreCode = source.getStoreCode(); 
		
		if (!sourceStoreCode.equals(targetStoreCode)) {
			throw new EpServiceException("Store code " + targetStoreCode + " does not match store association code " + sourceStoreCode + ".");
		}
		
		Collection <String> directlyAssociatedStoreCodes = source.getAssociatedStoreCodes();
		
		for (String associatedStoreCode : directlyAssociatedStoreCodes) {
			Store associatedStore = findStoreWithCode(associatedStoreCode);
			
			Long associatedStoreUid = associatedStore.getUidPk();
			
			if (!target.getAssociatedStoreUids().contains(associatedStoreUid)) {
				target.getAssociatedStoreUids().add(associatedStoreUid);
			}
		}
	}

	/**
	 * Returns the {@link com.elasticpath.domain.store.Store} associated with the given code. 
	 * @param storeCode code for the {@link com.elasticpath.domain.store.Store} 
	 * @return the requested {@link com.elasticpath.domain.store.Store}
	 * @throws EpServiceException if the {@link com.elasticpath.domain.store.Store} is not found.
	 */
	protected Store findStoreWithCode(final String storeCode) {
		Store store = storeService.findStoreWithCode(storeCode);
		
		if (store == null) {
			throw new EpServiceException("Store with code " + storeCode + " not found.");
		}
		return store;
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @return the storeAssociationService
	 */
	public StoreAssociationService getStoreAssociationService() {
		return storeAssociationService;
	}

	/**
	 * @param storeAssociationService the storeAssociationService to set
	 */
	public void setStoreAssociationService(final StoreAssociationService storeAssociationService) {
		this.storeAssociationService = storeAssociationService;
	}

}
