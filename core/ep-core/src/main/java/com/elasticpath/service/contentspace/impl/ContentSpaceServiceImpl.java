/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.contentspace.impl;

import java.util.List;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.contentspace.ContentSpaceService;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ContentSpaceRelation;
import com.elasticpath.service.targetedselling.impl.AbstractTargetedSellingServiceImpl;

/**
 * Default implementation of {@link ContentSpaceService}.
 */
public class ContentSpaceServiceImpl extends AbstractTargetedSellingServiceImpl<ContentSpace> implements ContentSpaceService {

	private QueryService<ContentSpace> contentSpaceQueryService;

	private static final String QUERY_DYNAMIC_CONTENT_DELIVERIES_BY_CONTENT_SPACE = "QUERY_DYNAMIC_CONTENT_DELIVERIES_BY_CONTENT_SPACE";

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link QueryService} directly instead.
	 */
	@Override
	@Deprecated
	public List<ContentSpace> findAll() throws EpServiceException {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class).returning(ResultType.ENTITY);
		QueryResult<ContentSpace> result = getContentSpaceQueryService().query(criteria);
		return result.getResults();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link QueryService} directly instead.
	 */
	@Override
	@Deprecated
	public ContentSpace findByGuid(final String guid) throws EpServiceException {
		if (guid == null) {
			throw new EpServiceException("Cannot retrieve content with null guid.");
		}

		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class)
				.with(ContentSpaceRelation.having().guids(escapeSpecialCharacters(guid))).returning(ResultType.ENTITY);
		QueryResult<ContentSpace> result = getContentSpaceQueryService().query(criteria);
		return result.getSingleResult();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link QueryService} directly instead.
	 */
	@Override
	@Deprecated
	public ContentSpace findByName(final String name) throws EpServiceException {
		if (name == null) {
			throw new EpServiceException("Cannot retrieve content with null name.");
		}

		// TODO: super method impl doesn't escape name. We will follow its impl here, too.
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class).with(ContentSpaceRelation.having().names(name))
				.returning(ResultType.ENTITY);
		QueryResult<ContentSpace> result = getContentSpaceQueryService().query(criteria);
		return result.getSingleResult();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Use {@link QueryService} directly instead.
	 */
	@Override
	@Deprecated
	public List<ContentSpace> findByNameLike(final String token) throws EpServiceException {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class)
				.with(ContentSpaceRelation.having().nameLike(escapeSpecialCharacters(token))).returning(ResultType.ENTITY);
		QueryResult<ContentSpace> result = getContentSpaceQueryService().query(criteria);
		return result.getResults();
	}

	@Override
	public List<DynamicContentDelivery> findUsesByDynamicContentDelivery(final String guid) throws EpServiceException {
		final List<DynamicContentDelivery> results = getPersistenceEngine().retrieveByNamedQuery(QUERY_DYNAMIC_CONTENT_DELIVERIES_BY_CONTENT_SPACE,
				escapeSpecialCharacters(guid));
		return results;
	}

	/**
	 * @return The ContentSpace QueryService.
	 */
	protected QueryService<ContentSpace> getContentSpaceQueryService() {
		return contentSpaceQueryService;
	}

	/**
	 * @param contentSpaceQueryService The ContentSpace QueryService.
	 */
	public void setContentSpaceQueryService(final QueryService<ContentSpace> contentSpaceQueryService) {
		this.contentSpaceQueryService = contentSpaceQueryService;
	}
}
