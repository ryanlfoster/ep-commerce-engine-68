package com.elasticpath.common.pricing.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.common.pricing.service.PromotedPriceLookupService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.pricing.PriceLookupService;
import com.elasticpath.service.pricing.PriceProvider;
import com.elasticpath.service.pricing.PricedEntityFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactoryBuilder;
import com.elasticpath.service.pricing.datasource.impl.NoPreprocessBaseAmountDataSourceFactory;
import com.elasticpath.service.rules.EpRuleEngine;

/**
 * This class looks up the promoted price for products/SKUs.
 */
public class PromotedPriceLookupServiceImpl implements PromotedPriceLookupService {

	private PriceLookupService priceLookupService;
	private PricedEntityFactory pricedEntityFactory;
	private BundleIdentifier bundleIdentifier;
	private EpRuleEngine epRuleEngine;
	private BeanFactory beanFactory;
	private static final Logger LOG = Logger.getLogger(PromotedPriceLookupServiceImpl.class);

	@Override
	public Map<String, Price> getSkuPrices(final ProductSku sku, final PriceListStack plStack, final Store store) {
		if (getBundleIdentifier().isCalculatedBundle(sku)) {
			LOG.error("Operation is not supported on calculated bundles.");
			return Collections.emptyMap();
		}
		Map<String, Price> prices = priceLookupService.getSkuPrices(sku, plStack);

		for (Price price : prices.values()) {
			applyCatalogPromotions(sku.getProduct(), store, price.getCurrency(), price, new HashSet<Long>());
		}
		return prices;
	}

	@Override
	public Price getSkuPrice(final ProductSku sku, final PriceListStack plStack, final Store store, final Set<Long> ruleTracker) {
		return getSkuPrice(sku, plStack, store, ruleTracker, new NoPreprocessBaseAmountDataSourceFactory());
	}

	private Price getSkuPrice(final ProductSku sku, final PriceListStack plStack, final Store store,
			final Set<Long> ruleTracker, final BaseAmountDataSourceFactory dataSourceFactory) {
		PriceProvider priceProvider = getPriceProvider(store, plStack, ruleTracker, dataSourceFactory);
		Price priceForSku = getPricedEntityFactory().createPricedProductSku(sku, plStack, priceProvider, dataSourceFactory).getPrice();
		if (priceForSku != null) {
			applyCatalogPromotions(sku.getProduct(), store, priceForSku.getCurrency(), priceForSku, ruleTracker);
		}
		return priceForSku;
	}

	@Override
	public Price getProductPrice(final Product product, final PriceListStack plStack, final Store store, final Set<Long> ruleTracker) {
		return getProductPrice(product, plStack, store, ruleTracker, new NoPreprocessBaseAmountDataSourceFactory());
	}

	@Override
	public Price getProductPrice(final Product product, final PriceListStack plStack, final Store store,
			final Set<Long> ruleTracker,  final BaseAmountDataSourceFactory dataSourceFactory) {
		PriceProvider priceProvider = getPriceProvider(store, plStack, ruleTracker, dataSourceFactory);
		return getPricedEntityFactory().createPricedProduct(product, priceProvider).getPrice();
	}

	private PriceProvider getPriceProvider(final Store store, final PriceListStack plStack,
			final Set<Long> ruleTracker, final BaseAmountDataSourceFactory dataSourceFactory) {
		return new PriceProvider() {

			public Price getProductSkuPrice(final ProductSku productSku) {
				return PromotedPriceLookupServiceImpl.this.getSkuPrice(productSku, plStack, store, ruleTracker, dataSourceFactory);
			}

			public Price getProductPrice(final Product product) {
				return PromotedPriceLookupServiceImpl.this.getProductPrice(product, plStack, store, ruleTracker, dataSourceFactory);
			}

			public Currency getCurrency() {
				return plStack.getCurrency();
			}
		};
	}

	@Override
	public Map<String, Price> getProductsPrices(final Collection<Product> products,
			final PriceListStack plStack, final Store store, final Set<Long> ruleTracker) {
		final Map<String, Price> productCodePrice = new HashMap<String, Price>(products.size());
		BaseAmountDataSourceFactoryBuilder builder = getDataSourceFactoryBuilder();
		BaseAmountDataSourceFactory dataSourceFactory = builder.priceListStack(plStack).products(products).build();
		for (Product product : products) {
			Price price = getProductPrice(product, plStack, store, ruleTracker, dataSourceFactory);
			productCodePrice.put(product.getCode(), price);
		}
		return productCodePrice;
	}

	/**
	 * @param product product
	 * @param store store
	 * @param currency currency
	 * @param priceForSku price for sku
	 * @param ruleTracker tracking rules applied
	 */
	public void applyCatalogPromotions(final Product product, final Store store, final Currency currency,
			final Price priceForSku, final Set <Long> ruleTracker) {
		if (!getBundleIdentifier().isCalculatedBundle(product)) {
			getEpRuleEngine().fireCatalogPromotionRules(Arrays.asList(product), ruleTracker, currency, store,
					Collections.singletonMap(product.getCode(), Arrays.asList(priceForSku)));
		}
	}



	protected BaseAmountDataSourceFactoryBuilder getDataSourceFactoryBuilder() {
		return getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_DATA_SOURCE_FACTORY_BUILDER);
	}


	public void setPriceLookupService(final PriceLookupService priceLookupService) {
		this.priceLookupService = priceLookupService;
	}

	protected PriceLookupService getPriceLookupService() {
		return priceLookupService;
	}

	public void setPricedEntityFactory(final PricedEntityFactory pricedEntityFactory) {
		this.pricedEntityFactory = pricedEntityFactory;
	}

	protected PricedEntityFactory getPricedEntityFactory() {
		return pricedEntityFactory;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	protected BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setEpRuleEngine(final EpRuleEngine epRuleEngine) {
		this.epRuleEngine = epRuleEngine;
	}

	protected EpRuleEngine getEpRuleEngine() {
		return epRuleEngine;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

}
