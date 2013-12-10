/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.service.changeset.BusinessObjectMetadataResolver;
import com.elasticpath.service.changeset.BusinessObjectResolver;
import com.elasticpath.service.changeset.ChangeSetDependencyResolver;
import com.elasticpath.service.changeset.dao.ChangeSetDao;

/**
 * Tests for {@link ChangeSetPolicyImpl}.
 */
public class ChangeSetPolicyImplTest {
	
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private ChangeSetPolicyImpl changeSetPolicy;
	
	/**
	 * A map of interface to object type mappings.
	 */
	private ChangeSetDao changeSetDao;

	/**
	 *
	 * @throws java.lang.Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		
		changeSetDao = context.mock(ChangeSetDao.class);
		
		changeSetPolicy = new ChangeSetPolicyImpl();
		
		changeSetPolicy.setChangeSetDao(changeSetDao);
	}
	
	/**
	 * Tests that a change set can be changed only in case it is in an active state.
	 */
	@Test
	public void testChangeNotAllowedWhenChangeSetNotActive() {
		final ChangeSet changeSetLocked = context.mock(ChangeSet.class);
		
		final String guid = "csGuid";
		context.checking(new Expectations() { {
			oneOf(changeSetLocked).getStateCode();
			will(returnValue(ChangeSetStateCode.LOCKED));
			
			oneOf(changeSetDao).findByGuid(guid);
			will(returnValue(changeSetLocked));

		} });
		
		assertFalse("The change set is LOCKED and should not be able to be modified", this.changeSetPolicy.isChangeAllowed(guid));
		
	}

	/**
	 * Tests that if the argument is null changeAllowed() would not throw exception.
	 */
	@Test
	public void testChangeNotAllowedWhenChangeSetNull() {
		final String guid = "csGuid";
		context.checking(new Expectations() { {
			
			oneOf(changeSetDao).findByGuid(guid);
			will(returnValue(null));

		} });
		
		this.changeSetPolicy.isChangeAllowed(guid);
		
	}
	
	/**
	 * Test that resolving metadata behaves as expected.
	 */
	@Test
	public void testResolveMetadata() {
		final BusinessObjectDescriptor descriptor = new BusinessObjectDescriptorImpl();
		Map<String, String> metaData = changeSetPolicy.resolveMetaData(descriptor);
		assertTrue("No metadata should be returned if there are no resolvers", metaData.isEmpty());
		
		final BusinessObjectMetadataResolver resolver1 = context.mock(BusinessObjectMetadataResolver.class, "resolver1");
		final BusinessObjectMetadataResolver resolver2 = context.mock(BusinessObjectMetadataResolver.class, "resolver2");
		List<BusinessObjectMetadataResolver> metadataResolvers = new ArrayList<BusinessObjectMetadataResolver>();
		metadataResolvers.add(resolver1);
		metadataResolvers.add(resolver2);
		changeSetPolicy.setMetadataResolvers(metadataResolvers);

		final Map<String, String> resolvedMetaData1 = new HashMap<String, String>();
		resolvedMetaData1.put("key1", "value1");

		final Map<String, String> resolvedMetaData2 = new HashMap<String, String>();
		resolvedMetaData1.put("key2", "value2");
		
		context.checking(new Expectations() {
			{
				oneOf(resolver1).resolveMetaData(descriptor); will(returnValue(resolvedMetaData1));
				oneOf(resolver2).resolveMetaData(descriptor); will(returnValue(resolvedMetaData2));
			}
		});
		
		metaData = changeSetPolicy.resolveMetaData(descriptor);
		assertEquals("There should be 2 metadata entries", 2, metaData.size());
		assertEquals("The first metadata value should be found", "value1", metaData.get("key1"));
		assertEquals("The second metadata value should be found", "value2", metaData.get("key2"));
	}
	
	/**
	 * Test get dependent objects.
	 */
	@Test
	public void testGetDependentObjects() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectType("Product");
		objectDescriptor.setObjectIdentifier("ProductCode");
		
		final Product product = new ProductImpl();
		final Category category = new CategoryImpl();
		final Set<Object> dependencdyObjects = new HashSet<Object>();
		dependencdyObjects.add(category);
		
		final BusinessObjectDescriptor dependencyObjectDescriptor = new BusinessObjectDescriptorImpl();
		dependencyObjectDescriptor.setObjectIdentifier("CategoryGuid");
		dependencyObjectDescriptor.setObjectType("Category");
		final Set<BusinessObjectDescriptor> dependencyObjectDescriptors = new HashSet<BusinessObjectDescriptor>();
		dependencyObjectDescriptors.add(dependencyObjectDescriptor);
		
		final ChangeSetDependencyResolver resolver = context.mock(ChangeSetDependencyResolver.class);
		List<ChangeSetDependencyResolver> changeSetDependentResolvers =  new ArrayList<ChangeSetDependencyResolver>();
		changeSetDependentResolvers.add(resolver);
		changeSetPolicy.setChangeSetDependentResolvers(changeSetDependentResolvers);
		
		final BusinessObjectResolver businessObjectResolver = context.mock(BusinessObjectResolver.class);
		changeSetPolicy.setBusinessObjectResolver(businessObjectResolver);
		
		context.checking(new Expectations() { {
			
			oneOf(resolver).getObject(objectDescriptor, Product.class);
			will(returnValue(product));
			
			oneOf(resolver).getChangeSetDependency(product);
			will(returnValue(dependencdyObjects));
			
			oneOf(businessObjectResolver).resolveObjectDescriptor(dependencdyObjects);
			will(returnValue(dependencyObjectDescriptors));

		} });
		
		Set<BusinessObjectDescriptor> retDependencyObjectDescriptors = changeSetPolicy.getDependentObjects(objectDescriptor, Product.class);
		BusinessObjectDescriptor retDependencyObjectDescriptor = retDependencyObjectDescriptors.iterator().next();
		assertEquals("get wrong dependency object", dependencyObjectDescriptor, retDependencyObjectDescriptor);
	}
	
	/**
	 * Test get dependent objects while it has multiple resolvers for parent - child objects.
	 */
	@Test
	public void testGetDependentObjectsWithInheriteResolver() {
		final ChangeSetDependencyResolver productResolver = context.mock(ChangeSetDependencyResolver.class, "productResolver");
		final ChangeSetDependencyResolver productBundleResolver = context.mock(ChangeSetDependencyResolver.class, "productBundleResolver");
		List<ChangeSetDependencyResolver> changeSetDependentResolvers =  new ArrayList<ChangeSetDependencyResolver>();
		changeSetDependentResolvers.add(productResolver);
		changeSetDependentResolvers.add(productBundleResolver);
		changeSetPolicy.setChangeSetDependentResolvers(changeSetDependentResolvers);
		
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectType("Product Bundle");
		objectDescriptor.setObjectIdentifier("ProductBundleCode");
		
		final Product product = new ProductImpl();
		final ProductBundle productBundle = new ProductBundleImpl();
		final Category category = new CategoryImpl();
		final Set<Object> dependencdyObjects1 = new HashSet<Object>();
		dependencdyObjects1.add(category);
		final Product constitutentProduct = new ProductImpl();
		final Set<Object> dependencdyObjects2 = new HashSet<Object>();
		dependencdyObjects1.add(constitutentProduct);
		final Set<Object> mergedDependencdyObjects = new HashSet<Object>();
		mergedDependencdyObjects.addAll(dependencdyObjects1);
		mergedDependencdyObjects.addAll(dependencdyObjects2);
		
		final BusinessObjectResolver businessObjectResolver = context.mock(BusinessObjectResolver.class);
		changeSetPolicy.setBusinessObjectResolver(businessObjectResolver);
		
		final Set<BusinessObjectDescriptor> dependencyObjectDescriptors = new HashSet<BusinessObjectDescriptor>();
		
		context.checking(new Expectations() { {
			
			oneOf(productResolver).getObject(objectDescriptor, ProductBundle.class);
			will(returnValue(product));
			
			oneOf(productBundleResolver).getObject(objectDescriptor, ProductBundle.class);
			will(returnValue(productBundle));
			
			oneOf(productResolver).getChangeSetDependency(productBundle);
			will(returnValue(dependencdyObjects1));
			
			oneOf(productBundleResolver).getChangeSetDependency(productBundle);
			will(returnValue(dependencdyObjects2));
			
			oneOf(businessObjectResolver).resolveObjectDescriptor(mergedDependencdyObjects);
			will(returnValue(dependencyObjectDescriptors));
			
			oneOf(businessObjectResolver).resolveObjectDescriptor(Collections.emptySet());
			will(returnValue(Collections.emptySet()));

		} });
		
		Set<BusinessObjectDescriptor> retDependencyObjectDescriptors = changeSetPolicy.getDependentObjects(objectDescriptor, ProductBundle.class);
		assertEquals(dependencyObjectDescriptors, retDependencyObjectDescriptors);
	}

}
