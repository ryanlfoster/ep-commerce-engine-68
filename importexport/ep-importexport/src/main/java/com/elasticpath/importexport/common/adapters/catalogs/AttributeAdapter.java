package com.elasticpath.importexport.common.adapters.catalogs;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.AttributeTypeType;
import com.elasticpath.importexport.common.dto.catalogs.AttributeUsageType;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>Attribute</code> and
 * <code>AttributeDTO</code> objects.
 */
public class AttributeAdapter extends AbstractDomainAdapterImpl<Attribute, AttributeDTO> {

	@Override
	public void populateDTO(final Attribute attribute, final AttributeDTO attributeDTO) {
		attributeDTO.setKey(attribute.getKey());
		attributeDTO.setName(attribute.getName());
		attributeDTO.setUsage(AttributeUsageType.valueOf(attribute.getAttributeUsage()));		
		attributeDTO.setType(AttributeTypeType.valueOf(attribute.getAttributeType()));		
		attributeDTO.setMultiLanguage(attribute.isLocaleDependant());
		attributeDTO.setRequired(attribute.isRequired());
		attributeDTO.setMultivalue(attribute.isMultiValueEnabled());		
		attributeDTO.setGlobal(attribute.isGlobal());
	}

	@Override
	public void populateDomain(final AttributeDTO attributeDTO, final Attribute attribute) {
		checkAttributeTypeForMultilanguage(attributeDTO.getKey(), attributeDTO.getType(), attributeDTO.getMultiLanguage());
		checkAttributeTypeForMultivalue(attributeDTO.getKey(), attributeDTO.getType(), attributeDTO.getMultivalue());
		
		attribute.setKey(attributeDTO.getKey());
		attribute.setName(attributeDTO.getName());
		attribute.setAttributeUsage(attributeDTO.getUsage().usage());		
		attribute.setAttributeType(attributeDTO.getType().type());
		attribute.setLocaleDependant(attributeDTO.getMultiLanguage());
		attribute.setRequired(attributeDTO.getRequired());
		attribute.setMultiValueEnabled(attributeDTO.getMultivalue());		
		attribute.setGlobal(attributeDTO.getGlobal());
	}
	
	private void checkAttributeTypeForMultivalue(final String key, final AttributeTypeType type, final boolean multivalue) {
		if (multivalue && type != AttributeTypeType.ShortText) {
			throw new PopulationRollbackException("IE-10002", key, type.toString());
		}
	}

	private void checkAttributeTypeForMultilanguage(final String key, final AttributeTypeType type, final boolean multiLanguageEnabled) {
		if (multiLanguageEnabled) {
			switch(type) {
			case Image:
				break;
			case ShortText:
				break;
			case LongText:
				break;
			case File:
				break;
			default:
				throw new PopulationRollbackException("IE-10003", key, type.toString());
			}
		}
	}

	@Override
	public Attribute createDomainObject() {
		return getBean(ContextIdNames.ATTRIBUTE);
	}

	@Override
	public AttributeDTO createDtoObject() {
		return new AttributeDTO();
	}
}
