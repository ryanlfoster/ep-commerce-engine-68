package com.elasticpath.sfweb.search.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.impl.AdvancedSearchFilteredNavSeparatorFilterImpl;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.catalogview.search.impl.AdvancedSearchRequestImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.sfweb.formbean.AdvancedSearchControllerFormBean;
import com.elasticpath.sfweb.formbean.impl.AdvancedSearchControllerFormBeanImpl;
import com.elasticpath.sfweb.view.helpers.ParameterMapper;

/**
 * Test for the advanced store search request builder.
 *
 */
public class AdvancedStoreSearchRequestFactoryTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private AdvancedStoreSearchRequestFactoryImpl storeSearchRequestFactoryImpl;
	
	private ParameterMapper parameterMapper;
	
	private AdvancedSearchFilteredNavSeparatorFilter dummyFilter;
	
	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		storeSearchRequestFactoryImpl = new AdvancedStoreSearchRequestFactoryImpl();
		parameterMapper = new ParameterMapper();
		storeSearchRequestFactoryImpl.setParameterMapper(parameterMapper);
		
		final FilterFactory filterFactory = context.mock(FilterFactory.class);
		storeSearchRequestFactoryImpl.setFilterFactory(filterFactory);
		
		dummyFilter = new AdvancedSearchFilteredNavSeparatorFilterImpl();
		
		context.checking(new Expectations() {
			{
				PriceFilter priceFilter = context.mock(PriceFilter.class);
				allowing(priceFilter).getId();
				will(returnValue(""));
				
				allowing(filterFactory).createPriceFilter(with(any(Currency.class)), with(any(BigDecimal.class)), with(any(BigDecimal.class)));
				will(returnValue(priceFilter));
				
				allowing(filterFactory).getAttributeRangeFiltersWithoutPredefinedRanges(with(aNull(String.class)));
				will(returnValue(Collections.emptyList()));
				
				allowing(filterFactory).createAdvancedSearchFiteredNavSeparatorFilter();
				will(returnValue(dummyFilter));
				
			}
		});
		
	}
	
	/**
	 * Spring will null out the brands list if nothing selected.
	 */
	@Test
	public void testNullBrands() {
		final String filterIdStr = "";
		final AdvancedSearchRequest searchRequestTesting = new AdvancedSearchRequestImplTesting();
		
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		
		final Store store = new StoreImpl();
		final Locale locale = Locale.ENGLISH;
		final Currency currency = Currency.getInstance(Locale.US);
		
		storeSearchRequestFactoryImpl.setBeanFactory(beanFactory);
		
		
		
		context.checking(new Expectations() {
			{
				AdvancedSearchControllerFormBean bean = new AdvancedSearchControllerFormBeanImpl();
				bean.setBrands(null);
				allowing(request).getAttribute("advancedSearchControllerFormBean");
				will(returnValue(bean));
				
				allowing(request).getParameter("amountFrom");
				will(returnValue("0"));
				
				allowing(request).getParameter("amountTo");
				will(returnValue("100"));
				
				allowing(request).getParameter("filters");
				will(returnValue(filterIdStr));
				
				allowing(request).getParameter("sorter");
				will(returnValue(null));
				
				allowing(beanFactory).getBean(ContextIdNames.ADVANCED_SEARCH_REQUEST);
				will(returnValue(searchRequestTesting));

				allowing(request).getParameter(parameterMapper.getSearchFormButtonName());
				will(returnValue(null));
			}
		});
		
		AdvancedSearchRequest searchRequest = storeSearchRequestFactoryImpl.build(request, store, locale, currency);
		
		assertEquals(filterIdStr, searchRequest.getFilterIds());
		//by default it is sort by relevance-descending
		assertEquals("relevance-desc", searchRequest.getSortTypeOrderString());
		assertEquals(locale, searchRequest.getLocale());
		assertEquals(currency, searchRequest.getCurrency());
	}
	
	/**
	 * Test to make sure a search request with filters and sorters will work. 
	 */
	@Test
	public void testBuildWithFilters() {
		
		final String filterIdStr = "c90000003+prUSD_100";
		
		final AdvancedSearchRequest searchRequestTesting = new AdvancedSearchRequestImplTesting();
		
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		
		
		final Store store = new StoreImpl();
		final Locale locale = Locale.ENGLISH;
		final Currency currency = Currency.getInstance(Locale.US);
		
		storeSearchRequestFactoryImpl.setBeanFactory(beanFactory);
		
		
		
		context.checking(new Expectations() {
			{
				allowing(request).getParameter("filters");
				will(returnValue(filterIdStr));
				
				allowing(request).getParameter("sorter");
				will(returnValue(null));
				
				allowing(beanFactory).getBean(ContextIdNames.ADVANCED_SEARCH_REQUEST);
				will(returnValue(searchRequestTesting));
								
				allowing(request).getParameter(parameterMapper.getSearchFormButtonName());
				will(returnValue(null));
			}
		});
		
		AdvancedSearchRequest searchRequest = storeSearchRequestFactoryImpl.build(request, store, locale, currency);
		
		assertEquals(filterIdStr, searchRequest.getFilterIds());
		//by default it is sort by relevance-descending
		assertEquals("relevance-desc", searchRequest.getSortTypeOrderString());
		assertEquals(locale, searchRequest.getLocale());
		assertEquals(currency, searchRequest.getCurrency());
				
	}

	/**
	 * Private class for testing purposes.
	 *
	 */
	private class AdvancedSearchRequestImplTesting extends AdvancedSearchRequestImpl {

		private static final long serialVersionUID = -5125120766086304818L;

		private String filtersIdstr;

		@Override
		public void setFiltersIdStr(final String filtersIdStr, final Store store) throws EpCatalogViewRequestBindException {
			this.filtersIdstr = filtersIdStr;
		}
				
		@Override
		public String getFilterIds() {
			return filtersIdstr;
		}

	}
}


