/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test for StoreProductImpl class.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class StoreProductImplTest extends AbstractEPTestCase {

	private static final int PRODUCT_SKU_UID = 1;

	private static final long TIME_UNIT = 20000;

	private static final long ASSOCIATION_UID_2 = 200L;

	private static final long ASSOCIATION_UID_1 = 100L;

	private StoreProductImpl storeProduct;

	private ProductSkuImpl productSku;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		final ProductImpl product = (ProductImpl) getProduct();
		storeProduct = new StoreProductImpl(product);

		productSku = new ProductSkuImpl();
		productSku.setUidPk(PRODUCT_SKU_UID);
		productSku.setGuid("1");

		product.addOrUpdateSku(productSku);
	}

	/**
	 * Test getting and setting the product associations,
	 * and test that the returned set of associations is unmodifiable.
	 */
	@Test
	public void testGetSetProductAssociations() {
		final Set<ProductAssociation> associations = new HashSet<ProductAssociation>();
		storeProduct.setProductAssociations(associations);
		assertEquals(associations, storeProduct.getProductAssociations());
		try {
			storeProduct.getProductAssociations().add(new ProductAssociationImpl());
			fail("The returned product associations should be an unmodifiable set.");
		} catch (final UnsupportedOperationException ex) {
			assertNotNull(ex);
		}
	}

	/**
	 * Tests equals and friends.
	 */
	@Test
	public void testEqualsContainment() {
		// For StoreProducts A, B, and C:
		// A != B
		// B != C
		// A == C

		final ProductImpl productA = (ProductImpl) getProduct();
		StoreProductImpl storeProductA = new StoreProductImpl(productA);

		final ProductImpl productB = (ProductImpl) getProduct();
		StoreProductImpl storeProductB = new StoreProductImpl(productB);

		final ProductImpl productC = (ProductImpl) getProduct();
		productC.setCode(productA.getCode());
		StoreProductImpl storeProductC = new StoreProductImpl(productC);

		// Test equality in a list.
		ArrayList<StoreProductImpl> productList = new ArrayList<StoreProductImpl>();

		productList.add(storeProductA);
		assertTrue(String.format("The list does contain A with code [%s]", storeProductA.getCode()),
				productList.contains(storeProductA));
		assertFalse(String.format("The list does not contain B with code [%s]", storeProductB.getCode()),
				productList.contains(storeProductB));
		assertTrue(String.format("The list does contain C with code [%s] (since C == A)", storeProductC.getCode()),
				productList.contains(storeProductC));

		productList.clear();
		productList.add(storeProductB);
		assertFalse(String.format("The list does not contain A with code [%s]", storeProductA.getCode()),
				productList.contains(storeProductA));
		assertTrue(String.format("The list does contain B with code [%s]", storeProductB.getCode()),
				productList.contains(storeProductB));
		assertFalse(String.format("The list does not contain C with code [%s]", storeProductC.getCode()),
				productList.contains(storeProductC));

		productList.clear();
		productList.add(storeProductA);
		productList.add(storeProductB);
		assertTrue(String.format("The list does contain A with code [%s]", storeProductA.getCode()),
				productList.contains(storeProductA));
		assertTrue(String.format("The list does contain B with code [%s]", storeProductB.getCode()),
				productList.contains(storeProductB));
		assertTrue(String.format("The list does contain C with code [%s] (since C == A)", storeProductC.getCode()),
				productList.contains(storeProductC));

		productList.clear();
		productList.add(storeProductC);
		assertTrue(String.format("The list does contain A with code [%s] (since C == A)", storeProductA.getCode()),
				productList.contains(storeProductA));
		assertFalse(String.format("The list does not contain B with code [%s]", storeProductB.getCode()),
				productList.contains(storeProductB));
		assertTrue(String.format("The list does contain C with code [%s]", storeProductC.getCode()),
				productList.contains(storeProductC));

		productList.clear();
		productList.add(storeProductA);
		productList.add(storeProductC);
		assertTrue(String.format("The list does contain A with code [%s] (since C == A)", storeProductA.getCode()),
				productList.contains(storeProductA));
		assertFalse(String.format("The list does not contain B with code [%s]", storeProductB.getCode()),
				productList.contains(storeProductB));
		assertTrue(String.format("The list does contain C with code [%s]", storeProductC.getCode()),
				productList.contains(storeProductC));

		productList.clear();
		productList.add(storeProductA);
		productList.add(storeProductB);
		productList.add(storeProductC);
		assertTrue(String.format("The list does contain A with code [%s]", storeProductA.getCode()),
				productList.contains(storeProductA));
		assertTrue(String.format("The list does contain B with code [%s]", storeProductB.getCode()),
				productList.contains(storeProductB));
		assertTrue(String.format("The list does contain C with code [%s]", storeProductC.getCode()),
				productList.contains(storeProductC));
	}

	/**
	 * Tests hashCode and friends.
	 */
	@Test
	public void testHashCodeContainment() {
		// For StoreProducts A, B, and C:
		// A != B
		// B != C
		// A == C

		final ProductImpl productA = (ProductImpl) getProduct();
		StoreProductImpl storeProductA = new StoreProductImpl(productA);

		final ProductImpl productB = (ProductImpl) getProduct();
		StoreProductImpl storeProductB = new StoreProductImpl(productB);

		final ProductImpl productC = (ProductImpl) getProduct();
		productC.setCode(productA.getCode());
		StoreProductImpl storeProductC = new StoreProductImpl(productC);

		HashSet<StoreProductImpl> productSet = new HashSet<StoreProductImpl>();

		// Test identity via add/contains:
		productSet.add(storeProductA);
		assertTrue("Failed to add product A to set", productSet.contains(storeProductA));

		// Test idempotence of hashCode:
		productSet.add(storeProductA);
		assertEquals("Unexpected set size after adding A again", 1, productSet.size());

		// Test inequality via A != B:
		productSet.add(storeProductB);
		assertTrue("Failed to add product B to set", productSet.contains(storeProductA));
		assertTrue("Product C does not match product A (same code)", productSet.contains(storeProductC));
		assertEquals("Unexpected set size after adding A and B", 2, productSet.size());

		// Test equality via A == C:
		assertTrue("Failed to match A to C in set (same code)", productSet.contains(storeProductC));
		productSet.add(storeProductC);
		assertTrue("Set should still contain A after adding C", productSet.contains(storeProductA));
		assertTrue("Set should still contain C after adding C", productSet.contains(storeProductC));
		assertEquals("Unexpected set size after adding C", 2, productSet.size());
	}

	/**
	 * Test getting a set of product associations of a given type.
	 */
	@Test
	public void testGetAssociations() {
		final Set<ProductAssociation> associations = new HashSet<ProductAssociation>();

		final ProductAssociation productAssociationUpSell = getProductAssociation();
		productAssociationUpSell.setAssociationType(ProductAssociation.UP_SELL);
		productAssociationUpSell.setTargetProduct(getProduct());
		associations.add(productAssociationUpSell);

		final ProductAssociation productAssociationCrossSell = getProductAssociation();
		productAssociationCrossSell.setAssociationType(ProductAssociation.CROSS_SELL);
		productAssociationCrossSell.setTargetProduct(getProduct());
		associations.add(productAssociationCrossSell);

		storeProduct.setProductAssociations(associations);
		final Set<ProductAssociation> returnedAssociations = storeProduct.getAssociationsByType(ProductAssociation.UP_SELL);
		assertEquals(1, returnedAssociations.size());

		final ProductAssociation returnedAssociation = returnedAssociations.iterator().next();
		assertSame(productAssociationUpSell, returnedAssociation);

		assertEquals(0, storeProduct.getAssociationsByType(ProductAssociation.ACCESSORY).size());
		assertEquals(1, storeProduct.getAssociationsByType(ProductAssociation.CROSS_SELL).size());

		storeProduct.setProductAssociations(Collections.<ProductAssociation>emptySet());
		assertEquals(0, storeProduct.getAssociationsByType(ProductAssociation.UP_SELL).size());
	}

	/**
	 * Test getting a set of product associations of a given type.
	 */
	@Test
	public void testGetAssociationsWithInvalidAssociation() {
		final Set<ProductAssociation> associations = new HashSet<ProductAssociation>();

		final ProductAssociation productAssociation1 = getProductAssociation();
		productAssociation1.setAssociationType(ProductAssociation.UP_SELL);
		productAssociation1.setTargetProduct(getProduct());
		associations.add(productAssociation1);

		final ProductAssociation productAssociation2 = getProductAssociation();
		productAssociation2.setAssociationType(ProductAssociation.UP_SELL);
		productAssociation2.setTargetProduct(getProduct());
		final Date now = new Date(new Date().getTime() - 1);
		final Date beforeNow = new Date(now.getTime() - TIME_UNIT);
		productAssociation2.setStartDate(beforeNow);
		productAssociation2.setEndDate(now);
		associations.add(productAssociation2);

		storeProduct.setProductAssociations(associations);
		final Set<ProductAssociation> returnedAssociations = storeProduct.getAssociationsByType(ProductAssociation.UP_SELL);
		assertEquals(1, returnedAssociations.size());
		assertEquals(2, storeProduct.getProductAssociations().size());
		assertSame(productAssociation1, returnedAssociations.iterator().next());
	}

	/**
	 * Get a <code>ProductAssociation</code> object initialized with valid dates.
	 *
	 * @return a <code>ProductAssociation</code>
	 */
	private ProductAssociation getProductAssociation() {
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setStartDate(new Date());
		productAssociation.setEndDate(new Date(productAssociation.getStartDate().getTime() + TIME_UNIT));
		return productAssociation;
	}

	/**
	 * Test getting an association by its UID.
	 */
	@Test
	public void testGetAssociationById() {
		final Set<ProductAssociation> associations = new HashSet<ProductAssociation>();

		final ProductAssociation productAssociationUpSell = new ProductAssociationImpl();
		productAssociationUpSell.setAssociationType(ProductAssociation.UP_SELL);
		productAssociationUpSell.setTargetProduct(getProduct());
		productAssociationUpSell.setUidPk(ASSOCIATION_UID_1);
		associations.add(productAssociationUpSell);

		final ProductAssociation productAssociationCrossSell = new ProductAssociationImpl();
		productAssociationCrossSell.setAssociationType(ProductAssociation.CROSS_SELL);
		productAssociationCrossSell.setTargetProduct(getProduct());
		productAssociationCrossSell.setUidPk(ASSOCIATION_UID_2);
		associations.add(productAssociationCrossSell);

		storeProduct.setProductAssociations(associations);
		assertSame(productAssociationCrossSell, storeProduct.getAssociationById(ASSOCIATION_UID_2));
		assertSame(productAssociationUpSell, storeProduct.getAssociationById(ASSOCIATION_UID_1));
	}

	/**
	 * @return a new <code>Product</code> instance.
	 */
	protected Product getProduct() {
		final Product product = new ProductImpl();
		product.initialize();
		product.setCode((new RandomGuidImpl()).toString());
		return product;
	}
}
