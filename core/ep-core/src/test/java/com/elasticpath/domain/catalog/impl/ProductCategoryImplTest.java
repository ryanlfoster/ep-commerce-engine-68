/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.domain.misc.RandomGuid;

/**
 * Test <code>CategoryImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ProductCategoryImplTest {
	
	private static final String GUID_1 = "GUID-1";
	private static final String GUID_2 = "GUID-2";
	
	private ProductCategoryImpl productCategoryImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private Catalog catalog;
	
	private ElasticPath elasticPath;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		final String randomGuid = "RANDOMGUID";
		this.productCategoryImpl = new ProductCategoryImpl();
		this.catalog = context.mock(Catalog.class);
		this.elasticPath = context.mock(ElasticPath.class);
		context.checking(new Expectations() { {
			allowing(elasticPath).getBean(ContextIdNames.RANDOM_GUID); will(returnValue(new RandomGuid() {
				private static final long serialVersionUID = -5717947651551792329L;

				@Override
				public String toString() {
					return randomGuid;
				}
			}));
		}
		});
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductCategoryImpl.setCategory(Category)'.
	 */
	@Test
	public void testSetCategory() {
		final Category categoryImpl = getCategory();
		this.productCategoryImpl.setCategory(categoryImpl);
		assertSame(categoryImpl, this.productCategoryImpl.getCategory());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductCategoryImpl.hashCode()'.
	 */
	@Test
	public void testHashCode() {
		//One transient instance
		ProductCategory pc1 = new ProductCategoryImpl();
		assertEquals("Consecutive hashCode calls should be consistent", pc1.hashCode(), pc1.hashCode());
		assertNotSame("It is very unlikely that hashCode should be zero", 0, pc1.hashCode());
		//Two transient instances
		ProductCategory pc2 = new ProductCategoryImpl();
		
		//Two persistent instances
		final long uidpk5000 = 5000;
		pc1.setUidPk(uidpk5000);
		pc2.setUidPk(uidpk5000);
		assertEquals("hashCodes of equal persistent objects are the same", pc1.hashCode(), pc2.hashCode());
		
		//Hashcode is determined from category and product guids, so make it different on one of the PCs
		CategoryImpl category = new CategoryImpl();
		category.setGuid("CatGuid");
		pc2.setCategory(category);
		assertNotSame("Ideal hashcodes give different values for non-equal objects", pc1.hashCode(), pc2.hashCode());
		
		final Category categoryImpl = getCategory();
		final Product productImpl = getProduct();
		this.productCategoryImpl.setCategory(categoryImpl);
		this.productCategoryImpl.setProduct(productImpl);

		final ProductCategory productCategoryImpl1 = new ProductCategoryImpl();

		productCategoryImpl1.setCategory(categoryImpl);
		productCategoryImpl1.setProduct(productImpl);
		assertEquals("The two objects should be equal", productCategoryImpl, productCategoryImpl1);
		assertEquals("hashcodes must be the same for objects that are equal", productCategoryImpl.hashCode(), productCategoryImpl1.hashCode());
		
		categoryImpl.setGuid(null);
		assertNotNull("hashcode should not throw an exception if the category is transient", productCategoryImpl.hashCode());
		
		productImpl.setGuid(null);
		assertNotNull("hashcode should not throw an exception if the product is transient", productCategoryImpl.hashCode());
		
		// different parent categories
		pc1 = getTestProductCategory(GUID_1, GUID_1);
		pc2 = getTestProductCategory(null, null);
		assertNotSame("Different hashcodes expected if different parent categories.", pc1.hashCode(), pc2.hashCode());
		
		// different parent products
		pc1 = getTestProductCategory(GUID_1, GUID_1);
		pc2 = getTestProductCategory(GUID_1, GUID_1);
		assertNotSame("Different hashcodes expected if different parent products.", pc1.hashCode(), pc2.hashCode());
		
		// different parent products and categories
		pc1 = getTestProductCategory(GUID_1, GUID_1);
		pc2 = getTestProductCategory(GUID_2, GUID_2);
		assertNotSame("Different hashcodes expected if different parent products and categories.", pc1.hashCode(), pc2.hashCode());
		
		// same parent products and categories
		pc1 = getTestProductCategory(GUID_1, GUID_1);
		pc2 = getTestProductCategory(GUID_1, GUID_1);
		assertEquals("Same hashcodes expected if same parent products and categories.", pc1.hashCode(), pc2.hashCode());
		
		// Featured product order does not affect hashCode
		pc1 = getTestProductCategory(GUID_1, GUID_1);
		pc2 = getTestProductCategory(GUID_1, GUID_1);
		pc1.setFeaturedProductOrder(1);
		pc2.setFeaturedProductOrder(2);
		assertEquals("Different featured product order numbers should not affect hashcode.", pc1.hashCode(), pc2.hashCode());
		
		// Different UIDPK should not give different hashcode
		pc1 = getTestProductCategory(GUID_1, GUID_1);
		pc2 = getTestProductCategory(GUID_1, GUID_1);
		pc1.setUidPk(1);
		pc2.setUidPk(2);
		assertEquals("UIDPK does not affect hashCode.", pc1.hashCode(), pc2.hashCode());
		
	}
	
	/**
	 * Creates a test ProductCategory object.
	 * 
	 * @param productGuid The GUID to assign to the product.
	 * @param categoryGuid The GUID to assign to the category.
	 * @return The new ProductCategory.
	 */
	private ProductCategory getTestProductCategory(final String productGuid, final String categoryGuid) {
		ProductCategory productCategory = new ProductCategoryImpl();
		
		if (productGuid != null) {
			productCategory.setProduct(getTestProduct(productGuid));
		}
		
		if (categoryGuid != null) {
			productCategory.setCategory(getTestCategory(categoryGuid));
		}
		
		return productCategory;
	}
	
	/**
	 * Returns a new product with the given GUID.
	 * 
	 * @param guid The GUID to use.
	 * @return The newly created product object.
	 */
	private Product getTestProduct(final String guid) {
		Product product = getProduct();
		product.setGuid(guid);
		
		return product;
	}
	
	/**
	 * Returns a new category with the given GUID.
	 * 
	 * @param guid The GUID to use.
	 * @return The newly created category object.
	 */
	private Category getTestCategory(final String guid) {
		Category category = getCategory();
		category.setGuid(guid);
		
		return category;
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductCategoryImpl.equals(ProductCategory)'.
	 */
	@Test
	public void testEquals() {
		final Category categoryImpl = getCategory();
		final Product productImpl = getProduct();
		this.productCategoryImpl.setCategory(categoryImpl);
		this.productCategoryImpl.setProduct(productImpl);

		final ProductCategory productCategoryImpl1 = new ProductCategoryImpl();
		assertFalse("A should not equal B", productCategoryImpl.equals(productCategoryImpl1));
		assertFalse("B should not equal A", productCategoryImpl1.equals(productCategoryImpl));

		productCategoryImpl1.setCategory(categoryImpl);
		productCategoryImpl1.setProduct(productImpl);
		assertEquals("Two ProductCategories with the same product and the same category are considered equal.", 
				productCategoryImpl, productCategoryImpl1);
		assertEquals("Symmetric test", productCategoryImpl1, productCategoryImpl);
		
		final ProductCategory productCategoryImpl2 = productCategoryImpl;
		assertEquals("Same objects should be equal", productCategoryImpl, productCategoryImpl2);
		
		productCategoryImpl1.setCategory(null);
		assertFalse("ProductCategory with null category will not equal one with category", productCategoryImpl.equals(productCategoryImpl1));
		
		//Test with linked category
		final ProductCategory linkedPC = new ProductCategoryImpl();
		final Category linkedCategory = new LinkedCategoryImpl();
		linkedCategory.setMasterCategory(categoryImpl);
		linkedPC.setCategory(linkedCategory);
		linkedPC.setProduct(productImpl);
		assertFalse("ProductCategory in master category is different to one in a linked category", productCategoryImpl.equals(linkedPC));

		productCategoryImpl.setUidPk(1);
		productImpl.setUidPk(1);
		assertFalse("Different object types are not equal", productCategoryImpl.equals(productImpl));
		
		// Test with different UIDPK
		productCategoryImpl1.setUidPk(2);
		assertFalse("Persistent objects with different UIDPK should not be equal", productCategoryImpl.equals(productCategoryImpl1));
		
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.ProductCategoryImpl.compareTo(object)'.
	 */
	@Test
	public void testCompareTo() {
		final long categoryUid = 12345L;
		final Category categoryImpl = getCategory();
		categoryImpl.setUidPk(categoryUid);
		this.productCategoryImpl.setCategory(categoryImpl);
		final int order1 = 1;

		this.productCategoryImpl.setFeaturedProductOrder(order1);

		final ProductCategory productCategoryImpl1 = new ProductCategoryImpl();
		productCategoryImpl1.setCategory(categoryImpl);
		final int order2 = 2;
		productCategoryImpl1.setFeaturedProductOrder(order2);

		final ProductCategory productCategoryImpl2 = new ProductCategoryImpl();
		productCategoryImpl2.setCategory(categoryImpl);
		final int order3 = 3;
		productCategoryImpl2.setFeaturedProductOrder(order3);

		assertTrue(productCategoryImpl.compareTo(productCategoryImpl1) < 0);
		assertTrue(productCategoryImpl2.compareTo(productCategoryImpl1) > 0);
	}

	private Category getCategory() {
		final Category category = new CategoryImpl() {
			private static final long serialVersionUID = -4708024085108093352L;

			@Override
			public ElasticPath getElasticPath() {
				return elasticPath;
			}
		};
		category.initialize();
		category.setCatalog(catalog);
		return category;
	}

	private Product getProduct() {
		final Product product = new ProductImpl();
		product.initialize();
		return product;
	}

}
