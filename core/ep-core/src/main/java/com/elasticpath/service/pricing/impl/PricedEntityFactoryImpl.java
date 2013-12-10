package com.elasticpath.service.pricing.impl;

import java.util.Collection;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.pricing.BaseAmountFinder;
import com.elasticpath.service.pricing.PaymentScheduleHelper;
import com.elasticpath.service.pricing.PricePopulator;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.Priced;
import com.elasticpath.service.pricing.PricedEntityFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;

/**
 * A factory for getting Priced objects.
 */
public class PricedEntityFactoryImpl implements PricedEntityFactory {
	private BaseAmountFinder baseAmountFinder;
	private PricePopulator pricePopulator;
	private BeanFactory beanFactory;
	private PaymentScheduleHelper paymentScheduleHelper;
	private BundleIdentifier bundleIdentifier;
	private BaseAmountDataSource defaultDataSource;

	@Override
	public Priced createPricedProductSku(final ProductSku productSku, final PriceListStack plStack, final PriceProvider priceProvider, 
			final BaseAmountDataSourceFactory dataSourceFactory) {
		if (getBundleIdentifier().isCalculatedBundle(productSku)) {
			return createPricedCalculatedBundle(getBundleIdentifier().asProductBundle(productSku.getProduct()), priceProvider);
		}
		
		return createPricedRegularProductSku(productSku, plStack, dataSourceFactory);
	}

	/**
	 * Create a Priced object for a regular (i.e. not the SKU of a CalculatedBundle) SKU.
	 *
	 * @param productSku the SKU
	 * @param plStack the plStack to be used for look up
	 * @param dataSourceFactory the data source to be used to get the required base amounts
	 * @return the Priced object
	 */
	protected Priced createPricedRegularProductSku(final ProductSku productSku, 
			final PriceListStack plStack, final BaseAmountDataSourceFactory dataSourceFactory) {
		BaseAmountDataSource dataSource = dataSourceFactory.createDataSource(getDefaultDataSource());
		Collection<BaseAmount> baseAmounts = getBaseAmountFinder().getBaseAmounts(productSku, plStack.getPriceListStack(), dataSource);
		PricedProductSku priced = new PricedProductSku(productSku, plStack, 
				getPaymentScheduleHelper().getPaymentSchedule(productSku), baseAmounts);
		injectDependencies(priced);
		return priced;
	}

	
	/**
	 * Injects the required dependencies to the AbstractPricedEntity and returns it.
	 * @param <T> the type of the entity
	 * @param priced the priced entity to inject dependencies to
	 */
	protected <T> void injectDependencies(final AbstractPricedEntity<T> priced) {
		priced.setBeanFactory(getBeanFactory());
		priced.setBaseAmountFinder(getBaseAmountFinder());
		priced.setPricePopulator(getPricePopulator());
		priced.setPaymentScheduleHelper(getPaymentScheduleHelper());
		priced.setBundleIdentifier(getBundleIdentifier());
	}
	

	@Override
	public Priced createPricedProduct(final Product product, final PriceProvider priceProvider) {
		if (getBundleIdentifier().isCalculatedBundle(product)) {
			return createPricedCalculatedBundle(getBundleIdentifier().asProductBundle(product), priceProvider);
		}
		
		if (getPaymentScheduleHelper().isPaymentScheduleCapable(product)) {
			return new PricedPaymentScheduleCapableProduct(product, priceProvider);
		} 
		
		PricedProduct priced = new PricedProduct(product, priceProvider);
		injectDependencies(priced);
		return priced;
	}


	/**
	 * Create a priced calculated bundle.
	 * 
	 * @param bundle the bundle to price
	 * @param priceProvider the price provider
	 * @return a priced product
	 */
	public Priced createPricedCalculatedBundle(final ProductBundle bundle, final PriceProvider priceProvider) {
		return new PricedCalculatedBundle(bundle, getBeanFactory(), priceProvider);
	}
	
	
	/**
	 * Set the base amount finder instance.
	 * 
	 * @param baseAmountFinder the base amount finder
	 */
	public void setBaseAmountFinder(final BaseAmountFinder baseAmountFinder) {
		this.baseAmountFinder = baseAmountFinder;
	}

	/**
	 * Set the price populator instance.
	 * 
	 * @param pricePopulator instance of {@link PricePopulator} to use
	 */
	public void setPricePopulator(final PricePopulator pricePopulator) {
		this.pricePopulator = pricePopulator;
	}


	/**
	 * @param beanFactory instance of {@link BeanFactory} to set.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the BaseAmountFinder instance
	 */
	protected BaseAmountFinder getBaseAmountFinder() {
		return baseAmountFinder;
	}

	/**
	 * @return the PricePopulator instance
	 */
	protected PricePopulator getPricePopulator() {
		return pricePopulator;
	}


	/**
	 * @return the BeanFactory instance
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @return the paymentScheduleHelper instance
	 */
	protected PaymentScheduleHelper getPaymentScheduleHelper() {
		return paymentScheduleHelper;
	}

	/**
	 * @param paymentScheduleHelper the paymentScheduleHelper instance
	 */
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


	public void setDefaultDataSource(final BaseAmountDataSource defaultDataSource) {
		this.defaultDataSource = defaultDataSource;
	}


	protected BaseAmountDataSource getDefaultDataSource() {
		return defaultDataSource;
	}


}
