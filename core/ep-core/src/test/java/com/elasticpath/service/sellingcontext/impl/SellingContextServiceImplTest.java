/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.sellingcontext.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
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
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test for DynamicContentImpl class.
 */
public class SellingContextServiceImplTest {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private SellingContextServiceImpl sellingContextServiceImpl;

	private static final String QUERY_FIND_ALL = "FIND_ALL_SELLING_CONTEXTS";
	private static final String QUERY_FIND_BY_GUID = "SELLING_CONTEXT_FIND_BY_GUID";

	@Before
	public void setUp() throws Exception {
		sellingContextServiceImpl = new SellingContextServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		sellingContextServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.SELLING_CONTEXT);
				will(returnValue(new SellingContextImpl()));
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
		sellingContextServiceImpl.setPersistenceEngine(null);
		try {
			sellingContextServiceImpl.saveOrUpdate(new SellingContextImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.sellingcontext.impl.SellingContextServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(sellingContextServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.domain.sellingcontext.impl.SellingContextServiceImpl.getObject(long)'.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final String guid = "1234";
		final SellingContext sellingContext = new SellingContextImpl();
		sellingContext.setUidPk(uid);
		sellingContext.setGuid(guid);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(SellingContextImpl.class, uid);
				will(returnValue(sellingContext));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.SELLING_CONTEXT);
				will(returnValue(SellingContextImpl.class));
			}
		});
		final SellingContext loadedSellingContext = (SellingContext) sellingContextServiceImpl.getObject(uid);
		assertEquals(sellingContext, loadedSellingContext);

	}

	/**
	 * Test method for 'com.elasticpath.domain.sellingcontext.impl.SellingContextServiceImpl.getByGuid(String)'.
	 */
	@Test
	public void testGetByGuid() {
		final String guid = "2345";
		final Object[] parameters = new Object[] { guid };
		final SellingContext sellingContext = new SellingContextImpl();
		sellingContext.setGuid(guid);

		final List<SellingContext> scList = new ArrayList<SellingContext>();
		scList.add(sellingContext);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(QUERY_FIND_BY_GUID, parameters);
				will(returnValue(scList));

				allowing(beanFactory).getBeanImplClass(ContextIdNames.SELLING_CONTEXT);
				will(returnValue(SellingContextImpl.class));
			}
		});
		final SellingContext loadedSellingContext = sellingContextServiceImpl.getByGuid(guid);
		assertSame(sellingContext, loadedSellingContext);

	}

	/**
	 * Test method for 'com.elasticpath.domain.sellingcontext.impl.SellingContextServiceImpl.saveOrUpdate(SellingContext)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		final SellingContext sellingContext = new SellingContextImpl();
		final SellingContext updatedSellingContext = new SellingContextImpl();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrUpdate(with(same(sellingContext)));
				will(returnValue(updatedSellingContext));
			}
		});
		sellingContextServiceImpl.saveOrUpdate(sellingContext);
	}

	/**
	 * Test method for 'com.elasticpath.domain.sellingcontext.impl.SellingContextServiceImpl.remove(SellingContext)'.
	 */
	@Test
	public void testRemove() {
		final SellingContext sellingContext = new SellingContextImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(sellingContext)));

				oneOf(mockPersistenceEngine).saveOrUpdate(with(same(sellingContext)));
				will(returnValue(sellingContext));
			}
		});
		sellingContextServiceImpl.remove(sellingContext);
	}

	/**
	 * Test method for 'com.elasticpath.domain.sellingcontext.impl.SellingContextServiceImpl.findAll()'.
	 */
	@Test
	public void testFindAll() {
		final SellingContext sellingContext1 = new SellingContextImpl();
		sellingContext1.setName("aaa");
		final SellingContext sellingContext2 = new SellingContextImpl();
		sellingContext2.setName("bbb");
		final List<SellingContext> scList = new ArrayList<SellingContext>();
		scList.add(sellingContext1);
		scList.add(sellingContext2);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(with(QUERY_FIND_ALL), with(any(Object[].class)));
				will(returnValue(scList));
			}
		});
		final List<SellingContext> retrievedCGList = sellingContextServiceImpl.findAll();
		assertEquals(scList, retrievedCGList);
	}

}
