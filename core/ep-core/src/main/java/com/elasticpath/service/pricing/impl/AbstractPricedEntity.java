package com.elasticpath.service.pricing.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.pricing.BaseAmountFinder;
import com.elasticpath.service.pricing.PaymentScheduleHelper;
import com.elasticpath.service.pricing.PricePopulator;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.Priced;

/**
 * Parent adapter that assembles prices for different catalog items.
 * @param <T> the entity type, e.g. Product or ProductSku
 */
public abstract class AbstractPricedEntity<T> implements Priced {
	private final T entity;
	private BeanFactory beanFactory;
	private PricePopulator pricePopulator;
	private BaseAmountFinder baseAmountFinder;
	private PaymentScheduleHelper paymentScheduleHelper;
	private BundleIdentifier bundleIdentifier;
	private PriceProvider priceProvider;

	
	/**
	 * Constructor that takes the object being priced, the price list stack, and the collection
	 * of base amounts with price schedules.
	 * 
	 * @param entity the object being priced
	 * @param priceProvider the price provider callback to the client
	 */
	public AbstractPricedEntity(final T entity, final PriceProvider priceProvider) {
		this.entity = entity;
		this.priceProvider = priceProvider;
	}
			
	
	/**
	 * Get the price.
	 * 
	 * @return the price
	 */
	public abstract Price getPrice();

	/**
	 * Get the price schedule associated with the given payment schedule.
	 * 
	 * @param paymentSchedule the payment schedule (or null if none)
	 * @return the price schedule
	 */
	protected PriceSchedule getPriceSchedule(final PaymentSchedule paymentSchedule) {
		PriceSchedule priceSchedule = beanFactory.getBean(ContextIdNames.PRICE_SCHEDULE);
		
		if (paymentSchedule == null) {
			priceSchedule.setType(PriceScheduleType.PURCHASE_TIME);
		} else {
			priceSchedule.setType(PriceScheduleType.RECURRING);
			priceSchedule.setPaymentSchedule(paymentSchedule);
		}
		
		return priceSchedule;
	}		

	
	
	/**
	 * Get the object being priced.
	 * 
	 * @return the priced object
	 */
	protected T getEntity() {
		return entity;
	}

	
	/**
	 * @return an empty instance of {@link Price}
	 */
	protected Price getPriceBean() {
		return beanFactory.getBean(ContextIdNames.PRICE);
	}
	

	/**
	 * Set the {@link BeanFactory} instance.
	 *
	 * @param beanFactory the bean factory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * @return the bean factory
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	/**
	 * Set the {@link BaseAmountFinder} instance to be used.
	 *
	 * @param baseAmountFinder the instance to set
	 */
	public void setBaseAmountFinder(final BaseAmountFinder baseAmountFinder) {
		this.baseAmountFinder = baseAmountFinder;
	}
	
	/**
	 * @return the base amount finder instance
	 */
	protected BaseAmountFinder getBaseAmountFinder() {
		return baseAmountFinder;
	}
	
	
	/**
	 * Set the {@link PricePopulator} instance to be used.
	 *
	 * @param pricePopulator the instance to set
	 */
	public void setPricePopulator(final PricePopulator pricePopulator) {
		this.pricePopulator = pricePopulator;
	}
	
	/**
	 * @return the price populator instance
	 */
	protected PricePopulator getPricePopulator() {
		return pricePopulator;
	}


	/**
	 * @return the paymentScheduleHelper instance
	 */
	protected PaymentScheduleHelper getPaymentScheduleHelper() {
		return paymentScheduleHelper;
	}

	public void setPaymentScheduleHelper(final PaymentScheduleHelper paymentScheduleHelper) {
		this.paymentScheduleHelper = paymentScheduleHelper;
	}

	
	/**
	 * @return the BundleIdentifier instance
	 */
	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}
	
	/**
	 * Set the {@link BundleIdentifier} instance.
	 * @param bundleIdentifier the bundleIdentifier instance to set
	 */
	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	/**
	 * Set the price provider.
	 * @param priceProvider the price provider
	 */
	public void setPriceProvider(final PriceProvider priceProvider) {
		this.priceProvider = priceProvider;
	}

	/**
	 * Get the price provider.
	 * @return the price provider
	 */
	protected PriceProvider getPriceProvider() {
		return priceProvider;
	}


}
