/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>AttributeServiceImpl</code>.
 */
public class CategoryTypeServiceImplTest extends AbstractEPServiceTestCase {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private CategoryTypeServiceImpl categoryTypeService;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		categoryTypeService = new CategoryTypeServiceImpl() {
			@Override
			protected void throwExceptionIfDuplicate(final CategoryType type) { //NOPMD
				//Overide this mothod for some odd Class cast error in this mothod
			}
		};

		categoryTypeService.setPersistenceEngine(getPersistenceEngine());

		categoryTypeService.setFetchPlanHelper(getFetchPlanHelper());
	}

	/**
	 * Test method for 'CategoryTypeServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		categoryTypeService.setPersistenceEngine(null);
		try {
			categoryTypeService.add(new CategoryTypeImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(categoryTypeService.getPersistenceEngine());
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.add(Attribute)'.
	 */
	@Test
	public void testAdd() {
		final CategoryType categoryType = new CategoryTypeImpl();
		categoryType.setName("new name");
		categoryType.setDescription("description");
		categoryType.setTemplate("template");

		CategoryTypeService categoryTypeService = new CategoryTypeServiceImpl() {
			@Override
			protected void throwExceptionIfDuplicate(final CategoryType type) { //NOPMD
				//Overide this mothod for some odd Class cast error in this mothod
			}
		};
		categoryTypeService.setPersistenceEngine(getPersistenceEngine());
		context.checking(new Expectations() {
			{
				List<Integer> noDuplicates = new ArrayList<Integer>();
				noDuplicates.add(Integer.valueOf(0));

				// checks for duplicate keys;
				allowing(getMockPersistenceEngine()).retrieveByNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(noDuplicates));

				oneOf(getMockPersistenceEngine()).save(with(same(categoryType)));
			}
		});

		CategoryType returnVal = categoryTypeService.add(categoryType);
		assertSame(categoryType, returnVal);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.update(Attribute)'.
	 */
	// Simple test, doesn't really check the deleting of attribute values.
	@Test
	public void testUpdate() {
		final long uid = 9090L;

		final CategoryType categoryType = new CategoryTypeImpl();
		categoryType.setName("new name");
		categoryType.setDescription("description");
		categoryType.setTemplate("template");
		categoryType.setUidPk(uid);

		AttributeGroup group = new AttributeGroupImpl();
		group.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		categoryType.setAttributeGroup(group);

		// expectations
		stubGetBean(ContextIdNames.CATEGORY_TYPE, CategoryTypeImpl.class);
		List<Integer> noDuplicates = new ArrayList<Integer>();
		noDuplicates.add(Integer.valueOf(0));

		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).merge(with(same(categoryType)));
				will(returnValue(null));

				allowing(getMockPersistenceEngine()).get(CategoryTypeImpl.class, uid);
				will(returnValue(categoryType));

				oneOf(getMockFetchPlanHelper()).configureCategoryTypeFetchPlan(with(aNull(CategoryTypeLoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		categoryTypeService.update(categoryType);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.remove(Attribute)'.
	 */
	@Test
	public void testDelete() {
		final CategoryType categoryType = new CategoryTypeImpl();
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(with(same(categoryType)));
			}
		});
		categoryTypeService.remove(categoryType);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.initialize(CategoryType)'.
	 */
	@Test
	public void testInitializeNoCategory() {
		stubGetBean(ContextIdNames.CATEGORY_TYPE, CategoryTypeImpl.class);

		final CategoryType categoryType = new CategoryTypeImpl();
		AttributeGroup group = new AttributeGroupImpl();
		Set<AttributeGroupAttribute> attributes = new HashSet<AttributeGroupAttribute>();
		group.setAttributeGroupAttributes(attributes);
		categoryType.setAttributeGroup(group);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(CategoryTypeImpl.class, categoryType.getUidPk());
				will(returnValue(null));

				oneOf(getMockFetchPlanHelper()).configureCategoryTypeFetchPlan(with(aNull(CategoryTypeLoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});

		assertNull(categoryTypeService.initialize(categoryType));
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.initialize(CategoryType)'.
	 */
	@Test
	public void testInitializeWithCategory() {
		stubGetBean(ContextIdNames.CATEGORY_TYPE, CategoryTypeImpl.class);

		// one category type with one attribute
		final CategoryType categoryType = new CategoryTypeImpl();
		AttributeGroup group = new AttributeGroupImpl();
		Set<AttributeGroupAttribute> attributes = new HashSet<AttributeGroupAttribute>();
		AttributeGroupAttribute attributeGA = new AttributeGroupAttributeImpl();
		Attribute attribute = new AttributeImpl();
		attributeGA.setAttribute(attribute);
		attributes.add(attributeGA);
		group.setAttributeGroupAttributes(attributes);
		categoryType.setAttributeGroup(group);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).get(CategoryTypeImpl.class, categoryType.getUidPk());
				will(returnValue(categoryType));

				oneOf(getMockFetchPlanHelper()).configureCategoryTypeFetchPlan(with(aNull(CategoryTypeLoadTuner.class)));
				oneOf(getMockFetchPlanHelper()).clearFetchPlan();
			}
		});


		assertSame(categoryType, categoryTypeService.initialize(categoryType));
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATEGORY_TYPE_SELECT_ALL"), with(any(Object[].class)));
			}
		});
		categoryTypeService.list();
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryTypeServiceImpl.list()'.
	 */
	@Test
	public void testListUsedUids() {
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATEGORY_TYPE_USED_UIDS"), with(any(Object[].class)));
			}
		});
		categoryTypeService.listUsedUids();
	}

	/**
	 * Test method for {@link CategoryTypeServiceImpl#findAllCategoryTypeFromCatalog(long)}.
	 */
	@Test
	public void testFindAllBrandsFromCatalog() {
		final List<CategoryType> categoryTypeList = new ArrayList<CategoryType>();
		// expectations
		final CategoryType categoryType = new CategoryTypeImpl();
		final long categoryTypeUid = 1234L;
		categoryType.setUidPk(categoryTypeUid);
		categoryTypeList.add(categoryType);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATEGORY_TYPE_SELECT_CATALOG_ALL"), with(any(Object[].class)));
				will(returnValue(categoryTypeList));
			}
		});
		assertSame(categoryTypeList, categoryTypeService.findAllCategoryTypeFromCatalog(categoryTypeUid));
	}

	/**
	 * Test method for {@link CategoryTypeServiceImpl#isInUse(long)}.
	 */
	@Test
	public void testIsInUse() {
		final long nonExistantCategoryTypeUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATEGORY_TYPES_IN_USE"), with(any(Object[].class)));
				will(returnValue(new ArrayList<Long>()));
			}
		});
		assertFalse(categoryTypeService.isInUse(nonExistantCategoryTypeUid));

		final List<Long> categoryTypeList = new ArrayList<Long>();
		categoryTypeList.add(1L);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("CATEGORY_TYPES_IN_USE"), with(any(Object[].class)));
				will(returnValue(categoryTypeList));
			}
		});
		assertTrue(categoryTypeService.isInUse(nonExistantCategoryTypeUid));
	}
}
