package com.elasticpath.sfweb.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.search.SearchRequest;
import com.elasticpath.domain.catalogview.search.impl.SearchRequestImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sfweb.EpSearchKeyWordNotGivenException;
import com.elasticpath.sfweb.EpSearchKeyWordTooLongException;

/**
 * Test for the store search request builder.
 *
 */
public class StoreSearchRequestFactoryTest {
	
	private static final String CHARS_56 = "12345678901234567890123456789012345678901234567890123456";

	private static final String CHARS_50 = "12345678901234567890123456789012345678901234567890";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private StoreSearchRequestFactoryImpl storeSearchRequestFactoryImpl;
	
	/**
	 * Set up.
	 */
	@Before
	public void setUp() {	
		storeSearchRequestFactoryImpl = new StoreSearchRequestFactoryImpl();
	}
	
	/**
	 * Test to make sure a search request with filters and sorters and keywords will work.
	 */
	@Test
	public void testBuildWithKeywordsFiltersAndSorters() {
		
		final SearchRequest searchRequestTesting = new SearchRequestImpl() {
			private static final long serialVersionUID = -9100512304344089120L;

			private String filtersIdstr;
			private String keyWords;
			private Locale locale;
			private Currency currency;
			
			@Override
			public void setFiltersIdStr(final String filtersIdStr, final Store store) throws EpCatalogViewRequestBindException {
				this.filtersIdstr = filtersIdStr;
			}
					
			@Override
			public String getFilterIds() {
				return filtersIdstr;
			}
			
			@Override
			public void setKeyWords(final String keyWords)
					throws EpCatalogViewRequestBindException {
				this.keyWords = keyWords;
			}
			
			@Override
			public String getKeyWords() {
				return keyWords;
			}
			
			@Override
			public void setLocale(final Locale locale) {
				this.locale = locale;
			}
			
			@Override
			public Locale getLocale() {
				return locale;
			}
			
			@Override
			public void setCurrency(final Currency currency) {
				this.currency = currency;
			}
			@Override
			public Currency getCurrency() {
				return currency;
			}
		};
		
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		
		
		final Store store = new StoreImpl();
		final Locale locale = Locale.ENGLISH;
		final Currency currency = Currency.getInstance(Locale.US);
		
		storeSearchRequestFactoryImpl.setBeanFactory(beanFactory);
		
		
		final String filterIdStr = "c90000003+prUSD_100";
		final String sortStr = "relevance-desc";
		final String keywords = "canon";
		
		context.checking(new Expectations() {
			{ // NOPMD
				allowing(request).getParameter("filters");
				will(returnValue(filterIdStr));
				
				allowing(request).getParameter("sorter");
				will(returnValue(sortStr));
				
				allowing(beanFactory).getBean(ContextIdNames.SEARCH_REQUEST);
				will(returnValue(searchRequestTesting));
				
				allowing(request).getParameter("keyWords");
				will(returnValue(keywords));
				
				allowing(request).getParameter("categoryId");
				will(returnValue(null));
				
			}
		});
		
		SearchRequest searchRequest = storeSearchRequestFactoryImpl.build(request, store, locale, currency);
		
		assertEquals(filterIdStr, searchRequest.getFilterIds());
		assertEquals(sortStr, searchRequest.getSortTypeOrderString());
		assertEquals(keywords, searchRequest.getKeyWords());
		assertEquals(locale, searchRequest.getLocale());
		assertEquals(currency, searchRequest.getCurrency());
				
	}
	
	/**
	 * We test that keywords with 256 chars will fail the test.
	 */
	@Test
	public void testKeywordsTooLong() {
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		
		final SearchRequest searchRequest = context.mock(SearchRequest.class);
		
		final Store store = new StoreImpl();
		final Locale locale = Locale.ENGLISH;
		final Currency currency = Currency.getInstance(Locale.US);
		
		storeSearchRequestFactoryImpl.setBeanFactory(beanFactory);
		
		
		final String filterIdStr = "c90000003+prUSD_100";
		final String sortStr = "relevance-desc";
		final String keywords256chars = CHARS_50
				+ CHARS_50
				+ CHARS_50
				+ CHARS_50
				+ CHARS_56;
		
		context.checking(new Expectations() {
			{ // NOPMD
				allowing(request).getParameter("filters");
				will(returnValue(filterIdStr));
				
				allowing(request).getParameter("sorter");
				will(returnValue(sortStr));
				
				allowing(beanFactory).getBean(ContextIdNames.SEARCH_REQUEST);
				will(returnValue(searchRequest));
				
				allowing(request).getParameter("keyWords");
				will(returnValue(keywords256chars));
				
				allowing(request).getParameter("categoryId");
				will(returnValue(null));
				
				
				/**
				 *  checks that the below fields are added to the search request.
				 */
				allowing(searchRequest).setKeyWords(keywords256chars);
				
			}
		});
		
		try {
			storeSearchRequestFactoryImpl.build(request, store, locale, currency);
			fail("EpSearchKeyWordTooLongException should be thrown");
		} catch (EpSearchKeyWordTooLongException e) {
			assertEquals("Search key word is too long.", e.getMessage());
		}
		
	}
	
	/**
	 * We test that keywords with 256 chars will fail the test.
	 */
	@Test
	public void testNoKeyWords() {
		final HttpServletRequest request = context.mock(HttpServletRequest.class);
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		
		final SearchRequest searchRequest = context.mock(SearchRequest.class);
		
		final Store store = new StoreImpl();
		final Locale locale = Locale.ENGLISH;
		final Currency currency = Currency.getInstance(Locale.US);
		
		storeSearchRequestFactoryImpl.setBeanFactory(beanFactory);
		
		
		final String filterIdStr = "c90000003+prUSD_100";
		final String sortStr = "relevance-desc";
		
		//sets the keywords to null
		final String keywords = null;
		
		context.checking(new Expectations() {
			{ // NOPMD
				allowing(request).getParameter("filters");
				will(returnValue(filterIdStr));
				
				allowing(request).getParameter("sorter");
				will(returnValue(sortStr));
				
				allowing(beanFactory).getBean(ContextIdNames.SEARCH_REQUEST);
				will(returnValue(searchRequest));
				
				allowing(request).getParameter("keyWords");
				will(returnValue(keywords));
				
				allowing(request).getParameter("categoryId");
				will(returnValue(null));
								
				/**
				 *  checks that the below fields are added to the search request.
				 */
				allowing(searchRequest).setKeyWords(keywords);
				
				allowing(searchRequest).setCategoryUid(new Long(0));
				
				allowing(searchRequest).setFiltersIdStr(filterIdStr, store);
				
				allowing(searchRequest).parseSorterIdStr(sortStr);
				
				allowing(searchRequest).setCurrency(currency);
				
				allowing(searchRequest).setLocale(locale);
				
			}
		});
		
		try {
			storeSearchRequestFactoryImpl.build(request, store, locale, currency);
			fail("EpSearchKeyWordNotGivenException should be thrown");
		} catch (EpSearchKeyWordNotGivenException e) {
			assertEquals("No keywords in search request.", e.getMessage());
		}
		
	}
	
}
