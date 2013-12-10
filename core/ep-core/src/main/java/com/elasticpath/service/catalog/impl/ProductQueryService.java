package com.elasticpath.service.catalog.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.JpqlMatchType;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.Relation;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.impl.AbstractQueryService;
import com.elasticpath.service.query.impl.UnsupportedLoadTunerException;
import com.elasticpath.service.query.relations.ProductRelation;

/**
 * Product based <code>QueryService</code>.
 */
public class ProductQueryService extends AbstractQueryService<Product> {

	/**
	 * Process criteria.
	 *
	 * @param criteria the criteria
	 * @param queryBuilder the query builder
	 */
	@Override
	protected void processCriteria(final QueryCriteria<Product> criteria, final JpqlQueryBuilder queryBuilder) {
		if (criteria.getModifiedAfter() != null) {
			processModifiedAfterCriteria(criteria, queryBuilder);
		}
		
		if (criteria.getStartDate() != null) {
			processDateRangeCriteria(criteria, queryBuilder);
		}
	}
	
	/**
	 * Process modified after criteria.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @param queryBuilder the query builder
	 */
	protected <R> void processModifiedAfterCriteria(final QueryCriteria<R> criteria, final JpqlQueryBuilder queryBuilder) {
		JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		whereGroup.appendWhere(getSelfRelation().getAlias() + ".lastModifiedDate", ">=", criteria.getModifiedAfter(), JpqlMatchType.AS_IS);
	}

	/**
	 * Process date range criteria.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @param queryBuilder the query builder
	 */
	protected <R> void processDateRangeCriteria(final QueryCriteria<R> criteria, final JpqlQueryBuilder queryBuilder) {
		JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		whereGroup.appendWhere(getSelfRelation().getAlias() + ".startDate", "<=", criteria.getStartDate(), JpqlMatchType.AS_IS);
		JpqlQueryBuilderWhereGroup endDateWhereGroup = queryBuilder.createNewWhereGroup();
		whereGroup.appendWhereGroup(endDateWhereGroup);
		endDateWhereGroup.appendWhere(getSelfRelation().getAlias() + ".endDate IS NULL OR " 
				+ getSelfRelation().getAlias() + ".endDate", ">=", criteria.getEndDate(), JpqlMatchType.AS_IS);
	}
	
	/**
	 * Configure load tuner.
	 *
	 * @param loadTuner the load tuner
	 */
	@Override
	protected void configureLoadTuner(final LoadTuner loadTuner) {
		if (loadTuner != null) {
			if (loadTuner instanceof ProductLoadTuner) {
				getFetchPlanHelper().configureProductFetchPlan((ProductLoadTuner) loadTuner);
			} else if (loadTuner instanceof FetchGroupLoadTuner) {
				getFetchPlanHelper().configureFetchGroupLoadTuner((FetchGroupLoadTuner) loadTuner);
			} else {
				throw new UnsupportedLoadTunerException("Unsupported load tuner.");
			}
		}
	}

	/**
	 * Initialize select fields.
	 *
	 * @return the map of result type to select fields
	 */
	@Override
	protected Map<ResultType, String> initializeSelectFields() {
		Map<ResultType, String> fields = new HashMap<ResultType, String>();
		fields.put(ResultType.ENTITY, getSelfRelation().getAlias());
		fields.put(ResultType.GUID, getSelfRelation().getAlias() + ".code");
		fields.put(ResultType.UID, getSelfRelation().getAlias() + ".uidPk");
		fields.put(ResultType.DATE, getSelfRelation().getAlias() + ".lastModifiedDate");
		fields.put(ResultType.CONDITIONAL, "count(" + getSelfRelation().getAlias() + ")");
		return Collections.unmodifiableMap(fields);
	}

	@Override
	protected Relation<Product> getSelfRelation() {
		return new ProductRelation();
	}
}
