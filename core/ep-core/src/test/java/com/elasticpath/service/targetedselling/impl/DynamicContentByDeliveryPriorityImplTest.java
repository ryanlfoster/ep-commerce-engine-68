package com.elasticpath.service.targetedselling.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;

/**
 * A test case for {@link DynamicContentByDeliveryPriorityImpl}.
 */
public class DynamicContentByDeliveryPriorityImplTest extends Assert {

	private DynamicContentByDeliveryPriorityImpl actionResolution;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Sets up the test case.
	 */
	@Before
	public void setUp() {
		actionResolution = new DynamicContentByDeliveryPriorityImpl();
	}

	/**
	 * test that if resolution algorithm is supplied with null of empty collection
	 * of deliveries a null is returned for dynamic content.
	 */
	@Test
	public void testResolveNullOrEmptyCollection() {
		Collection<DynamicContentDelivery> deliveries = Collections.emptySet();
		DynamicContent dynamicContent = actionResolution.resolveDynamicContent(deliveries);
		
		assertNull(dynamicContent);
		
		DynamicContent dynamicContent2 = actionResolution.resolveDynamicContent(null);
		
		assertNull(dynamicContent2);
	}
	
	/**
	 * Tests that two actions are resolved by their delivery's priority.
	 */
	@Test
	public void testResolveDeliveryByPriority() {

		final DynamicContentDelivery delivery2 = context.mock(DynamicContentDelivery.class, "delivery2");	
		final DynamicContent content21 = context.mock(DynamicContent.class, "content21");
		
		context.checking(new Expectations() { { 
			allowing(delivery2).getPriority(); will(returnValue(2));
			allowing(delivery2).getDynamicContent(); will(returnValue(content21));
		} });
		
		final DynamicContentDelivery delivery1 = context.mock(DynamicContentDelivery.class, "delivery1");	
		final DynamicContent content11 = context.mock(DynamicContent.class, "content11");
		
		context.checking(new Expectations() { { 
			allowing(delivery1).getPriority(); will(returnValue(1));
			allowing(delivery1).getDynamicContent(); will(returnValue(content11));
		} });

		Collection<DynamicContentDelivery> deliveries = Arrays.asList(delivery2, delivery1);
		DynamicContent dynamicContent = actionResolution.resolveDynamicContent(deliveries);
		
		assertEquals(content11, dynamicContent);
	}

	/**
	 * Tests that a delivery with no dynamic content  will return null.
	 */
	@Test
	public void testResolveActionsWithEmptyDelivery() {
		
		final DynamicContentDelivery delivery1 = context.mock(DynamicContentDelivery.class, "delivery1");	
		
		context.checking(new Expectations() { { 
			allowing(delivery1).getPriority(); will(returnValue(1));
			allowing(delivery1).getDynamicContent(); will(returnValue(null));
		} });
		
		Collection<DynamicContentDelivery> deliveries = Arrays.asList(delivery1);
		DynamicContent dynamicContent = actionResolution.resolveDynamicContent(deliveries);

		assertNull(dynamicContent);
	}
}
