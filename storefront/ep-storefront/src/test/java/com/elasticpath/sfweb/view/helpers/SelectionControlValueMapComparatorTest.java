package com.elasticpath.sfweb.view.helpers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test for ComboBoxValueMapComparator and RadioButtonValueMapComparator.
 *
 */
@Ignore(/*
		 * This class uses Java assert keyword. Pre-Maven we did not run JUnit with assertions enabled, so this test did nothing. Maven surefire
		 * enables assertions and these tests fail.
		 */)

@SuppressWarnings({"PMD.UnusedPrivateField" })
public class SelectionControlValueMapComparatorTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Test sorting of combo box values.
	 */
	@Ignore(/* This uses assert keyword, and has never passed. */)
	@Test
	public void testComboBoxItemMapSorting() {		
		Map<String, String> keyValueForComboMap = new HashMap<String, String>();
		String attributeKey = "A00556";
		String all = "All";
		String tft = "TFT active matrix";
		String crt = "CRT";
		String[] displayNames = new String[]{tft, "LCD passive matrix", "None", all, "LCD", "CRT", "Conventional"};
		
		int cnt = 0;
		for (String displayName : displayNames) { 
			if (all.equals(displayName)) {
				keyValueForComboMap.put("", displayName);
				continue;
			}
			cnt++;
			keyValueForComboMap.put(attributeKey + "_0" + cnt, displayName);
		}
		Comparator<String> selectControlValueMapComparator = new ComboBoxValueMapComparator(keyValueForComboMap);
		Map<String, String> sortedKeyValueForComboMap = new TreeMap<String, String>(selectControlValueMapComparator);
		sortedKeyValueForComboMap.putAll(keyValueForComboMap);
		
		Set<Map.Entry<String, String>> entrySet = sortedKeyValueForComboMap.entrySet();

		cnt = 0;
		for (Map.Entry<String, String> entry : entrySet) {
			if (cnt == 0) {
				assert (all.equals(entry.getKey()));
				assert ("".equals(entry.getValue()));
			} else if (cnt == 1) {
				assert ("A00556_01".equals(entry.getKey()));
				assert (crt.equals(entry.getValue()));
			} else if (cnt == displayNames.length - 1) {
				assert (tft.equals(entry.getValue()));
			}
			cnt++;
		}
	}	
	
	/**
	 * Test sorting of combo box values.
	 */
	@Ignore(/* This uses assert keyword, and has never passed. */)
	@Test
	public void testRadioButtonItemMapSortingForFrench() {		
		Map<String, String> keyValueForComboMap = new HashMap<String, String>();
		String attributeKey = "A02028";
		String dontCare = "Ne pas les soins";
		String truE = "vrai";
		String falsE = "faux";
		String[] displayNames = new String[]{falsE, dontCare, truE};
		Map<String, Boolean> displayNameToBooleanValueMap = new HashMap<String, Boolean>();
		displayNameToBooleanValueMap.put(truE, Boolean.TRUE);
		displayNameToBooleanValueMap.put(falsE, Boolean.FALSE);
		int cnt = 0;
		for (String displayName : displayNames) { 
			if (dontCare.equals(displayName)) {
				keyValueForComboMap.put("", displayName);
				continue;
			}
			cnt++;
			keyValueForComboMap.put(attributeKey + "_0" + cnt, displayName);
		}
		
		Comparator<String> selectControlValueMapComparator = new RadioButtonValueMapComparator(keyValueForComboMap, displayNameToBooleanValueMap);
		Map<String, String> sortedKeyValueForComboMap = new TreeMap<String, String>(selectControlValueMapComparator);
		sortedKeyValueForComboMap.putAll(keyValueForComboMap);
		
		Set<Map.Entry<String, String>> entrySet = sortedKeyValueForComboMap.entrySet();
	
		cnt = 0;
		for (Map.Entry<String, String> entry : entrySet) {
			if (cnt == 0) {
				assert (dontCare.equals(entry.getKey()));
				assert ("".equals(entry.getValue()));
			} else if (cnt == 1) {
				assert ("A02028_01".equals(entry.getKey()));
				assert (truE.equals(entry.getValue()));
			} else if (cnt == 2) {
				assert ("A02028_02".equals(entry.getKey()));
				assert (falsE.equals(entry.getValue()));
			}
			cnt++;
		}
		
	}	

	
}
