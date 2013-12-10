package com.elasticpath.service.search.solr.query;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanClause.Occur;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.EpEmptySearchCriteriaException;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * A query compose for order returns search.
 * 
 * @deprecated the order return search should use the ReturnAndExchangeService to query the database directly.
 */
@Deprecated
public class OrderReturnQueryComposerImpl extends AbstractQueryComposerImpl implements QueryComposer {
	
	@Override
	protected boolean isValidSearchCriteria(final SearchCriteria searchCriteria) {
		return searchCriteria instanceof OrderReturnSearchCriteria;
	}

	@Override
	public Query composeQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = (OrderReturnSearchCriteria) searchCriteria;

		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (orderReturnSearchCriteria.getCustomerSearchCriteria() != null
				&& composeQueryForCustomerSearchCriteria(orderReturnSearchCriteria, booleanQuery, searchConfig)) {
			hasSomeCriteria = true;
		}

		hasSomeCriteria |= composeQueryForOtherSearchCriteria(orderReturnSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	@Override
	public Query composeFuzzyQueryInternal(final SearchCriteria searchCriteria, final SearchConfig searchConfig) {
		final OrderReturnSearchCriteria orderReturnSearchCriteria = (OrderReturnSearchCriteria) searchCriteria;

		final BooleanQuery booleanQuery = new BooleanQuery();
		boolean hasSomeCriteria = false;

		if (orderReturnSearchCriteria.getCustomerSearchCriteria() != null
				&& composeFuzzyQueryForCustomerSearchCriteria(orderReturnSearchCriteria, booleanQuery, searchConfig)) {
			hasSomeCriteria = true;
		}

		hasSomeCriteria |= composeQueryForOtherSearchCriteria(orderReturnSearchCriteria, booleanQuery, searchConfig);

		if (!hasSomeCriteria) {
			throw new EpEmptySearchCriteriaException("Empty search criteria is not allowed!");
		}

		return booleanQuery;
	}

	private boolean composeQueryForOtherSearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.ORDER_NUMBER, orderReturnSearchCriteria.getOrderNumber(),
				null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.RMA_CODE, orderReturnSearchCriteria.getRmaCode(), null,
				searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.OBJECT_UID, orderReturnSearchCriteria.getFilteredUids(), null,
				searchConfig, booleanQuery, Occur.MUST_NOT, false);
		hasSomeCriteria |= addWholeFieldToQuery(SolrIndexConstants.WAREHOUSES_CODE, orderReturnSearchCriteria.getWarehouseCodes(), null,
				searchConfig, booleanQuery, Occur.MUST, true);

		return hasSomeCriteria;
	}

	private boolean composeQueryForCustomerSearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.FIRST_NAME, orderReturnSearchCriteria
				.getCustomerSearchCriteria().getFirstName(), null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addSplitFieldToQuery(SolrIndexConstants.LAST_NAME, orderReturnSearchCriteria
				.getCustomerSearchCriteria().getLastName(), null, searchConfig, booleanQuery, Occur.MUST, true);

		return hasSomeCriteria;
	}

	private boolean composeFuzzyQueryForCustomerSearchCriteria(final OrderReturnSearchCriteria orderReturnSearchCriteria,
			final BooleanQuery booleanQuery, final SearchConfig searchConfig) {
		boolean hasSomeCriteria = false;

		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.FIRST_NAME, orderReturnSearchCriteria
				.getCustomerSearchCriteria().getFirstName(), null, searchConfig, booleanQuery, Occur.MUST, true);
		hasSomeCriteria |= addSplitFuzzyFieldToQuery(SolrIndexConstants.LAST_NAME, orderReturnSearchCriteria
				.getCustomerSearchCriteria().getLastName(), null, searchConfig, booleanQuery, Occur.MUST, true);

		return hasSomeCriteria;
	}

}
