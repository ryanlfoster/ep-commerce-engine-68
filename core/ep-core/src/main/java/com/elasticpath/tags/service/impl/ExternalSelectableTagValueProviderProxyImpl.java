package com.elasticpath.tags.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.SelectableTagValueProvider;

/**
 * 
 * External selectable values proxy. 
 *
 * @param <VALUE> value type
 */
public class ExternalSelectableTagValueProviderProxyImpl<VALUE> 
	implements SelectableTagValueProvider<VALUE> {
	
	private SelectableTagValueProvider<VALUE> selectableTagValueProvider = null;

	/**
	 * Get the list of value-name pair for given locale and optional search criteria.
	 * The call will be delegated to real provider if it configured
	 * @param tagValueType the tag value type, that request list of values
	 * @param locale the locale.
	 * @param searchCriteria - optional search criteria.
	 * @return list of {@link SelectableValue)s.
	 */	
	public List<SelectableValue<VALUE>> getSelectableValues(
			final Locale locale, final TagValueType tagValueType, final Map<?, ?> searchCriteria) {
		if (null != selectableTagValueProvider) {
			return selectableTagValueProvider.getSelectableValues(locale, tagValueType, searchCriteria);
		}
		return null;
	}

	/**
	 * Set the instance of SelectableTagValueProvider.
	 * @param selectableTagValueProvider values provider to set.
	 */
	public void setSelectableTagValueProvider(
			final SelectableTagValueProvider<VALUE> selectableTagValueProvider) {
		this.selectableTagValueProvider = selectableTagValueProvider;
	}



}
