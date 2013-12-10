package com.elasticpath.common.dto.assembler.giftcertificate;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.store.StoreService;

/**
 * Assembler for {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateDTO} and {@link com.elasticpath.domain.catalog.GiftCertificate}. 
 */
public class GiftCertificateDtoAssembler extends AbstractDtoAssembler<GiftCertificateDTO, GiftCertificate> {

	private BeanFactory beanFactory;

	private StoreService storeService;

	private CustomerService customerService;

	
	@Override
	public GiftCertificate getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE);
	}

	@Override
	public GiftCertificateDTO getDtoInstance() {
		return new GiftCertificateDTO();
	}

	@Override
	public void assembleDto(final GiftCertificate source, final GiftCertificateDTO target) {
		target.setGuid(source.getGuid());
		target.setCode(source.getGiftCertificateCode());
		target.setCreationDate(source.getCreationDate());
		target.setLastModifiedDate(source.getLastModifiedDate());
		
		target.setRecipientName(source.getRecipientName());
		target.setRecipientEmail(source.getRecipientEmail());
		target.setSenderName(source.getSenderName());
		target.setMessage(source.getMessage());

		target.setTheme(source.getTheme());
		target.setPurchaseAmount(source.getPurchaseAmount());
		target.setStoreCode(source.getStore().getCode());
		target.setCurrencyCode(source.getCurrencyCode());
		target.setPurchaserGuid(getPurchaserGuid(source));
		target.setOrderGuid(source.getOrderGuid());
	}

	@Override
	public void assembleDomain(final GiftCertificateDTO source, final GiftCertificate target) {
		target.setGuid(source.getGuid());
		target.setGiftCertificateCode(source.getCode());
		target.setCreationDate(source.getCreationDate());
		target.setLastModifiedDate(source.getLastModifiedDate());
		target.setRecipientName(source.getRecipientName());
		target.setRecipientEmail(source.getRecipientEmail());
		target.setSenderName(source.getSenderName());
		target.setMessage(source.getMessage());
		target.setTheme(source.getTheme());
		target.setPurchaseAmount(source.getPurchaseAmount());
		target.setStore(findStoreWithCode(source.getStoreCode()));
		target.setCurrencyCode(source.getCurrencyCode());
		target.setPurchaser(findCustomerByGuid(source.getPurchaserGuid()));
		target.setOrderGuid(source.getOrderGuid());
	}
	
	/**
	 * Retrieves purchaser guid from {@link com.elasticpath.domain.catalog.GiftCertificate).
	 * @param source the gift certificate
	 * @return the purchaser guid.
	 */
	protected String getPurchaserGuid(final GiftCertificate source) {
		Customer purchaser = source.getPurchaser();
		
		if (purchaser == null) {
			return null;
		}
		
		return purchaser.getGuid();
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
	 * Returns the {@link ccom.elasticpath.domain.customer.Customere} associated with the given guid. 
	 * @param customerGuid guid for the {@link com.elasticpath.domain.customer.Customer} 
	 * @return the requested {@link com.elasticpath.domain.customer.Customer}
	 */
	protected Customer findCustomerByGuid(final String customerGuid) {
		Customer customer = customerService.findByGuid(customerGuid);
		
		return customer;
	}

	
	/**
	 * @return the beanFactory
	 */
	protected BeanFactory getBeanFactory() {
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
	protected StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @return the customerService
	 */
	protected CustomerService getCustomerService() {
		return customerService;
	}

	/**
	 * @param customerService the customerService to set
	 */
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}
}
