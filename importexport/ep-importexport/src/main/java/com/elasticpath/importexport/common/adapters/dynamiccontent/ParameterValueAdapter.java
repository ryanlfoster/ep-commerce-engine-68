package com.elasticpath.importexport.common.adapters.dynamiccontent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ParameterLocaleDependantValue;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.dynamiccontent.ParameterValueDTO;

/**
 * This class is responsible for transforming data between <code>ParameterValue</code> 
 * and <code>ParameterValueDTO</code> objects.
 */
public class ParameterValueAdapter extends AbstractDomainAdapterImpl<ParameterValue, ParameterValueDTO> {

	@Override
	public void populateDTO(final ParameterValue source, final ParameterValueDTO target) {
		target.setGuid(source.getGuid());
		target.setDescription(source.getDescription());
		target.setParameterName(source.getParameterName());
		target.setLocalizable(source.isLocalizable());
		Map<String, ParameterLocaleDependantValue> values = source.getValues();
		List<DisplayValue> displayValues = new ArrayList<DisplayValue>();
		for (String language : values.keySet()) {
			DisplayValue displayValue = new DisplayValue();
			displayValue.setLanguage(language);
			ParameterLocaleDependantValue parameterLocaleDependantValue = values.get(language);
			displayValue.setValue(parameterLocaleDependantValue.getValue());
			displayValues.add(displayValue);
		}
		target.setValues(displayValues);
	}

	@Override
	public void populateDomain(final ParameterValueDTO source, final ParameterValue target) {
		target.setGuid(source.getGuid());
		target.setDescription(source.getDescription());
		target.setParameterName(source.getParameterName());
		target.setLocalizable(source.isLocalizable());
		for (DisplayValue displayValue : source.getValues()) {
			target.setValue(displayValue.getValue(), displayValue.getLanguage());
		}
	}

	@Override
	public ParameterValue createDomainObject() {
		return getBean(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_PARAMETER_VALUE);
	}

	@Override
	public ParameterValueDTO createDtoObject() {
		return new ParameterValueDTO();
	}
}

