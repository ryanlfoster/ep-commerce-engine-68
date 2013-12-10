package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;

/**
 * Test <code>DisplayNameComparatorImpl</code>.
 */
public class DisplayNameComparatorImplTest {

	public static final Locale LOCALE = Locale.US;

	private final DisplayNameComparatorImpl displayNameComparatorAsc = new DisplayNameComparatorImpl();

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompare() {
		this.displayNameComparatorAsc.initialize(LOCALE);
		Product product1 = createProductForDisplayNameComparison("product one");
		Product product2 = createProductForDisplayNameComparison("product two");
		Product product3 = createProductForDisplayNameComparison("product one");

		assertTrue(this.displayNameComparatorAsc.compare(product1, product2) < 0);
		assertTrue(this.displayNameComparatorAsc.compare(product2, product1) > 0);
		assertEquals(0, this.displayNameComparatorAsc.compare(product1, product3));

	}
	
	private Product createProductForDisplayNameComparison(final String displayName) {
		Product product = new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;

			@Override
			public String getDisplayName(final Locale locale) {
				return displayName;
			}
		};

		return product;
	}

	/**
	 * Test that an exception is thrown when attempting to compare names without calling initialize.
	 */
	@Test(expected = EpSystemException.class)
	public void testNoInitialization() {
		this.displayNameComparatorAsc.compare(
			createProductForDisplayNameComparison("irrelevant"),
			createProductForDisplayNameComparison("irrelevant"));
	}

	/**
	 * Test an exception is thrown when attempting to compare against null.
	 */
	@Test(expected = ClassCastException.class)
	public void testCompareError() {
		this.displayNameComparatorAsc.initialize(LOCALE);
		this.displayNameComparatorAsc.compare(
			null,
			createProductForDisplayNameComparison("irrelevant"));
	}
}
