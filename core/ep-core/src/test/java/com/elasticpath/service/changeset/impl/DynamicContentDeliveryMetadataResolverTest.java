/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.changeset.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectDescriptorImpl;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Test that dynamic content metadata gets resolved as expected.
 */
public class DynamicContentDeliveryMetadataResolverTest {

	private DynamicContentDeliveryMetadataResolver resolver;
	
	private PersistenceEngine persistenceEngine;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Set up required for each test.
	 */
	@Before
	public void setUp()  {
		resolver = new DynamicContentDeliveryMetadataResolver();
		persistenceEngine = context.mock(PersistenceEngine.class);
		resolver.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test validation behaves as required.
	 */
	@Test
	public void testIsValidResolverForObjectType() {
		assertTrue("Dynamic Content Delivery should be valid", resolver.isValidResolverForObjectType("Dynamic Content Delivery"));
		assertFalse("Null should not be valid", resolver.isValidResolverForObjectType(null));
		assertFalse("Empty string should not be valid", resolver.isValidResolverForObjectType(StringUtils.EMPTY));
		assertFalse("A different object string should not be valid", resolver.isValidResolverForObjectType("Product"));
		assertFalse("Abritrary string should not be valid", resolver.isValidResolverForObjectType("anything"));
	}

	/**
	 * Test name metadata for dynamic content delivery is retrieved by a named query.
	 */
	@Test
	public void testResolveMetaDataDynamicContentDeliveryAssignment() {
		final String dcdGuid = "DCD1";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(dcdGuid);
		objectDescriptor.setObjectType("Dynamic Content Delivery");
		
		final String name = "Test Content Delivery";
		final List<String> nameList = new ArrayList<String>();
		nameList.add(name);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("DYNAMIC_CONTENT_DELIVERY_NAME_BY_GUID", dcdGuid);
				will(returnValue(nameList));
			}
		});
		
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be metadata returned", 1, metadata.size());
		assertEquals("The dynamic content name should be as expected", name, metadata.get("objectName"));
	}

	/**
	 * Test no metadata returned if the query returns no results (e.g. DCD does not exist). 
	 */
	@Test
	public void testResolveMetaDataNonExistentDynamicContentDelivery() {
		final String dcdGuid = "NOSUCHCODE";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(dcdGuid);
		objectDescriptor.setObjectType("Dynamic Content Delivery");
		
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("DYNAMIC_CONTENT_DELIVERY_NAME_BY_GUID", dcdGuid);
				will(returnValue(Collections.emptyList()));
			}
		});
		
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}
	
	/**
	 * Test that no metadata is returned when the object is not a valid type.
	 */
	@Test
	public void testResolveMetaDataForNonDynamicContentDeliveryObject() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("WHATEVER");
		objectDescriptor.setObjectType("Product");
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}


}
