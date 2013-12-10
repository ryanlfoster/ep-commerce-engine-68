/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.viewbean;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.CatalogViewResult;

/**
 * Represents a bean for product view.
 */
public interface ProductViewBean extends EpViewBean {
	
	/**
	 * Set the product to be displayed.
	 * 
	 * @param product the product
	 */
	void setProduct(Product product);

	/**
	 * Get the product to be displayed.
	 * 
	 * @return the product
	 */
	Product getProduct();

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
	 * Returns <code>true</code> if the product view page is reached from search.
	 * @return <code>true</code> if the product view page is reached from search.
	 */
	boolean isFromSearch();

	/**
	 * Returns <code>true</code> if the product view page is reached from browsing.
	 * @return <code>true</code> if the product view page is reached from browsing.
	 */	
	boolean isFromBrowsing();
	
	/**
	 * Returns true if the product is being displayed for updating rather than the 
	 * initial add to cart.
	 * @return see above.
	 */
	boolean isUpdate();
	
	/**
	 * Returns the page that is being updated if <code>isUpdate</code> is true.
	 * @return a constant String corresponding to the page being updated.
	 */
	String getUpdatePage();
	
	/**
	 * Set the page that is being updated. If a product is being updated, this will indicate
	 * which page the user should return to upon updating.
	 * @param updatePage the page being updated, use a constant in <code>WebConstants</code>
	 */
	void setUpdatePage(final String updatePage);
	
	/**
	 * Get the UID of the cart item being updated.
	 * @return the UID of the cart item if a cart item is being updated.
	 */
	long getUpdateCartItemUid();
	
	/**
	 * Set the UID of the cart item to be updated.
	 * @param updateCartItemUid the cart item UID
	 */
	void setUpdateCartItemUid(final long updateCartItemUid);
	
	/**
	 * Get the Qty of the cart item being updated.
	 * @return the Qty of the cart item if a cart item is being updated.
	 */
	int getUpdateCartItemQty();
	
	/**
	 * Set the Qty of the cart item to be updated.
	 * @param updateCartItemQty the cart item Qty
	 */
	void setUpdateCartItemQty(final int updateCartItemQty);
	
	/**
	 * Gets the product parent category for the catalog linked to 
	 * the store where this product is being viewed.
	 *
	 * @return the {@link Category}
	 */
	Category getProductCategory();
	
	/**
	 * Sets the product parent category for the catalog linked to 
	 * the store where this product is being viewed.
	 *
	 * @param category the parent {@link Category}
	 */
	void setProductCategory(Category category);

}
