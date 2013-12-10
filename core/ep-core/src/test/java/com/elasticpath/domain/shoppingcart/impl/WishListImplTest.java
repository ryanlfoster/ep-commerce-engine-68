package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * The junit class for wishListImpl.
 */
public class WishListImplTest {
	
	private static final String SKU1 = "sku1";
	private WishListImpl wishList;
	
	/**
	 * The setup method.
	 */
	@Before
	public void setUp() {
		wishList  = new WishListImpl() {
			private static final long serialVersionUID = -4858143846698274756L;

			@Override
			public ShoppingItem addItem(final ShoppingItem item) {
				ShoppingItem shoppingItem = new ShoppingItemImpl();
				shoppingItem.setProductSku(item.getProductSku());
				getAllItems().add(shoppingItem);
				return item;
			}
		};
	}
	
	/**
	 * Test is not contained method.
	 */
	@Test
	public void testIsNotContained() {
		ShoppingItem item = new ShoppingItemImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode(SKU1);
		sku1.setGuid(SKU1);
		item.setProductSku(sku1);
		wishList.addItem(item);
		assertFalse("should return false for same sku instance", wishList.isNotContained(item));
		
		ShoppingItem item2 = new ShoppingItemImpl();
		ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("sku2");
		sku2.setGuid("sku2");
		item2.setProductSku(sku2);
		assertTrue("should not reture false for different sku.", wishList.isNotContained(item2));
		
		ShoppingItem item3 = new ShoppingItemImpl();
		ProductSku sku3 = new ProductSkuImpl();
		sku3.setSkuCode(SKU1);
		sku3.setGuid(SKU1);
		item3.setProductSku(sku3);
		
		assertFalse("should reture false for equivalent sku.", wishList.isNotContained(item3));
		
	}

}
