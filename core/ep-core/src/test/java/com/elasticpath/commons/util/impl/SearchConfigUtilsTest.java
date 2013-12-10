/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/**
 * Test the functionality of the methods in <code>SearchConfigUtils</code>.
 */
public class SearchConfigUtilsTest {

	/**
	 * Test map from null string.
	 */
	@Test
	public void testBoostMapFromNullString() {
				
		final String testString = null;	
		Map<String, Float> boostMap = SearchConfigUtils.boostMapFromString(testString);			
				
		assertEquals("Boost map is empty", MapUtils.EMPTY_MAP, boostMap);						
	}
	

	/**
	 * Test boost map from empty string.
	 */
	@Test
	public void testBoostMapFromEmptyString() {
	
		final String testString = StringUtils.EMPTY;	
		Map<String, Float> boostMap = SearchConfigUtils.boostMapFromString(testString);			
		
		assertEquals("Boost map is empty", MapUtils.EMPTY_MAP, boostMap);
	}
	
	/**
	 * Test that a string can be converted to a boost map.
	 */
	@Test
	public void testBoostMapFromString() {
		final Float productNameBoost = 0.2F;
		final Float brandNameBoost = 3.0F;
		final String testString = "productName=0.2,brandName=3.0";
		
		Map<String, Float> boostMap = SearchConfigUtils.boostMapFromString(testString);
		
		assertEquals("The map should contain 2 values", 2, boostMap.size());
		assertEquals("The boost value of productName should be 0.2", productNameBoost, boostMap.get("productName"));
		assertEquals("The boost value of brandName should be 3.0", brandNameBoost, boostMap.get("brandName"));
	}

	/**
	 * Test that a null boost map can be converted.
	 */
	@Test
	public void testNullBoostMapToString() {		

		Map<String, Float> boostMap = null;		
		
		String boostString = SearchConfigUtils.boostMapToString(boostMap);
		assertTrue(StringUtils.isEmpty(boostString));
	}
	
	/**
	 * Test that an empty boost map can be converted.
	 */
	@Test
	public void testEmptyBoostMapToString() {		

		Map<String, Float> boostMap = new HashMap<String, Float>();		
		
		String boostString = SearchConfigUtils.boostMapToString(boostMap);
		assertTrue(StringUtils.isEmpty(boostString));
	}		
	
	/**
	 * Test that a boost map can be converted to a string (and back).
	 */
	@Test
	public void testBoostMapToString() {
		final float startDateBoost = 0.2F;
		final float brandCodeBoost = 1.0F;

		Map<String, Float> boostMap = new HashMap<String, Float>();
		boostMap.put("startDate", startDateBoost);
		boostMap.put("brandCode", brandCodeBoost);
		
		String boostString = SearchConfigUtils.boostMapToString(boostMap);
		assertTrue("result string should contain expected startDate value", boostString.contains("startDate=0.2"));
		assertTrue("result string should contain expected brandCode value", boostString.contains("brandCode=1.0"));
		assertEquals("The string converted back to a map should be the same as the original map", 
				boostMap, SearchConfigUtils.boostMapFromString(boostString));
	}

	/**
	 * Test that a null string is handled correctly.
	 */
	@Test
	public void testAttributeExclusionSetFromNullString() {
		final String testString = null;
		
		Set<String> excluded = SearchConfigUtils.attributeExclusionSetFromString(testString);
		assertEquals("Attribute exclusion set is empty", SetUtils.EMPTY_SET, SetUtils.EMPTY_SET);
		assertEquals("The result set should contain the first attribute key", Boolean.FALSE, excluded.contains("A00140"));				
	}
	
	/**
	 * Test that a empty string is handled correctly.
	 */
	@Test
	public void testAttributeExclusionSetFromEmptyString() {
		final String testString = "";
		
		Set<String> excluded = SearchConfigUtils.attributeExclusionSetFromString(testString);
		assertEquals("Attribute exclusion set is empty", SetUtils.EMPTY_SET, SetUtils.EMPTY_SET);
		assertEquals("The result set should contain the first attribute key", Boolean.FALSE, excluded.contains("A00140"));				
	}
	
	/**
	 * Test that a string can be converted to a set of attribute keys.
	 */
	@Test
	public void testAttributeExclusionSetFromString() {
		final String testString = "A01282,A00140";
		
		Set<String> excluded = SearchConfigUtils.attributeExclusionSetFromString(testString);
		assertEquals("The set should contain 2 values", 2, excluded.size());
		assertTrue("The result set should contain the first attribute key", excluded.contains("A00140"));
		assertTrue("The result set should contain the second attribute key", excluded.contains("A01282"));
		
	}

	/**
	 * Test that a set of attribute keys can be converted to a string (and back).
	 */
	@Test
	public void testAttributeExclusionSetToString() {
		final String attribute1 = "A03519";
		final String attribute2 = "A00600";
		
		Set<String> attrSet = new HashSet<String>();
		attrSet.add(attribute1);
		attrSet.add(attribute2);
		
		String attrString = SearchConfigUtils.attributeExclusionSetToString(attrSet);
		assertTrue("result string should contain the first attribute", attrString.contains(attribute1));
		assertTrue("result string should contain the second attribute", attrString.contains(attribute2));
		assertEquals("The string converted back to a set should be the same as the original set",
				attrSet, SearchConfigUtils.attributeExclusionSetFromString(attrString));
	}

}
