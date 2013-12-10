package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.service.pricing.BaseAmountFinder;
import com.elasticpath.service.pricing.PriceAdjustmentService;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.PricedEntityFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSource;
import com.elasticpath.service.pricing.datasource.impl.NoPreprocessBaseAmountDataSourceFactory;

/**
 * Service for looking up prices of products from Price Lists.
 */
public class PriceLookupServiceImpl implements PriceLookupService {

	private BeanFactory beanFactory;

	private PriceAdjustmentService priceAdjustmentService;

	private PricedEntityFactory pricedEntityFactory;

	private BaseAmountFinder baseAmountFinder;
	
	private BaseAmountDataSource baseAmountDataSource;
	
	@Override
	public Map<Product, Price> getProductsPrices(final Collection<Product> products, final PriceListStack plStack) {
		Map<Product, Price> productPrices = new HashMap<Product, Price>(products.size());
		for (Product product : products) {
			Price price = getProductPrice(product, plStack);
			if (price != null) {
				productPrices.put(product, price);
			}
		}
		return productPrices;
	}

	@Override
	public Price getProductPrice(final Product product, final PriceListStack plStack) {
		PriceProvider priceProvider = getPriceProvider(plStack);
		return getPricedEntityFactory().createPricedProduct(product, priceProvider).getPrice();
	}

	@Override
	public Price getSkuPrice(final ProductSku productSku, final PriceListStack plStack) {
		PriceProvider priceProvider = getPriceProvider(plStack);
		return getPricedEntityFactory().createPricedProductSku(productSku, plStack, priceProvider, 
				new NoPreprocessBaseAmountDataSourceFactory()).getPrice();
	}

	@Override
	public String findPriceListWithPriceForProductSku(final ProductSku productSku, final PriceListStack plStack) {
		Collection<BaseAmount> baseAmounts = getBaseAmountFinder().getBaseAmounts(productSku, plStack.getPriceListStack(), getBaseAmountDataSource());

		List<BaseAmount> baseAmountsForGuid = Collections.emptyList();
		for (String plGuid : plStack.getPriceListStack()) {
			baseAmountsForGuid = getBaseAmountFinder().filterBaseAmounts(baseAmounts, plGuid, BaseAmountObjectType.SKU, productSku.getGuid());
			if (baseAmountsForGuid.isEmpty()) {
				baseAmountsForGuid = getBaseAmountFinder().filterBaseAmounts(baseAmounts, plGuid, 
						BaseAmountObjectType.PRODUCT,
						productSku.getProduct().getGuid());
			}
			if (!baseAmountsForGuid.isEmpty()) {
				return plGuid;
			}
		}
		return null;
	}

	@Override
	public Map<String, Price> getSkuPrices(final ProductSku productSku, final PriceListStack plStack) {
		final Map<String, Price> prices = new HashMap<String, Price>();

		for (String plGuid : plStack.getPriceListStack()) {
			PriceListStack singlePlStack = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_STACK);
			singlePlStack.setCurrency(plStack.getCurrency());
			singlePlStack.addPriceList(plGuid);
			Price price = getSkuPrice(productSku, singlePlStack);
			if (price != null) {
				prices.put(plGuid, price);
			}
		}

		return prices;
	}

	@Override
	public Collection<PriceAdjustment> getProductBundlePriceAdjustments(final ProductBundle bundle, final String plGuid) {
		return getProductBundlePriceAdjustmentsMap(bundle, plGuid).values();
	}

	@Override
	public Map<String, PriceAdjustment> getProductBundlePriceAdjustmentsMap(final ProductBundle bundle, final String plGuid) {
		Map<String, PriceAdjustment> adjustmentMap = getPriceAdjustmentService().findByPriceListAndBundleAsMap(plGuid, bundle);

		if (adjustmentMap == null || adjustmentMap.isEmpty()) {
			return Collections.emptyMap();
		}

		final Iterator<Map.Entry<String, PriceAdjustment>> iterator = adjustmentMap.entrySet().iterator();
		Predicate predicate = getUnacceptablePriceAdjustmentPredicate(bundle);

		while (iterator.hasNext()) {
			final Map.Entry<String, PriceAdjustment> entry = iterator.next();
			if (predicate.evaluate(entry.getValue())) {
				iterator.remove();
			}
		}

		return adjustmentMap;
	}

	
	/**
	 * An implementation of {@link Predicate} that evaluates unacceptable price adjustments on a bundle as true.
	 */
	static class UnacceptablePriceAdjustmentPredicate implements Predicate {
		private final boolean isCalculated;
		
		/**
		 * @param isCalculated whether the bundle is a calculated one
		 */
		public UnacceptablePriceAdjustmentPredicate(final boolean isCalculated) {
			this.isCalculated = isCalculated;
		}
		
		@Override
		public boolean evaluate(final Object object) {
			return evaluate((PriceAdjustment) object);
		}
		
		/**
		 * @param adjustment price adjustment to be evaluated
		 * @return <code>true</code> iff the price adjustment is null, zero, or it is a negative adjustment on an assigned bundle, or a positive 
		 * adjustment on a calculated bundle
		 */
		protected boolean evaluate(final PriceAdjustment adjustment) {
			boolean shouldRemove = adjustment == null || adjustment.getAdjustmentAmount() == null;
			if (!shouldRemove) {
				int comparisonResult = adjustment.getAdjustmentAmount().compareTo(BigDecimal.ZERO);
				shouldRemove = comparisonResult == 0 || (comparisonResult < 0) != isCalculated;
			}

			return shouldRemove;
		}
		
		
	}
	
	
	
	/**
	 * @param beanFactory instance of {@link BeanFactory} to set.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return instance of {@link BeanFactory} in use.
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param priceAdjustmentService the priceAdjustmentService to set
	 */
	public void setPriceAdjustmentService(final PriceAdjustmentService priceAdjustmentService) {
		this.priceAdjustmentService = priceAdjustmentService;
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
	 * @return the PriceAdjustmentService insatnce
	 */
	protected PriceAdjustmentService getPriceAdjustmentService() {
		return priceAdjustmentService;
	}

	
	/**
	 * Set the {@link PricedEntityFactory} instance to be used.
	 *
	 * @param pricedEntityFactory the factory to set
	 */
	public void setPricedEntityFactory(final PricedEntityFactory pricedEntityFactory) {
		this.pricedEntityFactory = pricedEntityFactory;
	}

	/**
	 * @return the PricedEntityFactory instance
	 */
	protected PricedEntityFactory getPricedEntityFactory() {
		return pricedEntityFactory;
	}

	/**
	 * @return the BaseAmountFinder instance
	 */
	protected BaseAmountFinder getBaseAmountFinder() {
		return baseAmountFinder;
	}

	
	/**
	 * Create a predicate for unacceptable price adjustments based on the bundle.
	 * @param bundle the bundle from which the price adjustments are extracted
	 * @return a {@link Predicate} that evaluates unacceptable adjustments to <code>true</code>
	 */
	protected Predicate getUnacceptablePriceAdjustmentPredicate(final ProductBundle bundle) {
		return new UnacceptablePriceAdjustmentPredicate(bundle.isCalculated());
	}

	
	/**
	 * Creates a {@link PriceProvider} as a callback to this instance of PriceLookupServiceImpl.
	 *
	 * @param plStack the price list stack to be used as the parameter for the callbacks
	 * @return the {@link PriceProvider}
	 */
	protected PriceProvider getPriceProvider(final PriceListStack plStack) {
		return new PriceProvider() {
			
			@Override
			public Price getProductSkuPrice(final ProductSku productSku) {
				return PriceLookupServiceImpl.this.getSkuPrice(productSku, plStack);
			}
			
			@Override
			public Price getProductPrice(final Product product) {
				return PriceLookupServiceImpl.this.getProductPrice(product, plStack);
			}
			
			@Override
			public Currency getCurrency() {
				return plStack.getCurrency();
			}
		};
	}

	public void setBaseAmountDataSource(final BaseAmountDataSource baseAmountDataSource) {
		this.baseAmountDataSource = baseAmountDataSource;
	}

	protected BaseAmountDataSource getBaseAmountDataSource() {
		return baseAmountDataSource;
	}
	
}
