package com.elasticpath.service.contentspace.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.contentspace.impl.ParameterValueImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryCriteria;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ParameterValueRelation;

/**
 * Test that {@see ParameterValueQueryService} behaves as expected.
 */
public class ParameterValueQueryServiceTest {

	private static final String NAME = "name";

	private final QueryService<ParameterValue> queryService = new ParameterValueQueryService();

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
		((ParameterValueQueryService) queryService).setBeanFactory(beanFactory);
		((ParameterValueQueryService) queryService).setPersistenceEngine(persistenceEngine);
		((ParameterValueQueryService) queryService).setFetchPlanHelper(fetchPlanHelper);

		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBeanImplClass(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_PARAMETER_VALUE);
				will(returnValue(ParameterValueImpl.class));
				allowing(fetchPlanHelper).clearFetchPlan();
			}
		});
	}

	/**
	 * Test find retrieval of all {@link ParameterValue}s.
	 */
	@Test
	public void testFindAll() {
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class).returning(ResultType.ENTITY);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT pv FROM ParameterValueImpl AS pv");
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test to find all parameter value guids.
	 */
	@Test
	public void testQueryForFindAllGuids() {
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class).returning(ResultType.GUID);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT pv.guid FROM ParameterValueImpl AS pv");
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test to find all parameter value guids.
	 */
	@Test
	public void testQueryForFindByGuid() {
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class)
			.with(ParameterValueRelation.having().guids("guid"))
			.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { "guid" };
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT pv FROM ParameterValueImpl AS pv WHERE pv.guid = ?1", parameters);
			}
		});

		queryService.query(criteria);
	}

	/**
	 * Test find by name.
	 */
	@Test
	public void testFindByName() {
		QueryCriteria<ParameterValue> criteria = CriteriaBuilder.criteriaFor(ParameterValue.class)
				.with(ParameterValueRelation.having().names(NAME))
				.returning(ResultType.ENTITY);

		final Object[] parameters = new Object[] { NAME };

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieve("SELECT pv FROM ParameterValueImpl AS pv WHERE pv.parameterName = ?1", parameters);
			}
		});

		queryService.query(criteria);
	}

}
