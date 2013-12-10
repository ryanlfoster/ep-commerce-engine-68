package com.elasticpath.test.persister;

import java.math.BigDecimal;
import java.util.Collection;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.domain.pricing.impl.PriceListDescriptorImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException;

/**
 * Persists price lists.
 */
public class PriceListPersister {

	private final BeanFactory beanFactory;
	private final BaseAmountService baseAmountService;
	private final PriceListDescriptorService priceListDescriptorService;
	
	/**
	 * 
	 * @param beanFactory
	 */
	public PriceListPersister(final BeanFactory beanFactory) {
		super();
		this.beanFactory = beanFactory;
		this.baseAmountService = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_SERVICE);
		this.priceListDescriptorService = beanFactory.getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR_SERVICE);
	}
	
	/**
	 * 
	 * @param priceListDescriptorGuid
	 * @param objectType
	 * @param objectGuid
	 * @param minQty
	 * @param listPrice
	 * @param salePrice
	 */
	public void addOrUpdateBaseAmount(final String priceListDescriptorGuid,
			final String objectType, final String objectGuid, final BigDecimal minQty,
			final BigDecimal listPrice, final BigDecimal salePrice) {
		BaseAmount oldBaseAmount = findBaseAmount(objectGuid, objectType, minQty, priceListDescriptorGuid);
		if (oldBaseAmount == null) {
			addBaseAmount(objectGuid, objectType, minQty, listPrice, salePrice, priceListDescriptorGuid, null);
		} else {
			updateBaseAmount(oldBaseAmount, listPrice, salePrice, null);
		}
	}
	
	/**
	 * 
	 * @param priceListDescriptorGuid
	 * @param objectType
	 * @param objectGuid
	 * @param minQty
	 * @param listPrice
	 * @param salePrice
	 * @param paymentScheduleName
	 */
	public void addOrUpdateBaseAmount(final String priceListDescriptorGuid,
			final String objectType, final String objectGuid, final BigDecimal minQty,
			final BigDecimal listPrice, final BigDecimal salePrice, final String paymentScheduleName) {
		BaseAmount oldBaseAmount = findBaseAmount(objectGuid, objectType, minQty, priceListDescriptorGuid);
		if (oldBaseAmount == null) {
			addBaseAmount(objectGuid, objectType, minQty, listPrice, salePrice, priceListDescriptorGuid, paymentScheduleName);
		} else {
			updateBaseAmount(oldBaseAmount, listPrice, salePrice, paymentScheduleName);
		}
	}

	/**
	 * 
	 * @param guid
	 * @param priceListDescriptorGuid
	 * @param objectType
	 * @param objectGuid
	 * @param minQty
	 * @param listPrice
	 * @param salePrice
	 */
	public void addOrUpdateBaseAmount(final String guid, final String priceListDescriptorGuid,
			final String objectType, final String objectGuid, final BigDecimal minQty,
			final BigDecimal listPrice, final BigDecimal salePrice) {
		BaseAmount oldBaseAmount = baseAmountService.findByGuid(guid);
		if (oldBaseAmount == null) {
			addBaseAmount(guid, objectGuid, objectType, minQty, listPrice, salePrice, priceListDescriptorGuid, null);
		} else {
			updateBaseAmount(oldBaseAmount, listPrice, salePrice, null);
		}
	}	
	

	
	private BaseAmount addBaseAmount(final String guid, final String objectGuid, final String objectType, final BigDecimal minQty, 
			final BigDecimal listPrice,	final BigDecimal salePrice, final String priceListDescriptorGuid, final String paymenScheduleName) {
		BaseAmountImpl newBaseAmount = new BaseAmountImpl();
		newBaseAmount.setObjectGuid(objectGuid);
		newBaseAmount.setObjectType(objectType);
		newBaseAmount.setListValue(listPrice);
		newBaseAmount.setSaleValue(salePrice);
		newBaseAmount.setQuantity(minQty);
		newBaseAmount.setPriceListDescriptorGuid(priceListDescriptorGuid);
		newBaseAmount.setGuid(guid);

		return baseAmountService.add(newBaseAmount);
	}
	
	private BaseAmount addBaseAmount(final String objectGuid, final String objectType, final BigDecimal minQty, final BigDecimal listPrice,
			final BigDecimal salePrice, final String priceListDescriptorGuid, final String paymenScheduleName) {
		return addBaseAmount(new RandomGuidImpl().toString(), objectGuid, objectType, minQty, listPrice, salePrice, priceListDescriptorGuid, 
				paymenScheduleName);
	}

	private BaseAmount findBaseAmount(final String objectGuid, final String objectType, final BigDecimal quantity,
			final String priceListDescriptorGuid) {
		BaseAmountFilter filter = beanFactory.getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter.setObjectGuid(objectGuid);
		filter.setObjectType(objectType);
		filter.setQuantity(quantity);
		filter.setPriceListDescriptorGuid(priceListDescriptorGuid);
		Collection<BaseAmount> persistentBaseAmounts = baseAmountService.findBaseAmounts(filter);

		if (persistentBaseAmounts.isEmpty()) {
			return null;
		}

		return persistentBaseAmounts.iterator().next();
	}

	private void updateBaseAmount(final BaseAmount baseAmount, final BigDecimal listPrice, final BigDecimal salePrice, 
			final String paymenScheduleCode) {
		baseAmount.setListValue(listPrice);
		baseAmount.setSaleValue(salePrice);

		try {
			baseAmountService.updateWithoutLoad(baseAmount);
		} catch (BaseAmountNotExistException e) {
			throw new EpServiceException("Failed to update " + baseAmount, e);
		}
	}
	
	/**
	 * Creates a price list and adds it to the system with using following parameters.
	 * @param code The price list GUID
	 * @param name The price list name
	 * @param currencyCode The currency code
	 * @param description The description
	 * @param isHidden Whether this price list is hidden or not
	 * 
	 * @return PriceListDescriptor the descriptor that was created
	 */
	public PriceListDescriptor createAndPersistPriceList(final String code, final String name, final String currencyCode,
			final String description, final boolean isHidden) {
		PriceListDescriptor priceListDescriptor = new PriceListDescriptorImpl();
		
		priceListDescriptor.setGuid(code);
		priceListDescriptor.setName(name);
		priceListDescriptor.setCurrencyCode(currencyCode);
		priceListDescriptor.setDescription(description);
		priceListDescriptor.setHidden(isHidden);

		return priceListDescriptorService.add(priceListDescriptor);
		
	}

	/**
	 * Updates the price list with the guid identified by priceListGuidToUpdate.
	 * @param priceListGuidToUpdate the guid to update
	 * @param name the name
	 * @param currencyCode the currency code to update
	 * @param description the description to update
	 */
	public void updatePriceList(final String priceListGuidToUpdate, final String name,
			final String currencyCode, final String description) {
		PriceListDescriptor priceListDescriptor = priceListDescriptorService
		.findByGuid(priceListGuidToUpdate);

		priceListDescriptor.setName(name);
		priceListDescriptor.setCurrencyCode(currencyCode);
		priceListDescriptor.setDescription(description);
		
		priceListDescriptorService.update(priceListDescriptor);
	}
}
