/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.catalog.SkuOptionKeyExistException;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>SkuOptionServiceImpl</code>.
 */
public class SkuOptionServiceImplTest extends AbstractEPServiceTestCase {
	private static final String SKU_OPTION_FIND_UIDPK_BY_KEY = "SKU_OPTION_FIND_UIDPK_BY_KEY";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private SkuOptionServiceImpl skuOptionService;

	private ProductDao mockProductDao;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		skuOptionService = new SkuOptionServiceImpl();
		skuOptionService.setPersistenceEngine(getPersistenceEngine());

		mockProductDao = context.mock(ProductDao.class);
		skuOptionService.setProductDao(mockProductDao);
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		skuOptionService.setPersistenceEngine(null);
		try {
			skuOptionService.add(new SkuOptionImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(skuOptionService.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.add(SkuOption)'.
	 */
	@Test
	public void testAdd() {
		final SkuOption skuOption = new SkuOptionImpl();
		final String key = "skuOptionKeyAdd";
		skuOption.setOptionKey(key);
		final List<Long> skuOptionUidPkList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
				will(returnValue(skuOptionUidPkList));

				oneOf(getMockPersistenceEngine()).save(with(same(skuOption)));
			}
		});

		skuOptionService.add(skuOption);
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.add(SkuOption)'.
	 */
	@Test
	public void testAddKeyExists() {
		try {
			final SkuOption skuOption = new SkuOptionImpl();
			final String key = "skuOptionKeyAdd";
			final long uidPk = 123456;
			skuOption.setUidPk(uidPk);
			skuOption.setOptionKey(key);
			final List<Long> skuOptionUidPkList = new ArrayList<Long>();
			skuOptionUidPkList.add(new Long(uidPk));
			context.checking(new Expectations() {
				{
					allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
					will(returnValue(skuOptionUidPkList));
				}
			});
			skuOptionService.add(skuOption);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final SkuOptionKeyExistException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		stubGetBean(ContextIdNames.SKU_OPTION, SkuOptionImpl.class);
		final long uid = 1234L;
		final SkuOption skuOption = new SkuOptionImpl();
		skuOption.setOptionKey("skuOptionKeyLoad");
		skuOption.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).load(SkuOptionImpl.class, uid);
				will(returnValue(skuOption));
			}
		});
		final SkuOption loadedSkuOption = skuOptionService.load(uid);
		assertSame(skuOption, loadedSkuOption);
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.update(SkuOption)'.
	 */
	@Test
	public void testUpdate() {
		final SkuOption skuOption = new SkuOptionImpl();
		final SkuOption updatedSkuOption = new SkuOptionImpl();
		final String key = "skuOptionUpdate";
		final long uidPk = 123456;
		skuOption.setUidPk(uidPk);
		skuOption.setOptionKey(key);
		updatedSkuOption.setUidPk(uidPk);
		updatedSkuOption.setOptionKey(key);
		final List<Long> skuOptionUidPkList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
				will(returnValue(skuOptionUidPkList));
			}
		});
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).update(with(same(skuOption)));
				will(returnValue(updatedSkuOption));

				final List<Long> productUids = Collections.singletonList(1L);
				oneOf(mockProductDao).findUidsBySkuOption(with(same(updatedSkuOption)));
				will(returnValue(productUids));

				oneOf(mockProductDao).updateLastModifiedTimes(productUids);
			}
		});

		SkuOption returnedSkuOption = skuOptionService.update(skuOption);
		assertSame(updatedSkuOption, returnedSkuOption);
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.update(SkuOption)'.
	 */
	@Test
	public void testUpdateKeyExists() {
		try {
			final SkuOption skuOption1 = new SkuOptionImpl();
			final String key1 = "skuOptionKeyUpdate1";
			skuOption1.setOptionKey(key1);
			skuOption1.setUidPk(1);
			final List<Long> skuOptionUidPkList = new ArrayList<Long>();
			skuOptionUidPkList.add(new Long(skuOption1.getUidPk()));
			final SkuOption skuOption2 = new SkuOptionImpl();
			final String key2 = "skuOptionKeyUpdate2";
			skuOption2.setOptionKey(key2);
			skuOption2.setUidPk(2);
			context.checking(new Expectations() {
				{
					allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key2);
					will(returnValue(skuOptionUidPkList));
				}
			});
			skuOptionService.update(skuOption2);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final SkuOptionKeyExistException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.remove(SkuOption)'.
	 */
	@Test
	public void testDelete() {
		final SkuOption skuOption = new SkuOptionImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(with(same(skuOption)));
			}
		});
		skuOptionService.remove(skuOption);
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final SkuOption skuOption1 = new SkuOptionImpl();
		skuOption1.setOptionKey("skuOptionKey1");
		final SkuOption skuOption2 = new SkuOptionImpl();
		skuOption2.setOptionKey("skuOptionKey2");
		final List<SkuOption> skuOptionList = new ArrayList<SkuOption>();
		skuOptionList.add(skuOption1);
		skuOptionList.add(skuOption2);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with("SKU_OPTION_SELECT_ALL_EAGER"), with(any(Object[].class)));
				will(returnValue(skuOptionList));
			}
		});
		final List<SkuOption> retrievedSkuOptionList = skuOptionService.list();
		assertEquals(skuOptionList, retrievedSkuOptionList);
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.findByKey(String)'.
	 */
	@Test
	public void testFindByKey() {
		final String key = "skuOptionKeyFind";
		final List<SkuOption> skuOptionList = new ArrayList<SkuOption>();

		final SkuOption skuOption = new SkuOptionImpl();
		skuOption.setOptionKey(key);
		skuOptionList.add(skuOption);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery("SKU_OPTION_FIND_BY_KEY", key);
				will(returnValue(skuOptionList));
			}
		});
		assertSame(skuOption, skuOptionService.findByKey(key));
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.findByKey(String)'.
	 */
	@Test
	public void testFindUidPkByKey() {
		final String key = "skuOptionKeyFind";
		final long uidPk = 123456;
		final List<Long> skuOptionUidPkList = new ArrayList<Long>();

		final SkuOption skuOption = new SkuOptionImpl();
		skuOption.setUidPk(uidPk);
		skuOption.setOptionKey(key);
		skuOptionUidPkList.add(new Long(skuOption.getUidPk()));
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
				will(returnValue(skuOptionUidPkList));
			}
		});
		assertEquals(uidPk, skuOptionService.findUidPkByKey(key));
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.keyExists(String)'.
	 */
	@Test
	public void testKeyExists() {
		final List<Long> skuOptionUidPkList = new ArrayList<Long>();
		final String key = "skuOptionKey";
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
				will(returnValue(skuOptionUidPkList));
			}
		});
		assertFalse(skuOptionService.keyExists(key));

		final long uidPk = 123456;
		skuOptionUidPkList.add(new Long(uidPk));
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
				will(returnValue(skuOptionUidPkList));
			}
		});
		assertTrue(skuOptionService.keyExists(key));
	}

	/**
	 * Test method for 'com.elasticpath.service.SkuOptionServiceImpl.keyExists(SkuOption)'.
	 */
	@Test
	public void testKeyExistsSkuOption() {
		final List<Long> skuOptionUidPkList = new ArrayList<Long>();
		final SkuOption skuOption = new SkuOptionImpl();
		final String key = "skuOptionKey";
		final long uidPk = 123456;
		final long uidPk2 = 234567;

		skuOption.setOptionKey(key);
		skuOption.setUidPk(uidPk);
		context.checking(new Expectations() {
			{

				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);

				will(returnValue(skuOptionUidPkList));
			}
		});
		assertFalse(skuOptionService.keyExists(skuOption));

		skuOptionUidPkList.add(new Long(uidPk2));
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(SKU_OPTION_FIND_UIDPK_BY_KEY, key);
				will(returnValue(skuOptionUidPkList));
			}
		});
		assertTrue(skuOptionService.keyExists(skuOption));
	}

	/**
	 * Test method for {@link SkuOptionServiceImpl#findAllSkuOptionFromCatalog(long)}.
	 */
	@Test
	public void testFindAllSkuOptionFromCatalog() {
		final List<SkuOption> skuOptionList = new ArrayList<SkuOption>();
		// expectations
		final SkuOption skuOption = new SkuOptionImpl();
		final long skuOptionUid = 1234L;
		skuOption.setUidPk(skuOptionUid);
		skuOptionList.add(skuOption);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SKU_OPTION_SELECT_CATALOG_ALL_EAGER"), with(any(Object[].class)));
				will(returnValue(skuOptionList));
			}
		});
		assertSame(skuOptionList, skuOptionService.findAllSkuOptionFromCatalog(skuOptionUid));
	}

	/**
	 * Test method for {@link SkuOptionServiceImpl#isSkuOptionInUse(long)}.
	 */
	@Test
	public void testIsSkuOptionInUse() {
		final long nonExistantSkuOptionUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SKU_OPTION_IN_USE_PRODUCT_TYPE_SINGLE"), with(any(Object[].class)));
				will(returnValue(new ArrayList<Long>()));
			}
		});
		assertFalse(skuOptionService.isSkuOptionInUse(nonExistantSkuOptionUid));

		final List<Long> skuOptionList = new ArrayList<Long>();
		skuOptionList.add(1L);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SKU_OPTION_IN_USE_PRODUCT_TYPE_SINGLE"), with(any(Object[].class)));
				will(returnValue(skuOptionList));
			}
		});
		assertTrue(skuOptionService.isSkuOptionInUse(nonExistantSkuOptionUid));
	}

	/**
	 * Test method for {@link SkuOptionServiceImpl#isSkuOptionValueInUse(long)}.
	 */
	@Test
	public void testIsSkuOptionValueInUse() {
		final long nonExistantSkuOptionValueUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SKU_OPTIONVALUE_IN_USE_PRODUCT_TYPE_SINGLE"), with(any(Object[].class)));
				will(returnValue(new ArrayList<Long>()));
			}
		});
		assertFalse(skuOptionService.isSkuOptionValueInUse(nonExistantSkuOptionValueUid));

		final List<Long> skuOptionValueList = new ArrayList<Long>();
		skuOptionValueList.add(1L);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SKU_OPTIONVALUE_IN_USE_PRODUCT_TYPE_SINGLE"), with(any(Object[].class)));
				will(returnValue(skuOptionValueList));
			}
		});
		assertTrue(skuOptionService.isSkuOptionValueInUse(nonExistantSkuOptionValueUid));
	}

	/**
	 * Test method for {@link SkuOptionServiceImpl#notifySkuOptionUpdated(SkuOption)}.
	 */
	@Test
	public final void testNotifySkuOptionUpdated() {
		final SkuOption mockSkuOption = context.mock(SkuOption.class);
		final List<Long> productUids = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(mockProductDao).findUidsBySkuOption(with(same(mockSkuOption)));
				will(returnValue(productUids));

				allowing(mockProductDao).updateLastModifiedTimes(productUids);
			}
		});

		skuOptionService.notifySkuOptionUpdated(mockSkuOption);
	}
}
