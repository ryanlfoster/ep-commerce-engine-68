package com.elasticpath.sfweb.viewbean.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.sfweb.viewbean.CatalogViewResultBean;

/**
 * CatalogViewResultBeanImplTest.
 */
public class CatalogViewResultBeanImplTest {

	private static final int PAGE_NUMBER_SIX = 6;

	private static final int PAGE_NUMBER_FOUR = 4;

	private CatalogViewResultBean catalogViewResultBean;

	private CatalogViewResultHistory catalogViewResultHistory;

	private CatalogViewResult catalogViewResult;

	private BrowsingResult browsingResult;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	@Before
	public void setUp() {

		catalogViewResultBean = new CatalogViewResultBeanImpl();

		browsingResult = context.mock(BrowsingResult.class);
		catalogViewResult = context.mock(CatalogViewResult.class);
		catalogViewResultHistory = context.mock(CatalogViewResultHistory.class);

		catalogViewResultBean.setCurrentCatalogViewResult(catalogViewResult);

		setCommonExpectations();
	}

	/**
	 * Set common expectations.
	 */
	@SuppressWarnings("PMD.EmptyInitializer")
	protected void setCommonExpectations() {

		context.checking(new Expectations() { {

			}
		});
	}

	/**
	 * Test for com.elasticpath.sfweb.viewbean.impl.CatalogViewResultBeanImpl.getCatalogViewResultHistory().
	 */
	@Test
	public void testCatalogViewResultHistory() {

		catalogViewResultBean.setCatalogViewResultHistory(catalogViewResultHistory);
		assertNotNull(catalogViewResultBean.getCatalogViewResultHistory());
	}

	/**
	 * Test for com.elasticpath.sfweb.viewbean.impl.CatalogViewResultBeanImpl.getCurrentCatalogViewResult().
	 */
	@Test
	public void testCurrentCatalogViewResult() {

		catalogViewResultBean.setCurrentCatalogViewResult(browsingResult);
		assertNotNull(catalogViewResultBean.getCurrentCatalogViewResult());
	}

	/**
	 * Test for com.elasticpath.sfweb.viewbean.impl.CatalogViewResultBeanImpl.getCurrentPageNumber().
	 */
	@Test
	public void testGetCurrentPageNumber() {
		catalogViewResultBean.setCurrentPageNumber(PAGE_NUMBER_FOUR);
		assertEquals("If total number of pages is 0, current page should return 0 no matter what you set it to.",
				0, catalogViewResultBean.getCurrentPageNumber());

		catalogViewResultBean.setTotalPageNumber(PAGE_NUMBER_FOUR);
		catalogViewResultBean.setCurrentPageNumber(PAGE_NUMBER_SIX);
		assertEquals("If you set current page # to > total # of pages, current page should return total # pages.",
				PAGE_NUMBER_FOUR, catalogViewResultBean.getCurrentPageNumber());

		catalogViewResultBean.setTotalPageNumber(PAGE_NUMBER_SIX);
		catalogViewResultBean.setCurrentPageNumber(PAGE_NUMBER_FOUR);
		assertEquals("If you set current page # < total # pages, current page should return what was set.",
				PAGE_NUMBER_FOUR, catalogViewResultBean.getCurrentPageNumber());
	}

	/**
	 * Test for com.elasticpath.sfweb.viewbean.impl.CatalogViewResultBeanImpl.getTotalPageNumber().
	 */
	@Test
	public void testGetTotalPageNumber() {
		catalogViewResultBean.setTotalPageNumber(PAGE_NUMBER_FOUR);
		assertEquals(PAGE_NUMBER_FOUR, catalogViewResultBean.getTotalPageNumber());
	}

}
