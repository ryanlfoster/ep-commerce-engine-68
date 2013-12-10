package com.elasticpath.tags.service.impl;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.SelectableTagValueProvider;

/**
 * 
 * Test for SelectableTagValueServiceLocatorImpl.
 *
 */
public class SelectableTagValueServiceLocatorImplTest  {
	
	
	private TagValueType genderTagValueType;
	
	private TagValueType simpleTagValueType;
	
	private SelectableTagValueProvider<?> genderValueProvider;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final SelectableTagValueServiceLocatorImpl serviceLocatorImpl = new 
		SelectableTagValueServiceLocatorImpl();
	
	/**
	 * Initializing test.
	 */
	@Before	
	public void setUp() {
		
		genderTagValueType = context.mock(TagValueType.class, "genderTagValueType");
		
		simpleTagValueType = context.mock(TagValueType.class, "simpleTagValueType");
		
		genderValueProvider = context.mock(SelectableTagValueProvider.class, "genderValueProvider");
		
		context.checking(new Expectations() {
			{
				allowing(genderTagValueType).getGuid();
				will(returnValue("gender"));
				
				allowing(simpleTagValueType).getGuid();
				will(returnValue("simpleText"));
				
			}
			
		});
		
		serviceLocatorImpl.getValueProviders().put("gender", genderValueProvider);
		
	}
	
	/**
	 * Test, that localor can find value provider service for configured tag value type guid. 
	 */
	@Test
	public void testLocateService() {
		
		SelectableTagValueProvider<?> provider = serviceLocatorImpl.getSelectableTagValueProvider(genderTagValueType);
		
		assertNotNull(provider);
		
	}
	
	/**
	 * Test, that localor can find value provider service will return null for not nullable and 
	 * not configured tag value type. 
	 */
	@Test
	public void testLocateServiceNotFound() {
		
		SelectableTagValueProvider<?> provider = serviceLocatorImpl.getSelectableTagValueProvider(simpleTagValueType);
		
		assertNull(provider);
		
	}
	
	/**
	 * Test, that localor can find value provider service for configured tag value type guid. 
	 */
	@Test
	public void testLocateServiceNotFoundForNullType() {
		
		SelectableTagValueProvider<?> provider = serviceLocatorImpl.getSelectableTagValueProvider(null);
		
		assertNull(provider);
		
	}
	
	
	

}
