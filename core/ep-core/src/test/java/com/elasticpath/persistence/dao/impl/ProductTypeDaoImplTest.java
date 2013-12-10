package com.elasticpath.persistence.dao.impl;

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
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;

/**
 * Tests DAO operations on <code>ProductType</code>.
 */
public class ProductTypeDaoImplTest {

	private ProductType productType;
	private ProductTypeDaoImpl productTypeDao;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;
	private FetchPlanHelper mockFetchPlanHelper;
	private TimeService mockTimeService;
	private BeanFactory mockBeanFactory;

	/**
	 * Sets up each test.
	 * @throws Exception on failure.
	 */
	@Before
	public void setUp() throws Exception {
		productType = new ProductTypeImpl();
		productType.setName("name");
		productTypeDao = new ProductTypeDaoImpl() {
			@Override
			protected void throwExceptionIfDuplicate(final ProductType type) {
				// override this method to not call SQL
			}
		};
		
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		mockFetchPlanHelper = context.mock(FetchPlanHelper.class);
		mockTimeService = context.mock(TimeService.class);
		mockBeanFactory = context.mock(BeanFactory.class);
		
		productTypeDao.setPersistenceEngine(mockPersistenceEngine);
		productTypeDao.setFetchPlanHelper(mockFetchPlanHelper);
		productTypeDao.setTimeService(mockTimeService);
		productTypeDao.setBeanFactory(mockBeanFactory);
	}
	
	/**
	 * Test the finding of a product type by name method.
	 */
	@Test
	public void testFindProductType() {
		// test null name
		try {
			productTypeDao.findProductType(null);
			fail();
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		
		// test no matching name
		final List<ProductType> emptyList = new ArrayList<ProductType>();
		
		final ProductType productType = new ProductTypeImpl();
		productType.setName("No Such Name");
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_NAME", productType.getName());
				will(returnValue(emptyList));
			}
		});
		
		assertNull(productTypeDao.findProductType(productType.getName()));
		
		// test matching name
		final List<ProductType> list = new ArrayList<ProductType>();
		list.add(productType);
		
		productType.setName("Valid Name");
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_NAME", productType.getName());
				will(returnValue(list));
			}
		});
		
		assertNotNull(productTypeDao.findProductType(productType.getName()));
		
		// test inconsistent data
		final ProductType duplicateProductType = new ProductTypeImpl();
		list.add(duplicateProductType);
		
		productType.setName("Duplicate Name");
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("PRODUCT_TYPE_FIND_BY_NAME", productType.getName());
				will(returnValue(list));
			}
		});
		
		try {
			productTypeDao.findProductType(productType.getName());
			fail("EpServiceException must be thrown.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Tests the product type initialize method for a product type that does not exist yet.
	 */
	@Test
	public void testInitializeNoProduct() {
		// create a product with no product attributes
		final ProductType productType = new ProductTypeImpl();
		AttributeGroup group = new AttributeGroupImpl();
		Set<AttributeGroupAttribute> attributes = new HashSet<AttributeGroupAttribute>();
		group.setAttributeGroupAttributes(attributes);
		productType.setProductAttributeGroup(group);

		context.checking(new Expectations() {
			{
				oneOf(mockFetchPlanHelper).configureProductTypeFetchPlan(with(aNull(ProductTypeLoadTuner.class)));
				oneOf(mockPersistenceEngine).load(ProductTypeImpl.class, productType.getUidPk());
				will(returnValue(null));
				oneOf(mockFetchPlanHelper).clearFetchPlan();

				oneOf(mockBeanFactory).getBeanImplClass(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(ProductTypeImpl.class));
			}
		});
		
		assertNull(productTypeDao.initialize(productType));
	}

	/**
	 * Tests the product type initialize method using a product type that does exist.
	 */
	@Test
	public void testInitializeWithProduct() {
		// create a product type with attributes and SKU options
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
				oneOf(mockFetchPlanHelper).configureProductTypeFetchPlan(with(aNull(ProductTypeLoadTuner.class)));

				oneOf(mockPersistenceEngine).load(ProductTypeImpl.class, productType.getUidPk());
				will(returnValue(productType));

				oneOf(mockFetchPlanHelper).clearFetchPlan();

				oneOf(mockBeanFactory).getBeanImplClass(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(ProductTypeImpl.class));
			}
		});
		
				
		assertSame(productType, productTypeDao.initialize(productType));
	}
	
	/**
	 * Test method for {@link ProductTypeDaoImpl#isInUse(long)}.
	 */
	@Test
	public void testIsInUse() {
		final long nonExistantProductTypeUid = 123L;
		
		// test the return of an empty list, meaning, not in use
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("PRODUCT_TYPE_IN_USE", nonExistantProductTypeUid);
				will(returnValue(new ArrayList<Long>()));
			}
		});
		
		// an empty list means not in use
		assertFalse(productTypeDao.isInUse(nonExistantProductTypeUid));

		// test the return of single item list, meaning, is in use
		final List<Long> productTypeList = new ArrayList<Long>();
		productTypeList.add(1L);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("PRODUCT_TYPE_IN_USE", nonExistantProductTypeUid);
				will(returnValue(productTypeList));
			}
		});
		
		// a list with an ID means in use
		assertTrue(productTypeDao.isInUse(nonExistantProductTypeUid));
	}
	
	/**
	 * Tests listing of all used UIDs.
	 */
	@Test
	public void testListUsedUids() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("PRODUCT_TYPE_USED_UIDS"), with(any(Object[].class)));
			}
		});
		
		productTypeDao.listUsedUids();
	}
	
	/**
	 * Test method for {@link ProductTypeDaoImpl#findAllProductTypeFromCatalog(long)}.
	 */
	@Test
	public void testFindAllProductTypeFromCatalog() {
		final List<ProductType> productTypeList = new ArrayList<ProductType>();
		
		final ProductType productType = new ProductTypeImpl();
		final long productTypeUid = 1234L;
		productType.setUidPk(productTypeUid);
		productTypeList.add(productType);
		
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery("PRODUCT_TYPE_SELECT_CATALOG_ALL", productTypeUid);
				will(returnValue(productTypeList));
			}
		});
		
		assertSame("Invalid list returned.", productTypeList, productTypeDao.findAllProductTypeFromCatalog(productTypeUid));
	}
	
	/**
	 * Tests the named query for listing all product types.
	 */
	@Test
	public void testList() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(with("PRODUCT_TYPE_SELECT_ALL"), with(any(Object[].class)));
			}
		});

		productTypeDao.list();
	}
	
	/**
	 * Tests remove.
	 */
	@Test
	public void testRemove() {
		final ProductType productType = new ProductTypeImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(productType);
			}
		});
		
		productTypeDao.remove(productType);
	}
	
	/**
	 * Tests the get by UID.
	 */
	@Test
	public void testGet() {
		final long negativeUid = -1;
		context.checking(new Expectations() {
			{
				oneOf(mockBeanFactory).getBean(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(productType));
			}
		});
		ProductType productType = productTypeDao.get(negativeUid);
		
		assertFalse("The ProductType should not have been persisted. Negative UID.", productType.isPersisted());
		
		final long nonExistantProductTypeUid = 123L;
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).get(ProductTypeImpl.class, nonExistantProductTypeUid);
				will(returnValue(null));

				oneOf(mockBeanFactory).getBeanImplClass(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(ProductTypeImpl.class));
			}
		});
		
		productType = productTypeDao.get(nonExistantProductTypeUid);
		assertNull("get should return null for non-existent object", productType);
	}
	
	/**
	 * Tests the generic getObject method.
	 */
	@Test
	public void testGetObject() {
		final List<String> fieldsToLoad = new ArrayList<String>();
		context.checking(new Expectations() {
			{
				oneOf(mockFetchPlanHelper).addFields(ProductTypeImpl.class, fieldsToLoad);
				oneOf(mockFetchPlanHelper).clearFetchPlan();
				oneOf(mockBeanFactory).getBean(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(productType));
			}
		});
		
		productTypeDao.getObject(0, fieldsToLoad);
	}
	
	/**
	 * Tests persistence with a null <code>PersistenceEngine</code>.  
	 */
	@Test
	public void testNullPersistenceEngine() {
		productTypeDao.setPersistenceEngine(null);
		try {
			// this call should fail since the engine has not been set
			productTypeDao.add(new ProductTypeImpl());
			fail("Exception should have been thrown by add method.");
		} catch (final EpServiceException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Tests saving a <code>ProductType</code>.
	 */
	@Test
	public void testAdd() {
		final ProductType productType = new ProductTypeImpl();
		productType.setName("new name");
		productType.setDescription("description");
		productType.setTemplate("template");
		// other member variables are missing but shouldn't matter for testing purposes
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(productType);
			}
		});

		ProductType returnVal = productTypeDao.add(productType);
		
		assertSame("The ProductType should be persisted and returned.", productType, returnVal);
	}
	
	/**
	 * Tests persistence of a product type with null object.
	 * NOTE: This documents the current behaviour only, which is incorrect.
	 * This should throw EpPersistenceException instead in this scenario.
	 */
	@Test
	public void testNullSave() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(aNull(Persistable.class)));
			}
		});
		ProductType returnProductType = productTypeDao.add(null);
		
		assertSame("Adding nothing should have just returned nothing.", null, returnProductType);
	}
	
	/**
	 * Tests persistence of a product type with null name.
	 * NOTE: This documents the current behaviour only, which is incorrect.
	 * This should throw EpPersistenceException instead in this scenario.
	 */
	@Test
	public void testNullNameSave() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(productType);
			}
		});
		productType.setName(null);
		ProductType returnProductType = productTypeDao.add(productType);
		assertSame(productType, returnProductType);
	}
	
	/**
	 * Tests persistence of a product type with empty name.
	 * NOTE: This documents the current behaviour only, which is incorrect.
	 * This should throw EpPersistenceException instead in this scenario.
	 */
	@Test
	public void testEmptyNameSave() {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(productType);
			}
		});
		productType.setName(" ");
		ProductType returnProductType = productTypeDao.add(productType);
		assertSame(productType, returnProductType);
	}
	
	/**
	 * Test method for 'com.elasticpath.service.ProductTypeServiceImpl.update(Attribute)'.
	 * Simple test, doesn't really check the deleting of attribute values.
	 */
	@Test
	public void testUpdate() {
		final long uid = 9090L;

		final ProductType productType = getEmptyProductType(uid);
		productType.setProductAttributeGroupAttributes(new HashSet<AttributeGroupAttribute>());
		
		final ProductType updatedProductType = getEmptyProductType(uid);

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).merge(productType);
				will(returnValue(updatedProductType));

				oneOf(mockPersistenceEngine).load(ProductTypeImpl.class, productType.getUidPk());
				will(returnValue(productType));

				oneOf(mockPersistenceEngine).get(ProductTypeImpl.class, productType.getUidPk());
				will(returnValue(productType));

				oneOf(mockPersistenceEngine).executeNamedQuery(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(1));

				oneOf(mockFetchPlanHelper).configureProductTypeFetchPlan(with(aNull(ProductTypeLoadTuner.class)));
				oneOf(mockFetchPlanHelper).clearFetchPlan();

				oneOf(mockTimeService).getCurrentTime();

				exactly(2).of(mockBeanFactory).getBeanImplClass(ContextIdNames.PRODUCT_TYPE);
				will(returnValue(ProductTypeImpl.class));
			}
		});
		
		ProductType returnedProductType = productTypeDao.update(productType);
		assertSame(updatedProductType, returnedProductType);
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
	
}
