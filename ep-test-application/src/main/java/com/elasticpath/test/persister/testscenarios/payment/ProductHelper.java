package com.elasticpath.test.persister.testscenarios.payment;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Helper class which acts as a factory to produce Shopping Cart Items and Order SKUs.
 */
public class ProductHelper {
	private ElasticPath elasticPath;

	private Product product;

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(final Product product) {
		this.product = product;
	}

	/**
	 * @return the elasticPath
	 */
	public ElasticPath getElasticPath() {
		return elasticPath;
	}

	/**
	 * @param elasticPath the elasticPath to set
	 */
	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}

	/**
	 * Create a list of ShoppingCartItem containing precisely 1 ShoppingCartItem item with <code>quantity</code> skus.
	 * 
	 * @param quantity quantity of skus in ShoppingCartItem
	 * @return list of ShoppingCartItem
	 */
	public List<ShoppingItem> getShoppingCartItem(final int quantity) {
		List<ShoppingItem> list = new ArrayList<ShoppingItem>(1);
		ShoppingItem shoppingCartItem = elasticPath.getBean(ContextIdNames.SHOPPING_ITEM);
		shoppingCartItem.setProductSku(product.getDefaultSku());
		shoppingCartItem.setPrice(quantity, null);
		list.add(shoppingCartItem);
		return list;
	}

	/**
	 * Create a list of OrderSku containing 1 OrderSku item with <code>quantity</code> skus.
	 * 
	 * @param quantity quantity of skus in OrderSku
	 * @return list of OrderSku
	 */
	public List<OrderSku> getOrderSku(final int quantity, final OrderHandler orderHandler) {
		List<OrderSku> list = new ArrayList<OrderSku>(1);
		OrderSku orderSku = elasticPath.getBean(ContextIdNames.ORDER_SKU);
		orderSku.copyFrom(orderHandler.getOrderShipment(1).getShipmentOrderSkus().iterator().next());
		orderSku.setPrice(quantity, null);
		list.add(orderSku);
		return list;
	}
}
