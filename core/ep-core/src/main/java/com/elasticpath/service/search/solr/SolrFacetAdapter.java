/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.search.solr;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.util.CollectionUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.DisplayableFilter;
import com.elasticpath.domain.catalogview.FeaturedProductFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.domain.catalogview.RangeFilterType;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationLoader;
import com.elasticpath.service.search.CatalogAwareSearchCriteria;
import com.elasticpath.service.search.StoreAwareSearchCriteria;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SearchHint;
import com.elasticpath.settings.SettingsReader;

/**
 * Converts between EP's filters and Solr's facets and back again.
 */
public class SolrFacetAdapter {
	private static final Logger LOG = Logger.getLogger(SolrFacetAdapter.class);
	
	private static final String DEFAULT_RANGE_QUERY = "*";

	private final Map<String, Filter< ? >> filterLookup = new HashMap<String, Filter< ? >>();
		
	private FilteredNavigationConfigurationLoader fncLoader;
	
	private IndexUtility indexUtility;
	
	private Analyzer analyzer;
	
	private SettingsReader settingsReader;

	private CategoryService categoryService;

	/**
	 * Gets the <code>FilteredNavigationConfiguration</code> for the <code>Store</code>
	 * with the given StoreCode, using this instance's <code>FilteredNavigationConfigurationLoader</code>.
	 * @param storeCode the code the store whose filtered navigation configuration should be retrieved
	 * @return the FilteredNavigationConfiguration object for Store with the given StoreCode
	 */
	FilteredNavigationConfiguration getFilteredNavigationConfiguration(final String storeCode) {
		return getFncLoader().loadFilteredNavigationConfiguration(storeCode);
	}
	
	/**
	 * Adds the configured facets to the specified query.
	 * 
	 * @param query the query to add the faceting information to.
	 * @param searchCriteria the Locale, Currency, and StoreCode are retrieved from here.
	 */
	public void addFacets(final SolrQuery query, final SearchCriteria searchCriteria) {
		LOG.debug("Adding facets to SOLR query.");
		if (searchCriteria instanceof StoreAwareSearchCriteria) {
			StoreAwareSearchCriteria criteria = (StoreAwareSearchCriteria) searchCriteria;
			String storeCode = criteria.getStoreCode();
		
			if (isAttributeFilterEnabled(storeCode)) {
				addAttributeRangeFacets(query, criteria.getLocale(), storeCode);
				addAttributeFacets(query, criteria.getLocale(), storeCode);
			}
		}
		if (searchCriteria instanceof CatalogAwareSearchCriteria) {
			CatalogAwareSearchCriteria criteria = (CatalogAwareSearchCriteria) searchCriteria;
			String catalogCode = criteria.getCatalogCode();
			addCategoryFacets(query, catalogCode);
		}

		addPriceFacets(query, searchCriteria.getCurrency(), 
				getStoreCodeFromSearchCriteria(searchCriteria), 
				getCatalogCodeFromSearchCriteria(searchCriteria),
				getPriceListStackFromSearchCriteria(searchCriteria));
		addBrandFacets(query);
	}
	
	/**
	 *
	 * @param searchCriteria
	 * @return
	 */
	private List<String> getPriceListStackFromSearchCriteria(final SearchCriteria searchCriteria) {
		SearchHint<PriceListStack> priceListStackHint = searchCriteria.getSearchHint("priceListStack");
		if (priceListStackHint != null) {
			return priceListStackHint.getValue().getPriceListStack();
		}
		return Collections.emptyList();
	}

	/**
	 * @param storeCode the code for the store in which to check whether attribute filters are enabled.
	 * @return true if attribute filtering is enabled in the given store, false if not.
	 */
	boolean isAttributeFilterEnabled(final String storeCode) {
		return getSettingsReader().getSettingValue("COMMERCE/STORE/FILTEREDNAVIGATION/attributeFilterEnabled", storeCode).getBooleanValue();
	}
	
	/**
	 * Constructs a Lucene {@link Query} from the given {@link Filter}.
	 *
	 * @param filter the filter
	 * @param searchCriteria the search criteria
	 * if the filter being constructed is not for a store-specific search field (e.g. Price).
	 * @return a {@link Query}
	 */
	public String constructFilterQuery(final Filter< ? > filter, final SearchCriteria searchCriteria) {
		String queries = null;
		
		if (filter instanceof PriceFilter) {
			final String catalogCode = getCatalogCodeFromSearchCriteria(searchCriteria);
			return getPriceQueryForStack((PriceFilter) filter, catalogCode, getPriceListStackFromSearchCriteria(searchCriteria)).toString();
			
		} else if (filter instanceof AttributeRangeFilter) {
			queries = constructAttributeRangeFilterQuery((AttributeRangeFilter) filter);
			
		} else if (filter instanceof BrandFilter) {
			queries = constructBrandFilterQuery((BrandFilter) filter).toString();
			
		} else if (filter instanceof AttributeValueFilter) {
			queries = constructAttributeValueFilterQuery((AttributeValueFilter) filter, true).toString();
			
		} else if (filter instanceof CategoryFilter) {
			final String catalogCode = getCatalogCodeFromSearchCriteria(searchCriteria);
			queries = constructCategoryFilterQuery((CategoryFilter) filter, catalogCode).toString();
			
		} else if (filter instanceof FeaturedProductFilter) {
			final String catalogCode = getCatalogCodeFromSearchCriteria(searchCriteria);
			queries = constructFeaturedProductFilterQuery((FeaturedProductFilter) filter, catalogCode).toString();
			
		} else if (filter instanceof DisplayableFilter) {
			//TODO: investigate whether displayableFilter use the store code that's passed in.
			queries = constructDisplayableOnlyFilterQuery((DisplayableFilter) filter).toString();
			
		} else if (filter instanceof AdvancedSearchFilteredNavSeparatorFilter) {
			queries = "";
		}
		
		if (queries == null) {
			throw new EpSystemException("Unimplemented filter type: " + filter.getClass());
		}
		
		return queries;
	}
	
	/**
	 * Get the catalog code from the given search criteria in case it is of type
	 * {@link CatalogAwareSearchCriteria}.
	 * 
	 * @param searchCriteria the search criteria
	 * @return the catalog code or null if criteria does not support catalog codes
	 */
	String getCatalogCodeFromSearchCriteria(final SearchCriteria searchCriteria) {
		if (searchCriteria instanceof CatalogAwareSearchCriteria) {
			return ((CatalogAwareSearchCriteria) searchCriteria).getCatalogCode();
		}
		return null;
	}

	/**
	 * Get the store code from the given search criteria, if it's a type
	 * of SearchCriteria which has a store code.
	 * 
	 * @param searchCriteria the criteria
	 * @return the store code if available, else null
	 */
	String getStoreCodeFromSearchCriteria(final SearchCriteria searchCriteria) {
		if (searchCriteria instanceof StoreAwareSearchCriteria) {
			return ((StoreAwareSearchCriteria) searchCriteria).getStoreCode();
		}
		return null;
	}

	/**
	 * Gets the map of query filters. This is a map of Solr queries to filters.
	 *
	 * @return the map of query to filters
	 */
	public Map<String, Filter< ? >> getFilterLookupMap() {
		return filterLookup;
	}

	/**
	 * Adds Price facets to the given query.
	 * @param query the query
	 * @param currency the currency of the prices
	 * @param storeCode the code for the store in which the price facets are valid
	 * @param catalogCode the code for the catalog in which the price facets are valid
	 * @param priceListGuids the price list guids ordered by priority
	 */
	void addPriceFacets(final SolrQuery query, final Currency currency, final String storeCode, 
			final String catalogCode, final List<String> priceListGuids) {
		LOG.debug("Adding Price Facets to query.");
		if (CollectionUtils.isEmpty(priceListGuids)) {
			LOG.debug("No price list stack is defined.");
			return;
		}
		Collection<PriceFilter> priceFilters = 
			getFilteredNavigationConfiguration(storeCode).getAllPriceRanges().values();
		for (PriceFilter priceFilter : priceFilters) {
			// don't want filters for currencies we aren't searching for
			if (!priceFilter.getCurrency().equals(currency)) {
				continue;
			}
			// remove filters that do nothing (filter then filters nothing)
			if (priceFilter.getRangeType() == RangeFilterType.ALL) {
				continue;
			}
			String luceneQuery = getPriceQueryForStack(priceFilter, catalogCode, priceListGuids).toString();
			filterLookup.put(luceneQuery, priceFilter);
			query.addFacetQuery(luceneQuery);
		}
	}

	/**
	 * Gets a price query for the given price list stack.
	 * @param priceFilter the filter to use
	 * @param catalogCode the catalog code
	 * @param stackGuids the guids of the price lists on the stack
	 * @return a facet query string
	 */
	protected Query getPriceQueryForStack(final PriceFilter priceFilter, final String catalogCode, final List<String> stackGuids) {
		if (stackGuids == null || stackGuids.isEmpty()) {
			return new BooleanQuery();
		}
		String priceListGuid = stackGuids.get(0);
		Query facetForPriceList = constructPriceFilterQuery(priceFilter, catalogCode, priceListGuid);
		if (stackGuids.size() == 1) {
			return facetForPriceList;
		}
		BooleanQuery facetQuery = new BooleanQuery();
		facetQuery.add(facetForPriceList, BooleanClause.Occur.SHOULD);
		
		BooleanQuery subQuery = new BooleanQuery();
		String fieldName = getIndexUtility().createPriceFieldName(SolrIndexConstants.PRICE, catalogCode, priceListGuid);
		final Query excludeQuery = new ConstantScoreRangeQuery(fieldName, DEFAULT_RANGE_QUERY, DEFAULT_RANGE_QUERY, true, true);
		subQuery.add(excludeQuery, BooleanClause.Occur.MUST_NOT);
		subQuery.add(getPriceQueryForStack(priceFilter, catalogCode, stackGuids.subList(1, stackGuids.size())), BooleanClause.Occur.MUST);
		facetQuery.add(subQuery, BooleanClause.Occur.SHOULD);
		return facetQuery;
	}

	/**
	 * Adds attribute range facets.
	 * @param query solr query
	 * @param locale locale
	 * @param storeCode store code
	 */
	void addAttributeRangeFacets(final SolrQuery query, final Locale locale, final String storeCode) {
		LOG.debug("Adding AttributeRange Facets to query.");
		Collection<AttributeRangeFilter> attrRangeFilters = 
			this.getFilteredNavigationConfiguration(storeCode).getAllAttributeRanges().values();
		for (AttributeRangeFilter attrRangeFilter : attrRangeFilters) {
			// don't want filters for locales we aren't searching for
			if (attrRangeFilter.getAttribute().isLocaleDependant() && !attrRangeFilter.getLocale().equals(locale)) {
				continue;
			}
			// remove filters that do nothing (filter then filters nothing)
			if (attrRangeFilter.getRangeType() == RangeFilterType.ALL) {
				continue;
			}
			final String luceneQuery = constructAttributeRangeFilterQuery(attrRangeFilter);
			filterLookup.put(luceneQuery, attrRangeFilter);
			query.addFacetQuery(luceneQuery);
		}
	}
	
	private void addAttributeFacets(final SolrQuery query, final Locale locale, final String storeCode) {
		LOG.debug("Adding AttributeValue Facets to query.");
		Collection<AttributeValueFilter> attrValueFilters = retrieveAttributeValueFilters(storeCode);
		for (AttributeValueFilter attrValueFilter : attrValueFilters) {
			// remove the filters used as unique keys - they don't have attribute values
			if (attrValueFilter.getAttributeValue() == null) {
				continue;
			}
			// don't want to filter for locales we aren't searching for
			if (attrValueFilter.isLocalized()
					&& !attrValueFilter.getLocale().equals(locale)) {
				continue;
			}
			
			final Query luceneQuery = constructAttributeValueFilterQuery(attrValueFilter, true);
			filterLookup.put(luceneQuery.toString(), attrValueFilter);
			query.addFacetQuery(luceneQuery.toString());
		}
	}

	/**
	 * Retrieves a collection of {@link AttributeValueFilter}s consisting of all simple values defined in
	 * the {@link FilteredNavigationConfiguration} for a given store.
	 * @param storeCode the store
	 * @return the collection of {@link AttributeValueFilter}s
	 */
	Collection<AttributeValueFilter> retrieveAttributeValueFilters(
			final String storeCode) {
		Collection<AttributeValueFilter> attrValueFilters = 
			this.getFilteredNavigationConfiguration(storeCode).getAllAttributeSimpleValues().values();
		return attrValueFilters;
	}
	
	/**
	 * Adds the Brand facets to the given query.
	 * @param query the query
	 */
	void addBrandFacets(final SolrQuery query) {
		query.addFacetField(SolrIndexConstants.BRAND_CODE_NON_LC);
	}

	/** 
	 * Adds the Category facets to the given query.
	 * @param query the query
	 * @param catalogCode the catalog code
	 */
	void addCategoryFacets(final SolrQuery query, final String catalogCode) {
		// we don't want facets here for the parent category UIDs
		query.addFacetField(getIndexUtility().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY_NON_LC, catalogCode));
	}
	
	private Query constructPriceFilterQuery(final PriceFilter filter, final String catalogCode, final String priceListGuid) {
		if (priceListGuid == null) {
			throw new EpServiceException("The price list guid cannot be null to filter on Price");
		}
		if (catalogCode == null) {
			throw new EpServiceException("The catalog code cannot be null to filter on Price");
		}

		final String fieldName = getIndexUtility().createPriceFieldName(SolrIndexConstants.PRICE, catalogCode, priceListGuid);
		return constructRangeFilterQuery(filter, fieldName);
	}
	
	private String constructAttributeRangeFilterQuery(final AttributeRangeFilter filter) {
		final String fieldName = getIndexUtility().createAttributeFieldName(filter.getAttribute(), filter.getLocale(), false, true);
		return constructRangeFilterQuery(filter, fieldName).toString();
	}
	
	/**
	 * Construct a range query.
	 * 
	 * @param filter The filter's range type property to use, such as BETWEEN, LT or MT.
	 * @param fieldName The field to query.
	 * @return The range query.
	 */
	Query constructRangeFilterQuery(final RangeFilter< ? , ? > filter, final String fieldName) {

		switch (filter.getRangeType()) {
		case BETWEEN:
			String lowerValue = filter.getLowerValue().toString();
			String upperValue = filter.getUpperValue().toString();
			
			BooleanQuery outerQuery = new BooleanQuery();			
			outerQuery.add(new TermQuery(new Term(fieldName, lowerValue)), Occur.SHOULD);
			outerQuery.add(new ConstantScoreRangeQuery(fieldName, lowerValue, upperValue, false, false), Occur.SHOULD);
			return outerQuery;
		case LESS_THAN:
			upperValue = filter.getUpperValue().toString();
			return new ConstantScoreRangeQuery(fieldName, DEFAULT_RANGE_QUERY, upperValue, false, false);
		case MORE_THAN:
			lowerValue = filter.getLowerValue().toString();
			return new ConstantScoreRangeQuery(fieldName, lowerValue, DEFAULT_RANGE_QUERY, true, true);
		default:
			break;
		}

		return null;
	}
	
	/**
	 * Construct a query out of the given brand filter.
	 * 
	 * @param filter the brand filter.
	 * @return the query.
	 */
	protected Query constructBrandFilterQuery(final BrandFilter filter) {
		if (filter != null && filter.getBrands() != null && !filter.getBrands().isEmpty()) {
			Iterator<Brand> iterator = filter.getBrands().iterator();
			final BooleanQuery query = new BooleanQuery();
			while (iterator.hasNext()) {
				Brand brand = iterator.next();
				TermQuery term = 
					new TermQuery(new Term(SolrIndexConstants.BRAND_CODE, getAnalyzer().analyze(brand.getCode())));
				query.add(term, Occur.SHOULD);
			}
			return query;
		}
		return null;
	}

	/**
	 * Constructs attribute value query.
	 * 
	 * @param filter filter
	 * @param faceting true if faceting is used
	 * @return constructed query
	 */
	Query constructAttributeValueFilterQuery(final AttributeValueFilter filter, final boolean faceting) {
		return new TermQuery(new Term(indexUtility.createAttributeFieldName(filter.getAttribute(), filter.getLocale(), faceting,
				faceting), analyzer.analyze(filter.getAttributeValue().getStringValue(), true)));
	}
	
	private Query constructCategoryFilterQuery(final CategoryFilter filter, final String catalogCode) {
		if (catalogCode == null) {
			throw new EpServiceException("The catalog code cannot be null to filter on Category");
		}

		final String categoryCode = this.getCategoryCodeFromCategoryUid(filter.getCategory().getUidPk());
		
		final BooleanQuery query = new BooleanQuery();
		final Query currentCategoryQuery = new TermQuery(
				new Term(getIndexUtility().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY, catalogCode), 
				getAnalyzer().analyze(categoryCode)));
		final Query parentCategoryQuery = new TermQuery(new Term(SolrIndexConstants.PARENT_CATEGORY_CODES, 
				getAnalyzer().analyze(categoryCode)));

		query.add(currentCategoryQuery, Occur.SHOULD);
		query.add(parentCategoryQuery, Occur.SHOULD);

		return query;
	}
	
	private Query constructFeaturedProductFilterQuery(final FeaturedProductFilter filter, final String catalogCode) {
		if (catalogCode == null) {
			throw new EpServiceException("The store code cannot be null to filter on featured product");
		}
		
		final BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term(SolrIndexConstants.FEATURED, String.valueOf(true))), Occur.MUST);
		
		if (filter.getCategoryUid() != null && filter.getCategoryUid() > 0) {
			final BooleanQuery innerQuery = new BooleanQuery();
			
			// case 1 - not featured in this category, but it's featured in a sub-category
			final BooleanQuery innerSubQuery = new BooleanQuery();
			innerSubQuery.add(new TermQuery(
					new Term(getIndexUtility().createFeaturedField(filter.getCategoryUid()), String.valueOf(0))), Occur.MUST);
			final String categoryCode = getCategoryCodeFromCategoryUid(filter.getCategoryUid());
			
			innerSubQuery.add(new TermQuery(new Term(SolrIndexConstants.PARENT_CATEGORY_CODES, categoryCode)), Occur.MUST);
			
			// case 2 - it is featured in this category and apart of this category
			final BooleanQuery innerSubQuery2 = new BooleanQuery();
			innerSubQuery2.add(new TermQuery(new Term(getIndexUtility().createFeaturedField(filter.getCategoryUid()), String
					.valueOf(0))), Occur.MUST_NOT);
			// have to explicitly say it's in this category or in combination with the other inner
			// query will give invalid results
			innerSubQuery2.add(new TermQuery(
					new Term(getIndexUtility().createProductCategoryFieldName(SolrIndexConstants.PRODUCT_CATEGORY, catalogCode), 
					categoryCode)),
					Occur.MUST);
			
			innerQuery.add(innerSubQuery, Occur.SHOULD);
			innerQuery.add(innerSubQuery2, Occur.SHOULD);
			query.add(innerQuery, Occur.MUST);
		}
		
		return query;
	}
	
	/**
	 * Gets the category code given a category UID.
	 * @param categoryUid the category UID
	 * @return the category code from the category specified in the search criteria
	 */
	private String getCategoryCodeFromCategoryUid(final long categoryUid) {
		return getCategoryService().findCodeByUid(categoryUid);
	}
	
	private Query constructDisplayableOnlyFilterQuery(final DisplayableFilter filter) {
		BooleanQuery booleanQuery = new BooleanQuery();
		
		Query termQuery = new TermQuery(
				new Term(getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, filter.getStoreCode()), String.valueOf(true)));
		
		booleanQuery.add(termQuery, Occur.MUST);
		
		return booleanQuery;
	}
	
	/**
	 * Sets the {@link SettingsReader} instance to use.
	 *
	 * @param settingsReader the {@link SettingsReader} instance to use
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Sets the {@link CategoryService} instance to use.
	 *
	 * @param categoryService the {@link CategoryService} instance to use
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}	
	
	/**
	 * Sets the {@link IndexUility} instance to use.
	 *
	 * @param indexUtility the {@link IndexUility} instance to use
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}
	
	/**
	 * Return the {@link IndexUtility} instance.
	 * @return the index utility instance
	 */
	protected IndexUtility getIndexUtility() {
		return indexUtility;
	}
	
	/**
	 * Sets the {@link Analyzer} instance to use.
	 *
	 * @param analyzer the {@link Analyzer} instance to use
	 */
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}
	
	/**
	 * Return the {@link Analyzer} instance.
	 * @return the analyzer instance
	 */
	protected Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * @return the fncLoader
	 */
	public FilteredNavigationConfigurationLoader getFncLoader() {
		return fncLoader;
	}

	/**
	 * @param fncLoader the fncLoader to set
	 */
	public void setFncLoader(final FilteredNavigationConfigurationLoader fncLoader) {
		this.fncLoader = fncLoader;
	}	
	
	/**
	 * @return the catagory service
	 */
	protected CategoryService getCategoryService() {
		return categoryService;
	}
	
	/**
	 * @return the settings reader
	 */
	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}
}
