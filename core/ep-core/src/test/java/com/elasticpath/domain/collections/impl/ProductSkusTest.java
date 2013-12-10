/**
 * 
 */
package com.elasticpath.domain.collections.impl;

import static com.elasticpath.domain.collections.predicates.impl.GuidMatcher.byGuids;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;

/**
 * Contains tests for ProductSkus collection.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class ProductSkusTest {
	private static final String SKU_GUID = "123";
	private static final String SKU_GUID2 = "223";
	private ProductSku productSku;
	private ProductSku productSku2;
	private ProductImpl product;
	
	/**
	 * Sets up a product with 2 skus to be used by all tests.
	 */
	@Before
	public void setUp() {
		productSku = new ProductSkuImpl();
		productSku.setGuid(SKU_GUID);
		productSku.setSkuCode("124");

		productSku2 = new ProductSkuImpl();
		productSku2.setGuid(SKU_GUID2);
		productSku2.setSkuCode("224");
		
		product = new ProductImpl();
		product.addOrUpdateSku(productSku);
	}
	
	/**
	 * Tests that getSkuByGuid() returns the sku in the product's ProductSkus collection that
	 * has the given guid for a product with two skus.
	 */
	@Test
	public void testGetSkuByGuid() {
		assertThat(ProductSkus.skusFor(product).byGuid(SKU_GUID), is(sameInstance(productSku)));
	}

	/**
	 * Tests that ContainsSkuWithGuid() returns true for a product's ProductSkus collection that
	 * has two skus, one of which has the guid passed in.
	 */
	@Test
	public void testContainsSkuWithGuid() {
		assertTrue(ProductSkus.skusFor(product).contains(SKU_GUID));
	}
	
	/**
	 * Tests that filter() returns a collection of product skus with only
	 * the sku with a guid matching the one setup in the guid selector. 
	 */
	@Test
	public void testFilterBySkuGuids() {
		AbstractDomainCollection<ProductSku> filteredProductSkus = ProductSkus.skusFor(product).filter(byGuids(SKU_GUID));
		assertTrue(filteredProductSkus.contains(SKU_GUID));
		assertFalse(filteredProductSkus.contains(SKU_GUID2));
	}

}
