package com.elasticpath.service.catalog.impl;


import java.util.Collection;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductCharacteristicsService;

/**
 * Caching implementation of the {@link ProductCharacteristicsService} that caches the results of the delegate
 * service's calls (at least the ones that are costly).
 */
public class CachingProductCharacteristicsServiceImpl implements ProductCharacteristicsService {

	private Ehcache cache;
	private ProductCharacteristicsService delegateService;

	@Override
	public ProductCharacteristics getProductCharacteristics(final Product product) {
		return getDelegateService().getProductCharacteristics(product);
	}

	@Override
	public ProductCharacteristics getProductCharacteristics(final ProductSku productSku) {
		return getDelegateService().getProductCharacteristics(productSku);
	}

	@Override
	public ProductCharacteristics getProductCharacteristicsForSkuCode(final String skuCode) {
		Element element = getCache().get(skuCode);
		if (element == null || element.isExpired()) {
			element = new Element(skuCode, getDelegateService().getProductCharacteristicsForSkuCode(skuCode));
			getCache().put(element);
		}
		return (ProductCharacteristics) element.getValue();
	}

	@Override
	public Map<String, ProductCharacteristics> getProductCharacteristicsMap(final Collection<? extends Product> products) {
		return getDelegateService().getProductCharacteristicsMap(products);
	}
	
	protected Ehcache getCache() {
		return cache;
	}

	public void setCache(final Ehcache cache) {
		this.cache = cache;
	}

	protected ProductCharacteristicsService getDelegateService() {
		return delegateService;
	}

	public void setDelegateService(final ProductCharacteristicsService delegateService) {
		this.delegateService = delegateService;
	}

}
