/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.impl.DynamicContentImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for DynamicContentImpl class.
 */
public class DynamicContentImplTest {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private DynamicContentServiceImpl dynamicContentServiceImpl;

	private static final String DYNAMIC_CONTENT_SELECT_ALL = "DYNAMIC_CONTENT_SELECT_ALL";

	private static final String DYNAMIC_CONTENT_FIND_BY_NAME = "DYNAMIC_CONTENT_FIND_BY_NAME";

	private static final String DYNAMIC_CONTENT_FIND_BY_NAME_LIKE = "DYNAMIC_CONTENT_FIND_BY_NAME_LIKE";

	private static final String DYNAMIC_CONTENT_SELECT_BY_WRAPPER_ID = "DYNAMIC_CONTENT_SELECT_BY_WRAPPER_ID";


	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		dynamicContentServiceImpl = new DynamicContentServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		dynamicContentServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.DYNAMIC_CONTENT);
				will(returnValue(new DynamicContentImpl()));
			}
		});
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test behaviour when the persistence engine is not set.
	 */
	@Test
	public void testPersistenceEngineIsNull() {
		dynamicContentServiceImpl.setPersistenceEngine(null);
		try {
			dynamicContentServiceImpl.add(new DynamicContentImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.campaign.impl.DynamicContentImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(dynamicContentServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.domain.campaign.impl.DynamicContentImpl.add(DynamicContent)'.
	 */
	@Test
	public void testAdd() {
		final DynamicContent dynamicContent = new DynamicContentImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(dynamicContent)));
			}
		});
		dynamicContentServiceImpl.add(dynamicContent);
	}

	/**
	 * Test method for finding DynamicContent by GUID.
	 */
	@Test
	public void testFindByGuid() {
		final String guid = "1234";

		final DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setGuid(guid);

		final List<DynamicContent> resultList = new ArrayList<DynamicContent>();
		resultList.add(dynamicContent);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_GUID", guid);
				will(returnValue(resultList));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.DYNAMIC_CONTENT);
				will(returnValue(DynamicContentImpl.class));
			}
		});

		final DynamicContent loadedDynamicContent = dynamicContentServiceImpl.findByGuid(guid);
		assertEquals(dynamicContent, loadedDynamicContent);
	}

	/**
	 * Test method for 'com.elasticpath.domain.campaign.impl.DynamicContentImpl.saveOrUpdate(DynamicContent)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		final DynamicContent dynamicContent = new DynamicContentImpl();
		final DynamicContent updatedDynamicContent = new DynamicContentImpl();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrUpdate(with(same(dynamicContent)));
				will(returnValue(updatedDynamicContent));
			}
		});
		dynamicContentServiceImpl.saveOrUpdate(dynamicContent);
	}

	/**
	 * Test method for 'com.elasticpath.domain.campaign.impl.DynamicContentImpl.remove(DynamicContent)'.
	 */
	@Test
	public void testRemove() {
		final DynamicContent dynamicContent = new DynamicContentImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(dynamicContent)));
			}
		});
		dynamicContentServiceImpl.remove(dynamicContent);
	}

	/**
	 * Test method for 'com.elasticpath.domain.campaign.impl.DynamicContentImpl.list()'.
	 */
	@Test
	public void testList() {
		final DynamicContent dynamicContent1 = new DynamicContentImpl();
		dynamicContent1.setName("aaa");
		final DynamicContent dynamicContent2 = new DynamicContentImpl();
		dynamicContent2.setName("bbb");
		final List<DynamicContent> cgList = new ArrayList<DynamicContent>();
		cgList.add(dynamicContent1);
		cgList.add(dynamicContent2);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(with(DYNAMIC_CONTENT_SELECT_ALL), with(any(Object[].class)));
				will(returnValue(cgList));
			}
		});
		final List<DynamicContent> retrievedCGList = dynamicContentServiceImpl.findAll();
		assertEquals(cgList, retrievedCGList);
	}

	/**
	 * Test method for 'com.elasticpath.domain.campaign.impl.DynamicContentImpl.nameExists(DynamicContent)'.
	 */
	@Test
	public void testNameExists() {
		final String name = "test name";
		final String existGroupName = name;
		final Object[] parameters = new Object[] { existGroupName };
		final DynamicContent dc1 = new DynamicContentImpl();
		final long uidPk1 = 1L;
		dc1.setUidPk(uidPk1);
		dc1.setName(name);
		final List<DynamicContent> dcList = new ArrayList<DynamicContent>();
		dcList.add(dc1);

		// Mock
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(DYNAMIC_CONTENT_FIND_BY_NAME, parameters);
				will(returnValue(dcList));
			}
		});

		// Test nameExists(DynamicContent)
		final DynamicContent dc2 = new DynamicContentImpl();
		dc2.setName(name);
		assertTrue(dynamicContentServiceImpl.exists(dc2));
		final long uidPk2 = 2L;
		dc2.setUidPk(uidPk2);
		assertTrue(dynamicContentServiceImpl.exists(dc2));
		assertFalse(dynamicContentServiceImpl.exists(dc1));
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerGroupServiceImpl.findByName(String)'.
	 */
	@Test
	public void testFindByName() {
		final String testName = "TEST NAME";
		final Object[] parameters = new Object[] { testName };
		final DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setName(testName);
		final List<DynamicContent> dcList = new ArrayList<DynamicContent>();
		dcList.add(dynamicContent);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(DYNAMIC_CONTENT_FIND_BY_NAME, parameters);
				will(returnValue(dcList));
			}
		});
		final DynamicContent retrievedDC = dynamicContentServiceImpl.findByName(testName);
		assertSame(dynamicContent, retrievedDC);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerGroupServiceImpl.findByNameLike(String)'.
	 */
	@Test
	public void testFindByNameLike() {
		final String testName = "%TEST%";
		final Object[] parameters = new Object[] { testName };
		final DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setName(testName);
		final List<DynamicContent> dcList = new ArrayList<DynamicContent>();
		dcList.add(dynamicContent);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(DYNAMIC_CONTENT_FIND_BY_NAME_LIKE, parameters);
				will(returnValue(dcList));
			}
		});

		final String testNameLike = "TEST";
		final List<DynamicContent> retrievedDC = dynamicContentServiceImpl.findByNameLike(testNameLike);
		assertSame(dcList, retrievedDC);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerGroupServiceImpl.findByWrapperId(String)'.
	 */
	@Test
	public void testFindWrapperId() {
		final String testWrapperId = "TEST_WRAPPER_ID";
		final String testName = "TEST NAME";
		final Object[] parameters = new Object[] { testWrapperId };
		final DynamicContent dynamicContent = new DynamicContentImpl();
		dynamicContent.setContentWrapperId(testWrapperId);
		dynamicContent.setName(testName);
		final List<DynamicContent> dcList = new ArrayList<DynamicContent>();
		dcList.add(dynamicContent);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(DYNAMIC_CONTENT_SELECT_BY_WRAPPER_ID, parameters);
				will(returnValue(dcList));
			}
		});
		final List<DynamicContent> retrievedDC = dynamicContentServiceImpl.getAllByContentWrapperId(testWrapperId);
		assertSame(dcList, retrievedDC);
	}
}
