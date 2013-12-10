/**
 * 
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Date;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.GiftCertificateCodeGenerator;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.shoppingcart.GiftCertificateFactory;

/**
 * Creates GiftCertificates. 
 * This implementation uses the GiftCertificateCodeGenerator to generate unique codes.
 */
public class GiftCertificateFactoryImpl implements GiftCertificateFactory {

	private BeanFactory beanFactory;
	
	private GiftCertificateService giftCertificateService;
	
	private GiftCertificateCodeGenerator giftCertificateCodeGenerator;
	
	@Override
	public GiftCertificate createGiftCertificate(final ShoppingItem shoppingItem, final Customer customer, final Store store) {
		GiftCertificate giftCertificate = getBeanFactory().getBean(ContextIdNames.GIFT_CERTIFICATE);
		giftCertificate.setGiftCertificateCode(generateUniqueGiftCertificateCode(store.getUidPk()));
		giftCertificate.setPurchaser(customer);
		giftCertificate.setCreationDate(new Date());
		giftCertificate.setCurrencyCode(shoppingItem.getTotal().getCurrency().getCurrencyCode());
		giftCertificate.setPurchaseAmount(shoppingItem.getTotal().getAmount());
		giftCertificate.setRecipientEmail(shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_EMAIL));
		giftCertificate.setRecipientName(shoppingItem.getFieldValue(GiftCertificate.KEY_RECIPIENT_NAME));
		giftCertificate.setSenderName(shoppingItem.getFieldValue(GiftCertificate.KEY_SENDER_NAME));
		giftCertificate.setStore(store);
		giftCertificate.setTheme(shoppingItem.getProductSku().getSkuCode()); //FIXME: UPDATE when themes are SKUs
		giftCertificate.setMessage(shoppingItem.getFieldValue(GiftCertificate.KEY_MESSAGE));
		
		return giftCertificate;
	}

	/**
	 * Generates a gift certificate code that is guaranteed to be unique within the {@code Store} having
	 * the given UIDPK.
	 * @param storeUidPk the store's UID
	 * @return the unique code
	 */
	String generateUniqueGiftCertificateCode(final long storeUidPk) {
		String giftCertificateCode = generateCode();
		while (getGiftCertificateService().isGiftCertificateCodeExist(giftCertificateCode, storeUidPk)) {
			giftCertificateCode = generateCode();
		}
		return giftCertificateCode;
	}
	
	private String generateCode() {
		return getGiftCertificateCodeGenerator().generateCode();
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * @param giftCertificateCodeGenerator the giftCertificateCodeGenerator to set
	 */
	public void setGiftCertificateCodeGenerator(final GiftCertificateCodeGenerator giftCertificateCodeGenerator) {
		this.giftCertificateCodeGenerator = giftCertificateCodeGenerator;
	}

	/**
	 * @return the giftCertificateCodeGenerator
	 */
	public GiftCertificateCodeGenerator getGiftCertificateCodeGenerator() {
		return giftCertificateCodeGenerator;
	}

	/**
	 * @param giftCertificateService the giftCertificateService to set
	 */
	public void setGiftCertificateService(final GiftCertificateService giftCertificateService) {
		this.giftCertificateService = giftCertificateService;
	}

	/**
	 * @return the giftCertificateService
	 */
	public GiftCertificateService getGiftCertificateService() {
		return giftCertificateService;
	}
}
