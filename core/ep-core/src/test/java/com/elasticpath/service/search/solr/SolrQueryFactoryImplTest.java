package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.CommonParams;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.RangeFilterType;
import com.elasticpath.domain.catalogview.impl.AdvancedSearchFilteredNavSeparatorFilterImpl;
import com.elasticpath.domain.catalogview.impl.BrandFilterImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.search.CatalogAwareSearchCriteria;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.SpellSuggestionSearchCriteria;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.SpellingConstants.SpellingParams;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.impl.CachedSettingsReaderImpl;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Test {@link SolrQueryFactoryImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.ExcessiveImports" })
public class SolrQueryFactoryImplTest {

	private static final String CATEGORY_CODE = "code1";

	private static final long CATEGORY_UID = 2L;

	private static final int START_INDEX = 3;

	private static final int MAX_ROWS = 5;

	private static final String USD = "USD";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SolrQueryFactoryImpl solrQueryFactoryImpl;

	private QueryComposer queryComposer;

	private CatalogAwareSearchCriteria searchCriteria;
	
	private AttributeService attributeService;
	
	private Analyzer analyzer;
	
	private SpellSuggestionSearchCriteria spellSuggestionSearchCriteria;

	private KeywordSearchCriteria keywordSearchCriteria;

	private SearchConfig searchConfig;
	
	private List<Filter< ? >> filterQueries;
	
	private CategoryService categoryService;
	
	private BrandService brandService;

	private BeanFactory beanFactory;

	private SearchConfigFactory searchConfigFactory;
	
	private SettingsReader settingsReader;
	
	private SettingValue settingValue;

	private Category category;

	private Catalog catalog;

	/**
	 * Prepare for test.
	 * 
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		category = context.mock(Category.class);
		filterQueries = constructNonStoreAwareFilters();
		
		final SortOrder sortOrder = SortOrder.ASCENDING;
		final SortBy sortBy = StandardSortBy.RELEVANCE;

		keywordSearchCriteria = new KeywordSearchCriteria();
		keywordSearchCriteria.setSortingOrder(sortOrder);
		keywordSearchCriteria.setSortingType(sortBy);
		keywordSearchCriteria.setCurrency(Currency.getInstance(USD));
		keywordSearchCriteria.setLocale(Locale.US);
		keywordSearchCriteria.setFilters(filterQueries);

		queryComposer = context.mock(QueryComposer.class);
		searchCriteria = context.mock(CatalogAwareSearchCriteria.class);
		
		spellSuggestionSearchCriteria = context.mock(SpellSuggestionSearchCriteria.class);

		searchConfig = context.mock(SearchConfig.class);
		
		beanFactory = context.mock(BeanFactory.class);
		searchConfigFactory = context.mock(SearchConfigFactory.class);
		
		solrQueryFactoryImpl = new SolrQueryFactoryImpl();
		solrQueryFactoryImpl.setBeanFactory(beanFactory); 
		solrQueryFactoryImpl.setSearchConfigFactory(searchConfigFactory);
		
		attributeService = context.mock(AttributeService.class);
		
		categoryService = context.mock(CategoryService.class);
		
		brandService = context.mock(BrandService.class);
		
		catalog = context.mock(Catalog.class);
		
		analyzer = context.mock(Analyzer.class);
		
		settingsReader = context.mock(SettingsReader.class);
		settingValue = context.mock(SettingValue.class);

		context.checking(new Expectations() {
			{
				allowing(searchCriteria).getIndexType(); will(returnValue(IndexType.PRODUCT));
				allowing(searchCriteria).getCurrency(); will(returnValue(Currency.getInstance(USD)));
				allowing(searchCriteria).getLocale(); will(returnValue(Locale.US));
				allowing(searchCriteria).getSortingType(); will(returnValue(sortBy));
				allowing(searchCriteria).getSortingOrder(); will(returnValue(sortOrder));
				allowing(searchCriteria).getFilters(); will(returnValue(filterQueries));
				allowing(searchCriteria).getCatalogCode(); will(returnValue("catalog_code"));
				
				allowing(spellSuggestionSearchCriteria).getLocale(); will(returnValue(Locale.US));
				
				allowing(beanFactory).getBean(ContextIdNames.ATTRIBUTE_SERVICE); will(returnValue(attributeService));
				allowing(beanFactory).getBean(ContextIdNames.CATEGORY); will(returnValue(category));
				
				allowing(category).getCode(); will(returnValue("myCategoryCode"));
				allowing(categoryService).load(with(any(Long.class))); will(returnValue(category));

				allowing(catalog).getCode(); will(returnValue("myCatalogCode"));
				
				ignoring(analyzer);
				
				allowing(settingsReader).getSettingValue(""); will(returnValue(settingValue));
				allowing(settingValue).getBooleanValue(); will(returnValue(false));
			}
		});

		// Override the methods in IndexUtilityImpl that make calls to the database, to provide sensible defaults for testing
		IndexUtilityImpl indexUtilityImpl = new IndexUtilityImpl() {
			@Override
			public String createDisplayableFieldName(final String name, final String storeCode) {
				return name + storeCode;
			}
			@Override
			public String createPriceFieldName(final String name, final String storeCode, final String priceListGuid) {
				return name + storeCode + priceListGuid;
			}
		};
		indexUtilityImpl.setSolrAttributeTypeExt(new HashMap<String, String>());
		solrQueryFactoryImpl.setIndexUtility(indexUtilityImpl);
		
		SolrFacetAdapter facetAdapter = new SolrFacetAdapter();
		facetAdapter.setAnalyzer(analyzer);
		facetAdapter.setCategoryService(categoryService);
		facetAdapter.setIndexUtility(indexUtilityImpl);
		solrQueryFactoryImpl.setSolrFacetAdapter(facetAdapter);
		
		solrQueryFactoryImpl.setSettingsReader(settingsReader);
	}

	private List<Filter< ? >> constructNonStoreAwareFilters() {
		
		final AttributeRangeFilter attributeRangeFilter = context.mock(AttributeRangeFilter.class);
		final AttributeValueWithType attributeValue = context.mock(AttributeValueWithType.class);
		final AttributeValueFilter attributeValueFilter = context.mock(AttributeValueFilter.class);
		final BrandFilter brandFilter = context.mock(BrandFilter.class);
		final Brand brand = context.mock(Brand.class);
		final Set<Brand> brands = new HashSet<Brand>();
		brands.add(brand);
		brands.add(brand);
		final CategoryFilter categoryFilter = context.mock(CategoryFilter.class, "non store aware");
		context.checking(new Expectations() {
			{
				allowing(attributeValue).getStringValue(); will(returnValue("some string"));
				
				allowing(attributeRangeFilter).getLowerValue(); will(returnValue(attributeValue));
				allowing(attributeRangeFilter).getUpperValue(); will(returnValue(attributeValue));
				allowing(attributeRangeFilter).getRangeType(); will(returnValue(RangeFilterType.BETWEEN));
				allowing(attributeRangeFilter).getAttribute(); will(returnValue(constructAttribute("some key")));
				allowing(attributeRangeFilter).getLocale(); will(returnValue(Locale.US));
				
				allowing(attributeValueFilter).getAttribute(); will(returnValue(constructAttribute("some other key")));
				allowing(attributeValueFilter).getLocale(); will(returnValue(Locale.US));
				allowing(attributeValueFilter).getAttributeValue(); will(returnValue(attributeValue));
				
				allowing(brand).getCode(); will(returnValue("some code"));
				allowing(brandFilter).getBrand(); will(returnValue(brand));
				allowing(brandFilter).getBrands(); will(returnValue(brands));
				
				allowing(category).getUidPk(); will(returnValue(CATEGORY_UID));
				allowing(categoryFilter).getCategory(); will(returnValue(category));
			}
		});
		
		final List<Filter< ? >> filters = new ArrayList<Filter< ? >>();
		filters.add(attributeRangeFilter);
		filters.add(attributeValueFilter);
		filters.add(brandFilter);
		filters.add(categoryFilter);
		
		return filters;
	}

	private Attribute constructAttribute(final String key) {
		final Attribute attribute = context.mock(Attribute.class, key);
		context.checking(new Expectations() {
			{
				allowing(attribute).isLocaleDependant(); will(returnValue(true));
				allowing(attribute).getKey(); will(returnValue(key));
				allowing(attribute).getAttributeType(); will(returnValue(AttributeType.LONG_TEXT));
			}
		});

		return attribute;
	}

	/**
	 * Test that createLuceneQuery creates a fuzzy query when it should, and a nonFuzzy query when it should not.
	 */
	@Test
	public void testcreateLuceneQueryFuzzy() {
		final Query fuzzyQuery = new TermQuery(new Term("fuzzyQueryTerm", "with a search"));
		final Query nonFuzzyQuery = new TermQuery(new Term("nonFuzzyQueryTerm", "with a search"));
		context.checking(new Expectations() {
			{
				oneOf(queryComposer).composeFuzzyQuery(null, null); will(returnValue(fuzzyQuery));
				oneOf(queryComposer).composeQuery(null, null); will(returnValue(nonFuzzyQuery));
			}
		});
		
		SolrQueryFactoryImpl factory = new SolrQueryFactoryImpl();
		
		assertSame(fuzzyQuery, factory.createLuceneQuery(queryComposer, null, null, true));
		assertSame(nonFuzzyQuery, factory.createLuceneQuery(queryComposer, null, null, false));
	}
	
	/**
	 * Test that createSolrQueryFromLuceneQuery() creates a solr query of type "standard".
	 */
	@Test
	public void testCreateSolrQuery() {
		SolrQueryFactoryImpl factory = new SolrQueryFactoryImpl();
		SolrQuery query = factory.createSolrQueryFromLuceneQuery(new MatchAllDocsQuery());
		assertEquals("standard", query.getQueryType());
	}

	/**
	 * Test that the start index, max results are set properly.
	 */
	@Test
	public void testAddInvariantTerms() {
		context.checking(new Expectations() {
			{
				exactly(2).of(searchConfig).getMaxReturnNumber(); will(returnValue(0));
			}
		});

		SolrQueryFactoryImpl factory = new SolrQueryFactoryImpl();
		//Manually create a query that has the properties we're looking for
		SolrQuery startIndexQuery = new SolrQuery();
		startIndexQuery.setStart(START_INDEX);
		//compare the manually-created query against the one modified by the method we're testing
		SolrQuery query = new SolrQuery();
		factory.addInvariantTerms(query, START_INDEX, MAX_ROWS, searchConfig);
		assertTrue(query.toString().contains(startIndexQuery.toString()));
		
		//Manually create a query that has the properties we're looking for
		SolrQuery rowsQuery = new SolrQuery();
		startIndexQuery.setRows(MAX_ROWS);
		//compare the manually-created query against the one modified by the method we're testing
		query = new SolrQuery();
		factory.addInvariantTerms(query, START_INDEX, MAX_ROWS, searchConfig);
		
		assertTrue(query.toString().contains(rowsQuery.toString()));		
	}
	
	/**
	 * Test that filters are added to the solr query.
	 */
	@Test
	public void testAddFiltersToQuery() {
		context.checking(new Expectations() {
			{
				oneOf(categoryService).findCodeByUid(CATEGORY_UID); will(returnValue(CATEGORY_CODE));
			}
		});
		
		SolrQuery query = new SolrQuery();
		
		this.solrQueryFactoryImpl.addFiltersToQuery(query, searchCriteria);
		// check that filters are added
		Matcher matcher = Pattern.compile("fq=").matcher(query.toString());
		int numMatches = 0;
		while (matcher.find()) {
			++numMatches;
		}
		assertEquals(filterQueries.size(), numMatches);
	}

	
	/**
	 * Test that dummy filters are ignored.
	 */
	@Test
	public void testAddDummyFilterToQuery() {
		SolrQuery query = new SolrQuery();
		AdvancedSearchFilteredNavSeparatorFilter dummyFilter = new AdvancedSearchFilteredNavSeparatorFilterImpl();
		dummyFilter.initialize("");
		final List<Filter<AdvancedSearchFilteredNavSeparatorFilter>> filters 
			= new ArrayList<Filter<AdvancedSearchFilteredNavSeparatorFilter>>();
		filters.add(dummyFilter);
		
		final CatalogAwareSearchCriteria searchCriteria = context.mock(CatalogAwareSearchCriteria.class, "criteriaForDummy");
		context.checking(new Expectations() {
			{
				allowing(searchCriteria).getFilters(); will(returnValue(filters));
			}
		});
		
		this.solrQueryFactoryImpl.addFiltersToQuery(query, searchCriteria);

		assertNull(query.getFilterQueries());
	}	

	/**
	 * Test that real filters are added to the query.
	 */
	@Test
	public void testAddRealFilterToQuery() {
		context.checking(new Expectations() {
			{
				oneOf(brandService).findByCode("F00001"); will(returnValue(new BrandImpl() {
					private static final long serialVersionUID = 1L;
					
					@Override
					public String getCode() {
						return "F00001";
					}
					@Override
					public LocalizedProperties getLocalizedProperties() {
						return null;
					}
				}));
				oneOf(categoryService).findCodeByUid(CATEGORY_UID); will(returnValue(CATEGORY_CODE));
			}
		});
		
		SolrQuery query = new SolrQuery();
		
		BrandFilter  brandFilter = new BrandFilterImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			protected BrandService getBrandService() {
				return brandService;
			}				
		};
		brandFilter.initialize("bF00001");
		List<Filter<BrandFilter>> filters = new ArrayList<Filter<BrandFilter>>();
		filters.add(brandFilter);
		
		this.solrQueryFactoryImpl.addFiltersToQuery(query, searchCriteria);

		assertNotNull(query.getFilterQueries());		
	}		
	
	/**
	 * Test method for
	 * {@link SolrQueryFactoryImpl#composeKeywordQuery(KeywordSearchCriteria, int, int, SearchConfig)}.
	 */
	@Test
	public void testComposeKeywordQuery() {
		// check that solr specific terms are added
		context.checking(new Expectations() {
			{
				oneOf(attributeService).getProductAttributes(); will(returnValue(new ArrayList<Attribute>()));
				oneOf(searchConfig).getExclusiveAttributes(); will(returnValue(null));
				allowing(searchConfig).getBoostValue(with(any(String.class))); will(returnValue(1.0F));
				oneOf(searchConfigFactory).getSearchConfig("product"); will(returnValue(searchConfig));
				oneOf(searchConfig).getMaxReturnNumber(); will(returnValue(0));
				oneOf(categoryService).findCodeByUid(CATEGORY_UID); will(returnValue(CATEGORY_CODE));
			}
		});
		keywordSearchCriteria.setCatalogCode("catalogCode1");
		solrQueryFactoryImpl.setSettingsReader(new CachedSettingsReaderImpl() {
			@Override
			public SettingValue getSettingValue(final String path) {
				return new SettingValueImpl() {
					private static final long serialVersionUID = 1L;

					@Override
					@SuppressWarnings("PMD.BooleanGetMethodName")
					public boolean getBooleanValue() {
						return false;
					}
				};
			}
			
			@Override
			public SettingValue getSettingValue(final String path, final String context) {
				return new SettingValueImpl() {
					private static final long serialVersionUID = 1L;

					@Override
					@SuppressWarnings("PMD.BooleanGetMethodName")
					public boolean getBooleanValue() {
						return false;
					}
				};
			}
			
			
		});
		SolrQuery query = solrQueryFactoryImpl.composeKeywordQuery(keywordSearchCriteria, START_INDEX, MAX_ROWS, searchConfig, false);

		SolrQuery startIndexQuery = new SolrQuery();
		startIndexQuery.setStart(START_INDEX);
		assertTrue(query.toString().contains(startIndexQuery.toString()));

		SolrQuery rowsQuery = new SolrQuery();
		rowsQuery.setRows(MAX_ROWS);
		assertTrue(query.toString().contains(rowsQuery.toString()));
		assertTrue(query.toString().contains(CommonParams.QT));
		assertTrue(query.toString().contains(CommonParams.SORT));
		
		// check that filters are added
		Matcher matcher = Pattern.compile("fq=").matcher(query.toString());
		int numMatches = 0;
		while (matcher.find()) {
			++numMatches;
		}
		// the catalog brings one more filter query
		assertEquals(filterQueries.size() + 1, numMatches);
	}
	
	/**
	 * Test that addCategoryFilter adds a CategoryFilter to the list of filter queries if 
	 * the search criteria contains a categoryUid.
	 */
	@Test
	public void testAddCategoryFilter() {

		final CategoryFilter categoryFilter = context.mock(CategoryFilter.class);
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean(ContextIdNames.CATEGORY_FILTER); will(returnValue(categoryFilter));
				oneOf(category).setUidPk(1L);
				oneOf(categoryFilter).setCategory(category);
			}
		});
		
		List<Filter< ? >> filterQueries = new ArrayList<Filter< ? >>();
		KeywordSearchCriteria searchCriteria = new KeywordSearchCriteria();
		searchCriteria.setCategoryUid(1L);
		solrQueryFactoryImpl.addCategoryFilter(filterQueries, searchCriteria);

		boolean containsCategoryFilter = false;
		for (Filter< ? > filter : filterQueries) {
			if (filter instanceof CategoryFilter) {
				containsCategoryFilter = true;
			}
		}
		assertTrue(containsCategoryFilter);
	}
	
	/**
	 * Test method for {@link SolrQueryFactoryImpl#composeSpellingQuery(String, SearchConfig)}.
	 */
	@Test
	public void testComposeSpellingQuery() {
		final Set<String> misspellings = new HashSet<String>(Arrays.asList("some", "user"));
		final Locale locale = Locale.US;
		context.checking(new Expectations() {
			{
				exactly(2).of(searchConfig).getAccuracy(); will(returnValue(0F));
				exactly(2).of(searchConfig).getMaximumSuggestionsPerWord(); will(returnValue(0));
				oneOf(spellSuggestionSearchCriteria).getPotentialMisspelledStrings(); will(returnValue(misspellings));
			}
		});
		
		SolrQuery query = solrQueryFactoryImpl.composeSpellingQuery(spellSuggestionSearchCriteria, searchConfig);
		SolrQuery testQuery = new SolrQuery();
		for (String str : misspellings) {
			testQuery.setQuery(str);
			assertTrue(query.toString().contains(testQuery.toString()));
		}

		testQuery = new SolrQuery();
		testQuery.set(SpellingParams.ACCURACY, String.valueOf(searchConfig.getAccuracy()));
		assertTrue(query.toString().contains(testQuery.toString()));

		testQuery = new SolrQuery();
		testQuery.set(SpellingParams.NUM_SUGGESTIONS, String.valueOf(searchConfig.getMaximumSuggestionsPerWord()));
		assertTrue(query.toString().contains(testQuery.toString()));

		assertTrue(query.toString().contains(CommonParams.QT));
		
		testQuery = new SolrQuery();
		testQuery.set(SpellingParams.LOCALE, locale.toString());
		assertTrue(query.toString().contains(testQuery.toString()));
	}
	
	/**
	 * Test method that checks to see if a match all query is handled correctly. The output should
	 * produce a query that is able to be parsed by the query parser.
	 * @throws ParseException in case of any errors
	 */
	@Test
	public void testMatchAllQueryDocsQuery() throws ParseException {
		context.checking(new Expectations() {
			{
				oneOf(searchConfig).getMaxReturnNumber(); will(returnValue(0));
				oneOf(categoryService).findCodeByUid(CATEGORY_UID); will(returnValue(CATEGORY_CODE));
				oneOf(queryComposer).composeQuery(searchCriteria, searchConfig); will(returnValue(new MatchAllDocsQuery()));
			}
		});

		SolrQuery query = solrQueryFactoryImpl.composeSpecificQuery(queryComposer, searchCriteria, START_INDEX, MAX_ROWS,
				searchConfig, false);
		
		final QueryParser queryParser = new QueryParser(Version.LUCENE_24, "", new SimpleAnalyzer());
		assertNotNull(query.get(CommonParams.QT));
		queryParser.parse(query.get(CommonParams.QT));
	}
	
	/**
	 * Test that the query for date range rounds up to the minute.
	 */
	@Test
	public void testCreateTermsForStartEndDateRange() {
		final Analyzer analyzer = context.mock(Analyzer.class, "dateAnalyzer");
		final Calendar calendar = new GregorianCalendar(2012, 9, 3, 8, 12, 23);
		final Calendar roundedCalendar = new GregorianCalendar(2012, 9, 3, 8, 13);
		solrQueryFactoryImpl.setAnalyzer(analyzer);
		context.checking(new Expectations() {
			{
				oneOf(analyzer).analyze(roundedCalendar.getTime()); will(returnValue("ROUNDED_DATE"));
			}
		});
		BooleanQuery query = solrQueryFactoryImpl.createTermsForStartEndDateRange(calendar.getTime());
		assertEquals("+startDate:[* TO ROUNDED_DATE] -endDate:[* TO ROUNDED_DATE]", query.toString());
	}
}
