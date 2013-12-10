package com.elasticpath.sfweb.viewbean.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.sfweb.viewbean.CatalogViewResultBean;

/**
 * Represents a bean implementation for catalog view view.
 */
public class CatalogViewResultBeanImpl extends EpViewBeanImpl implements CatalogViewResultBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private CatalogViewResultHistory catalogViewResultHistory;

	private CatalogViewResult catalogViewResult;

	private int currentPageNumber;

	private int totalPageNumber;
	
	private Map <String, Price> prices;
	
	private Map<String, ProductCharacteristics> characteristics;
	
	/**
	 * Sets the catalog view result history.
	 * 
	 * @param catalogViewResults the catalog view result history to set
	 */
	public void setCatalogViewResultHistory(final CatalogViewResultHistory catalogViewResults) {
		this.catalogViewResultHistory = catalogViewResults;
	}

	/**
	 * Returns the catalog view result history.
	 * 
	 * @return the catalog view result history
	 */
	public CatalogViewResultHistory getCatalogViewResultHistory() {
		return this.catalogViewResultHistory;
	}

	/**
	 * Sets the current catalog view result.
	 * 
	 * @param catalogViewResult the current catalog view result
	 */
	public void setCurrentCatalogViewResult(final CatalogViewResult catalogViewResult) {
		this.catalogViewResult = catalogViewResult;
	}

	/**
	 * Returns the current catalog view result.
	 * 
	 * @return the current catalog view result
	 */
	public CatalogViewResult getCurrentCatalogViewResult() {
		return this.catalogViewResult;
	}

	/**
	 * Sets the page number.
	 * 
	 * @param pageNumber the page number
	 */
	public void setCurrentPageNumber(final int pageNumber) {
		this.currentPageNumber = pageNumber;
	}

	/**
	 * Returns the page number.
	 * 
	 * @return the page number
	 */
	public int getCurrentPageNumber() {
		
		if (this.currentPageNumber > getTotalPageNumber()) {
			this.currentPageNumber = getTotalPageNumber();
		}
		return this.currentPageNumber;
	}

	/**
	 * Returns the total page number.
	 * 
	 * @return the total page number
	 */
	public int getTotalPageNumber() {
		return totalPageNumber;
	}
	
	/**
	 * Sets the total page number.
	 * 
	 * @param total the total page number
	 */
	public void setTotalPageNumber(final int total) {
		this.totalPageNumber = total;
	}
	
	/**
	 * Returns the total number of products.
	 *
	 * @return the total number of products
	 */
	public int getTotalNumberProducts() {
		return catalogViewResult.getResultsCount();
	}


	/**
	 * Returns products at the page of the given page number.
	 * 
	 * @return products at the page of the given page number
	 */
	public List<StoreProduct> getProducts() {
		return catalogViewResult.getProducts();
	}

	/**
	 * Returns the current search result.
	 * 
	 * @return the current search result
	 */
	public SearchResult getCurrentSearchResult() {
		return (SearchResult) getCurrentCatalogViewResult();
	}

	@Override
	public void setPrices(final Map <String, Price> prices) {
		this.prices = prices;
	}

	@Override
	public Price getPrice(final String productCode) {
		return prices.get(productCode);
	}

	@Override
	public void addPrices(final Map<String, Price> productPrices) {
		if (prices == null) {
			prices = new HashMap<String, Price>();
		}
		prices.putAll(productPrices);
	}

	@Override
	public ProductCharacteristics getProductCharacteristics(final String productCode) {
		return characteristics.get(productCode);
	}

	@Override
	public void addProductCharacteristics(final Map<String, ProductCharacteristics> productCharacteristics) {
		if (characteristics == null) {
			characteristics = new HashMap<String, ProductCharacteristics>();
		}
		characteristics.putAll(productCharacteristics);
	}
}
