/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.search.solr.query;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SkuSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.query.SkuSearchCriteria.SkuOptionAndValues;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.search.solr.SolrQueryFactory;

/**
 * A query composer for sku search.
 */
public class SkuQueryComposerImpl extends AbstractQueryComposerImpl {

	private SolrQueryFactory solrQueryFactory;

	@Override
	protected Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final SkuSearchCriteria skuSearchCriteria = (SkuSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on sku search criteria");
		}

		Set<Locale> locales = skuSearchCriteria.getCatalogSearchableLocales();
		if (locales.isEmpty()) {
			locales.add(skuSearchCriteria.getLocale());
		}
		
		hasSomeCriteria |= addSplitFuzzyFieldToQueryWithMultipleLocales(SolrIndexConstants.PRODUCT_NAME,
					skuSearchCriteria.getProductName(), locales,
					searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.PRODUCT_SKU_CODE, skuSearchCriteria.getSkuCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		//SKU option does not support fuzzy search
		hasSomeCriteria |= addSkuOptionAndValuesToQuery(searchConfig, skuSearchCriteria, locales, booleanQuery);
		
		hasSomeCriteria |= addFuzzyInvariableTerms(skuSearchCriteria, booleanQuery, searchConfig);
		hasSomeCriteria |= addTermForActiveOnly(skuSearchCriteria, booleanQuery);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	@Override
	protected Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final SkuSearchCriteria skuSearchCriteria = (SkuSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (searchCriteria.getLocale() == null) {
			throw new EpServiceException("Locale not set on sku search criteria");
		}

		Set<Locale> locales = skuSearchCriteria.getCatalogSearchableLocales();
		if (locales.isEmpty()) {
			locales.add(skuSearchCriteria.getLocale());
		}
		
		hasSomeCriteria |= addSplitFieldToQueryWithMultipleLocales(SolrIndexConstants.PRODUCT_NAME,
					skuSearchCriteria.getProductName(), locales,
					searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PRODUCT_SKU_CODE, skuSearchCriteria.getSkuCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		
		hasSomeCriteria |= addSkuOptionAndValuesToQuery(searchConfig, skuSearchCriteria, locales, booleanQuery);

		hasSomeCriteria |= addFuzzyInvariableTerms(skuSearchCriteria, booleanQuery, searchConfig);
		hasSomeCriteria |= addTermForActiveOnly(skuSearchCriteria, booleanQuery);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;

	}

	private boolean addSkuOptionAndValuesToQuery(
			final SearchConfig searchConfig,
			final SkuSearchCriteria skuSearchCriteria,
			final Set<Locale> locales,
			final BooleanQuery booleanQuery) {
		
		boolean innerQueryhasSomeCriteria = false;
		
		if (CollectionUtils.isEmpty(skuSearchCriteria.getSkuOptionAndValuesSet())) {
			return false;
		}
		
		for (SkuOptionAndValues skuOptionAndValues : skuSearchCriteria.getSkuOptionAndValuesSet()) {
			final BooleanQuery queryForDifferentSkuOptions = new BooleanQuery();
			for (Locale locale : locales) {
				String fieldName = getIndexUtility().createSkuOptionFieldName(locale, skuOptionAndValues.getSkuOptionKey());
				innerQueryhasSomeCriteria |= addWholeFieldToQuery(fieldName, skuOptionAndValues.getSkuOptionValues(), null,
						searchConfig, queryForDifferentSkuOptions, Occur.SHOULD, true);
			}

			booleanQuery.add(queryForDifferentSkuOptions, Occur.MUST);
		}
		
		return innerQueryhasSomeCriteria;
	}

	/**
	 * Add the invariable search terms to the product index query.
	 * @param skuSearchCriteria the product search criteria
	 * @param booleanQuery the query being composed
	 * @param searchConfig the search configuration
	 * @return true if any fields were added to the query, false if not
	 */
	protected boolean addFuzzyInvariableTerms(final SkuSearchCriteria skuSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PRODUCT_CODE, skuSearchCriteria.getProductCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.BRAND_CODE, skuSearchCriteria.getBrandCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATALOG_CODE, skuSearchCriteria.getCatalogCodes(),
				null, searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, skuSearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);

		return hasSomeCriteria;
	}

	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof SkuSearchCriteria;
	}


	@Override
	protected void fillSortFieldMap(final Map<String, SortOrder> sortFieldMap, final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final String sortField = resolveSortField(searchCriteria);
		if (StringUtils.isNotEmpty(sortField)) {
			sortFieldMap.put(sortField, searchCriteria.getSortingOrder());
		}
	}

	private String resolveSortField(final SearchCriteria searchCriteria) {
		switch (searchCriteria.getSortingType().getOrdinal()) {
		case StandardSortBy.BRAND_NAME_ORDINAL:
			return SolrIndexConstants.SORT_BRAND_NAME_EXACT;
		case StandardSortBy.PRODUCT_CODE_ORDINAL:
			return SolrIndexConstants.PRODUCT_CODE;
		case StandardSortBy.PRODUCT_NAME_NON_LC_ORDINAL:
			return SolrIndexConstants.PRODUCT_NAME_NON_LC;
		case StandardSortBy.SKU_CODE_ORDINAL:
			return SolrIndexConstants.PRODUCT_SKU_CODE;
		case StandardSortBy.SKU_CONFIG_ORDINAL:
			return SolrIndexConstants.SKU_CONFIG_DEFAULT_LOCALE;
		case StandardSortBy.SKU_RESULT_TYPE_ORDINAL:
			return SolrIndexConstants.SKU_RESULT_TYPE;
		default:
			return null;
		}
	}

	/**
	 * Add query for active products. 
	 * @param skuSearchCriteria search criteria
	 * @param booleanQuery boolean query
	 * @return return true if flag isActive activated
	 */
	protected boolean addTermForActiveOnly(final SkuSearchCriteria skuSearchCriteria, final BooleanQuery booleanQuery) {
		boolean hasSomeCriteria = false;

		if (skuSearchCriteria.isActiveOnly()) {
			hasSomeCriteria = true;
			// only query for products currently active 
			Date now = new Date();
			BooleanQuery dateRangeQuery = getSolrQueryFactory().createTermsForStartEndDateRange(now);
			booleanQuery.add(dateRangeQuery, Occur.MUST);
		}
		return hasSomeCriteria;

	}

	/**
	 * @param solrQueryFactory the solrQueryFactory to set
	 */
	public void setSolrQueryFactory(final SolrQueryFactory solrQueryFactory) {
		this.solrQueryFactory = solrQueryFactory;
	}

	/**
	 * @return the solrQueryFactory
	 */
	public SolrQueryFactory getSolrQueryFactory() {
		return solrQueryFactory;
	}

}
