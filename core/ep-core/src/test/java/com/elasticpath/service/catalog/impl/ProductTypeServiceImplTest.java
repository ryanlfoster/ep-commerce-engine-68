/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupImpl;
import com.elasticpath.domain.attribute.impl.ProductAttributeValueFactoryImpl;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.persistence.dao.ProductTypeDao;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>AttributeServiceImpl</code>.
 */
public class ProductTypeServiceImplTest extends AbstractEPServiceTestCase {

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private ProductTypeServiceImpl productTypeService;

	private ProductTypeDao mockProductTypeDao;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		productTypeService = new ProductTypeServiceImpl();

		mockProductTypeDao = context.mock(ProductTypeDao.class);

		productTypeService.setPersistenceEngine(getPersistenceEngine());
		productTypeService.setProductTypeDao(mockProductTypeDao);
	}

	/**
	 * Tests the service sanity check.
	 */
	@Test
	public void testSanityCheck() {
		productTypeService.setProductTypeDao(null);

		try {
			productTypeService.sanityCheck();

			fail("Sanity check should fail: ProductTypeDao is null.");

		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test the finding of a product type by name method.
	 */
	@Test
	public void testFindProductType() {
		// OLD: List<ProductType> typeList =
		//     getPersistenceEngine().retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_NAME", new String[] { name });

		// NEW: productTypeDao.findProductType(name);

		// the DAO to which this call delegates, throws an EpServiceException if the name is null
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).findProductType(null);
				will(throwException(new EpServiceException("")));
			}
		});

		try {
			productTypeService.findProductType(null);
			fail();
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		// test no matching name
		final String noSuchName = "No Such Name";

		final List<ProductType> list = new ArrayList<ProductType>();
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).findProductType(noSuchName);
				will(returnValue(null));
			}
		});

		assertNull(productTypeService.findProductType(noSuchName));

		// test matching name
		final String validName = "Valid Name";

		final ProductType productType = new ProductTypeImpl();
		list.add(productType);
		context.checking(new Expectations() {
			{

				oneOf(mockProductTypeDao).findProductType(validName);
				will(returnValue(productType));
			}
		});

		assertNotNull(productTypeService.findProductType(validName));

		// test inconsistent data
		final String dupeName = "Duplicate Name";
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).findProductType(dupeName);
				will(throwException(new EpServiceException("")));
			}
		});

		try {
			productTypeService.findProductType(dupeName);
			fail("EpServiceException must be thrown.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'ProductTypeServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetProductTypeDao() {
		productTypeService.setProductTypeDao(null);

		try {
			productTypeService.update(new ProductTypeImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.list()'.
	 */
	@Test
	public void testList() {
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).list();
			}
		});
		productTypeService.list();
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.list()'.
	 */
	@Test
	public void testListUsedUids() {
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).listUsedUids();
			}
		});

		productTypeService.listUsedUids();
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.add(Attribute)'.
	 */
	@Test
	public void testAdd() {
		final ProductType productType = new ProductTypeImpl();
		productType.setName("new name");
		productType.setDescription("description");
		productType.setTemplate("template");
		// other member variables are missing but shouldn't matter for testing purposes

		List<Integer> noDuplicates = new ArrayList<Integer>();
		noDuplicates.add(Integer.valueOf(0));
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).add(with(same(productType)));
			}
		});

		ProductType returnVal = productTypeService.add(productType);
		assertSame(productType, returnVal);
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.update(Attribute)'.
	 */
	// Simple test, doesn't really check the deleting of attribute values.
	public void testUpdate() {
		final long uid = 9090L;

		final ProductType productType = getEmptyProductType(uid);
		productType.setProductAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).update(with(same(productType)));
				will(returnValue(productType));
			}
		});

		ProductType returnedProductType = productTypeService.update(productType);
		assertSame(productType, returnedProductType);
	}

	private ProductType getEmptyProductType(final long uid) {
		ProductType productType = new ProductTypeImpl();
		productType.setName("new name");
		productType.setDescription("description");
		productType.setTemplate("template");
		productType.setUidPk(uid);

		// empty attributes
		AttributeGroup group = new AttributeGroupImpl();
		group.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		productType.setProductAttributeGroup(group);
		productType.setSkuAttributeGroup(group);

		return productType;
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.update(Attribute)'
	 * with a product attribute removed.
	 * Very brittle test and not a very good example.
	 * Should try to have a better way of creating test objects or extracting out methods
	 * so that the service would be easier to test.
	 */
	@Test
	public void testUpdateAttributesRemoved() {
		final long uid = 9090L;

		ProductType productType = new ProductTypeImpl();
		productType.setName("new name");
		productType.setDescription("description");
		productType.setTemplate("template");
		productType.setUidPk(uid);

		// empty attributes
		AttributeGroup emptyGroup = new AttributeGroupImpl();
		emptyGroup.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		productType.setSkuAttributeGroup(emptyGroup);

		// existing attribute
		AttributeGroup prodGroup = new AttributeGroupImpl();
		Set<AttributeGroupAttribute> groupSet = new HashSet<AttributeGroupAttribute>();
		AttributeGroupAttribute ata = new AttributeGroupAttributeImpl();
		ata.setAttribute(new AttributeImpl());
		groupSet.add(ata);
		prodGroup.setAttributeGroupAttributes(groupSet);
		productType.setProductAttributeGroup(prodGroup);

		// check that before product attributes exist
		Set<AttributeGroupAttribute> beforePAs = productType.getProductAttributeGroup().getAttributeGroupAttributes();
		assertEquals(1, beforePAs.size());

		List<Product> matchingProducts = new ArrayList<Product>();
		Product matchingProduct = new ProductImpl();
		AttributeValueGroup avg = new AttributeValueGroupImpl(new ProductAttributeValueFactoryImpl());
		avg.setAttributeValueMap(new HashMap<String, AttributeValue>());
		matchingProduct.setAttributeValueGroup(avg);

		matchingProduct.setGuid("0");
		matchingProducts.add(matchingProduct);

		// check that we are expecting one attribute to be removed
		final ProductType emptyProductType = getEmptyProductType(uid);
		final ProductType updatedProductType = getEmptyProductType(uid);
		Set<Attribute> removedAtts = emptyProductType.getProductAttributeGroup().getRemovedAttributes(beforePAs);
		assertEquals(1, removedAtts.size());

		// expectations
		List<Integer> noDuplicates = new ArrayList<Integer>();
		noDuplicates.add(Integer.valueOf(0));
		context.checking(new Expectations() {
			{
				atLeast(1).of(mockProductTypeDao).update(with(same(emptyProductType)));
				will(returnValue(updatedProductType));
			}
		});

		ProductType returnedProductType = productTypeService.update(emptyProductType);
		assertSame(updatedProductType, returnedProductType);
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.initialize(ProductType)'.
	 */
	@Test
	public void testInitializeNoProduct() {
		final ProductType productType = new ProductTypeImpl();
		AttributeGroup group = new AttributeGroupImpl();
		Set<AttributeGroupAttribute> attributes = new HashSet<AttributeGroupAttribute>();
		group.setAttributeGroupAttributes(attributes);
		productType.setProductAttributeGroup(group);

		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).initialize(productType);
				will(returnValue(null));
			}
		});

		assertNull(productTypeService.initialize(productType));
	}

	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.initialize(ProductType)'.
	 */
	@Test
	public void testInitializeWithProduct() {
		// one category type with one attribute
		final ProductType productType = new ProductTypeImpl();
		AttributeGroup group = new AttributeGroupImpl();
		Set<AttributeGroupAttribute> attributes = new HashSet<AttributeGroupAttribute>();
		AttributeGroupAttribute attributeGA = new AttributeGroupAttributeImpl();
		Attribute attribute = new AttributeImpl();
		attributeGA.setAttribute(attribute);
		attributes.add(attributeGA);
		group.setAttributeGroupAttributes(attributes);
		productType.setProductAttributeGroup(group);

		AttributeGroup skuGroup = new AttributeGroupImpl();
		skuGroup.setAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		productType.setSkuAttributeGroup(skuGroup);

		Set<SkuOption> skuOptions = new HashSet<SkuOption>();
		productType.setSkuOptions(skuOptions);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).initialize(productType);
				will(returnValue(productType));
			}
		});

		assertSame(productType, productTypeService.initialize(productType));
	}

	/**
	 * Test method for {@link ProductTypeServiceImpl#findAllProductTypeFromCatalog(long)}.
	 */
	@Test
	public void testFindAllBrandsFromCatalog() {
		final List<ProductType> productTypeList = new ArrayList<ProductType>();
		// expectations
		final ProductType productType = new ProductTypeImpl();
		final long productTypeUid = 1234L;
		productType.setUidPk(productTypeUid);
		productTypeList.add(productType);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).findAllProductTypeFromCatalog(with(any(long.class)));
				will(returnValue(productTypeList));
			}
		});

		assertSame(productTypeList, productTypeService.findAllProductTypeFromCatalog(1));
	}

	/**
	 * Test method for {@link ProductTypeServiceImpl#isInUse(long)}.
	 */
	@Test
	public void testIsInUse() {
		final long nonExistantProductTypeUid = 123L;
		context.checking(new Expectations() {
			{

				oneOf(mockProductTypeDao).isInUse(with(same(nonExistantProductTypeUid)));
				will(returnValue(false));
			}
		});

		assertFalse(productTypeService.isInUse(nonExistantProductTypeUid));
		context.checking(new Expectations() {
			{

				oneOf(mockProductTypeDao).isInUse(with(same(nonExistantProductTypeUid)));
				will(returnValue(true));
			}
		});

		assertTrue(productTypeService.isInUse(nonExistantProductTypeUid));
	}

	/**
	 * Tests the generic getObject method.
	 */
	@Test
	public void testGetObject() {
		// productTypeDao.getObject(uid, fieldsToLoad);

		final long nonExistantProductTypeUid = 123L;

		final Object result = new Object();

		final List<String> fieldsToLoad = new ArrayList<String>();
		fieldsToLoad.add("field name");
		context.checking(new Expectations() {
			{

				oneOf(mockProductTypeDao).getObject(with(nonExistantProductTypeUid), with(same(fieldsToLoad)));
				will(returnValue(result));
			}
		});

		productTypeService.getObject(nonExistantProductTypeUid, fieldsToLoad);
	}

	/**
	 * Tests remove.
	 */
	@Test
	public void testRemove() {
		final ProductType productType = new ProductTypeImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).remove(with(same(productType)));
			}
		});

		productTypeService.remove(productType);
	}

	/**
	 * Test method for {@link ProductTypeServiceImpl#get(long)}.
	 */
	@Test
	public void testGet() {
		final long nonExistantProductTypeUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(mockProductTypeDao).get(nonExistantProductTypeUid);
			}
		});

		productTypeService.get(nonExistantProductTypeUid);
	}


}
