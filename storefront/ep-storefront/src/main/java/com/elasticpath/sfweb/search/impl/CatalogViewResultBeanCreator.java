package com.elasticpath.sfweb.search.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.catalog.ProductCharacteristicsService;
import com.elasticpath.service.catalogview.PaginationService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.util.PriceFinderForCart;
import com.elasticpath.sfweb.viewbean.CatalogViewResultBean;

/**
 * Creates CatalogViewResultBean from input.
 */
public class CatalogViewResultBeanCreator {

	private PaginationService paginationService;

	private PriceFinderForCart priceFinderForCart;
	
	private ProductCharacteristicsService productCharacteristicsService;

	private BeanFactory beanFactory;

	/**
	 * Creates a catalog view result bean with pagination, history and navigation for the catalog view result bean.
	 * @param pageNumber is the page number
	 * @param browsingResult is the browsing result
	 * @param catalogResultHistory the catalog result view history
	 * @param shopper shopper
	 * @param storeConfig The store configuration.
	 * @return a catalog view result bean
	 */
	public CatalogViewResultBean createCatalogViewResultBean(final int pageNumber,
			                                                      final CatalogViewResult browsingResult,
			                                                      final CatalogViewResultHistory catalogResultHistory,
			                                                      final Shopper shopper,
			                                                      final StoreConfig storeConfig) {
		final CatalogViewResultBean catalogViewResultBean = getBeanFactory().getBean("catalogViewResultBean");

		int lastPageNumber = getPaginationService().getLastPageNumber(browsingResult.getResultsCount(),
				storeConfig.getStoreCode());

		catalogViewResultBean.setCatalogViewResultHistory(catalogResultHistory);
		catalogViewResultBean.setCurrentCatalogViewResult(browsingResult);
		catalogViewResultBean.setCurrentPageNumber(pageNumber);
		catalogViewResultBean.setTotalPageNumber(lastPageNumber);
		catalogViewResultBean.addPrices(getPriceFinderForCart().findPrices(getProductsNotNull(browsingResult), shopper));
		catalogViewResultBean.addPrices(getPriceFinderForCart().findPrices(browsingResult.getFeaturedProducts(), shopper));
		catalogViewResultBean.addProductCharacteristics(
				getProductCharacteristicsService().getProductCharacteristicsMap(getProductsNotNull(browsingResult)));
		catalogViewResultBean.addProductCharacteristics(
				getProductCharacteristicsService().getProductCharacteristicsMap(browsingResult.getFeaturedProducts()));

		return catalogViewResultBean;
	}

	/**
	 * Gets the Guid and Price map from the {@link CatalogViewResult}.
	 *
	 * @param catalogViewResult {@link CatalogViewResult}
	 * @return product collection - never null
	 */
	protected Collection<StoreProduct> getProductsNotNull(final CatalogViewResult catalogViewResult) {
		Set<StoreProduct> products = new HashSet<StoreProduct>();
		if (catalogViewResult.getProducts() != null) {
			products.addAll(catalogViewResult.getProducts());
		}
		return products;
	}


	/**
	 * @param paginationService service
	 */
	public void setPaginationService(final PaginationService paginationService) {
		this.paginationService = paginationService;
	}

	/**
	 * @return pagination service
	 */
	protected PaginationService getPaginationService() {
		return paginationService;
	}

	/**
	 * @param priceFinderForCart the priceFinderForCart to set
	 */
	public void setPriceFinderForCart(final PriceFinderForCart priceFinderForCart) {
		this.priceFinderForCart = priceFinderForCart;
	}

	/**
	 * @return the priceFinderForCart
	 */
	protected PriceFinderForCart getPriceFinderForCart() {
		return priceFinderForCart;
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
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public ProductCharacteristicsService getProductCharacteristicsService() {
		return productCharacteristicsService;
	}

	public void setProductCharacteristicsService(final ProductCharacteristicsService productCharacteristicsService) {
		this.productCharacteristicsService = productCharacteristicsService;
	}
}