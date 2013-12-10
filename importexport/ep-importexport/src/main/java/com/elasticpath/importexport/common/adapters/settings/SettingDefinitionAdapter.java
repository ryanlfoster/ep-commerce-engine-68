package com.elasticpath.importexport.common.adapters.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.settings.DefaultValueDTO;
import com.elasticpath.importexport.common.dto.settings.MetadataDTO;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;


/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between 
 * <code>SettingDefinition</code> and <code>SettingDTO</code> objects.
 */
public class SettingDefinitionAdapter extends AbstractDomainAdapterImpl<SettingDefinition, SettingDTO> {

	@Override
	public void populateDTO(final SettingDefinition source, final SettingDTO target) {
		target.setNameSpace(source.getPath());
		target.setDescription(source.getDescription());
		target.setMaximumOverrides(source.getMaxOverrideValues());
		target.setDefaultValue(createDefaultValueDTO(source));
		target.setMetadataValues(createMetadataDTOList(source));
	}

	/**
	 * Creates a list of populated <code>MetadataDTO</code> objects based on meta data from <code>SettingDefinitions</code>.
	 * 
	 * @param source <code>SettingDefinition</code> object to retrieve meta data from
	 * @return list of <code>MetadataDTO</code>
	 */
	List<MetadataDTO> createMetadataDTOList(final SettingDefinition source) {
		final List<MetadataDTO> metadataValues = new ArrayList<MetadataDTO>();		

		for (SettingMetadata settingMetadata : source.getMetadata().values()) {
			metadataValues.add(createMetadataDTO(settingMetadata));
		}

		return metadataValues;
	}

	/**
	 * Creates and populates new instance of <code>MetadataDTO</code>.
	 * 
	 * @param settingMetadata <code>SettingMetadata</code> domain object
	 * @return populated <code>MetadataDTO</code> object
	 */
	MetadataDTO createMetadataDTO(final SettingMetadata settingMetadata) {
		MetadataDTO metadataDTO = new MetadataDTO();
		
		metadataDTO.setKey(settingMetadata.getKey());
		metadataDTO.setValue(settingMetadata.getValue());
		
		return metadataDTO;
	}

	/**
	 * Creates and populates new instance of <code>DefaultValueDTO</code>.
	 * 
	 * @param source <code>SettingDefinition</code> domain object
	 * @return populated <code>DefaultValueDTO</code> object
	 */
	DefaultValueDTO createDefaultValueDTO(final SettingDefinition source) {
		DefaultValueDTO defaultValue = new DefaultValueDTO();

		defaultValue.setType(source.getValueType());
		defaultValue.setValue(source.getDefaultValue());

		return defaultValue;
	}

	@Override
	public void populateDomain(final SettingDTO source, final SettingDefinition target) {
		target.setPath(source.getNameSpace());
		target.setDescription(source.getDescription());
		target.setMaxOverrideValues(source.getMaximumOverrides());
		target.setValueType(source.getDefaultValue().getType());
		target.setDefaultValue(source.getDefaultValue().getValue());
		setMetadata(target, source.getMetadataValues());
	}

	/**
	 * Sets metadata to the target.
	 * 1) Prepares new entries to set.
	 * 2) Adds those of old entries which doesn't exist.
	 * If there is new and old metadata with same key then new value will be set
	 * 
	 * @param target <code>SettingDefinition</code> to be populated
	 * @param metadataValues the list of additional meta data entries to put into the target
	 */
	void setMetadata(final SettingDefinition target, final List<MetadataDTO> metadataValues) {
		final Map<String, SettingMetadata> settingMetadata = new HashMap<String, SettingMetadata>(createSettingMetadataMap(metadataValues));

		if (target.getMetadata() != null) {
			for (String metadataKey : target.getMetadata().keySet()) {
				if (!settingMetadata.containsKey(metadataKey)) {
					settingMetadata.put(metadataKey, target.getMetadata().get(metadataKey));
				}
			}
		}

		target.setMetadata(settingMetadata);
	}

	/**
	 * Creates ant populates a <code>Map</code> between keys and <code>SettingMetadata</code> instances using <code>SettingDTO</code>. 
	 * 
	 * @param metadataValues the List of <code>MetadataDTO</code> to populate from.
	 * @return Map instance
	 */
	Map<String, SettingMetadata> createSettingMetadataMap(final List<MetadataDTO> metadataValues) {
		final Map<String, SettingMetadata> settingMetadataMap = new HashMap<String, SettingMetadata>();
		
		for (MetadataDTO metadataDTO : metadataValues) {
			settingMetadataMap.put(metadataDTO.getKey(), createSettingMetaData(metadataDTO));			
		}
		
		return settingMetadataMap;
	}

	/**
	 * Creates and populates new instance of <code>SettingMatadata</code> using metadataDTO.
	 * 
	 * @param metadataDTO the DTO to populate from
	 * @return populated <code>SettingMetadata</code>
	 */
	SettingMetadata createSettingMetaData(final MetadataDTO metadataDTO) {
		SettingMetadata settingMetadata = getBean(ContextIdNames.SETTING_METADATA);
		
		settingMetadata.setKey(metadataDTO.getKey());
		settingMetadata.setValue(metadataDTO.getValue());
		
		return settingMetadata;
	}
	
	@Override
	public SettingDTO createDtoObject() {
		return new SettingDTO();
	}
	
	@Override
	public SettingDefinition createDomainObject() {
		return getBean(ContextIdNames.SETTING_DEFINITION);
	}

}
