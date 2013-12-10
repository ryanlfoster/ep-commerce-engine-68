/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
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
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.impl.CustomerGroupImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.customer.GroupExistException;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test {@link CustomerGroupServiceImpl}.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class CustomerGroupServiceImplTest {
	private static final String CUSTOMERGROUP_FIND_BY_NAME = "CUSTOMERGROUP_FIND_BY_NAME";
	private static final String TEST_GROUP_NAME = "Test Group";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CustomerGroupService customerGroupServiceImpl;
	private PersistenceEngine persistenceEngine;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		customerGroupServiceImpl = new CustomerGroupServiceImpl();

		persistenceEngine = context.mock(PersistenceEngine.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		customerGroupServiceImpl.setPersistenceEngine(persistenceEngine);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test(expected = EpServiceException.class)
	public void testSetPersistenceEngine() {
		customerGroupServiceImpl.setPersistenceEngine(null);
		customerGroupServiceImpl.add(new CustomerGroupImpl());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(customerGroupServiceImpl.getPersistenceEngine());
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.add(CustomerGroup)'.
	 */
	@Test
	public void testAdd() {
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setName(TEST_GROUP_NAME);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).save(customerGroup);
				allowing(persistenceEngine).retrieveByNamedQuery(CUSTOMERGROUP_FIND_BY_NAME, customerGroup.getName());
				will(returnValue(Collections.emptyList()));
			}
		});

		this.customerGroupServiceImpl.add(customerGroup);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.update(CustomerGroup)'.
	 */
	@Test(expected = GroupExistException.class)
	public void testUpdate() {
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setUidPk(1L);
		customerGroup.setName(TEST_GROUP_NAME);
		final CustomerGroup updatedCustomerGroup = new CustomerGroupImpl();
		updatedCustomerGroup.setUidPk(1L);
		updatedCustomerGroup.setName(TEST_GROUP_NAME);

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery(with(containsString(CUSTOMERGROUP_FIND_BY_NAME)),
						with(equalTo(new Object[] { customerGroup.getName() })));
				will(returnValue(Collections.emptyList()));
				oneOf(persistenceEngine).merge(customerGroup);
				will(returnValue(updatedCustomerGroup));
			}
		});

		final CustomerGroup returnedCustomerGroup = customerGroupServiceImpl.update(customerGroup);
		assertSame(updatedCustomerGroup, returnedCustomerGroup);

		// test faliure
		final CustomerGroup customerGroup2 = new CustomerGroupImpl();
		customerGroup2.setUidPk(2L);
		customerGroup2.setName(TEST_GROUP_NAME);
		final List<CustomerGroup> cgList = new ArrayList<CustomerGroup>();
		cgList.add(customerGroup2);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(with(containsString(CUSTOMERGROUP_FIND_BY_NAME)),
						with(equalTo(new Object[] { customerGroup.getName() })));
				will(returnValue(cgList));
			}
		});

		customerGroupServiceImpl.update(customerGroup);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.delete(CustomerGroup)'.
	 */
	@Test
	public void testDelete() {
		final CustomerGroup customerGroup = new CustomerGroupImpl();

		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).delete(customerGroup);
			}
		});

		customerGroupServiceImpl.remove(customerGroup);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final CustomerGroup custmerGroup1 = new CustomerGroupImpl();
		custmerGroup1.setName("aaa");
		final CustomerGroup custmerGroup2 = new CustomerGroupImpl();
		custmerGroup2.setName("bbb");
		final List<CustomerGroup> cgList = new ArrayList<CustomerGroup>();
		cgList.add(custmerGroup1);
		cgList.add(custmerGroup2);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(with(containsString("CUSTOMERGROUP_SELECT_ALL")),
						with(any(Object[].class)));
				will(returnValue(cgList));
			}
		});
		final List<CustomerGroup> retrievedCGList = customerGroupServiceImpl.list();
		assertEquals(cgList, retrievedCGList);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerGroupServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setName("aaa");
		customerGroup.setUidPk(uid);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).load(CustomerGroupImpl.class, uid);
				will(returnValue(customerGroup));
				allowing(beanFactory).getBeanImplClass(ContextIdNames.CUSTOMER_GROUP);
				will(returnValue(CustomerGroupImpl.class));
			}
		});

		final CustomerGroup loadedCustomerGroup = customerGroupServiceImpl.load(uid);
		assertSame(customerGroup, loadedCustomerGroup);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerGroupServiceImpl.get(Long)'.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setName("aaa");
		customerGroup.setUidPk(uid);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).get(CustomerGroupImpl.class, uid);
				will(returnValue(customerGroup));
				allowing(beanFactory).getBeanImplClass(ContextIdNames.CUSTOMER_GROUP);
				will(returnValue(CustomerGroupImpl.class));
			}
		});

		final CustomerGroup loadedCustomerGroup = customerGroupServiceImpl.get(uid);
		assertSame(customerGroup, loadedCustomerGroup);
	}

	/**
	 * Test method for 'com.elasticpath.service.CustomerGroupServiceImpl.getDefaultGroup(Long)'.
	 */
	@Test
	public void testGetDefaultGroup() {
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setName(CustomerGroup.DEFAULT_GROUP_NAME);
		final List<CustomerGroup> cgList = new ArrayList<CustomerGroup>();
		cgList.add(customerGroup);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(CUSTOMERGROUP_FIND_BY_NAME,
						CustomerGroup.DEFAULT_GROUP_NAME);
				will(returnValue(cgList));
			}
		});

		final CustomerGroup loadedCustomerGroup = customerGroupServiceImpl.getDefaultGroup();
		assertSame(customerGroup, loadedCustomerGroup);
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.groupExists(String)'.
	 */
	@Test
	public void testGroupExists() {
		final String groupName = "test group";
		final String existGroupName = groupName;
		final CustomerGroup customerGroup1 = new CustomerGroupImpl();
		final long uidPk1 = 1L;
		customerGroup1.setUidPk(uidPk1);
		customerGroup1.setName(groupName);
		final List<CustomerGroup> cgList = new ArrayList<CustomerGroup>();
		cgList.add(customerGroup1);

		// Test emailExists(String)email
		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(with(containsString(CUSTOMERGROUP_FIND_BY_NAME)),
						with(equalTo(new Object[] { existGroupName })));
				will(returnValue(cgList));
			}
		});
		assertTrue(customerGroupServiceImpl.groupExists(existGroupName));

		// Test emailExists(Customer)
		final CustomerGroup customerGroup2 = new CustomerGroupImpl();
		customerGroup2.setName(groupName);
		assertTrue(customerGroupServiceImpl.groupExists(customerGroup2));
		final long uidPk2 = 2L;
		customerGroup2.setUidPk(uidPk2);
		assertTrue(customerGroupServiceImpl.groupExists(customerGroup2));
		assertFalse(customerGroupServiceImpl.groupExists(customerGroup1));
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.CustomerGroupServiceImpl.findByName(String)'.
	 */
	@Test
	public void testFindByGroup() {
		final String groupName = "TEST GROUP";
		final CustomerGroup customerGroup = new CustomerGroupImpl();
		customerGroup.setName(groupName);
		final List<CustomerGroup> cgList = new ArrayList<CustomerGroup>();
		cgList.add(customerGroup);

		context.checking(new Expectations() {
			{
				allowing(persistenceEngine).retrieveByNamedQuery(CUSTOMERGROUP_FIND_BY_NAME, groupName);
				will(returnValue(cgList));
			}
		});

		final CustomerGroup retrievedCG = customerGroupServiceImpl.findByGroupName(groupName);
		assertSame(customerGroup, retrievedCG);
	}
}
