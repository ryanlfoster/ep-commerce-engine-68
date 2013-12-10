package com.elasticpath.sfweb.view.helpers;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * A comparator to compare values of boolean attributes.
 *
 */
public class RadioButtonValueMapComparator implements Comparator<String>, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<String, String> attributeKeyDisplayNameMap;

	private final Map<String, Boolean> displayNameToBooleanValueMap;

	/**
	 * Constructor.
	 * 
	 * @param attributeKeyDisplayNameMap the map of attribute keys vs display names
	 * @param displayNameToBooleanValueMap the map of display names vs booleans
	 */
	public RadioButtonValueMapComparator(final Map<String, String> attributeKeyDisplayNameMap,
			final Map<String, Boolean> displayNameToBooleanValueMap) {
		this.attributeKeyDisplayNameMap = attributeKeyDisplayNameMap;
		this.displayNameToBooleanValueMap = displayNameToBooleanValueMap;
	}

	/**
	 * Compares to attributes.
	 * 
	 * @param key1 the first attribute key.
	 * @param key2 the second attribute key.
	 * @return an integer indicating the result of the comparison.
	 */
	public int compare(final String key1, final String key2) {
		if (StringUtils.equalsIgnoreCase(key1, key2)) {
			return 0;
		} else if (StringUtils.isBlank(key1)) {
			return 1;
		} else if (!displayNameToBooleanValueMap.get(attributeKeyDisplayNameMap.get(key1))) {
			return 1;
		}
		return -1;
	}
}


