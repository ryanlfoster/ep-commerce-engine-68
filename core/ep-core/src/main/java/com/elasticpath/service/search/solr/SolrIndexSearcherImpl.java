/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.search.solr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.SolrParams;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.FilterOption;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.StoreAwareSearchCriteria;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.index.QueryComposerFactory;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.query.SolrIndexSearchResult;
import com.elasticpath.service.store.StoreService;

/**
 * Data access for the SOLR index.
 */
public class SolrIndexSearcherImpl {

	private static final Logger LOG = Logger.getLogger(SolrIndexSearcherImpl.class);
	
	private static final Pattern RANGE_REGEX_PATTERN = Pattern
			.compile("[\\[\\{](?:\\w+|\\d+\\.\\d+|\\*)\\s+TO\\s+(?:\\w+|\\d+\\.\\d+|\\*)[\\]\\}]");

	private SolrProvider solrProvider;

	private SolrQueryFactory solrQueryFactory;
	
	private ElasticPath elasticPath;
	
	private FilterFactory filterFactory;
	
	private SolrFacetAdapter solrFacetAdapter;
	
	private QueryComposerFactory queryComposerFactory;

	private IndexUtility indexUtility;
	
	private final Map<String, Catalog> storeCodeToCatalogCodeMap = new ConcurrentHashMap<String, Catalog>();

	private boolean retrieveCatalogFromCache = true;

	/**
	 * Searches an index (determined by the type of the search criteria).
	 * The results are only those on the first page.
	 * 
	 * @param searchCriteria the search criteria
	 * @param maxResults the maximum results to return (maximum per page).
	 * @param searchResult the search result object to populate.
	 */
	public void search(final SearchCriteria searchCriteria, final int maxResults, final SolrIndexSearchResult searchResult) {
		search(searchCriteria, 0, maxResults, searchResult);
	}

	/**
	 * Searches an index (determined by the type of the search criteria)
	 * starting from the specified index. The number of results is limited
	 * by the specified <code>maxResults</code>. The results are only those on the first page.
	 * The given SolrIndexSearchResult is populated with <code>FilterOption</code>s corresponding to
	 * the facets that are returned as part of the SOLR search results (the resultant facets are dependent
	 * upon the filters that are set in the search criteria).
	 * 
	 * @param searchCriteria the search criteria
	 * @param startIndex the initial index to display results for
	 * @param maxResults the maximum results to return from given start index (maximum per page).
	 * @param searchResult the search result object to populate.
	 */
	public void search(final SearchCriteria searchCriteria, final int startIndex, final int maxResults,
			final SolrIndexSearchResult searchResult) {
		final SearchConfig searchConfig = solrProvider.getSearchConfig(searchCriteria.getIndexType());

		SolrQuery solrQuery;
		if (searchCriteria instanceof KeywordSearchCriteria) {
			solrQuery = solrQueryFactory.composeKeywordQuery((KeywordSearchCriteria) searchCriteria, startIndex, maxResults,
					searchConfig, false);
		} else {
			final QueryComposer queryComposer = queryComposerFactory.getComposerForCriteria(searchCriteria);
			solrQuery = solrQueryFactory.composeSpecificQuery(queryComposer, searchCriteria, startIndex, maxResults,
					searchConfig, false);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Generated query: " + solrQuery.toString());
		}

		// get the SOLR server and do the search
		final SolrServer server = solrProvider.getServer(searchCriteria.getIndexType());
		doUidSearch(server, solrQuery, searchResult, searchCriteria);

		// If we don't have any results try again with a 'fuzzy search'
		if (searchResult.getLastNumFound() == 0 && !searchCriteria.isFuzzySearchDisabled()) {
			if (searchCriteria instanceof KeywordSearchCriteria) {
				solrQuery = solrQueryFactory.composeKeywordQuery((KeywordSearchCriteria) searchCriteria, startIndex, maxResults,
						searchConfig, true);
			} else {
				final QueryComposer queryComposer = queryComposerFactory.getComposerForCriteria(searchCriteria);
				solrQuery = solrQueryFactory.composeSpecificQuery(queryComposer, searchCriteria, startIndex, maxResults,
						searchConfig, true);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Generated fuzzy query: " + solrQuery.toString());
			}
			doUidSearch(server, solrQuery, searchResult, searchCriteria);
		}
		
		// make sure the results numFound doesn't exceed the max return number
		if (searchConfig.getMaxReturnNumber() > 0) {
			searchResult.setNumFound(Math.min(searchConfig.getMaxReturnNumber(), searchResult.getLastNumFound()));
		}
	}

	/**
	 * Searches the given SOLR server with the given query and parses the response,
	 * using it to populate the given <code>SearchResult</code>. The SearchResult will contain a list of UIDs
	 * matching the given query. If the search is initiated by a store-aware search criteria then the response
	 * document may have a store-specific facet to parse, otherwise any store-specific facets will be assumed not
	 * to exist and will not be parsed.
	 * 
	 * @param server the SOLR server to search
	 * @param query the query to search with
	 * @param searchResult the search result to modify
	 * @param storeAwareSearchCriteria the store-aware search criteria initiating the search. Can be null if the
	 * initiating search critiera is not store-aware
	 */
	private void doUidSearch(final SolrServer server, final SolrParams query, final SolrIndexSearchResult searchResult, 
			final SearchCriteria storeAwareSearchCriteria) {
		try {
			final QueryRequest queryRequest = new QueryRequest(query);
			queryRequest.setMethod(METHOD.POST);
			final QueryResponse response = queryRequest.process(server);
			parseResponseDocument(response, searchResult, storeAwareSearchCriteria);
		} catch (final SolrServerException e) {
			if (server instanceof CommonsHttpSolrServer) {
				LOG.error("Error executing search. Solr Manager url : " + ((CommonsHttpSolrServer) server).getBaseURL(), e);
			}
			throw new EpPersistenceException("Solr exception executing search", e);
		}
	}

	private void parseResponseDocument(final QueryResponse response, final SolrIndexSearchResult searchResult, 
			final SearchCriteria storeAwareSearchCriteria) {
		List<Long> objectUidList = Collections.emptyList();
		if (response.getResults() == null) {
			searchResult.setNumFound(0);
			searchResult.setResultUids(objectUidList);
			return;
		}
		searchResult.setNumFound((int) response.getResults().getNumFound());
		objectUidList = new ArrayList<Long>(response.getResults().size());
		for (final SolrDocument document : response.getResults()) {
			objectUidList.add((Long) document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		}
		searchResult.setResultUids(objectUidList);
		
		if (!searchResult.isRememberOptions()) {
			parseFacetInformation(response, searchResult, storeAwareSearchCriteria);
		}
	}
	
	private void parseFacetInformation(final QueryResponse response, final SolrIndexSearchResult searchResult, 
			final SearchCriteria storeAwareSearchCriteria) {
		if (storeAwareSearchCriteria instanceof StoreAwareSearchCriteria) {
			parseCategoryFacets(response, searchResult, ((StoreAwareSearchCriteria) storeAwareSearchCriteria).getStoreCode());
		}
		parseBrandFacets(response, searchResult);
		
		// other facets
		final Map<String, Integer> facetQueries = response.getFacetQuery();
		if (facetQueries != null) {
			parseFacetQueries(searchResult, facetQueries);
		}
	}

	/**
	 * Parses facet queries for attribute values, attribute ranges or price values.
	 * @param searchResult search result
	 * @param facetQueries facet queries
	 */
	void parseFacetQueries(final SolrIndexSearchResult searchResult,
			final Map<String, Integer> facetQueries) {
		final List<FilterOption<PriceFilter>> priceFilterOptions = new ArrayList<FilterOption<PriceFilter>>(facetQueries.size());
		final List<FilterOption<AttributeValueFilter>> attributeValueFilterOptions = new ArrayList<FilterOption<AttributeValueFilter>>(
				facetQueries.size());
		final List<FilterOption<AttributeRangeFilter>> attributeRangeFilterOptions = new ArrayList<FilterOption<AttributeRangeFilter>>();
		for (final Entry<String, Integer> entry : facetQueries.entrySet()) {
			// don't care about entries with a zero count
			if (entry.getValue() == 0) {
				continue;
			}
			if (isPriceKey(entry.getKey())) {
				final PriceFilter priceFilter = (PriceFilter) solrFacetAdapter.getFilterLookupMap().get(entry.getKey());
				priceFilterOptions.add(constructFilterOption(entry.getValue(), priceFilter));
			} else if (isAttributeRangeKey(entry.getKey())) {
				final AttributeRangeFilter rangeFilter = (AttributeRangeFilter) solrFacetAdapter.getFilterLookupMap().get(entry.getKey());
				attributeRangeFilterOptions.add(constructFilterOption(entry.getValue(), rangeFilter));
			} else if (isAttributeKey(entry.getKey())) {
				final AttributeValueFilter valueFilter = (AttributeValueFilter) solrFacetAdapter.getFilterLookupMap().get(entry.getKey());
				attributeValueFilterOptions.add(constructFilterOption(entry.getValue(), valueFilter));
			}
		}
		searchResult.setPriceFilterOptions(priceFilterOptions);
		searchResult.setAttributeValueFilterOptions(createAttributeFilterMap(attributeValueFilterOptions));
		searchResult.setAttributeRangeFilterOptions(createAttributeFilterMap(attributeRangeFilterOptions));
	}

	private boolean isPriceKey(final String key) {
		return queryStartsWith(key, SolrIndexConstants.PRICE);
	}

	private boolean isAttributeKey(final String attributeKey) {
		return attributeKey.startsWith(SolrIndexConstants.ATTRIBUTE_PREFIX);
	}

	private boolean isAttributeRangeKey(final String attributeKey) {
		final Matcher matcher = RANGE_REGEX_PATTERN.matcher(attributeKey);
		return matcher.find() && isAttributeKey(attributeKey);
	}

	/**
	 * A query starts with a {@code fieldPrefix } if the prefix is in the begging of the {@code queryString } 
	 * or the {@code queryString } starts with a bracket '('.
	 * 
	 * @param queryString the query string to check
	 * @param fieldPrefix the field prefix to look for
	 * @return true if the query starts with the given prefix
	 */
	boolean queryStartsWith(final String queryString, final String fieldPrefix) {
		final String queryToCheck = StringUtils.remove(queryString, "(");
		return queryToCheck.startsWith(fieldPrefix);
	}

	private void parseBrandFacets(final QueryResponse response, final SolrIndexSearchResult searchResult) {
		FacetField field;
		field = response.getFacetField(SolrIndexConstants.BRAND_CODE_NON_LC);
		if (field != null && field.getValueCount() > 0) {
			final List<FilterOption<BrandFilter>> filterOptions = new ArrayList<FilterOption<BrandFilter>>(field.getValueCount());
			for (final Count count : field.getValues()) {
				if (count.getCount() > 0L) {
					filterOptions.add(constructFilterOption((int) count.getCount(), filterFactory.createBrandFilter(count.getName())));
				}
			}
			searchResult.setBrandFilterOptions(filterOptions);
		}
	}

	/**
	 * Parses the given QueryResponse for a CategoryCode facet field (e.g. Digital Cameras), and if one is found, creates
	 * FilterOption objects (e.g. "SLR Cameras", "Point and Shoot Cameras") and populates the SearchResult with them.
	 * Since category filters need to know in which Catalog they're valid, and any search with Category facets is a search
	 * on a particular Store, then the store code is required so the category filter can be created on the Store's category. 
	 * 
	 * @param response the solr query response
	 * @param searchResult the search result to populate with the search details
	 * @param storeCode the code representing the store on which the query must have taken place for there to be a CategoreCode facet. This
	 * code is used to create the CategoryFilter.
	 */
	void parseCategoryFacets(final QueryResponse response, final SolrIndexSearchResult searchResult, final String storeCode) {
		if (storeCode == null) {
			LOG.warn("Cannot parse category facets without store code.");
			return;
		}
		final Catalog storeCatalog = getStoreCatalog(storeCode);

		final String productCategoryFieldName = indexUtility.createProductCategoryFieldName(
				SolrIndexConstants.PRODUCT_CATEGORY_NON_LC, storeCatalog.getCode());
		final FacetField field = response.getFacetField(productCategoryFieldName);
		
		if (field != null && field.getValueCount() > 0) {
			// if CategoryCode facet field exists, load the catalog associated with the store
			
			final List<FilterOption<CategoryFilter>> filterOptions = new ArrayList<FilterOption<CategoryFilter>>(field.getValueCount());
			for (final Count count : field.getValues()) {
				if (count.getCount() > 0L) {
					filterOptions.add(constructFilterOption((int) count.getCount(), 
					filterFactory.createCategoryFilter(count.getName(), storeCatalog)));
				}
			}
			searchResult.setCategoryFilterOptions(filterOptions);
		}
	}
	
	/**
	 * Get the <code>Catalog</code> associated with the <code>Store</code> that is represented by the given StoreCode.
	 * @param storeCode the store code
	 * @return the requested Catalog
	 */
	protected Catalog getStoreCatalog(final String storeCode) {
		Catalog catalog;
		if (isRetrieveCatalogFromCache() && getStoreCodeToCatalogCodeMap().containsKey(storeCode)) {
			catalog = getStoreCodeToCatalogCodeMap().get(storeCode);
		} else {
			final String catalogCode = getStoreService().getCatalogCodeForStore(storeCode);
			catalog = getCatalogService().findByCode(catalogCode);
			getStoreCodeToCatalogCodeMap().put(storeCode, catalog);
		}
		return catalog;
	}
	
	/**
	 * if retrieve catalog from cache.
	 * 
	 * @return true if setting is true to get catalog from cache
	 */
	public boolean isRetrieveCatalogFromCache() {
		return retrieveCatalogFromCache;
	}
	
	/**
	 * Set the value of retrieve catalog from cache.
	 * 
	 * @param retrieveCatalogFromCache the retrieve catalog from cache
	 */
	public void setRetrieveCatalogFromCache(final boolean retrieveCatalogFromCache) {
		this.retrieveCatalogFromCache = retrieveCatalogFromCache;
	}

	/**
	 * Get the store to catalog map.
	 * @return the store to catalog map.
	 */
	protected Map<String, Catalog> getStoreCodeToCatalogCodeMap() {
		return storeCodeToCatalogCodeMap;
	}

	/**
	 * @return Store Service
	 */
	protected StoreService getStoreService() {
		return elasticPath.getBean(ContextIdNames.STORE_SERVICE);
	}
	/**
	 * @return Catalog service
	 */
	protected CatalogService getCatalogService() {
		return elasticPath.getBean(ContextIdNames.CATALOG_SERVICE);
	}
	

	private <T extends AttributeFilter<T>> Map<Attribute, List<FilterOption<T>>> createAttributeFilterMap(
			final List<FilterOption<T>> attributeFilterOptions) {
		final Map<Attribute, List<FilterOption<T>>> resultMap = new LinkedHashMap<Attribute, List<FilterOption<T>>>(
				attributeFilterOptions.size());
		for (final FilterOption<T> option : attributeFilterOptions) {
			final Attribute attribute = option.getFilter().getAttribute();
			if (resultMap.get(attribute) == null) {
				resultMap.put(attribute, new ArrayList<FilterOption<T>>());
			}
			resultMap.get(attribute).add(option);
		}
		return resultMap;
	}

	/**
	 * Sets the {@link SolrProvider}instance.
	 * 
	 * @param solrProvider the {@link SolrProvider} instance
	 */
	public void setSolrProvider(final SolrProvider solrProvider) {
		this.solrProvider = solrProvider;
	}
	
	private <T extends Filter<T>> FilterOption<T> constructFilterOption(final int hitsNumber, final T filter) {
		final FilterOption<T> filterOption = elasticPath.getBean(ContextIdNames.FILTER_OPTION);
		filterOption.setHitsNumber(hitsNumber);
		filterOption.setFilter(filter);
		return filterOption;
	}

	/**
	 * Sets the {@link SolrQueryFactory} instance to use.
	 * 
	 * @param solrQueryFactory the {@link SolrQueryFactory} instance to use
	 */
	public void setSolrQueryFactory(final SolrQueryFactory solrQueryFactory) {
		this.solrQueryFactory = solrQueryFactory;
	}
	
	/**
	 * Sets the {@link FilterFactory} instance to use.
	 *
	 * @param filterFactory the {@link FilterFactory} instance to use
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}
	
	/**
	 * Sets the {@link ElasticPath} instance to use.
	 *
	 * @param elasticPath the {@link ElasticPath} instance to use
	 */
	public void setElasticPath(final ElasticPath elasticPath) {
		this.elasticPath = elasticPath;
	}
	
	/**
	 * Sets the {@link SolrFacetAdapter} instance to use.
	 *
	 * @param solrFacetAdapter the {@link SolrFacetAdapter} instance to use
	 */
	public void setSolrFacetAdapter(final SolrFacetAdapter solrFacetAdapter) {
		this.solrFacetAdapter = solrFacetAdapter;
	}
	
	/**
	 * Sets the {@link QueryComposerFactory} instance to use.
	 *
	 * @param queryComposerFactory the {@link QueryComposerFactory} instance to use
	 */
	public void setQueryComposerFactory(final QueryComposerFactory queryComposerFactory) {
		this.queryComposerFactory = queryComposerFactory;
	}
	
	/**
	 * Sets the index utility instance.
	 * 
	 * @param indexUtility the index utility instance
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}
	
	/**
	 * Gets the {@link IndexUtility} instance.
	 * 
	 * @return the {@link IndexUtility} instance
	 */
	protected IndexUtility getIndexUtility() {
		return indexUtility;
	}
}
