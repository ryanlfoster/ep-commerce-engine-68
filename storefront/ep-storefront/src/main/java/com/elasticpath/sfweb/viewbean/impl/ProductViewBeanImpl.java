package com.elasticpath.sfweb.viewbean.impl;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.sfweb.viewbean.ProductViewBean;

/**
 * Represents a bean for catalog view.
 */
public class ProductViewBeanImpl extends EpViewBeanImpl implements ProductViewBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private Product product;

	private CatalogViewResult catalogViewResult;

	private String updatePage;

	private long updateCartItemUid;
	
	private int updateCartItemQty;

	private Category parentCategory;

	/**
	 * Set the product to be displayed.
	 * 
	 * @param product the product
	 */
	public void setProduct(final Product product) {
		this.product = product;
	}

	/**
	 * Get the product to be displayed.
	 * 
	 * @return the product
	 */
	public Product getProduct() {
		return this.product;
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
	 * Returns <code>true</code> if the product view page is reached from search.
	 * 
	 * @return <code>true</code> if the product view page is reached from search.
	 */
	public boolean isFromSearch() {
		return this.catalogViewResult instanceof SearchResult;
	}

	/**
	 * Returns <code>true</code> if the product view page is reached from browsing.
	 * 
	 * @return <code>true</code> if the product view page is reached from browsing.
	 */
	public boolean isFromBrowsing() {
		return this.catalogViewResult instanceof BrowsingResult;
	}

	/**
	 * Returns true if the product is being displayed for updating rather than the initial add to cart.
	 * 
	 * @return see above.
	 */
	public boolean isUpdate() {
		return updatePage != null && !"".equals(updatePage);
	}

	/**
	 * Returns the page that is being updated if <code>isUpdate</code> is true.
	 * 
	 * @return a constant String corresponding to the page being updated.
	 */
	public String getUpdatePage() {
		return updatePage;
	}

	/**
	 * Set the page that is being updated. If a product is being updated, this will indicate which page the user should return to upon updating.
	 * 
	 * @param updatePage the page being updated, use a constant in <code>WebConstants</code>
	 */
	public void setUpdatePage(final String updatePage) {
		this.updatePage = updatePage;
	}

	/**
	 * Get the UID of the cart item being updated.
	 * 
	 * @return the UID of the cart item if a cart item is being updated.
	 */
	public long getUpdateCartItemUid() {
		return this.updateCartItemUid;
	}

	/**
	 * Set the UID of the cart item to be updated.
	 * 
	 * @param updateCartItemUid the cart item UID
	 */
	public void setUpdateCartItemUid(final long updateCartItemUid) {
		this.updateCartItemUid = updateCartItemUid;
	}
	
	/**
	 * Get the Qty of the cart item being updated.
	 * @return the Qty of the cart item if a cart item is being updated.
	 */
	public int getUpdateCartItemQty() {
		return this.updateCartItemQty;
	}
	
	/**
	 * Set the Qty of the cart item to be updated.
	 * @param updateCartItemQty the cart item Qty
	 */
	public void setUpdateCartItemQty(final int updateCartItemQty) {
		this.updateCartItemQty = updateCartItemQty;
	}

	/**
	 * Gets the product's parent folder with fetched fields which by default are omitted (like parent folder).  
	 * 
	 * @return the product's parent category
	 */
	public Category getProductCategory() {
		return parentCategory;
	}
	
	/**
	 * Sets the parent product category.
	 * 
	 * @param category the parent {@link Category}
	 */
	public void setProductCategory(final Category category) {
		this.parentCategory = category;
	}

}
