package com.elasticpath.service.search.solr.query;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A query compose for shipping service level search.
 */
public class ShippingServiceLevelQueryComposerImpl extends AbstractQueryComposerImpl implements QueryComposer {

	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof ShippingServiceLevelSearchCriteria;
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final ShippingServiceLevelSearchCriteria serviceLevelSearchCriteria = (ShippingServiceLevelSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.CARRIER, serviceLevelSearchCriteria.getCarrier(), null, searchConfig,
				booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CARRIER_EXACT, serviceLevelSearchCriteria.getCarrierExact(), null, searchConfig,
				booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.SERVICE_LEVEL_NAME, serviceLevelSearchCriteria.getServiceLevelName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.SERVICE_LEVEL_NAME_EXACT, serviceLevelSearchCriteria.getServiceLevelNameExact(),
				null, searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.REGION, serviceLevelSearchCriteria.getRegion(), null, searchConfig, booleanQuery,
				Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.REGION_EXACT, serviceLevelSearchCriteria.getRegionExact(), null, searchConfig,
				booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.STORE_NAME, serviceLevelSearchCriteria.getStore(), null, searchConfig,
				booleanQuery, Occur.MUST, true);
		
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.STORE_NAME_EXACT, serviceLevelSearchCriteria.getStoreExactNames(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.SERVICE_LEVEL_CODE, serviceLevelSearchCriteria.getServiceLevelCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		
		if (serviceLevelSearchCriteria.getActiveFlag() != null) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.ACTIVE_FLAG, String.valueOf(serviceLevelSearchCriteria.getActiveFlag()),
					null, searchConfig, booleanQuery, Occur.MUST, true);
		}

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final ShippingServiceLevelSearchCriteria serviceLevelSearchCriteria = (ShippingServiceLevelSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.CARRIER, serviceLevelSearchCriteria.getCarrier(), null, searchConfig,
				booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.CARRIER_EXACT, serviceLevelSearchCriteria.getCarrierExact(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.SERVICE_LEVEL_NAME, serviceLevelSearchCriteria.getServiceLevelName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.SERVICE_LEVEL_NAME_EXACT, serviceLevelSearchCriteria
				.getServiceLevelNameExact(), null, searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.REGION, serviceLevelSearchCriteria.getRegion(), null, searchConfig,
				booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.REGION_EXACT, serviceLevelSearchCriteria.getRegionExact(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.STORE_NAME, serviceLevelSearchCriteria.getStore(), null, searchConfig,
				booleanQuery, Occur.MUST, true);
		
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.STORE_NAME_EXACT, serviceLevelSearchCriteria.getStoreExactNames(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.SERVICE_LEVEL_CODE, serviceLevelSearchCriteria.getServiceLevelCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		if (serviceLevelSearchCriteria.getActiveFlag() != null) {
			hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.ACTIVE_FLAG, String.valueOf(serviceLevelSearchCriteria.getActiveFlag()),
					null, searchConfig, booleanQuery, Occur.MUST, true);
		}

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
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
		case StandardSortBy.ACTIVE_ORDINAL:
			return SolrIndexConstants.ACTIVE_FLAG;
		case StandardSortBy.STORE_NAME_ORDINAL:
			return SolrIndexConstants.STORE_NAME_EXACT;
		case StandardSortBy.CARRIER_ORDINAL:
			return SolrIndexConstants.CARRIER_EXACT;
		case StandardSortBy.REGION_ORDINAL:
			return SolrIndexConstants.REGION_EXACT;
		case StandardSortBy.SERVICE_LEVEL_CODE_ORDINAL:
			return SolrIndexConstants.SERVICE_LEVEL_CODE;
		case StandardSortBy.SERVICE_LEVEL_NAME_ORDINAL:
			return SolrIndexConstants.SERVICE_LEVEL_NAME_EXACT;
		default:
			return null;
		}
	}

	/**
	 * @return SolrIndexConstants.SERVICE_LEVEL_CODE
	 */
	protected String getBusinessCodeField() {
		return SolrIndexConstants.SERVICE_LEVEL_CODE;
	}

}
