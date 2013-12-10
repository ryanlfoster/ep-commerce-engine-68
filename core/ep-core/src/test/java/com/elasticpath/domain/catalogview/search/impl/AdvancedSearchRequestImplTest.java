package com.elasticpath.domain.catalogview.search.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.impl.AdvancedSearchFilteredNavSeparatorFilterImpl;
import com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl;
import com.elasticpath.domain.catalogview.impl.AttributeValueFilterImpl;
import com.elasticpath.domain.catalogview.impl.PriceFilterImpl;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for AdvancedSearchRequestImpl.
 *
 */
public class AdvancedSearchRequestImplTest {

	private static final int DEFAULT_MAP_SIZE = 3;

	private AdvancedSearchRequestImpl advancedSearchRequest;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private static final String ATTRIBUTE_ID  = "atA00051_01";

	private static final int THREE = 3;


	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		advancedSearchRequest = new AdvancedSearchRequestImpl();
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that if there are no filters (or if filters are null) then will throw exception.
	 */
	@Test (expected = EpDomainException.class)
	public void testSanityCheckThrowsException() {
		advancedSearchRequest.sanityCheck();

	}

	/**
	 * Tests that if we have filters in the request that it will not throw exceptions.
	 */
	@Test
	public void testSanityCheckNoException() {
		advancedSearchRequest.addFilter(new AttributeValueFilterImpl());
		advancedSearchRequest.sanityCheck();
	}

	/**
	 * Tests that the same request will be compared against and it will return 0.
	 */
	@Test
	public void testCompareSameRequest() {
		assertEquals(0, advancedSearchRequest.compare(advancedSearchRequest));
	}

	/**
	 * Test that passing in null will throw an exception.
	 */
	@Test (expected = EpDomainException.class)
	public void testCompareNull() {
		advancedSearchRequest.compare(null);
	}
	/**
	 * Tests that a different request throw an exception.
	 */
	@Test (expected = EpDomainException.class)
	public void testCompareDifferentRequest() {
		CatalogViewRequest browsingRequest = new SearchRequestImpl();
		advancedSearchRequest.compare(browsingRequest);
	}

	/**
	 * Tests three things.
	 * 1.) categoryId is the same as the category id that was given
	 * 2.) getting a single filter will return the correct
	 * 3.) featured products and descending is the default sorting order
	 */
	@Test
	public void testGetQueryPropertiesSingleFilter() {
		String categoryId = "100000";

		AttributeValueFilter attributeValueFilter = new AttributeValueFilterImpl();
		attributeValueFilter.setId(ATTRIBUTE_ID);

		advancedSearchRequest.setCategoryUid(Long.parseLong(categoryId));
		advancedSearchRequest.addFilter(attributeValueFilter);

		Map<String, String> propertiesMap = advancedSearchRequest.getQueryProperties();

		assertEquals(DEFAULT_MAP_SIZE, propertiesMap.size());
		assertEquals(categoryId, propertiesMap.get(WebConstants.REQUEST_CATEGORY_ID));
		assertEquals(ATTRIBUTE_ID, propertiesMap.get(WebConstants.REQUEST_FILTERS));
		//if sorting type not specified, defaults to featuredProducts-desc
		assertEquals("featuredProducts-desc", propertiesMap.get(WebConstants.REQUEST_SORTER));

	}

	/**
	 * Tests that getting specifying multiple filters will return the correct string.
	 */
	@Test
	public void testGetQueryPropertiesMultipleFilters() {

		String attributeRangeId = "arA00051_1_2";

		AttributeValueFilter attributeValueFilter = new AttributeValueFilterImpl();
		attributeValueFilter.setId(ATTRIBUTE_ID);
		AttributeRangeFilter attributeRangeFilter = new AttributeRangeFilterImpl();
		attributeRangeFilter.setId(attributeRangeId);

		advancedSearchRequest.addFilter(attributeValueFilter);
		advancedSearchRequest.addFilter(attributeRangeFilter);

		Map<String, String> propertiesMap = advancedSearchRequest.getQueryProperties();

		assertEquals(2, propertiesMap.size());
		assertEquals(ATTRIBUTE_ID + WebConstants.SPACE + attributeRangeId, propertiesMap.get(WebConstants.REQUEST_FILTERS));

	}

	/**
	 * Tests getting the query string with one filter, one category and default sorting type.
	 */
	@Test
	public void testGetQueryStringOneFilter() {
		String categoryId = "100000";

		AttributeValueFilter attributeValueFilter = new AttributeValueFilterImpl();
		attributeValueFilter.setId(ATTRIBUTE_ID);

		advancedSearchRequest.setCategoryUid(Long.parseLong(categoryId));
		advancedSearchRequest.addFilter(attributeValueFilter);

		String filterString = "categoryId=100000&filters=atA00051_01&sorter=featuredProducts-desc";
		assertEquals(filterString, advancedSearchRequest.getQueryString());
	}

	/**
	 * Test that will throw an exception of no filters.
	 */
	@Test (expected = EpDomainException.class)
	public void testNoFiltersThrowsException() {
		advancedSearchRequest.getQueryString();
	}

	/**
	 * Tests getting the query string with multiple filters.
	 */
	@Test
	public void testGetQueryStringMultipleFilters() {
		String attributeRangeId = "arA00051_1_2";
		String priceId = "prUSD_0_2000";

		AttributeValueFilter attributeValueFilter = new AttributeValueFilterImpl();
		attributeValueFilter.setId(ATTRIBUTE_ID);
		AttributeRangeFilter attributeRangeFilter = new AttributeRangeFilterImpl();
		attributeRangeFilter.setId(attributeRangeId);
		PriceFilter priceFilter = new PriceFilterImpl();
		priceFilter.setId(priceId);

		advancedSearchRequest.addFilter(attributeValueFilter);
		advancedSearchRequest.addFilter(attributeRangeFilter);
		advancedSearchRequest.addFilter(priceFilter);

		String filterString = "filters=atA00051_01 arA00051_1_2 prUSD_0_2000&sorter=featuredProducts-desc";

		assertEquals(filterString, advancedSearchRequest.getQueryString());
	}

	/**
	 * Tests that the advanced search filter factory creates filters if no filtered nav filters.
	 */
	@Test
	public void testCreateFiltersNoFilteredNav() {
		final String asfilterStr = "asfilter";
		final String separator = "separator";

		final Store store = context.mock(Store.class);
		final Filter< ? > asfilter = context.mock(Filter.class);
		final AdvancedSearchFilteredNavSeparatorFilter separatorFilter = context.mock(AdvancedSearchFilteredNavSeparatorFilter.class);

		final FilterFactory advancedSearchFactory = context.mock(FilterFactory.class, "asfactory");
		expectationsFactory.allowingBeanFactoryGetBean("advancedSearchFilterFactory", advancedSearchFactory);

		context.checking(new Expectations() {
			{
				allowing(advancedSearchFactory).getFilter(asfilterStr, store); will(returnValue(asfilter));
				allowing(advancedSearchFactory).getFilter(separator, store); will(returnValue(separatorFilter));
			}
		});

		List<Filter< ? >> result = advancedSearchRequest.createFilters(new String[] { asfilterStr, separator }, store);
		assertEquals(asfilter, result.get(0));
	}

	/**
	 * Tests that filters after a separator are created using filtered nav filter factory.
	 */
	@Test
	public void testCreateFiltersFilteredNav() {
		final String asfilterStr = "asfilter";
		final String separator = "separator";
		final String fnfilterStr = "fnfilter";

		final Store store = context.mock(Store.class);
		final Filter< ? > asfilter = context.mock(Filter.class, "asfilter");
		final AdvancedSearchFilteredNavSeparatorFilter separatorFilter = context.mock(AdvancedSearchFilteredNavSeparatorFilter.class);
		final Filter< ? > fnfilter = context.mock(Filter.class, "fnfilter");

		context.checking(new Expectations() {
			{
				FilterFactory filteredNavFactory = context.mock(FilterFactory.class, "fnfactory");
				FilterFactory advancedSearchFactory = context.mock(FilterFactory.class, "asfactory");

				allowing(beanFactory).getBean("filterFactory"); will(returnValue(filteredNavFactory));
				allowing(beanFactory).getBean("advancedSearchFilterFactory"); will(returnValue(advancedSearchFactory));

				allowing(advancedSearchFactory).getFilter(asfilterStr, store); will(returnValue(asfilter));
				allowing(advancedSearchFactory).getFilter(separator, store); will(returnValue(separatorFilter));
				allowing(filteredNavFactory).getFilter(fnfilterStr, store); will(returnValue(fnfilter));
			}
		});

		List<Filter< ? >> result = advancedSearchRequest.createFilters(new String[] { asfilterStr, separator, fnfilterStr }, store);
		assertEquals(asfilter, result.get(0));
		assertEquals(fnfilter, result.get(2));
	}

	/**
	 * Tests AdvancedSearchRequestImpl.getAdvancedSearchFilters().
	 */
	@Test
	public void testAdvSearchFilters() {
		final AdvancedSearchFilteredNavSeparatorFilter dummyFilter = new AdvancedSearchFilteredNavSeparatorFilterImpl();
		dummyFilter.setId("|");
		AdvancedSearchRequest advancedSearchRequest = new StubbedAdvancedSearchRequestImpl(dummyFilter);
		//populating adv search request with 3 filters i.e. price , dummy, and attribute range filters.
		Filter<?> filter = new PriceFilterImpl();
		filter.setId("prUSD__100");
		advancedSearchRequest.addFilter(filter);
		advancedSearchRequest.addFilter(dummyFilter);
		AttributeRangeFilter attrRangeFilter = new AttributeRangeFilterImpl();
		attrRangeFilter.setId("arA001__4");
		advancedSearchRequest.addFilter(attrRangeFilter);
		//expect 3 filters all together returned by getFilters().
		assertEquals(THREE, advancedSearchRequest.getFilters().size());
		assertEquals(1, advancedSearchRequest.getAdvancedSearchFilters().size());
		//expect 1 filter returned by getAdvancedSearchFilters()
		assertEquals(filter, advancedSearchRequest.getAdvancedSearchFilters().get(0));

		advancedSearchRequest.getFilters().remove(dummyFilter);
		//expect 2 filters returned by getAdvancedSearchFilters() if there is no dummy filter in the list
		assertEquals(2, advancedSearchRequest.getAdvancedSearchFilters().size());
	}

	/**
	 * Separate class to work around a Checkstyle defect.
	 * 
	 * @see <a href="http://sourceforge.net/p/checkstyle/bugs/472/">the defect</a>
	 */
	private static final class StubbedAdvancedSearchRequestImpl extends AdvancedSearchRequestImpl {
		private static final long serialVersionUID = 2407713724903023378L;

		private final AdvancedSearchFilteredNavSeparatorFilter dummyFilter;

		public StubbedAdvancedSearchRequestImpl(final AdvancedSearchFilteredNavSeparatorFilter dummyFilter) {
			this.dummyFilter = dummyFilter;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <T> T getBean(final String beanName) {
			return (T) dummyFilter;
		}
	}

}
