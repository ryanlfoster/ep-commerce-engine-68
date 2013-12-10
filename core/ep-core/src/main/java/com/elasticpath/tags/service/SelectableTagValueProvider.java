package com.elasticpath.tags.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagValueType;
/**
 * 
 * Retrieve selecteable values.
 *
 * @param <VALUE> value type
 */
public interface SelectableTagValueProvider<VALUE> {
	
	/**
	 * Get the list of value-name pair for given locale and optional search criteria.
	 * @param tagValueType the tag value type, that request list of values
	 * @param locale the locale.
	 * @param searchCriteria - optional search criteria.
	 * @return list of {@SelectableTagValue).
	 */
	List<SelectableValue<VALUE>> getSelectableValues(
			Locale locale, 
			TagValueType tagValueType,
			Map<?, ?> searchCriteria);

}
