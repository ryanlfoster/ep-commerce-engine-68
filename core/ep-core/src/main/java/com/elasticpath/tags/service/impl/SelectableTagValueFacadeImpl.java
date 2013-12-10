package com.elasticpath.tags.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.SelectableTagValueProvider;
import com.elasticpath.tags.service.SelectableTagValueFacade;
import com.elasticpath.tags.service.SelectableTagValueServiceLocator;

/**
 *
 *
 * Retrieve service for particular {@link TagValueType} and retrieve list of selectable values.
 * 
 *
 */
public class SelectableTagValueFacadeImpl implements SelectableTagValueFacade {
	
	private SelectableTagValueServiceLocator selectableTagValueServiceLocator;	

	@Override
	public <T> List<SelectableValue<T>> getSelectableValues(
			final TagValueType tagValueType, 
			final Locale locale, 
			final Map<?, ?> searchCriteria) {
		List<SelectableValue<T>> selectableTagValues = null;

		SelectableTagValueProvider<T> selectableTagValueProvider =
			selectableTagValueServiceLocator.getSelectableTagValueProvider(tagValueType);

		if (null != selectableTagValueProvider) {
			selectableTagValues = selectableTagValueProvider.getSelectableValues(locale, tagValueType, searchCriteria);
		}

		return selectableTagValues;
	}

	/**
	 * Set the instance of SelectableTagValueServiceLocator.
	 * @param selectableTagValueServiceLocator to set.
	 */
	public void setSelectableTagValueServiceLocator(
			final SelectableTagValueServiceLocator selectableTagValueServiceLocator) {
		this.selectableTagValueServiceLocator = selectableTagValueServiceLocator;
	}

}
