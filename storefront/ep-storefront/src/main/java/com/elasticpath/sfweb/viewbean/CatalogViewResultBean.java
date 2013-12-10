package com.elasticpath.sfweb.viewbean;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductCharacteristics;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.search.SearchResult;

/**
 * Represents a bean for catalog view.
 */
public interface CatalogViewResultBean extends EpViewBean {

	/**
	 * Sets the catalog view result history.
	 * 
	 * @param catalogViewResults the catalog view result history to set
	 */
	void setCatalogViewResultHistory(CatalogViewResultHistory catalogViewResults);

	/**
	 * Returns the catalog view result history.
	 * 
	 * @return the catalog view result history
	 */
	CatalogViewResultHistory getCatalogViewResultHistory();

	/**
	 * Sets the current catalog view result.
	 * 
	 * @param catalogViewResult the current catalog view result
	 */
	void setCurrentCatalogViewResult(CatalogViewResult catalogViewResult);

	/**
	 * Returns the current catalog view result.
	 * 
	 * @return the current catalog view result
	 */
	CatalogViewResult getCurrentCatalogViewResult();

	/**
	 * Returns the current search result.
	 * 
	 * @return the current search result
	 */
	SearchResult getCurrentSearchResult();

	/**
	 * Sets the current page number.
	 * 
	 * @param pageNumber the current page number
	 */
	void setCurrentPageNumber(int pageNumber);

	/**
	 * Returns the current page number.
	 * 
	 * @return the current page number
	 */
	int getCurrentPageNumber();

	/**
	 * Returns the total page number.
	 * 
	 * @return the total page number
	 */
	int getTotalPageNumber();
	
	/**
	 * Returns the total number of products.
	 *
	 * @return the total number of products
	 */
	int getTotalNumberProducts();

	/**
	 * Sets the total number of pages.
	 *
	 * @param total sets the total number of pages
	 */
	void setTotalPageNumber(final int total);
	
	/**
	 * Returns products at the page of the given page number.
	 * 
	 * @return products at the page of the given page number
	 */
	List<StoreProduct> getProducts();
	
	/**
	 * Retrieve the lowest prices corresponding to the given product code.
	 * 
	 * @param productCode code of the product
	 * @return product price
	 */
	Price getPrice(String productCode);

	/**
	 * @param prices list of prices
	 */
	void setPrices(final Map <String, Price> prices);

	/**
	 * @param productPrices the prices to add to the price lookup data 
	 */
	void addPrices(Map<String, Price> productPrices);
	
	/**
	 * Gets the product characteristics for the product with the given product code.
	 *
	 * @param productCode the product code
	 * @return the product characteristics
	 */
	ProductCharacteristics getProductCharacteristics(String productCode);
	
	/**
	 * Adds the product characteristics.
	 *
	 * @param productCharacteristics the product characteristics
	 */
	void addProductCharacteristics(Map<String, ProductCharacteristics> productCharacteristics);
}
