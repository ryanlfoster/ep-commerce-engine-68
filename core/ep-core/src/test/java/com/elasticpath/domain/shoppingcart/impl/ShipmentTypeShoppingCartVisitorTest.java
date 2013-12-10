/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;

/**
 * Test <code>ShipmentTypeShoppingCartVisitor</code>.
 */
public class ShipmentTypeShoppingCartVisitorTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShoppingItem bundleItem;

	private ShoppingItem electronicItem;

	private ShoppingItem physicalItem;

	private ShoppingItem serviceItem;

	private ProductSku productSkuIsNotShippable;

	private ProductSku productSkuIsShippable;

	/**
	 * Setup instance variables for each test. <br>
	 * This ensures the mockery is initialized each time and the tests are independent.
	 *
	 * @throws Exception If an exception occurs.
	 */
	@Before
	public void setUp() throws Exception {
		productSkuIsNotShippable = context.mock(ProductSku.class, "productSkuIsNotShippable");

		productSkuIsShippable = context.mock(ProductSku.class, "productSkuIsShippable");

		electronicItem = new ShoppingItemImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public Price getPrice() {
				return null;
			}

			@Override
			public boolean isBundle() {
				return false;
			}

			@Override
			public ProductSku getProductSku() {
				return productSkuIsNotShippable;
			}
		};

		physicalItem = new ShoppingItemImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public Price getPrice() {
				return null;
			}

			@Override
			public boolean isBundle() {
				return false;
			}

			@Override
			public ProductSku getProductSku() {
				return productSkuIsShippable;
			}
		};

		bundleItem = new ShoppingItemImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle() {
				return true;
			}

			@Override
			public List<ShoppingItem> getBundleItems() {
				return Collections.singletonList(physicalItem);
			}
		};

		serviceItem = new ShoppingItemImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBundle() {
				return false;
			}
		};
	}

	/**
	 * The shopping cart contains... service item
	 */
	@Test
	public void testServiceItem() {
		final ShoppingItemHasRecurringPricePredicate hasRecurringPrice = new ShoppingItemHasRecurringPricePredicate() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean evaluate(final Object object) {
				return true;
			}
		};

		ShoppingCart shoppingCart = new ShoppingCartImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<ShoppingItem> getCartItems() {
				return Arrays.asList(serviceItem);
			}
		};

		ShipmentTypeShoppingCartVisitor visitor = new ShipmentTypeShoppingCartVisitor(hasRecurringPrice);
		shoppingCart.accept(visitor);

		assertEquals(1, visitor.getServiceSkus().size());
		assertTrue(visitor.getServiceSkus().contains(serviceItem));
	}

	/**
	 * The shopping cart contains... electronic item bundle physical item
	 */
	@Test
	public void testElectronicAndBundleWithPhysical() {
		ShoppingCart shoppingCart = new ShoppingCartImpl() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<ShoppingItem> getCartItems() {
				return Arrays.asList(electronicItem, bundleItem);
			}
		};

		context.checking(new Expectations() {
			{
				oneOf(productSkuIsNotShippable).isShippable();
				will(returnValue(false));

				oneOf(productSkuIsShippable).isShippable();
				will(returnValue(true));
			}
		});

		ShipmentTypeShoppingCartVisitor visitor = new ShipmentTypeShoppingCartVisitor(new ShoppingItemHasRecurringPricePredicate());
		shoppingCart.accept(visitor);

		assertEquals(1, visitor.getElectronicSkus().size());
		assertTrue(visitor.getElectronicSkus().contains(electronicItem));

		assertEquals(1, visitor.getPhysicalSkus().size());
		assertTrue(visitor.getPhysicalSkus().contains(physicalItem));
	}
}
