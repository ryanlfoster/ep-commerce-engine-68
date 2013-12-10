/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Operations to assist when working with a <code>SearchConfig</code>.
 */
public final class SearchConfigUtils {

	private static final Logger LOG = Logger.getLogger(SearchConfigUtils.class);
	
	/**
	 * The delimiter between config elements in a string.
	 */
	public static final String ELEMENT_DELIMITER = ",";

	/**
	 * The delimiter between key/value pairs in a string.
	 */
	public static final String PAIR_DEMLIMITER = "=";

	/**
	 * SearchConfigUtils instances should NOT be constructed in standard programming. 
	 * Instead, the class should be used as <code>SearchConfigUtils.method();</code>
	 */
	private SearchConfigUtils() { }

	/**
	 * Returns a map of boost field names to boost values given a string that contains
	 * a set of boost elements, which consist of field name and boost value pairs.
	 * 
	 * @param boostString a string containing a comma separated list of boost elements.
	 * @return a <code>Map</code> of field name to boost value
	 */
	public static Map<String, Float> boostMapFromString(final String boostString) {
		
		Map<String, Float> boosts = new HashMap<String, Float>();
		String[] boostPairs = StringUtils.split(boostString, ELEMENT_DELIMITER);
		
		if (!ArrayUtils.isEmpty(boostPairs) && StringUtils.isNotEmpty(boostString)) {			 
					
			Float value = null;
			
			for (String pair : boostPairs) {
				
				try {
					value = Float.parseFloat(StringUtils.substringAfter(pair, PAIR_DEMLIMITER));
					boosts.put(StringUtils.substringBefore(pair, PAIR_DEMLIMITER), value);
				} catch (NumberFormatException nfe) {
					LOG.error("Invalid boost value, ignoring", nfe);
				}							
			}
		}
		return boosts;
	}
	
	/**
	 * Returns a string representation of a map of field names to boost values.
	 *
	 * @param boostMap the map of field names to boost values
	 * @return a string with comma delimited key/value pairs
	 */
	public static String boostMapToString(final Map<String, Float> boostMap) {
		Set<String> boostSet = new HashSet<String>();
		
		if (!MapUtils.isEmpty(boostMap)) {
			for (Map.Entry<String, Float> boost : boostMap.entrySet()) {
				boostSet.add(boost.getKey() + "=" + boost.getValue());
			}
		}
		return StringUtils.join(boostSet, ',');
	}
	
	/**
	 * Returns a set of attribute keys given a string that contains a delimited set.
	 * 
	 * @param attrExclusionString a string containing a comma separated set of attribute keys
	 * @return a <code>Set</code> of attribute keys
	 */
	public static Set<String> attributeExclusionSetFromString(final String attrExclusionString) {
		
		Set<String> exclusions = new HashSet<String>(); 
		
		if (StringUtils.isNotEmpty(attrExclusionString)) {
			String[] elements = StringUtils.split(attrExclusionString, ELEMENT_DELIMITER);
			exclusions.addAll(Arrays.asList(elements));
		}
		
		return exclusions;
	}
	
	/**
	 * Returns a string representation of a set of attribute keys.
	 * 
	 * @param attrExclusionSet the set of attribute keys
	 * @return a string with comma delimited attribute keys
	 */
	public static String attributeExclusionSetToString(final Set<String> attrExclusionSet) {
		return StringUtils.join(attrExclusionSet, ELEMENT_DELIMITER);
	}
	
}
