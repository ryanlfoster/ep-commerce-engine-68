package com.elasticpath.persistence.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.advancedsearch.AdvancedSearchQuery;
import com.elasticpath.domain.advancedsearch.impl.AdvancedSearchQueryImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;

/**
 * Tests DAO operations on <code>AdvancedSearchQueryDaoImpl</code>.
 * TODO: this test case contains not enough tests and should be enhanced
 */
public class AdvancedSearchQueryDaoTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private FetchPlanHelper mockFetchPlanHelper;

	private AdvancedSearchQueryDaoImpl advancedSearchQueryDao;

	/**
	 * Prepares elastic path, tested and mock objects.
	 * 
	 * @throws Exception if set up fails
	 */
	@Before
	public void setUp() throws Exception {
		advancedSearchQueryDao = new AdvancedSearchQueryDaoImpl();

		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		mockFetchPlanHelper = context.mock(FetchPlanHelper.class);

		final BeanFactory mockBeanFactory = context.mock(BeanFactory.class);
		context.checking(new Expectations() {
			{
				allowing(mockBeanFactory).getBeanImplClass(with(any(String.class)));
				will(returnValue(AdvancedSearchQueryImpl.class));

				allowing(mockBeanFactory).getBean(with(any(String.class)));
				will(returnValue(new AdvancedSearchQueryImpl()));
			}
		});
		
		advancedSearchQueryDao.setPersistenceEngine(mockPersistenceEngine);
		advancedSearchQueryDao.setFetchPlanHelper(mockFetchPlanHelper);
		advancedSearchQueryDao.setBeanFactory(mockBeanFactory);
	}

	/**
	 * Tests that advanced search query DAO uses persistence engine for saving and updating query.
	 */
	@Test
	public void testSaveOrUpdate() {
		final AdvancedSearchQuery advancedSearchQuery = new AdvancedSearchQueryImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrMerge(advancedSearchQuery);
				will(returnValue(advancedSearchQuery));
			}
		});
		advancedSearchQueryDao.saveOrUpdate(advancedSearchQuery);
	}
	
	/**
	 * Tests getter finding search query by impossible id.
	 */
	@Test
	public void testGet1() {
		AdvancedSearchQuery searchQuery = advancedSearchQueryDao.get(-1);
		assertNotNull(searchQuery);
	}

	/**
	 * Tests getter finding existing search query by its id. 
	 */
	@Test
	public void testGet2() {
		final long queryUidPk = 1234L;
		final AdvancedSearchQuery expectedSearchQuery = new AdvancedSearchQueryImpl();
		expectedSearchQuery.setUidPk(queryUidPk);
		context.checking(new Expectations() {
			{
				allowing(mockFetchPlanHelper).addField(with(any(Class.class)), with(any(String.class)));
				oneOf(mockFetchPlanHelper).clearFetchPlan();

				oneOf(mockPersistenceEngine).get(AdvancedSearchQueryImpl.class, queryUidPk);
				will(returnValue(expectedSearchQuery));
			}
		});
		AdvancedSearchQuery searchQuery = advancedSearchQueryDao.get(queryUidPk);
		assertNotNull(searchQuery);
		assertEquals(expectedSearchQuery.getUidPk(), searchQuery.getUidPk());
	}
}
