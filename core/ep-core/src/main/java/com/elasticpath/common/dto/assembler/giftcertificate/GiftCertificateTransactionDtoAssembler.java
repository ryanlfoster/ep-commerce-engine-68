package com.elasticpath.common.dto.assembler.giftcertificate;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.payment.GiftCertificateTransaction;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.GiftCertificateService;
/**
 * Assembler for {@link com.elasticpath.common.dto.giftcertificate.GiftCertificateTransactionDTO} and 
 * {@link com.elasticpath.domain.payment.GiftCertificateTransaction}. 
 */
public class GiftCertificateTransactionDtoAssembler extends AbstractDtoAssembler<GiftCertificateTransactionDTO, GiftCertificateTransaction> {

	private BeanFactory beanFactory;

	private GiftCertificateService giftCertificateService;
	
	@Override
	public GiftCertificateTransaction getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE_TRANSACTION);
	}

	@Override
	public GiftCertificateTransactionDTO getDtoInstance() {
		return new GiftCertificateTransactionDTO();
	}

	@Override
	public void assembleDto(final GiftCertificateTransaction source, final GiftCertificateTransactionDTO target) {
		GiftCertificate giftCertificateFromSource = source.getGiftCertificate();
		target.setGiftCertificateGuid(giftCertificateFromSource.getGuid());
		target.setCreationDate(source.getCreatedDate());
		target.setAmount(source.getAmount());
		target.setAuthorizationCode(source.getAuthorizationCode());
		target.setTransactionType(source.getTransactionType());
	}

	@Override
	public void assembleDomain(final GiftCertificateTransactionDTO source, final GiftCertificateTransaction target) {
		String giftCertificateGuid = source.getGiftCertificateGuid();
		GiftCertificate giftCertificateFromSource = giftCertificateService.findByGuid(giftCertificateGuid);
		
		if (giftCertificateFromSource == null) {
			throw new EpServiceException("GiftCertificate with guid " + giftCertificateGuid + " not found.");
		}
	
		target.setGiftCertificate(giftCertificateFromSource);
		target.setCreatedDate(source.getCreationDate());
		target.setAuthorizationCode(source.getAuthorizationCode());
		target.setAmount(source.getAmount());
		target.setAuthorizationCode(source.getAuthorizationCode());
		target.setTransactionType(source.getTransactionType());		
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
	 * @return the giftCertificateService
	 */
	public GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}

	/**
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

}
