package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.CatalogViewResult;
import com.elasticpath.service.search.ProductCategorySearchCriteria;
import com.elasticpath.service.search.index.IndexSearchResult;

/**
 * Test case for {@link AbstractCatalogViewServiceImpl}.
 */
public class AbstractCatalogViewServiceImplTest {

	private AbstractCatalogViewServiceImpl catalogViewServiceImpl;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private IndexSearchResult mockSearchResult;

	private static final int PAGINATION = 8;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {

		mockSearchResult = context.mock(IndexSearchResult.class);

		catalogViewServiceImpl = new AbstractCatalogViewServiceImpl() {
			@Override
			protected CatalogViewResult createCatalogViewResult() {
				// not tested
				return null;
			}

			@Override
			protected ProductCategorySearchCriteria createCriteriaForProductSearch(final CatalogViewRequest request,
					final boolean includeSubCategories) {
				// not tested
				return null;
			}
		};
	}

	/**
	 * Test method for
	 * {@link AbstractCatalogViewServiceImpl#getResults(IndexSearchResult, int) when viewing all products.
	 */
	@Test
	public void testGetResultsViewAll() {
		final List<Long> results = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockSearchResult).getAllResults();
				will(returnValue(results));
			}
		});
		assertSame(results, catalogViewServiceImpl.getPagedResults(mockSearchResult, 0, PAGINATION));
	}

	/**
	 * Test method for
	 * {@link AbstractCatalogViewServiceImpl#getResults(IndexSearchResult, int) when viewing a specific page.
	 */
	@Test
	public void testGetResultsViewPage() {
		final List<Long> results = new ArrayList<Long>();

		final int page = 1;
		context.checking(new Expectations() {
			{
				oneOf(mockSearchResult).getResults(((page - 1) * PAGINATION), PAGINATION);
				will(returnValue(results));
			}
		});
		assertSame(results, catalogViewServiceImpl.getPagedResults(mockSearchResult, page, PAGINATION));

		final int page2 = 5;
		context.checking(new Expectations() {
			{
				oneOf(mockSearchResult).getResults(((page2 - 1) * PAGINATION), PAGINATION);
				will(returnValue(results));
			}
		});
		assertSame(results, catalogViewServiceImpl.getPagedResults(mockSearchResult, page2, PAGINATION));
	}

}
