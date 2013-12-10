package com.elasticpath.domain.shoppingcart.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalogview.SeoUrlBuilder;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.shoppingcart.ViewHistory;
import com.elasticpath.domain.shoppingcart.ViewHistoryProduct;

/**
 * This class represents a collection of products viewed by a user.
 */
public class ViewHistoryImpl extends AbstractEpDomainImpl implements ViewHistory {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final int MAX_HISTORY_LENGTH = 5;

	private final List<ViewHistoryProduct> viewedProducts = new ArrayList<ViewHistoryProduct>();

	/**
	 * Adds a product to the viewHistory.
	 *
	 * @param product the <code>Product</code> to be added
	 */
	public void addProduct(final Product product) {
		ViewHistoryProduct viewHistoryProduct = createHistoryProduct(product);

		for (ViewHistoryProduct curViewHistoryProduct : viewedProducts) {
			if (curViewHistoryProduct.getUidPk() == product.getUidPk()) {
				viewedProducts.remove(curViewHistoryProduct);
				break;
			}
		}
		viewedProducts.add(viewHistoryProduct);
		if (viewedProducts.size() > MAX_HISTORY_LENGTH) {
			viewedProducts.remove(0);
		}
	}


	/**
	 * Get the most recently viewed product.
	 *
	 * @return a <code>ViewHistoryProduct</code> representing the most recently viewed product
	 */
	public ViewHistoryProduct getLastViewedHistoryProduct() {
		if (viewedProducts.isEmpty()) {
			return null;
		}
		return viewedProducts.get(viewedProducts.size() - 1);
	}

	/**
	 * Creates a <code>ViewHistoryProduct</code> from a <code>Product</code>.
	 *
	 * @param product the product to create a <code>ViewHistoryProduct</code> for.
	 * @return the populated <code>ViewHistoryProduct</code>
	 */
	protected ViewHistoryProduct createHistoryProduct(final Product product) {

		ViewHistoryProduct viewHistoryProduct = getBean("viewHistoryProduct");

		StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory = getBean(ContextIdNames.STORE_SEO_URL_BUILDER_FACTORY);
		SeoUrlBuilder seoUrlBuilder = storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder();

		viewHistoryProduct.loadProductInfo(product, seoUrlBuilder);
		return viewHistoryProduct;
	}

	/**
	 * Get a list of the most recently viewed products in the view history.
	 *
	 * @return a <code>List</code> of <code>ViewHistoryProduct</code>s
	 */
	public List<ViewHistoryProduct> getViewedProducts() {
		return viewedProducts;
	}

}
