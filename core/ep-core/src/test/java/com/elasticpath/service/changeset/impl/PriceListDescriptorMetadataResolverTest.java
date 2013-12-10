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
 * Test that price list descriptor metadata gets resolved as expected.
 */
public class PriceListDescriptorMetadataResolverTest {

	private PriceListDescriptorMetadataResolver resolver;
	
	private PersistenceEngine persistenceEngine;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Set up required for each test.
	 */
	@Before
	public void setUp()  {
		resolver = new PriceListDescriptorMetadataResolver();
		persistenceEngine = context.mock(PersistenceEngine.class);
		resolver.setPersistenceEngine(persistenceEngine);
	}

	/**
	 * Test validation behaves as required.
	 */
	@Test
	public void testIsValidResolverForObjectType() {
		assertTrue("Price List should be valid", resolver.isValidResolverForObjectType("Price List Descriptor"));
		assertFalse("Null should not be valid", resolver.isValidResolverForObjectType(null));
		assertFalse("Empty string should not be valid", resolver.isValidResolverForObjectType(StringUtils.EMPTY));
		assertFalse("A different object string should not be valid", resolver.isValidResolverForObjectType("Product"));
		assertFalse("Abritrary string should not be valid", resolver.isValidResolverForObjectType("anything"));
	}

	/**
	 * Test name metadata for a price list is retrieved by a named query.
	 */
	@Test
	public void testResolveMetaDataPriceList() {
		final String priceListGuid = "PRICELIST";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(priceListGuid);
		objectDescriptor.setObjectType("Price List Descriptor");
		
		final String name = "CAD Price List";
		final List<String> nameList = new ArrayList<String>();
		nameList.add(name);
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("PRICE_LIST_DESCRIPTOR_NAME_BY_GUID", priceListGuid);
				will(returnValue(nameList));
			}
		});
		
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be metadata returned", 1, metadata.size());
		assertEquals("The price list name should be as expected", name, metadata.get("objectName"));
	}

	/**
	 * Test no metadata returned if the query returns no results (e.g. price list does not exist). 
	 */
	@Test
	public void testResolveMetaDataNonExistentPriceList() {
		final String priceListGuid = "NOSUCHCODE";
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier(priceListGuid);
		objectDescriptor.setObjectType("Price List Descriptor");
		
		context.checking(new Expectations() {
			{
				oneOf(persistenceEngine).retrieveByNamedQuery("PRICE_LIST_DESCRIPTOR_NAME_BY_GUID", priceListGuid);
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
	public void testResolveMetaDataForNonPriceListObject() {
		final BusinessObjectDescriptor objectDescriptor = new BusinessObjectDescriptorImpl();
		objectDescriptor.setObjectIdentifier("WHATEVER");
		objectDescriptor.setObjectType("Product");
		Map<String, String> metadata = resolver.resolveMetaData(objectDescriptor);
		assertEquals("There should be no metadata returned", 0, metadata.size());
	}


}
