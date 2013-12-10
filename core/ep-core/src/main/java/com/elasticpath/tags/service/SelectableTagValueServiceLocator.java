package com.elasticpath.tags.service;

import com.elasticpath.tags.domain.TagValueType;

/**
 * 
 * Locate particular service {@link SelectableTagValueProvider}, that return selectable value
 * for {@link TagValueType} .
 * 
 */
public interface SelectableTagValueServiceLocator {
	
	/**
	 * Get the {@link SelectableTagValueProvider} for given {@link TagValueType}.
	 * @param tagValueType given {@link TagValueType}.
	 * @param <T> the expected type of value object used within the returned {@link SelectableTagValueProvider}.
	 * @return particular {@link SelectableTagValueProvider} or null if service can not be located.
	 */
	<T> SelectableTagValueProvider<T> getSelectableTagValueProvider(TagValueType tagValueType);

}
