package com.elasticpath.service.search.solr.query;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A query compose for customer search.
 */
public class CustomerQueryComposerImpl extends AbstractQueryComposerImpl implements QueryComposer {
	
	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof CustomerSearchCriteria;
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final CustomerSearchCriteria customerSearchCriteria = (CustomerSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (customerSearchCriteria.isUserIdAndEmailMutualSearch()) {
			final BooleanQuery innerQuery = new BooleanQuery();
			boolean hasUserCriteria = false;

			hasUserCriteria |= addWholeFieldToQuery(SolrIndexConstants.USER_ID, customerSearchCriteria.getUserId(), null,
					searchConfig, innerQuery, Occur.SHOULD, true);
			hasUserCriteria |= addWholeFieldToQuery(SolrIndexConstants.EMAIL, customerSearchCriteria.getEmail(), null, searchConfig,
					innerQuery, Occur.SHOULD, true);
			hasSomeCriteria |= hasUserCriteria;
			if (hasUserCriteria) {
				booleanQuery.add(innerQuery, Occur.MUST);
			}
		} else {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.USER_ID, customerSearchCriteria.getUserId(), null,
					searchConfig, booleanQuery, Occur.MUST, true);
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.EMAIL, customerSearchCriteria.getEmail(), null, searchConfig,
					booleanQuery, Occur.MUST, true);
		}

		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.FIRST_NAME, customerSearchCriteria.getFirstName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.LAST_NAME, customerSearchCriteria.getLastName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.ZIP_POSTAL_CODE, customerSearchCriteria.getZipOrPostalCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addFuzzyInvariableTerms(customerSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final CustomerSearchCriteria customerSearchCriteria = (CustomerSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (customerSearchCriteria.isUserIdAndEmailMutualSearch()) {
			final BooleanQuery innerQuery = new BooleanQuery();
			boolean hasUserCriteria = false;
			
			hasUserCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.USER_ID, customerSearchCriteria.getUserId(), null,
					searchConfig, innerQuery, Occur.SHOULD, true);
			hasUserCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.EMAIL, customerSearchCriteria.getEmail(), null, searchConfig,
					innerQuery, Occur.SHOULD, true);
			
			hasSomeCriteria |= hasUserCriteria;
			if (hasUserCriteria) {
				booleanQuery.add(innerQuery, Occur.MUST);
			}
		} else {
			hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.USER_ID, customerSearchCriteria.getUserId(), null,
					searchConfig, booleanQuery, Occur.MUST, true);
			hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.EMAIL, customerSearchCriteria.getEmail(), null, searchConfig,
					booleanQuery, Occur.MUST, true);
		}
		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.FIRST_NAME, customerSearchCriteria.getFirstName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.LAST_NAME, customerSearchCriteria.getLastName(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.ZIP_POSTAL_CODE, customerSearchCriteria.getZipOrPostalCode(),
				null, searchConfig, booleanQuery, Occur.MUST, true);

		hasSomeCriteria |= addFuzzyInvariableTerms(customerSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	private boolean addFuzzyInvariableTerms(final CustomerSearchCriteria customerSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CUSTOMER_NUMBER, customerSearchCriteria.getCustomerNumber(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PHONE_NUMBER, customerSearchCriteria.getPhoneNumber(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.STORE_CODE, customerSearchCriteria.getStoreCodes(), null, searchConfig,
				booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, customerSearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);

		hasSomeCriteria |= addTermForCreateDate(customerSearchCriteria, booleanQuery, searchConfig);

		return hasSomeCriteria;
	}

	private boolean addTermForCreateDate(final CustomerSearchCriteria customerSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasCriteria = false;
		if (customerSearchCriteria.getFromDate() != null) {
			hasCriteria = true;
			final String fromDateAnalyzed = getAnalyzer().analyze(customerSearchCriteria.getFromDate());

			final ConstantScoreRangeQuery fromDateRangeQuery = new ConstantScoreRangeQuery(SolrIndexConstants.CREATE_TIME,
					fromDateAnalyzed, null, true, true);
			fromDateRangeQuery.setBoost(searchConfig.getBoostValue(SolrIndexConstants.CREATE_TIME));
			booleanQuery.add(fromDateRangeQuery, BooleanClause.Occur.MUST);
		}
		return hasCriteria;
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
		case StandardSortBy.CUSTOMER_ID_ORDINAL:
			return SolrIndexConstants.CUSTOMER_NUMBER;
		case StandardSortBy.ADDRESS_ORDINAL:
			return SolrIndexConstants.PREFERRED_BILLING_ADDRESS_EXACT;
		case StandardSortBy.EMAIL_ORDINAL:
			return SolrIndexConstants.EMAIL_EXACT;
		case StandardSortBy.FIRST_NAME_ORDINAL:
			return SolrIndexConstants.FIRST_NAME_EXACT;
		case StandardSortBy.LAST_NAME_ORDINAL:
			return SolrIndexConstants.LAST_NAME_EXACT;
		case StandardSortBy.PHONE_ORDINAL:			
			return SolrIndexConstants.PHONE_NUMBER_EXACT;
		default:
			return null;
		}
	}
	
	/**
	 * @return SolrIndexConstants.CUSTOMER_NUMBER
	 */
	protected String getBusinessCodeField() {		
		return SolrIndexConstants.CUSTOMER_NUMBER;
	}
}
