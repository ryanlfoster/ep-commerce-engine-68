/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.search.solr.query;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.solr.common.util.DateUtil;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.PromotionSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A query compose for products search.
 */
public class PromotionQueryComposerImpl extends AbstractQueryComposerImpl implements QueryComposer {
	
	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof PromotionSearchCriteria;
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final PromotionSearchCriteria promotionSearchCriteria = (PromotionSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;
		
		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.PROMOTION_NAME, promotionSearchCriteria.getPromotionName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		
		hasSomeCriteria |= addFuzzyInvariableTerms(promotionSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria.");
		}

		return booleanQuery;
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final PromotionSearchCriteria promotionSearchCriteria = (PromotionSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;
		
		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.PROMOTION_NAME, promotionSearchCriteria.getPromotionName(),
				null, searchConfig, booleanQuery, Occur.MUST, true);
		
		hasSomeCriteria |= addFuzzyInvariableTerms(promotionSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}
	
	private boolean addFuzzyInvariableTerms(final PromotionSearchCriteria promotionSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;
		
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PROMOTION_RULESET_NAME, promotionSearchCriteria.getRuleSetName(),
				null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, promotionSearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);
		
		if (promotionSearchCriteria.getCatalogUid() != null && promotionSearchCriteria.getCatalogUid() > 0) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATALOG_UID, 
					String.valueOf(promotionSearchCriteria.getCatalogUid()), null, searchConfig, booleanQuery, Occur.MUST, true);
		}
		
		Set<String> catalogCodes = promotionSearchCriteria.getCatalogCodes();
		Set<String> storeCodes = promotionSearchCriteria.getStoreCodes();
		if (CollectionUtils.isNotEmpty(catalogCodes) && CollectionUtils.isNotEmpty(storeCodes)) {
			//both catalog codes and store codes are not empty
			//means search catalog promotions in catalog codes 
			//or search shopping cart promotions in store codes
			BooleanQuery tempBooleanQuery = new BooleanQuery();
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATALOG_CODE, 
					catalogCodes, null, searchConfig, tempBooleanQuery, Occur.SHOULD, true);
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.STORE_CODE, storeCodes, null,
					searchConfig, tempBooleanQuery, Occur.SHOULD, true);
			booleanQuery.add(tempBooleanQuery, Occur.MUST);
		} else {		
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PROMOTION_RULESET_UID, promotionSearchCriteria.getRuleSetUid(),
					null, searchConfig, booleanQuery, Occur.MUST, true);
			if (CollectionUtils.isNotEmpty(catalogCodes)) {
				hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CATALOG_CODE, 
						catalogCodes, null, searchConfig, booleanQuery, Occur.MUST, true);
			}
			if (CollectionUtils.isNotEmpty(storeCodes)) {
				hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.STORE_CODE, storeCodes, null,
						searchConfig, booleanQuery, Occur.MUST, true);
			}
		}
		
		if (promotionSearchCriteria.getEnabled() != null) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PROMOTION_STATE,
					String.valueOf(promotionSearchCriteria.getEnabled()), null, searchConfig, booleanQuery, Occur.MUST, true);
		}

		if (promotionSearchCriteria.isActive() != null) {
			hasSomeCriteria = true;

			Occur occur;
			if (promotionSearchCriteria.isActive()) {
				occur = Occur.MUST_NOT;
			} else {
				occur = Occur.MUST;
			}
			// end date must not be in the past (this handles missing end dates)
			booleanQuery.add(new TermRangeQuery(SolrIndexConstants.END_DATE, "*", DateUtil.getThreadLocalDateFormat().format(new Date()),
					true, true), occur);
		}

		return hasSomeCriteria;
	}
	
	@Override
	protected void fillSortFieldMap(final Map<String, SortOrder> sortFieldMap, final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final SortOrder sortOrder = searchCriteria.getSortingOrder();
		switch (searchCriteria.getSortingType().getOrdinal()) {
		case StandardSortBy.PROMOTION_ENABLE_DATE_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.START_DATE, sortOrder);
			break;
		case StandardSortBy.PROMOTION_EXPIRATION_DATE_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.END_DATE, sortOrder);
			break;
		case StandardSortBy.PROMOTION_NAME_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.PROMOTION_NAME_EXACT, sortOrder);
			break;
		case StandardSortBy.PROMOTION_STATE_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.PROMOTION_STATE, sortOrder);
			break;
		case StandardSortBy.PROMOTION_TYPE_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.PROMOTION_RULESET_NAME_EXACT, sortOrder);
			break;
		default:
			//do nothing
		}
	}
	
	/**
	 * @return SolrIndexConstants.PRODUCT_CODE
	 */
	protected String getBusinessCodeField() {		
		return SolrIndexConstants.PROMOTION_NAME_EXACT;
	}
}
