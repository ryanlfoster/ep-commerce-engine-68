package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.collections.CollectionUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.service.changeset.ObjectGuidResolver;

/**
 * Tests for {@link com.elasticpath.service.changeset.impl.BusinessObjectResolverImpl}.
 */
public class BusinessObjectResolverImplTest {
	/**
	 * 
	 */
	private static final String OBJECT_TYPE_CATEGORY = "Category";
	private static final String OBJECT_TYPE_PRODUCT = "Product";
	private static final String OBJECT_TYPE_CATALOG = "Catalog";
	private static final String OBJECT_TYPE_BASE_AMOUNT = "Base Amount";
	private static final String OBJECT_TYPE_PRODUCT_BUNDLE = "Product Bundle";
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private BeanFactory beanFactory;
	
	/**
	 * A map of interface to object type mappings.
	 */
	private Map<Class < ? >, String> objectTypes;
	private ObjectGuidResolver defaultObjectGuidResolver;
	private Map<String, ObjectGuidResolver> objectGuidResolvers;
	private ObjectGuidResolver categoryGuidResolver;
	private BusinessObjectResolverImpl businessObjectResolver;

	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		
		beanFactory = context.mock(BeanFactory.class);
		defaultObjectGuidResolver = context.mock(ObjectGuidResolver.class, "default object guid resolver");
		
		objectTypes = new LinkedHashMap<Class< ? >, String>();
		objectTypes.put(Product.class, OBJECT_TYPE_PRODUCT);
		objectTypes.put(Catalog.class, OBJECT_TYPE_CATALOG);
		objectTypes.put(Category.class, OBJECT_TYPE_CATEGORY);
		objectTypes.put(ProductBundle.class, OBJECT_TYPE_PRODUCT_BUNDLE);
		
		Map<Class< ? >, String> secondaryObjectTypes = new HashMap<Class< ? >, String>();
		secondaryObjectTypes.put(BaseAmountDTO.class, OBJECT_TYPE_BASE_AMOUNT);
		
		objectGuidResolvers = new HashMap<String, ObjectGuidResolver>();
		categoryGuidResolver = context.mock(ObjectGuidResolver.class, "category guid resolver");
		objectGuidResolvers.put(Category.class.getName(), categoryGuidResolver);
		objectGuidResolvers.put(BaseAmountDTO.class.getName(), new BaseAmountDTOGuidResolver());
		
		businessObjectResolver = new BusinessObjectResolverImpl();
		businessObjectResolver.setObjectTypes(objectTypes);
		businessObjectResolver.setSecondaryObjectTypes(secondaryObjectTypes);
		businessObjectResolver.setBeanFactory(beanFactory);
		businessObjectResolver.setDefaultObjectGuidResolver(defaultObjectGuidResolver);
		businessObjectResolver.setObjectGuidResolvers(objectGuidResolvers);
	}

	/**
	 * Tests that the policy is able to resolve entities to object type by using the object type (interface) map.
	 * 
	 * <p>For example, com.elasticpath.domain.catalog.Product resolves to object type "product".
	 * <br>and com.elasticpath.domain.catalog.Catalog resolves to "catalog".
	 */
	@Test
	public void testResolveObjectDescriptor() {
		
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		
		final Product product1 = new ProductImpl();
		product1.setGuid("123");
		
		final Catalog catalog1 = new CatalogImpl();
		catalog1.setGuid("456");
		
		final ProductBundle productBundle1 = new ProductBundleImpl();
		productBundle1.setGuid("789");
		
		context.checking(new Expectations() { {

		    final int expectTime = 3;
			exactly(expectTime).of(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		    will(returnValue(objectDescriptor));
		    
		    oneOf(defaultObjectGuidResolver).resolveGuid(product1);
		    will(returnValue(product1.getGuid()));
		    
		    oneOf(defaultObjectGuidResolver).resolveGuid(catalog1);
		    will(returnValue(catalog1.getGuid()));
		    
		    oneOf(defaultObjectGuidResolver).resolveGuid(productBundle1);
		    will(returnValue(productBundle1.getGuid()));
		} });
		
		// test product resolution
		BusinessObjectDescriptor result = businessObjectResolver.resolveObjectDescriptor(product1);
		String objectType = result.getObjectType();
		String objectId = result.getObjectIdentifier();
		
		assertEquals("Incorrect object type resolved.", OBJECT_TYPE_PRODUCT, objectType);
		assertEquals("Incorrect object ID resolved.", product1.getGuid(), objectId);
		
		// test catalog resolution
		result = businessObjectResolver.resolveObjectDescriptor(catalog1);
		objectType = result.getObjectType();
		objectId = result.getObjectIdentifier();
		
		assertEquals("Incorrect object type resolved.", OBJECT_TYPE_CATALOG, objectType);
		assertEquals("Incorrect object ID resolved.", catalog1.getGuid(), objectId);
		
		// test product bundle resolution
		result = businessObjectResolver.resolveObjectDescriptor(productBundle1);
		objectType = result.getObjectType();
		objectId = result.getObjectIdentifier();
		
		assertEquals("Incorrect object type resolved.", OBJECT_TYPE_PRODUCT_BUNDLE, objectType);
		assertEquals("Incorrect object ID resolved.", productBundle1.getGuid(), objectId);
	}
	
	/**
	 * Test the function to get sub class first set.
	 */
	@Test 
	public void testGetSubClassFirstSet() {
		LinkedHashSet<Class<?>> subClassLastSet = new LinkedHashSet<Class<?>>();
		
		subClassLastSet.add(Product.class);
		subClassLastSet.add(ProductBundle.class);
		
		SortedSet<?> subClassFirstSet = businessObjectResolver.getSubClassFirstSet(subClassLastSet);
		
		Iterator<?> iterator = subClassFirstSet.iterator();
		
		assertEquals("sub class should appear first", ProductBundle.class, iterator.next());
	}

	/**
	 * Test that we can resolve an object descriptor for a dto.
	 */
	@Test
	public void testResolveObjectDescriptorDto() {
		String guid = "BA123";
		final BaseAmountDTO baDTO = new BaseAmountDTO();
		baDTO.setGuid(guid);
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		context.checking(new Expectations() { {

		    exactly(1).of(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		    will(returnValue(objectDescriptor));
		} });
		BusinessObjectDescriptor bod = businessObjectResolver.resolveObjectDescriptor(baDTO);
		assertEquals("Expected guid to be same", guid, bod.getObjectIdentifier());
		assertEquals("Expected type for Base Amount", OBJECT_TYPE_BASE_AMOUNT, bod.getObjectType());
	}	

	/**
	 * Tests that if an object type can't be resolved, a null BusinessObjectDescriptor is returned.
	 */
	@Test
	public void testResolveObjectDescriptorNoMapping() {
		
		// remove all object type mappings so nothing resolves correctly
		businessObjectResolver.getObjectTypes().clear();
		businessObjectResolver.getSecondaryObjectTypes().clear();
		
		final Product product1 = new ProductImpl();
		product1.setGuid("123");
		
		// test product resolution
		BusinessObjectDescriptor result = businessObjectResolver.resolveObjectDescriptor(product1);
		assertNull("The resolved BusinessObjectDescriptor should be null.", result);
	}
	
	/**
	 * Only objects of type Entity can be resolved, since the GUID is required.
	 */
	@Test
	public void testResolveObjectDescriptorMustBeEntity() {
		
		// try to resolve an object that is not of type Entity and expect a null
		BusinessObjectDescriptor result = businessObjectResolver.resolveObjectDescriptor(Integer.valueOf(1));
		assertEquals("Business object must be of type com.elasticpath.domain.Entity and should not be resolveable. ", null, result);
	}
	/**
	 * Tests to resolve object guid. 
	 */
	@Test
	public void testResolveObjectGuid() {
		final CategoryImpl category = new CategoryImpl();
		category.setGuid("category1");
		
		context.checking(new Expectations() { {
			
			oneOf(categoryGuidResolver).isSupportedObject(category);
			will(returnValue(true));
			
			oneOf(categoryGuidResolver).resolveGuid(category);
			will(returnValue("categoryGuid|catalogGuid"));

		} });
		
		assertNotNull(businessObjectResolver.resolveObjectGuid(category));
	}
	
	/**
	 * Test that we can resolve the guid of a dto.
	 */
	@Test
	public void testResolveObjectGuidForDTO() {
		String guid = "BA123";
		final BaseAmountDTO baDTO = new BaseAmountDTO();
		baDTO.setGuid(guid);
		
		String resolvedGuid = businessObjectResolver.resolveObjectGuid(baDTO);
		assertEquals("Expected guid to be same", guid, resolvedGuid);
	}
	
	/**
	 * Tests to resolve object guid without guid resolver explicitly defined. 
	 */
	@Test
	public void testResolveObjectGuidWithoutGuidResolverExplicitly() {
		final ProductImpl product = new ProductImpl();
		final String productGuid = "product1";
		product.setGuid(productGuid);
		
		context.checking(new Expectations() { {
			
			oneOf(defaultObjectGuidResolver).resolveGuid(product);
			will(returnValue(productGuid));
		} });
		
		assertNotNull(businessObjectResolver.resolveObjectGuid(product));		
	}
	
	/**
	 * Tests to resolve object guid without any guid resolver explicitly defined. 
	 */
	@Test
	public void testResolveObjectGuidWithoutAnyResolverDefined() {
		businessObjectResolver.setObjectGuidResolvers(null);
		final ProductImpl product = new ProductImpl();
		final String productGuid = "product1";
		product.setGuid(productGuid);
		
		context.checking(new Expectations() { {
			
			oneOf(defaultObjectGuidResolver).resolveGuid(product);
			will(returnValue(productGuid));
		} });
		
		assertNotNull(businessObjectResolver.resolveObjectGuid(product));		
	}	

	/**
	 * Expected result for a null parameter value is null.
	 */
	@Test
	public void testGetClassForObjectWithNullArgument() {
		assertNull("Expected result for a null parameter value is null",  businessObjectResolver.getObjectClass(null));
	}
	
	/**
	 * Expecting that asking for an null type returns null value.
	 */
	@Test
	public void testGetClassForNullType() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		assertNull("expected result is null class", businessObjectResolver.getObjectClass(objectDescriptor));
	}
	
	/**
	 * Expecting that asking for an unkown type returns null value.
	 */
	@Test
	public void testGetClassForUnkownType() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectType("unkownType");
		assertNull("expected result is null class", businessObjectResolver.getObjectClass(objectDescriptor));
	}

	/**
	 * Expecting that asking for an expected class returns the class.
	 */
	@Test
	public void testGetClassForKnownType() {
		BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectType(OBJECT_TYPE_PRODUCT);
		Class< ? > clazz = businessObjectResolver.getObjectClass(objectDescriptor);
		assertNotNull("class should not be null", clazz);
		assertEquals("Product class should have been instantiated", Product.class, clazz);
	}	
	
	/**
	 * Test resolve object descriptor for a collection of objects.
	 */
	@Test
	public void testResolveObjectDescriptorForCollection() {
		final Product product1 = new ProductImpl();
		product1.setGuid("123");
		
		final Catalog catalog1 = new CatalogImpl();
		catalog1.setGuid("456");
		
		final ProductBundle productBundle1 = new ProductBundleImpl();
		productBundle1.setGuid("789");
		
		Set<Object> objects = new LinkedHashSet<Object>();
		objects.add(product1);
		objects.add(catalog1);
		objects.add(productBundle1);
		
		context.checking(new Expectations() { {
			oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		    will(returnValue(new BusinessObjectDescriptorImpl()));
		    
		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		    will(returnValue(new BusinessObjectDescriptorImpl()));
		    
		    oneOf(beanFactory).getBean(ContextIdNames.BUSINESS_OBJECT_DESCRIPTOR);
		    will(returnValue(new BusinessObjectDescriptorImpl()));
		    
		    oneOf(defaultObjectGuidResolver).resolveGuid(product1);
		    will(returnValue(product1.getGuid()));
		    
		    oneOf(defaultObjectGuidResolver).resolveGuid(catalog1);
		    will(returnValue(catalog1.getGuid()));
		    
		    oneOf(defaultObjectGuidResolver).resolveGuid(productBundle1);
		    will(returnValue(productBundle1.getGuid()));
		} });
		
		// test product resolution
		Set<BusinessObjectDescriptor> results = businessObjectResolver.resolveObjectDescriptor(objects);
		BusinessObjectDescriptor result = (BusinessObjectDescriptor) CollectionUtils.get(results, 0);
		String objectType = result.getObjectType();
		String objectId = result.getObjectIdentifier();
		assertEquals("Incorrect object type resolved", OBJECT_TYPE_PRODUCT, objectType);
		assertEquals("Incorrect object ID resolved", product1.getGuid(), objectId);
		
		// test catalog resolution
		result = (BusinessObjectDescriptor) CollectionUtils.get(results, 1);
		objectType = result.getObjectType();
		objectId = result.getObjectIdentifier();
		assertEquals("Incorrect object type resolved", OBJECT_TYPE_CATALOG, objectType);
		assertEquals("Incorrect object ID resolved", catalog1.getGuid(), objectId);
		
		// test product bundle resolution
		result = (BusinessObjectDescriptor) CollectionUtils.get(results, 2);
		objectType = result.getObjectType();
		objectId = result.getObjectIdentifier();
		assertEquals("Incorrect object type resolved", OBJECT_TYPE_PRODUCT_BUNDLE, objectType);
		assertEquals("Incorrect object ID resolved", productBundle1.getGuid(), objectId);
	}
	
	/**
	 * Test that passing a null to the resolver doesn't cause an exception but rather
	 * just returns a null result.
	 */
	@Test
	public void testResolveObjectWithNull() {
		Object nullObject = null;
		BusinessObjectDescriptor result = businessObjectResolver.resolveObjectDescriptor(nullObject);
		assertNull("A null object should be resolved to null", result);
	}
}
