package com.elasticpath.importexport.common.adapters.dynamiccontent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.dynamiccontent.DynamicContentDTO;
import com.elasticpath.importexport.common.dto.dynamiccontent.ParameterValueDTO;

/**
 * Adapter for dynamic content responsible for translating between DynamicContent 
 * objects and DynamicContentDTO objects.
 */
public class DynamicContentAdapter extends AbstractDomainAdapterImpl<DynamicContent, DynamicContentDTO>  {
	
	private ParameterValueAdapter parameterValueAdapter;
	
	@Override
	public void populateDTO(final DynamicContent source, final DynamicContentDTO target) {
		target.setGuid(source.getGuid());
		target.setContentWrapperId(source.getContentWrapperId());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setParameterValues(populateParameterValueDtos(source));
	}

	private List<ParameterValueDTO> populateParameterValueDtos(final DynamicContent source) {
		List<ParameterValue> parameterValues = source.getParameterValues();
		ArrayList<ParameterValueDTO> parameterValueDtos = new ArrayList<ParameterValueDTO>();
		for (ParameterValue parameterValue : parameterValues) {
			ParameterValueDTO parameterValueDTO = parameterValueAdapter.createDtoObject();
			parameterValueAdapter.populateDTO(parameterValue, parameterValueDTO);
			parameterValueDtos.add(parameterValueDTO);
		}
		return parameterValueDtos;
	}

	@Override
	public void populateDomain(final DynamicContentDTO source, final DynamicContent target) {
		target.setGuid(source.getGuid());
		target.setContentWrapperId(source.getContentWrapperId());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		List<ParameterValue> parameterValues = populateParameterValues(source.getParameterValues(), target.getParameterValues());
		target.setParameterValues(parameterValues);
	}
	
	/**
	 * If a parameter value exists in target DB having the same guid of the parameter value dto,
	 * then the new parameter value populated from the dto can not be persisted in to DB due to 
	 * openjpa.persistence.EntityExistsException.
	 * In this case, need to get the parameter value using the guid from the DB first, 
	 * then populate it with the set of the DTO.
	 */
	private List<ParameterValue> populateParameterValues(
			final List<ParameterValueDTO> parameterValueDTOs,
			final List<ParameterValue> dbParameterValues) {
		Map<String, ParameterValue> parameterKeyValues = new HashMap<String, ParameterValue>();
		for (ParameterValue parameterValue : dbParameterValues) {
			parameterKeyValues.put(parameterValue.getGuid(), parameterValue);
		}
		
		List<ParameterValue> newParameterValues = new ArrayList<ParameterValue>();
		
		for (ParameterValueDTO parameterValueDTO : parameterValueDTOs) {
			ParameterValue parameterValue = parameterKeyValues.get(parameterValueDTO.getGuid());
			if (parameterValue == null) {
				parameterValue = parameterValueAdapter.createDomainObject();
			}
			// Populate the target domain object, preserving the JPA info if it came from the DB originally.
			parameterValueAdapter.populateDomain(parameterValueDTO, parameterValue);
			newParameterValues.add(parameterValue);
		}
		return newParameterValues;
	} 

	@Override
	public DynamicContent createDomainObject() {
		return getBean(ContextIdNames.DYNAMIC_CONTENT);
	}

	@Override
	public DynamicContentDTO createDtoObject() {
		return new DynamicContentDTO();
	}

	/**
	 * @param parameterValueAdapter the parameterValueAdapter to set
	 */
	public void setParameterValueAdapter(final ParameterValueAdapter parameterValueAdapter) {
		this.parameterValueAdapter = parameterValueAdapter;
	}

	/**
	 * @return the parameterValueAdapter
	 */
	public ParameterValueAdapter getParameterValueAdapter() {
		return parameterValueAdapter;
	}	
	
}
