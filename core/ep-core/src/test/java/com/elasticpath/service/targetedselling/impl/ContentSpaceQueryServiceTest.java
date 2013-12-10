package com.elasticpath.service.targetedselling.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.impl.ContentSpaceImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ContentSpaceRelation;

/**
 * Test that {@see ContentSpaceQueryService} behaves as expected.
 */
public class ContentSpaceQueryServiceTest {

	private static final String NAME = "name";

	private static final String WILDCARD_NAME = "%name%";

	private final QueryService<ContentSpace> queryService = new ContentSpaceQueryService();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine persistenceEngine;

	private BeanFactory beanFactory;

	private FetchPlanHelper fetchPlanHelper;

	/**
	 * Setup required before each test.
	 */
	@Before
	public void setUp() {
		persistenceEngine = context.mock(PersistenceEngine.class);
		beanFactory = context.mock(BeanFactory.class);
		fetchPlanHelper = context.mock(FetchPlanHelper.class);
		((ContentSpaceQueryService) queryService).setBeanFactory(beanFactory);
		((ContentSpaceQueryService) queryService).setPersistenceEngine(persistenceEngine);
		((ContentSpaceQueryService) queryService).setFetchPlanHelper(fetchPlanHelper);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBeanImplClass(ContextIdNames.CONTENTSPACE);
				will(returnValue(ContentSpaceImpl.class));
				allowing(fetchPlanHelper).clearFetchPlan();
			}
		});
	}

	/**
	 * Test find retrieval of all {@link ContentSpace}s.
	 */
	@Test
	public void testFindAll() {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class).returning(ResultType.ENTITY);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT con FROM ContentSpaceImpl AS con");
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test find by name.
	 */
	@Test
	public void testFindByName() {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class)
				.with(ContentSpaceRelation.having().names(NAME))
				.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { NAME };

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT con FROM ContentSpaceImpl AS con WHERE con.targetId = ?1", parameters);
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test to find all content space guids.
	 */
	@Test
	public void testQueryForFindAllGuids() {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class).returning(ResultType.GUID);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT con.guid FROM ContentSpaceImpl AS con");
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test to find all content space guids.
	 */
	@Test
	public void testQueryForFindByGuid() {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class)
			.with(ContentSpaceRelation.having().guids("guid"))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { "guid" };
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT con FROM ContentSpaceImpl AS con WHERE con.guid = ?1", parameters);
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test to find all content spaces like a given name.
	 */
	@Test
	public void testQueryForFindByNameLike() {
		QueryCriteria<ContentSpace> criteria = CriteriaBuilder.criteriaFor(ContentSpace.class)
			.with(ContentSpaceRelation.having().nameLike(NAME))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { WILDCARD_NAME };
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT con FROM ContentSpaceImpl AS con WHERE con.targetId LIKE ?1", parameters);
			}
		});

		queryService.query(criteria);
	}
}
