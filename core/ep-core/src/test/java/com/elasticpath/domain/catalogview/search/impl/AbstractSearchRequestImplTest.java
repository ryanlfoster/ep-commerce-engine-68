package com.elasticpath.domain.catalogview.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalogview.CatalogViewRequest.Breadcrumb;
import com.elasticpath.domain.catalogview.CatalogViewRequestUnmatchException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for abstract search request implementation.
 * 
 */
public class AbstractSearchRequestImplTest {
	
	private static final String CAT_4 = "cat-4";

	private static final String CAT_3 = "cat-3";

	private static final String DIGITAL_CAMERA = "digital camera";

	private static final String PRICE_ASC = SortUtility.constructSortTypeOrderString(StandardSortBy.PRICE, SortOrder.ASCENDING);

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private Store mockStore;
	
	private Store store;
	
	private FilterFactory mockFilterFactory;

	private FilterFactory filterFactory;
	
	private Filter<?> mockFilterOfCategory;

	private Filter<?> mockFilterOfPrice;

	private Filter<?> mockFilterOfBrand;

	private SearchRequest searchRequest;

	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		mockStore = context.mock(Store.class);
		store = mockStore;

		setupFilterFactory();
		this.searchRequest = getSearchRequest();
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}
	
	private void setupFilterFactory() {
		// Mock FilterFactory
		this.mockFilterFactory = context.mock(FilterFactory.class);
		this.filterFactory = this.mockFilterFactory;
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean("filterFactory");
				will(returnValue(filterFactory));
			}
		});

		mockFilterOfCategory = context.mock(Filter.class);
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfCategory).getId();
				will(returnValue(CAT_3));

				allowing(mockFilterOfCategory).getDisplayName(null);
				will(returnValue(CAT_3));

				allowing(mockFilterFactory).getFilter(with(CAT_3), with(any(Store.class)));
				will(returnValue(mockFilterOfCategory));
			}
		});

		mockFilterOfCategory = context.mock(Filter.class, "another filter");
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfCategory).getId();
				will(returnValue(CAT_4));

				allowing(mockFilterOfCategory).getDisplayName(null);
				will(returnValue(CAT_4));

				allowing(mockFilterFactory).getFilter(with(CAT_4), with(any(Store.class)));
				will(returnValue(mockFilterOfCategory));
			}
		});

		mockFilterOfPrice = context.mock(Filter.class, "price filter");
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfPrice).getId();
				will(returnValue("price-5"));

				allowing(mockFilterOfPrice).getDisplayName(null);
				will(returnValue("price-5"));

				allowing(mockFilterFactory).getFilter(with("price-5"), with(any(Store.class)));
				will(returnValue(mockFilterOfPrice));
			}
		});

		mockFilterOfBrand = context.mock(Filter.class, "brand filter");
		context.checking(new Expectations() {
			{
				allowing(mockFilterOfBrand).getId();
				will(returnValue("brand-8"));

				allowing(mockFilterOfBrand).getDisplayName(null);
				will(returnValue("brand-8"));

				allowing(mockFilterFactory).getFilter(with("brand-8"), with(any(Store.class)));
				will(returnValue(mockFilterOfBrand));
			}
		});
	}

	private SearchRequestImpl getSearchRequest() {
		SearchRequestImpl searchRequestImpl = new SearchRequestImpl();
		searchRequestImpl.setFilterFactory(this.filterFactory);
		return searchRequestImpl;
	}
	
	/**
	 * Test that categories that are different will throw exception.
	 */
	@Test
	public void testCompare() {
		searchRequest.setCategoryUid(1L);
		searchRequest.setKeyWords("test");
		SearchRequest request2 = new SearchRequestImpl();
		request2.setCategoryUid(2L);
		request2.setKeyWords("test");
		
		boolean exception = false;
		
		try {
			searchRequest.compare(request2);
		} catch (CatalogViewRequestUnmatchException e) {
			exception = true;
		}
		
		assertTrue(exception);
	}
	
	/**
	 * Test method getFilterQueryStrings() of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetFilterQueryStringsForMultipleFilters() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = "cat-3 price-5 brand-8";
		searchRequest.setFiltersIdStr(filtersStr, store);
		final List<Breadcrumb> filterQueryStrings = searchRequest.getFilterQueryStrings();

		final SearchRequest.Breadcrumb filterQueryString1 = filterQueryStrings.get(0);
		assertNotNull(filterQueryString1.getDisplayName());
		assertEquals("cat-3", filterQueryString1.getUrlFragment());
		assertEquals("price-5+brand-8", filterQueryString1.getUrlFragmentWithoutThisCrumb());

		final SearchRequest.Breadcrumb filterQueryString2 = filterQueryStrings.get(1);
		assertNotNull(filterQueryString2.getDisplayName());
		assertEquals("cat-3+price-5", filterQueryString2.getUrlFragment());
		assertEquals("cat-3+brand-8", filterQueryString2.getUrlFragmentWithoutThisCrumb());

		final SearchRequest.Breadcrumb filterQueryString3 = filterQueryStrings.get(2);
		assertNotNull(filterQueryString3.getDisplayName());
		assertEquals("cat-3+price-5+brand-8", filterQueryString3.getUrlFragment());
		assertEquals("cat-3+price-5", filterQueryString3.getUrlFragmentWithoutThisCrumb());
	}

	/**
	 * Test method getFilterQueryStrings() of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetFilterQueryStringsForOneFilter() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		final String filtersStr = CAT_3;
		searchRequest.setFiltersIdStr(filtersStr, store);
		final List<Breadcrumb> filterQueryStrings = searchRequest.getFilterQueryStrings();

		final SearchRequest.Breadcrumb filterQueryString1 = filterQueryStrings.get(0);
		assertNotNull(filterQueryString1.getDisplayName());
		assertEquals("cat-3", filterQueryString1.getUrlFragment());
		assertEquals("", filterQueryString1.getUrlFragmentWithoutThisCrumb());
	}

	/**
	 * Test method getFilterQueryStrings() of <code>SearchRequest</code>.
	 */
	@Test
	public void testGetFilterQueryStringsWithNoFilter() {
		searchRequest.setKeyWords(DIGITAL_CAMERA);
		searchRequest.parseSorterIdStr(PRICE_ASC);
		searchRequest.setFiltersIdStr(null, store);
		final List<Breadcrumb> filterQueryStrings = searchRequest.getFilterQueryStrings();
		assertEquals(0, filterQueryStrings.size());
	}
	
}
