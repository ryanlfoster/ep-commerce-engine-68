/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.attribute.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.support.impl.DistinctAttributeValueCriterionImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>AttributeServiceImpl</code>.
 */
public class AttributeServiceImplTest extends AbstractEPServiceTestCase {
	private static final String ATTRIBUTE_FIND_BY_KEY = "ATTRIBUTE_FIND_BY_KEY";

	private static final long ATTRIBUTE_UID = 123L;

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private AttributeService attributeService;

	private PersistenceEngine mockPersistenceEngine;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		attributeService = new AttributeServiceImpl();
		mockPersistenceEngine = getMockPersistenceEngine();
		attributeService.setPersistenceEngine(mockPersistenceEngine);

		stubGetBean(ContextIdNames.ATTRIBUTE_USAGE, AttributeUsageImpl.class);
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		attributeService.setPersistenceEngine(null);
		try {
			attributeService.add(new AttributeImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(attributeService.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.add(Attribute)'.
	 */
	@Test
	public void testAdd() {
		final Attribute attribute = new AttributeImpl();
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		final String key = "attKeyAdd";
		attribute.setKey(key);
		attribute.setName("testName");

		final List<Attribute> attributeList = new ArrayList<Attribute>();
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));

				allowing(mockPersistenceEngine).retrieveByNamedQuery("ATTRIBUTE_FIND_BY_NAME_USAGE",
						attribute.getName(),
						attribute.getAttributeUsage().getValue());
				will(returnValue(attributeList));

				oneOf(mockPersistenceEngine).save(with(same(attribute)));
			}
		});

		attributeService.add(attribute);
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.add(Attribute)'.
	 */
	@Test
	public void testAddKeyExists() {
		try {
			final Attribute attribute = new AttributeImpl();
			final String key = "attKeyAdd";
			attribute.setKey(key);
			final List<Attribute> attributeList = new ArrayList<Attribute>();
			attributeList.add(attribute);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));
			}
		});
			attributeService.add(attribute);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final DuplicateKeyException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final Attribute attribute = new AttributeImpl();
		attribute.setKey("attKeyLoad");
		attribute.setUidPk(uid);
		stubGetBean(ContextIdNames.ATTRIBUTE, AttributeImpl.class);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(AttributeImpl.class, uid);
				will(returnValue(attribute));
			}
		});
		final Attribute loadedAttribute = attributeService.load(uid);
		assertSame(attribute, loadedAttribute);
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.update(Attribute)'.
	 */
	@Test
	public void testUpdate() {
		final Attribute attribute = new AttributeImpl();
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		final String key = "attKeyUpdate";
		attribute.setKey(key);
		attribute.setName("testName");
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));

				allowing(mockPersistenceEngine).retrieveByNamedQuery("ATTRIBUTE_FIND_BY_NAME_USAGE",
						attribute.getName(),
						attribute.getAttributeUsage().getValue());
				will(returnValue(attributeList));

				oneOf(mockPersistenceEngine).merge(with(same(attribute)));
				will(returnValue(null));
			}
		});

		attributeService.update(attribute);
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.update(Attribute)'.
	 */
	@Test
	public void testUpdateKeyExists() {
		try {
			final Attribute attribute1 = new AttributeImpl();
			final String key1 = "attKeyUpdate1";
			attribute1.setKey(key1);
			attribute1.setUidPk(1);
			final List<Attribute> attributeList = new ArrayList<Attribute>();
			attributeList.add(attribute1);
			final Attribute attribute2 = new AttributeImpl();
			final String key2 = "attKeyUpdate2";
			attribute2.setKey(key2);
			attribute2.setUidPk(2);
			context.checking(new Expectations() {
				{
					allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key2);
					will(returnValue(attributeList));
				}
			});
			attributeService.update(attribute2);
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final DuplicateKeyException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.remove(Attribute)'.
	 */
	@Test
	public void testDelete() {
		final Attribute attribute = new AttributeImpl();
		attribute.setAttributeUsage(AttributeUsageImpl.PRODUCT_USAGE);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(attribute)));
			}
		});
		attributeService.remove(attribute);
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getAttributeUsageMap()'.
	 */
	@Test
	public void testGetAttributeUsageMap() {
		Map<String, String> usageMap = attributeService.getAttributeUsageMap();
		Map<String, String> expectedMap = AttributeUsageImpl.getAttributeUsageMapInternal();

		assertEquals(expectedMap.size(), usageMap.size());
		for (String usageKey : expectedMap.keySet()) {
			assertNotNull(usageMap.get(usageKey));
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		final Attribute attribute1 = new AttributeImpl();
		attribute1.setKey("attKey1");
		final Attribute attribute2 = new AttributeImpl();
		attribute2.setKey("attKey2");
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		attributeList.add(attribute1);
		attributeList.add(attribute2);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(with("ATTRIBUTE_SELECT_ALL"), with(any(Object[].class)));
				will(returnValue(attributeList));
			}
		});
		final List<Attribute> retrievedAttributeList = attributeService.list();
		assertEquals(attributeList, retrievedAttributeList);
	}

	/**
	 * Test method for {@link AttributeServiceImpl#findAllCatalogOrGlobalAttributes(long)}.
	 */
	@Test
	public void testFindCatalogOrGlobalAttributes() {
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		// expectations
		final Attribute attribute = new AttributeImpl();
		final long catalogUid = 1234L;
		attributeList.add(attribute);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("ATTRIBUTE_SELECT_CATALOG_OR_GLOBAL_ALL"), with(any(Object[].class)));
				will(returnValue(attributeList));
			}
		});
		assertEquals(attributeList, attributeService.findAllCatalogOrGlobalAttributes(catalogUid));
	}

	/**
	 * Test method for {@link AttributeServiceImpl#findAllGlobalAttributes()}.
	 */
	@Test
	public void testFindAllGlobalAttributes() {
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("ATTRIBUTE_SELECT_GLOBAL_ALL"), with(any(Object[].class)));
				will(returnValue(attributeList));
			}
		});
		assertEquals(attributeList, attributeService.findAllGlobalAttributes());
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.findByKey(String)'.
	 */
	@Test
	public void testFindByKey() {
		final String key = "attKeyFind";
		final List<Attribute> attributeList = new ArrayList<Attribute>();

		final Attribute attribute = new AttributeImpl();
		attribute.setKey(key);
		attributeList.add(attribute);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));
			}
		});
		assertSame(attribute, attributeService.findByKey(key));
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.keyExists(String)'.
	 */
	@Test
	public void testKeyExists() {
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		final String key = "attKey";
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));
			}
		});
		assertFalse(attributeService.keyExists(key));

		final Attribute attribute = new AttributeImpl();
		attributeList.add(attribute);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));
			}
		});
		assertTrue(attributeService.keyExists(key));
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.keyExists(Attribute)'.
	 */
	@Test
	public void testKeyExistsAttribute() {
		final List<Attribute> attributeList = new ArrayList<Attribute>();
		final Attribute attribute = new AttributeImpl();
		final String key = "attKey";
		attribute.setKey(key);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));
			}
		});
		assertFalse(attributeService.keyExists(key));

		attributeList.add(attribute);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQuery(ATTRIBUTE_FIND_BY_KEY, key);
				will(returnValue(attributeList));
			}
		});
		assertTrue(attributeService.keyExists(key));
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getCategoryAttributes()'.
	 */
	@Test
	public void testGetCategoryAttributes() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("ATTRIBUTE_FIND_BY_USAGE", AttributeUsage.CATEGORY);
			}
		});
		attributeService.getCategoryAttributes();
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getProductAttributes()'.
	 */
	@Test
	public void testGetProductAttributes() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("ATTRIBUTE_FIND_BY_USAGE", AttributeUsage.PRODUCT);
			}
		});
		attributeService.getProductAttributes();
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getSkuAttributes()'.
	 */
	@Test
	public void testGetSkuAttributes() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("ATTRIBUTE_FIND_BY_USAGE", AttributeUsage.SKU);
			}
		});
		attributeService.getSkuAttributes();
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getSkuAttributes()'.
	 */
	@Test
	public void testGetDistinctAttributeValueList() {
		Attribute attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.INTEGER);
		attribute.setUidPk(ATTRIBUTE_UID);

		AttributeUsage attributeUsage = new AttributeUsageImpl();
		attributeUsage.setValue(AttributeUsage.PRODUCT);
		attribute.setAttributeUsage(attributeUsage);

		attributeService.setDistinctAttributeValueCriterion(new DistinctAttributeValueCriterionImpl());
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieve(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(new ArrayList<Attribute>()));
			}
		});
		attributeService.getDistinctAttributeValueList(attribute, "");
	}

	/**
	 * Test method for 'com.elasticpath.service.AttributeServiceImpl.getAttributeTypeMap()'.
	 */
	@Test
	public void testGetAttributeTypeMap() {
		Collection<AttributeType> fromType = AttributeType.values();
		Map<String, String> typeMap = attributeService.getAttributeTypeMap();
		assertEquals(fromType.size(), typeMap.size());
		for (final AttributeType type : fromType) {
			String value = typeMap.get(String.valueOf(type.getTypeId()));
			assertEquals(type.getNameMessageKey(), value);
		}
	}

	/**
	 * Test method for {@link AttributeServiceImpl#isInUse(long)}.
	 */
	@Test
	public void testIsInUse() {
		final long nonExistantAttributeUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("CATEGORY_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("SKU_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("PRODUCT_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("CUSTOMER_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));
			}
		});
		assertFalse(attributeService.isInUse(nonExistantAttributeUid));

		final List<Long> attributeUidList = new ArrayList<Long>();
		attributeUidList.add(1L);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("CATEGORY_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("SKU_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("PRODUCT_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(attributeUidList));

				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("CUSTOMER_ATTRIBUTE_IN_USE"), with(any(Object[].class)));
				will(returnValue(Arrays.<Object>asList()));
			}
		});
		assertTrue(attributeService.isInUse(nonExistantAttributeUid));
	}
}
