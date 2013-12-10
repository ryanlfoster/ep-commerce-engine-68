package com.elasticpath.test.persister;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.GiftCertificateService;

/**
 * Persister allows to create and save into database store dependent domain objects.
 */
@SuppressWarnings({ "PMD.ExcessiveParameterList" })
public class GiftCertificateTestPersister {

	private final BeanFactory beanFactory;
	private final GiftCertificateService certificateService;

	/**
	 * Constructor initializes necessary services and beanFactory.
	 * 
	 * @param beanFactory the ElasticPath bean factory
	 */
	public GiftCertificateTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.certificateService = beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE_SERVICE);
	}

	/**
	 * Creates persisted gift certificate with a new store having specified store code, specified GC guid/code, 
	 * purchase amount in specified <code>currency</code>, specified recipient/sender name, and theme.
	 * 
	 * @param store the store 
	 * @param gcGuid the GC's guid
	 * @param gcCode the GC's code
	 * @param recipientName Name of recipient of the GC
	 * @param senderName Name of sender of the GC
	 * @param theme GC theme
	 * @param purchaseAmount Total purchase amount of the gift certificate
	 * @param storeCode guid of the store
	 * @param currencyCode ISO currency code
	 * @param purchaser the purchaser
	 * @return persisted gift certificate
	 */
	public GiftCertificate persistGiftCertificate(final Store store, final String gcGuid, final String gcCode,
			final String currencyCode, final BigDecimal purchaseAmount, final String recipientName, final String senderName,
			final String theme, final Customer purchaser) {

		return persistGiftCertificate(
				store, gcGuid,  gcCode, currencyCode, purchaseAmount, recipientName, 
				senderName,theme, purchaser, null, new Date(), new Date(), null, null);
	}
	
	/**
	 * Creates persisted gift certificate with all specified fields in parameters.
	 * 
	 * The method serves as the base method for persisting a gift certificate.
	 * 
	 * @param store the store 
	 * @param gcGuid the GC's guid
	 * @param gcCode the GC's code
	 * @param recipientName Name of recipient of the GC
	 * @param senderName Name of sender of the GC
	 * @param theme GC theme
	 * @param purchaseAmount Total purchase amount of the gift certificate
	 * @param storeCode guid of the store
	 * @param currencyCode ISO currency code
	 * @param purchaser the purchaser (customer)
	 * @param orderGuid the order's guid
	 * @param creationDate the creation date
	 * @param lastModifiedDate the last modified date
	 * @param message the message
	 * @param recipientEmail the recipient email
	 * @return persisted gift certificate
	 */
	public GiftCertificate persistGiftCertificate(
			final Store store, final String gcGuid, final String gcCode, final String currencyCode, 
			final BigDecimal purchaseAmount, final String recipientName, final String senderName,
			final String theme, final Customer purchaser, final String orderGuid, final Date creationDate, 
			final Date lastModifiedDate, final String message, final String recipientEmail) {
		
		final GiftCertificate giftCertificate = beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE);
		
		if (store != null) {
			giftCertificate.setStore(store);
		}
		if (gcGuid != null) {
			giftCertificate.setGuid(gcGuid);			
		}
		giftCertificate.setGiftCertificateCode(gcCode);
		giftCertificate.setCurrencyCode(currencyCode);
		if (purchaseAmount != null) {
			giftCertificate.setPurchaseAmount(purchaseAmount);
		}
		if (recipientName != null) {
			giftCertificate.setRecipientName(recipientName);
		}
		if (senderName != null) {
			giftCertificate.setSenderName(senderName);
		}
		giftCertificate.setTheme(theme);
		if (purchaser != null) {
			giftCertificate.setPurchaser(purchaser);
		}
		giftCertificate.setOrderGuid(orderGuid);
		if (creationDate != null) {
			giftCertificate.setCreationDate(creationDate);
		}
		if (lastModifiedDate != null) {
			giftCertificate.setLastModifiedDate(lastModifiedDate);
		}
		giftCertificate.setMessage(message);
		giftCertificate.setRecipientEmail(recipientEmail);
		return certificateService.add(giftCertificate);
	}
}
