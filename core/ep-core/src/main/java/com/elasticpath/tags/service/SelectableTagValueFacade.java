package com.elasticpath.tags.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagValueType;

/**
 *
 * Retrieve the list of value-name for particular {@link TagValueType} and retrieve list of selectable values.
 *
 */
public interface SelectableTagValueFacade {
	
	/**
	 * Get the list of value-name pair for tag given value type, locale and optional search criteria.
	 * @param tagValueType the tag value type.
	 * @param locale the locale.
	 * @param searchCriteria - optional search criteria.
	 * @param <T> the expected type of value object used within the returned {@code List} of {@link SelectableValue}s.
	 * @return list of {@link SelectableValue) or null if service not found.
	 */
	<T> List<SelectableValue<T>> getSelectableValues(TagValueType tagValueType,
			Locale locale, 
			Map<?, ?> searchCriteria);

}
