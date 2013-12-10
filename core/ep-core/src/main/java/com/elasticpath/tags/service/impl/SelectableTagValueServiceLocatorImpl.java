package com.elasticpath.tags.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.SelectableTagValueProvider;
import com.elasticpath.tags.service.SelectableTagValueServiceLocator;

/**
 * 
 * Locate particular service {@LINK SelectableTagValueProvider}, that return selectable value
 * for {@link TagValueType} .
 * 
 */
public class SelectableTagValueServiceLocatorImpl implements SelectableTagValueServiceLocator {

	/**
	 * Value providers.
	 */
	private Map<String, SelectableTagValueProvider<?>> valueProviders = new HashMap<String, SelectableTagValueProvider<?>>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> SelectableTagValueProvider<T> getSelectableTagValueProvider(final TagValueType tagValueType) {
		if (null == tagValueType) {
			return null;
		}
		return (SelectableTagValueProvider<T>) valueProviders.get(tagValueType.getGuid());
	}

	/**
	 * Set the configured for {@link TagValueType} guid value provider.
	 * @param valueProviders value provider
	 */
	public void setValueProviders(
			final Map<String, SelectableTagValueProvider<?>> valueProviders) {
		this.valueProviders = valueProviders;
	}
	
	/**
	 * Get the map of value providers.
	 * @return map of value providers.
	 */
	public Map<String, SelectableTagValueProvider<?>> getValueProviders() {
		return valueProviders;
	}

}
