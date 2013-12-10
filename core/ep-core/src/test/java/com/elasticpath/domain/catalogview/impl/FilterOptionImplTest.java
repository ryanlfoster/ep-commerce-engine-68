package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.search.SearchRequest;

/**
 * Test <code>FilterOptionImpl</code>.
 */
public class FilterOptionImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";

	private static final String QUERY_STRING = "query string";

	private static final Object DISPLAY_NAME = "display name";


	private FilterOptionImpl<PriceFilter> filterOption;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		this.filterOption = new FilterOptionImpl<PriceFilter>();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.FilterOptionImpl.getHitsNumber()'.
	 */
	@Test
	public void testGetHitsNumber() {
		assertEquals(0, filterOption.getHitsNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.FilterOptionImpl.setHitsNumber(int)'.
	 */
	@Test
	public void testSetHitsNumber() {
		final int hitsNumber = Integer.MAX_VALUE;
		filterOption.setHitsNumber(hitsNumber);
		assertEquals(hitsNumber, filterOption.getHitsNumber());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.FilterOptionImpl.getDisplayName(Locale)'.
	 */
	@Test
	public void testGetDisplayName() {
		// not initialized
		try {
			filterOption.getDisplayName(Locale.US);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (EpDomainException e) {
			// succed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.FilterOptionImpl.getQueryString()'.
	 */
	@Test
	public void testGetQueryString() {
		// not initialized
		try {
			filterOption.getQueryString();
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (EpDomainException e) {
			// succed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.FilterOptionImpl.setSearchRequest(SearchRequest)'.
	 */
	@Test
	public void testSetSearchRequest() {
		final SearchRequest mockSearchRequest = context.mock(SearchRequest.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchRequest).getQueryString();
				will(returnValue(QUERY_STRING));
			}
		});
		final SearchRequest searchRequest = mockSearchRequest;
		filterOption.setQueryString(searchRequest.getQueryString());
		assertEquals(QUERY_STRING, filterOption.getQueryString());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.FilterOptionImpl.setFilter(Filter)'.
	 */
	@Test
	public void testSetFilter() {
		final PriceFilter mockFilter = context.mock(PriceFilter.class);
		context.checking(new Expectations() {
			{
				allowing(mockFilter).getDisplayName(with(any(Locale.class)));
				will(returnValue(DISPLAY_NAME));
			}
		});
		final PriceFilter filter = mockFilter;
		filterOption.setFilter(filter);
		assertEquals(DISPLAY_NAME, filterOption.getDisplayName(Locale.US));

		// Search request can only be set once
		try {
			filterOption.setFilter(filter);
			fail(EP_DOMAIN_EXCEPTION_EXPECTED);
		} catch (EpDomainException e) {
			// succed
			assertNotNull(e);
		}
	}

}
