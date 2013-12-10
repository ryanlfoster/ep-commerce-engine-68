package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.constants.CatalogViewConstants;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.util.Utility;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;

/**
 * Test <code>PriceFilterImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class PriceFilterImplTest {

	private static final String STRING_100 = "100";

	private static final String LESSTHAN_USD_49_99 = "lessthan-USD-49.99";

	private static final String MORETHAN_USD_9999_99 = "morethan-USD-9999.99";

	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";

	private static final String BETWEEN_USD_50_AND_100 = "between-USD-50-and-100";

	private static final String EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED = "EpBindExceptionSearchRequest expected.";

	private PriceFilterImpl priceFilter;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		this.priceFilter = getPriceFilter();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.getId()'.
	 */
	@Test
	public void testGetId() {
		assertNull(priceFilter.getId());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayName() {
		final String testId = CatalogViewConstants.PRICE_FILTER_PREFIX + BETWEEN_USD_50_AND_100;
		this.priceFilter.initialize(testId);
		assertEquals("$50 - 100", this.priceFilter.getDisplayName(Locale.US));

		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + LESSTHAN_USD_49_99);
		assertEquals("< $49.99", this.priceFilter.getDisplayName(Locale.US));

		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + MORETHAN_USD_9999_99);
		assertEquals("> $9999.99", this.priceFilter.getDisplayName(Locale.US));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayNameWithoutInitializetion() {
		try {
			this.priceFilter.getDisplayName(Locale.US);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (final EpDomainException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.initialize()'.
	 */
	@Test
	public void testInitialize() {
		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + BETWEEN_USD_50_AND_100);
		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-49.99-and-99.99");
		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + LESSTHAN_USD_49_99);
		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + MORETHAN_USD_9999_99);
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.initialize()'.
	 */
	@Test
	public void testInitializeWithBadId() {
		try {
			this.priceFilter.initialize("bad price filter id");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-50-and-100");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-50");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "lessthan-USD-aade");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "bad-USD-aade");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-150-and-100");
			fail(EP_BIND_EXCEPTION_SEARCH_REQUEST_EXPECTED);
		} catch (final EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.getParent()'.
	 */
	@Test
	public void testGetParent() {
		assertNull(priceFilter.getParent());
		assertEquals(0, priceFilter.getChildren().size());

		PriceFilterImpl childPriceFilter = getPriceFilter();
		childPriceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-49.99-and-99.99");
		this.priceFilter.addChild(childPriceFilter);
		assertTrue(priceFilter.getChildren().contains(childPriceFilter));
		assertSame(priceFilter, childPriceFilter.getParent());
		assertSame(priceFilter, childPriceFilter.getParent());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.Contains(PriceFilter)'.
	 */
	@Test
	public void testContains() {
		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + BETWEEN_USD_50_AND_100);

		PriceFilterImpl priceFilter2 = getPriceFilter();

		priceFilter2.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-69.99-and-69.99");
		assertTrue(this.priceFilter.contains(priceFilter2));

		priceFilter2.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-100-and-200");
		assertFalse(this.priceFilter.contains(priceFilter2));

		priceFilter2.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "lessthan-USD-50");
		assertFalse(this.priceFilter.contains(priceFilter2));

		priceFilter2.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + "between-USD-51-and-101");
		assertFalse(this.priceFilter.contains(priceFilter2));
	}

	private PriceFilterImpl getPriceFilter() {
		return new PriceFilterImpl() {
			private static final long serialVersionUID = -6848250214863949732L;

			@Override
			public Utility getUtility() {
				return new UtilityImpl();
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.equals()'.
	 */
	@Test
	public void testEquals() {
		final String testId = CatalogViewConstants.PRICE_FILTER_PREFIX + BETWEEN_USD_50_AND_100;
		this.priceFilter.initialize(testId);

		final PriceFilterImpl anotherPriceFilter = getPriceFilter();
		anotherPriceFilter.initialize(testId);

		assertEquals(priceFilter, anotherPriceFilter);
		assertEquals(priceFilter.hashCode(), anotherPriceFilter.hashCode());
		assertFalse(priceFilter.equals(new Object()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PriceFilterImpl.getSeoId()'.
	 */
	@Test
	public void testGetSeoId() {
		String testId = CatalogViewConstants.PRICE_FILTER_PREFIX + BETWEEN_USD_50_AND_100;
		this.priceFilter.initialize(testId);
		final String currencyCode = "USD";
		assertEquals(SeoConstants.PRICE_FILTER_PREFIX + currencyCode + SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN 
				+ "50" + SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN + STRING_100, priceFilter.getSeoId());
		assertEquals("between-usd-50-and-100", priceFilter.getSeoName(Locale.US));

		testId = CatalogViewConstants.PRICE_FILTER_PREFIX + LESSTHAN_USD_49_99;
		this.priceFilter.initialize(testId);
		assertEquals(SeoConstants.PRICE_FILTER_PREFIX + currencyCode + SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN 
				+ SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN + "49.99", priceFilter.getSeoId());
		assertEquals("lessthan-usd-49.99", priceFilter.getSeoName(Locale.US));

		testId = CatalogViewConstants.PRICE_FILTER_PREFIX + MORETHAN_USD_9999_99;
		this.priceFilter.initialize(CatalogViewConstants.PRICE_FILTER_PREFIX + MORETHAN_USD_9999_99);
		assertEquals(SeoConstants.PRICE_FILTER_PREFIX + currencyCode + SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN 
				+ "9999.99" + SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN, priceFilter.getSeoId());
		assertEquals("morethan-usd-9999.99", priceFilter.getSeoName(Locale.US));
		
		String seoId = SeoConstants.PRICE_FILTER_PREFIX + currencyCode + SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN + "500" 
			+ SeoConstants.DEFAULT_SEPARATOR_IN_TOKEN + "900";
		this.priceFilter.initialize(seoId);
		assertEquals("The SEO Id should match the one passed in", seoId, this.priceFilter.getSeoId());
	}

	/**
	 * Ensure that output is sensible and doesn't just reflect the input which could be 
	 * tainted user input which may allow for a cross site scripting attack if printed
	 * on a page.  This test checks a filter created during browsing, not startup.
	 * 
	 * In EP5 this was changed to manage the output from the filter.  EP6 throws an exception
	 * if an invalid filter id is sent to the filter.
	 */
	@Test
	public void testGetSeoIdPreventXSS() {
		try {
			this.priceFilter.initialize("prUSD__dangerousUnsanitizedInput");
			fail("Invalid filter should have caused and exception");
		} catch (EpCatalogViewRequestBindException expected) { // NOPMD
			// good, we expect this because it was an invalid filter string
		}
	
		try {
			this.priceFilter.initialize("prUSD_dangerousUnsanitizedInput_");
			fail("Invalid filter should have caused and exception");
		} catch (EpCatalogViewRequestBindException expected) { // NOPMD
			// good, we expect this because it was an invalid filter string
		}

		try {
			this.priceFilter.initialize("prUSD_dangerousUnsanitizedInput_dangerousUnsanitizedInput");
			fail("Invalid filter should have caused and exception");
		} catch (EpCatalogViewRequestBindException expected) { // NOPMD
			// good, we expect this because it was an invalid filter string
		}		
	}	
}
