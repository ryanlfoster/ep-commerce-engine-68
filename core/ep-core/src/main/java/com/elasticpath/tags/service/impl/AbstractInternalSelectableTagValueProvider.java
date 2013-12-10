package com.elasticpath.tags.service.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.OrderingComparator;
import com.elasticpath.service.EpService;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagAllowedValue;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.domain.impl.SelectableValueImpl;
import com.elasticpath.tags.service.SelectableTagValueProvider;
/**
 * 
 * Internal retrieve selectable values using tag allowed values table.
 *
 * @param <VALUE> value type
 */
public abstract class AbstractInternalSelectableTagValueProvider<VALUE> extends AbstractEpServiceImpl 
	implements SelectableTagValueProvider<VALUE>, EpService {
	
	/**
	 * Adapt string value to VALUE type. 
	 * @param stringValue the value to adapt.
	 * @return VALUE
	 */
	protected abstract VALUE adaptString(String stringValue);
	
	/**
	 * Get the OrderingComparator.
	 * @return Instance of OrderingComparator.
	 */
	protected OrderingComparator getOrderingComparator() {
		return getBean(ContextIdNames.ORDERING_COMPARATOR);
	}

	/**
	 * Get the list of value-name pair for given locale and optional search criteria.
	 * @param tagValueType the tag value type, that request list of values
	 * @param locale the locale.
	 * @param searchCriteria - optional search criteria.
	 * @return list of {@link SelectableValue}s.
	 */	
	@Override
	public List<SelectableValue<VALUE>> getSelectableValues(
			final Locale locale, 
			final TagValueType tagValueType,
			final Map<?, ?> searchCriteria) {
		
		Set<TagAllowedValue> allowedValuesSet = tagValueType.getAllowedValues();
		
		if (allowedValuesSet != null && !allowedValuesSet.isEmpty()) {
			List<TagAllowedValue> allowedValues = Arrays.asList(allowedValuesSet.toArray(new TagAllowedValue[allowedValuesSet.size()]));
			Collections.sort(allowedValues, getOrderingComparator());
			List<SelectableValue<VALUE>> result = new ArrayList<SelectableValue<VALUE>>();
			for (TagAllowedValue tagAllowedValue : allowedValues) {
				SelectableValue<VALUE> selectableTagValue = new SelectableValueImpl<VALUE>(
						adaptString(tagAllowedValue.getValue()),
						tagAllowedValue.getLocalizedDescription(locale));
				result.add(selectableTagValue);
			}
			return result;
		}
		return null;
	}

}
