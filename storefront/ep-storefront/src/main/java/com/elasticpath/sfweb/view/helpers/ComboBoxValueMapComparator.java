package com.elasticpath.sfweb.view.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * A comparator to compare values of short text attributes.
 *
 */
public class ComboBoxValueMapComparator implements Comparator<String>, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> attrKeyValueMap;
	private final List<String> sortedValueList = new ArrayList<String>();


	/**
	 * Constructor.
	 * 
	 * @param attrKeyValueMap the map of attribute keys vs attribute values
	 */
	public ComboBoxValueMapComparator(final Map<String, String> attrKeyValueMap) {
		this.attrKeyValueMap = attrKeyValueMap;
		sortedValueList.addAll(attrKeyValueMap.values());
		Collections.sort(sortedValueList);
	}

	/**
	 * Compares to attributes.
	 * 
	 * @param key1 the first attribute key.
	 * @param key2 the second attribute key.
	 * @return an integer indicating the result of the comparison.
	 */
	public int compare(final String key1, final String key2) {
		final String value1 = attrKeyValueMap.get(key1);
		final String value2 = attrKeyValueMap.get(key2);

		if (sortedValueList.indexOf(value1) > sortedValueList.indexOf(value2)) {
			return 1;
		} else if (sortedValueList.indexOf(value1) == sortedValueList.indexOf(value2)) {
			return 0;
		}
		return -1;
	}
}