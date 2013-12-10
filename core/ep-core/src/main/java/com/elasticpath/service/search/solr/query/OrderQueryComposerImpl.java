package com.elasticpath.service.search.solr.query;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A query compose for orders search.
 * 
 * @deprecated the order search should use the orderService to query the database directly.
 */
@Deprecated
public class OrderQueryComposerImpl extends AbstractQueryComposerImpl implements QueryComposer {
	
	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof OrderSearchCriteria;
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final OrderSearchCriteria orderSearchCriteria = (OrderSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (orderSearchCriteria.getCustomerSearchCriteria() != null) {
			hasSomeCriteria |= composeQueryForCustomerSearchCriteria(orderSearchCriteria, booleanQuery, searchConfig);
		}

		hasSomeCriteria |= composeQueryForOtherSearchCriteria(orderSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final OrderSearchCriteria orderSearchCriteria = (OrderSearchCriteria) searchCriteria;
		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (orderSearchCriteria.getCustomerSearchCriteria() != null) {
			hasSomeCriteria |= composeFuzzyQueryForCustomerSearchCriteria(orderSearchCriteria, booleanQuery, searchConfig);
		}

		hasSomeCriteria |= composeQueryForOtherSearchCriteria(orderSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	private boolean composeQueryForOtherSearchCriteria(final OrderSearchCriteria orderSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.ORDER_NUMBER, orderSearchCriteria.getOrderNumber(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.SHIPMENT_ZIPCODE, orderSearchCriteria.getShipmentZipcode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.SKU_CODE, orderSearchCriteria.getSkuCode(), null, searchConfig,
				booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.RMA_CODES, orderSearchCriteria.getRmaCode(), null, searchConfig,
				booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, orderSearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);

		// check for both so that we can optimize the queries (send 1 query instead of 2)
		hasSomeCriteria |= addOrderDateCriteria(booleanQuery, orderSearchCriteria.getOrderFromDate(), 
				orderSearchCriteria.getOrderToDate(), searchConfig.getBoostValue(SolrIndexConstants.CREATE_TIME));
		
		hasSomeCriteria |= addTermForStoreCode(orderSearchCriteria, booleanQuery, searchConfig);

		if (orderSearchCriteria.getOrderStatus() != null) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.ORDER_STATUS, orderSearchCriteria.getOrderStatus().toString(),
					null, searchConfig, booleanQuery, Occur.MUST, true);
		}

		if (orderSearchCriteria.getShipmentStatus() != null) {
			hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.SHIPMENT_STATUS, orderSearchCriteria.getShipmentStatus()
					.toString(), null, searchConfig, booleanQuery, Occur.MUST, true);
		}

		return hasSomeCriteria;
	}
	
	private boolean addOrderDateCriteria(final BooleanQuery booleanQuery, final Date fromDate, final Date toDate, final float boostValue) {

		String analyzedFromDate = null;
		String analyzedToDate = null;
		
		if (fromDate != null) {
			analyzedFromDate = getAnalyzer().analyze(fromDate);
		}
		
		if (toDate != null) {
			analyzedToDate = getAnalyzer().analyze(toDate);
		}
		
		if (analyzedFromDate != null || analyzedToDate != null) {
			final Query dateQuery = new ConstantScoreRangeQuery(SolrIndexConstants.CREATE_TIME, analyzedFromDate, analyzedToDate, true, true);
			dateQuery.setBoost(boostValue);
			booleanQuery.add(dateQuery, Occur.MUST);
			return true;
		}
		
		return false;
	}

	private boolean composeQueryForCustomerSearchCriteria(final OrderSearchCriteria orderSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;
		CustomerSearchCriteria customerSearchCriteria = orderSearchCriteria.getCustomerSearchCriteria();
		
		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.FIRST_NAME, customerSearchCriteria
				.getFirstName(), null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.LAST_NAME, customerSearchCriteria
				.getLastName(), null, searchConfig, booleanQuery, Occur.MUST, true);
		
		BooleanQuery userIdEmailQuery = new BooleanQuery();
		boolean hasUserCriteria = false;
		hasUserCriteria |= addWholeFieldToQuery(SolrIndexConstants.USERID_AND_EMAIL, customerSearchCriteria.getUserId(), null,
				searchConfig, userIdEmailQuery, Occur.SHOULD, true);
		hasUserCriteria |= addWholeFieldToQuery(SolrIndexConstants.USERID_AND_EMAIL, customerSearchCriteria.getEmail(), null,
				searchConfig, userIdEmailQuery, Occur.SHOULD, true);
		hasSomeCriteria |= hasUserCriteria;
		if (hasUserCriteria) {
			booleanQuery.add(userIdEmailQuery, Occur.MUST);
		}
		
		hasSomeCriteria |= addFuzzyInvariableCustomerTerms(orderSearchCriteria, booleanQuery, searchConfig);

		return hasSomeCriteria;
	}

	private boolean composeFuzzyQueryForCustomerSearchCriteria(final OrderSearchCriteria orderSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;
		CustomerSearchCriteria customerSearchCriteria = orderSearchCriteria.getCustomerSearchCriteria();
		
		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.FIRST_NAME, customerSearchCriteria
				.getFirstName(), null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.LAST_NAME, customerSearchCriteria
				.getLastName(), null, searchConfig, booleanQuery, Occur.MUST, true);
		
		
		BooleanQuery userIdEmailQuery = new BooleanQuery();
		boolean hasUserCriteria = false;
		hasUserCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.USERID_AND_EMAIL, customerSearchCriteria.getUserId(), null,
				searchConfig, userIdEmailQuery, Occur.SHOULD, true);
		hasUserCriteria |= addWholeFuzzyFieldToQuery(SolrIndexConstants.USERID_AND_EMAIL, customerSearchCriteria.getEmail(), null,
				searchConfig, userIdEmailQuery, Occur.SHOULD, true);
		hasSomeCriteria |= hasUserCriteria;
		if (hasUserCriteria) {
			booleanQuery.add(userIdEmailQuery, Occur.MUST);
		}


		hasSomeCriteria |= addFuzzyInvariableCustomerTerms(orderSearchCriteria, booleanQuery, searchConfig);

		return hasSomeCriteria;
	}

	private boolean addFuzzyInvariableCustomerTerms(final OrderSearchCriteria orderSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.PHONE_NUMBER, orderSearchCriteria.getCustomerSearchCriteria()
				.getPhoneNumber(), null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.CUSTOMER_NUMBER, orderSearchCriteria.getCustomerSearchCriteria()
				.getCustomerNumber(), null, searchConfig, booleanQuery, Occur.MUST, true);

		return hasSomeCriteria;
	}
	
	private boolean addTermForStoreCode(final OrderSearchCriteria orderSearchCriteria, final BooleanQuery booleanQuery,
			final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;
		Set<String> storeCodes = orderSearchCriteria.getStoreCodes();

		if (storeCodes != null && !storeCodes.isEmpty()) {
			hasSomeCriteria = true;

			final BooleanQuery query = new BooleanQuery();
			for (String code : storeCodes) {
				final Query categoryQuery = new TermQuery(new Term(SolrIndexConstants.STORE_CODE, getAnalyzer().analyze(code)));
				query.add(categoryQuery, BooleanClause.Occur.SHOULD);
			}
			query.setBoost(searchConfig.getBoostValue(SolrIndexConstants.STORE_CODE));
			booleanQuery.add(query, BooleanClause.Occur.MUST);
		}
		return hasSomeCriteria;
	}
	
	@Override
	protected void fillSortFieldMap(final Map<String, SortOrder> sortFieldMap, final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final SortOrder sortOrder = searchCriteria.getSortingOrder();
		switch (searchCriteria.getSortingType().getOrdinal()) {
		case StandardSortBy.CUSTOMER_NAME_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.USERID_AND_EMAIL_EXACT, sortOrder);
			break;
		case StandardSortBy.DATE_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.CREATE_TIME, sortOrder);
			break;
		case StandardSortBy.ORDER_NUMBER_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.ORDER_NUMBER, sortOrder);
			break;
		case StandardSortBy.STATUS_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.ORDER_STATUS, sortOrder);
			break;
		case StandardSortBy.STORE_CODE_ORDINAL:
			sortFieldMap.put(SolrIndexConstants.STORE_CODE, sortOrder);
			break;
		case StandardSortBy.TOTAL_ORDINAL:			
			sortFieldMap.put(SolrIndexConstants.CURRENCY_SYMBOL, SortOrder.ASCENDING);
			sortFieldMap.put(SolrIndexConstants.TOTAL_MONEY, sortOrder);
			break;
		default:
			//do nothing
		}
	}
	
	/**
	 * @return SolrIndexConstants.ORDER_NUMBER
	 */
	@Override
	protected String getBusinessCodeField() {		
		return SolrIndexConstants.ORDER_NUMBER;
	}
}
