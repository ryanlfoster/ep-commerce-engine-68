package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.misc.OrderingComparator;
import com.elasticpath.domain.misc.impl.OrderingComparatorImpl;
import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagAllowedValue;
import com.elasticpath.tags.domain.TagValueType;

/**
 * 
 * Test for AbstractSelectableInternalTagValueServiceImpl.
 *
 */
public class AbstractSelectableInternalTagValueServiceImplTest  {
	
	private final Locale locale = Locale.getDefault();
	
	private AbstractInternalSelectableTagValueProvider<Float> internalServiceImpl;
	
	private TagValueType noValuesTagValueType;
	
	private TagValueType timeZoneTagValueType;
	
	private final Set<TagAllowedValue> timeZones = new HashSet<TagAllowedValue>();
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Initializing test.
	 */
	@Before	
	public void setUp() {
		
		internalServiceImpl = new AbstractInternalSelectableTagValueProvider<Float>() {
			
			protected OrderingComparator getOrderingComparator() {
				return new OrderingComparatorImpl();
			}

			@Override
			protected Float adaptString(final String stringValue) {
				return Float.valueOf(stringValue);
			}
			
		};
		
		final TagAllowedValue zone1 = context.mock(TagAllowedValue.class, "zone1");		
		final TagAllowedValue zone2 = context.mock(TagAllowedValue.class, "zone2");		
		final TagAllowedValue zone3 = context.mock(TagAllowedValue.class, "zone3");
		
		timeZones.add(zone1);
		timeZones.add(zone2);
		timeZones.add(zone3);
		
		timeZoneTagValueType = context.mock(TagValueType.class, "timeZoneTagValueType");
		noValuesTagValueType = context.mock(TagValueType.class, "noValuesTagValueType");
		
		context.checking(new Expectations() {
			{
				allowing(zone1).getLocalizedDescription(locale);
				will(returnValue("zone1"));
				allowing(zone1).getValue();
				will(returnValue("-3"));
				allowing(zone1).getOrdering();
				will(returnValue(2));
				
				allowing(zone2).getLocalizedDescription(locale);
				will(returnValue("zone2"));
				allowing(zone2).getValue();
				will(returnValue("7.5"));
				allowing(zone2).getOrdering();
				will(returnValue(0));
				
				allowing(zone3).getLocalizedDescription(locale);
				will(returnValue("zone3"));
				allowing(zone3).getValue();
				will(returnValue("0"));
				allowing(zone3).getOrdering();
				will(returnValue(1));
				
				
				allowing(timeZoneTagValueType).getAllowedValues();
				will(returnValue(timeZones));
				
				allowing(noValuesTagValueType).getAllowedValues();
				will(returnValue(Collections.emptySet()));
				
				
			}
		});
		
		
	}
	
	/**
	 * Test, that service return allowed values for configured tag value type.	 
	 * Test, order of allowed values.  
	 */
	@Test
	public void testGetSelectableValues() {
		
		List<SelectableValue<Float>> allowedValues = 
			internalServiceImpl.getSelectableValues(locale, timeZoneTagValueType, null);
		
		// test , that we have values
		assertNotNull(allowedValues);
		
		//test that quantity of allowed values correspond to configured  
		assertEquals(timeZones.size(), allowedValues.size());
		
		//test, that order of allowed values
		assertEquals("zone2", allowedValues.get(0).getName());
		assertEquals("zone3", allowedValues.get(1).getName());
		assertEquals("zone1", allowedValues.get(2).getName());
		
		
	}
	
	/**
	 * TEst, that show if no selectable values configured, service will return null.
	 */
	@Test
	public void testGetNoSelectableValues() {
		List<SelectableValue<Float>> allowedValues = 
			internalServiceImpl.getSelectableValues(locale, noValuesTagValueType, null);
		
		assertNull(allowedValues);
		
	}
	


}
