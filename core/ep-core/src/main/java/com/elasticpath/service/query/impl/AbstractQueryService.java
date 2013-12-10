/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */
package com.elasticpath.service.query.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.query.IdentifierType;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.Relation;
import com.elasticpath.service.query.RelationJoin;
import com.elasticpath.service.query.ResultType;

/**
 * Common functionality for query services.
 *
 * @param <T> the generic type
 */
public abstract class AbstractQueryService<T> implements QueryService<T> {

	private PersistenceEngine persistenceEngine;
	private BeanFactory beanFactory;
	private FetchPlanHelper fetchPlanHelper;
	private final Map<ResultType, String> selectFields = initializeSelectFields();

	@Override
	public <R> QueryResult<R> query(final QueryCriteria<T> criteria) {
		JpqlQueryBuilder queryBuilder = getQueryBuilder(criteria);
		processCriteria(criteria, queryBuilder);
		processRelatedTypes(criteria, queryBuilder);
		return performQuery(criteria, queryBuilder);
	}

	/**
	 * Gets the query builder.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @return the query builder
	 */
	protected <R> JpqlQueryBuilder getQueryBuilder(final QueryCriteria<R> criteria) {
		if (!isResultTypeSupported(criteria.getResultType())) {
			throw new UnsupportedResultTypeException("Unexpected result type");
		}

		String selectFields = getSelectFieldsForType(criteria.getResultType());
		return new JpqlQueryBuilder(getObjectName(getSelfRelation().getBeanName()), getSelfRelation().getAlias(), selectFields);
	}

	/**
	 * Checks if the given result type is supported.
	 *
	 * @param resultType the result type
	 * @return true, if the result type is supported
	 */
	protected boolean isResultTypeSupported(final ResultType resultType) {
		return getSelectFields().containsKey(resultType);
	}

	/**
	 * Process related types.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @param queryBuilder the query builder
	 */
	protected <R> void processRelatedTypes(final QueryCriteria<R> criteria, final JpqlQueryBuilder queryBuilder) {
		JpqlQueryBuilderWhereGroup whereGroup = queryBuilder.getDefaultWhereGroup();
		for (Relation<?> relation : criteria.getRelations()) {
			addJoinsForRelation(queryBuilder, relation);
			addWhereClauseForRelation(criteria, whereGroup, relation);
		}
	}

	/**
	 * Adds the joins for the given relation.
	 *
	 * @param queryBuilder the query builder
	 * @param relation the relation
	 */
	protected void addJoinsForRelation(final JpqlQueryBuilder queryBuilder, final Relation<?> relation) {
		RelationJoin relationJoin = getRelationJoin(relation);
		addRelationJoin(queryBuilder, relationJoin, getSelfRelation().getAlias());
		addJoinClause(queryBuilder, relationJoin, relation.getAlias(), relation.getBeanName());
		if (relationJoin.getJoinRelation() != null) {
			addRelationJoin(queryBuilder, relationJoin.getJoinRelation(), relation.getAlias());
		}
	}

	/**
	 * Adds the join clause.
	 *
	 * @param queryBuilder the query builder
	 * @param relationJoin the relation join
	 * @param joinAlias the join alias
	 * @param joinBeanName the join bean name
	 */
	protected void addJoinClause(final JpqlQueryBuilder queryBuilder, final RelationJoin relationJoin, final String joinAlias,
			final String joinBeanName) {
		if (relationJoin.getJoinClause() != null) {
			queryBuilder.appendInnerJoin(getObjectName(joinBeanName), joinAlias, relationJoin.getJoinClause());
		}
	}

	/**
	 * Gets the relation join.
	 *
	 * @param relation the relation
	 * @return the relation join
	 */
	protected RelationJoin getRelationJoin(final Relation<?> relation) {
		return relation.relationWith(getSelfRelation().getRelationClass());
	}

	/**
	 * If the relation has a join field, add the join to the query.
	 *
	 * @param queryBuilder the query builder
	 * @param relationJoin the relation join
	 * @param alias the alias
	 */
	protected void addRelationJoin(final JpqlQueryBuilder queryBuilder, final RelationJoin relationJoin, final String alias) {
		if (relationJoin.getJoinField() != null) {
			queryBuilder.appendInnerJoin(alias + "." + relationJoin.getJoinField(), relationJoin.getJoinAlias());
		}
	}

	/**
	 * Gets the object name from the given bean name.
	 *
	 * @param beanName the bean name
	 * @return the object name
	 */
	protected String getObjectName(final String beanName) {
		return getBeanFactory().getBeanImplClass(beanName).getSimpleName();
	}

	/**
	 * Adds the where clause for a related type.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @param whereGroup the where group
	 * @param relation the relation
	 */
	protected <R> void addWhereClauseForRelation(final QueryCriteria<R> criteria, final JpqlQueryBuilderWhereGroup whereGroup,
			final Relation<?> relation) {
		for (IdentifierType identifierType : relation.getSupportedIdentifiers()) {
			if (relation.hasValuesForIdentifier(identifierType)) {
				whereGroup.appendWhereInCollection(getWhereClauseField(relation, identifierType), relation.getValuesForIdentifier(identifierType));
			}
			if (relation.hasLikeValueForIdentifier(identifierType)) {
				whereGroup.appendLikeWithWildcards(getWhereClauseField(relation, identifierType), relation.getLikeValueForIdentifier(identifierType));
			}
		}
	}

	/**
	 * Gets the where clause field.
	 *
	 * @param relation the relation
	 * @param identifierType the identifier type
	 * @return the where clause field
	 */
	protected String getWhereClauseField(final Relation<?> relation, final IdentifierType identifierType) {
		RelationJoin relationJoin = getRelationJoin(relation);
		StringBuilder fieldName = new StringBuilder();
		if (relationJoin.getJoinField() == null && relationJoin.getJoinRelation() == null) {
			fieldName.append(getSelfRelation().getAlias());
		} else {
			fieldName.append(relation.getAlias());
		}
		fieldName.append('.');
		if (relationJoin.getClauseField() != null) {
			fieldName.append(relationJoin.getClauseField());
			fieldName.append('.');
		}
		fieldName.append(relation.getIdentifierColumn(identifierType));
		return fieldName.toString();
	}

	/**
	 * Perform query.
	 *
	 * @param <R> the generic type
	 * @param criteria the criteria
	 * @param queryBuilder the query builder
	 * @return the query result
	 */
	@SuppressWarnings("unchecked")
	protected <R> QueryResult<R> performQuery(final QueryCriteria<T> criteria, final JpqlQueryBuilder queryBuilder) {
		LoadTuner loadTuner = criteria.getLoadTuner();
		configureLoadTuner(loadTuner);

		QueryResultImpl<R> queryResult = new QueryResultImpl<R>();
		if (ResultType.CONDITIONAL.equals(criteria.getResultType())) {
			List<Long> results = retrieveResults(queryBuilder);
			List<Boolean> booleanResults = Arrays.asList(!results.isEmpty() && results.get(0) > 0);
			queryResult.setResults((List<R>) booleanResults);
		} else {
			List<R> results = retrieveResults(queryBuilder);
			queryResult.setResults(results);
		}

		getFetchPlanHelper().clearFetchPlan();
		return queryResult;
	}

	/**
	 * Retrieve results.
	 *
	 * @param <R> the generic type of the results
	 * @param queryBuilder the query builder
	 * @return the list
	 */
	protected <R> List<R> retrieveResults(final JpqlQueryBuilder queryBuilder) {
		List<R> results;
		if (queryBuilder.getParameterList().isEmpty()) {
			results = getPersistenceEngine().retrieve(queryBuilder.toString());
		} else {
			results = getPersistenceEngine().retrieve(queryBuilder.toString(), queryBuilder.getParameterList().toArray());
		}
		return results;
	}

	/**
	 * Gets the select fields for the given type.
	 *
	 * @param resultType the result type
	 * @return the select fields for type
	 */
	protected String getSelectFieldsForType(final ResultType resultType) {
		return selectFields.get(resultType);
	}

	/**
	 * Initialize select fields. This initializes a map of result types to field names.
	 *
	 * @return the map
	 */
	protected abstract Map<ResultType, String> initializeSelectFields();

	/**
	 * Process the query criteria.
	 *
	 * @param criteria the criteria
	 * @param queryBuilder the query builder
	 */
	protected abstract void processCriteria(final QueryCriteria<T> criteria, final JpqlQueryBuilder queryBuilder);

	/**
	 * Gets the Relation object for the object being queried by this service.
	 *
	 * @return the self relation
	 */
	protected abstract Relation<T> getSelfRelation();

	/**
	 * Configure load tuner.
	 *
	 * @param loadTuner the load tuner
	 */
	protected abstract void configureLoadTuner(final LoadTuner loadTuner);


	protected Map<ResultType, String> getSelectFields() {
		return selectFields;
	}

	protected PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected FetchPlanHelper getFetchPlanHelper() {
		return fetchPlanHelper;
	}

	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

}
