package com.elasticpath.domain.shoppingcart;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.collections.Predicate;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;

/**
 * Tests that the Shopping Item Predicate Utils class works as expected.
 */
public class ShoppingItemPredicateUtilsTest {

	/**
	 * Test configurable items not equal.
	 */
	@Test
	public void testConfigurableItemsNotEqual() {
		ShoppingItem item1 = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isConfigurable() {
				return true;
			}
		};

		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item1);
		assertFalse("Configurable items should never be equal", matchingShoppingItemPredicate.evaluate(item1));
	}

	/**
	 * Test random object not equal.
	 */
	@Test
	public void testRandomObjectNotEqual() {
		ShoppingItem item = createShoppingItem("SKU");
		ProductSku sku = new ProductSkuImpl();
		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item);
		assertFalse("Another type of object should not match a shopping item", matchingShoppingItemPredicate.evaluate(sku));
	}
	
	/**
	 * Test same object is equal.
	 */
	@Test
	public void testSameObjectIsEqual() {
		ShoppingItem item = createShoppingItem("SKU");
		
		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item);
		assertTrue("A shopping item should match itself", matchingShoppingItemPredicate.evaluate(item));
	}
	
	/**
	 * Test different shopping items not equal.
	 */
	@Test
	public void testDifferentShoppingItemsNotEqual() {
		ShoppingItem item1 = createShoppingItem("SKU-1");
		ShoppingItem item2 = createShoppingItem("SKU-2");
		Predicate matchingShoppingItemPredicate = ShoppingItemPredicateUtils.matchingShoppingItemPredicate(item1);
		assertFalse("Shopping items with different skus should not match", matchingShoppingItemPredicate.evaluate(item2));
	}

	private ShoppingItem createShoppingItem(final String skuCode) {
		ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(skuCode);
		ShoppingItem item = new ShoppingItemImpl() {
			private static final long serialVersionUID = 1L;
			
			@Override
			public boolean isConfigurable() {
				return false;
			};
		};
		item.setProductSku(productSku);
		return item;
	}
	
}
