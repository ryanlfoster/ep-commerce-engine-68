package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;

/**
 * Test <code>TopSellerComparatorImpl</code>.
 */
public class TopSellerComparatorImplTest {

	private TopSellerComparatorImpl topSellerComparatorImpl;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		this.topSellerComparatorImpl = new TopSellerComparatorImpl();
	}

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompare() {

		Product product1 = getProduct();
		product1.setSalesCount(1);
		Product product2 = getProduct();
		product2.setSalesCount(2);
		Product product3 = getProduct();
		product3.setSalesCount(1);

		assertTrue(this.topSellerComparatorImpl.compare(product1, product2) < 0);
		assertTrue(this.topSellerComparatorImpl.compare(product2, product1) > 0);
		assertEquals(0, this.topSellerComparatorImpl.compare(product1, product3));

	}

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompareError() {
		try {
			this.topSellerComparatorImpl.compare(null, getProduct());
			fail("ClassCastException expected.");
		} catch (EpSystemException e) {
			// succeed!
			assertNotNull(e);
		}
	}
	
	protected Product getProduct() {
		return new ProductImpl();
	}

}
