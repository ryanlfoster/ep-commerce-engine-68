package com.elasticpath.importexport.common.dto.settings;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface for Settings.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "setting")
@XmlRootElement(name = SettingDTO.ROOT_ELEMENT)
public class SettingDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of root element in xml representation.
	 */
	public static final String ROOT_ELEMENT = "configuration_setting";
	
	@XmlElement(name = "name_space", required = true)
	private String nameSpace;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElement(name = "default_value", required = true)
	private DefaultValueDTO defaultValue;
	
	@XmlElement(name = "max_overrides")
	private int maximumOverrides;

	@XmlElementWrapper(name = "defined_values")
	@XmlElement(name = "value")
	private List<DefinedValueDTO> definedValues;
	
	@XmlElementWrapper(name = "setting_metadata")
	@XmlElement(name = "value")
	private List<MetadataDTO> metadataValues;

	/**
	 * Gets the value of nameSpace.
	 *
	 * @return the value of nameSpace.
	 */
	public final String getNameSpace() {
		return nameSpace;
	}

	/**
	 * Sets the value of nameSpace.    
	 *
	 * @param nameSpace the nameSpace to set
	 */
	public final void setNameSpace(final String nameSpace) {
		this.nameSpace = nameSpace;
	}

	/**
	 * Gets the value of description.
	 *
	 * @return the value of description.
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Sets the value of description.    
	 *
	 * @param description the description to set
	 */
	public final void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Gets the value of defaultValue.
	 *
	 * @return the value of defaultValue.
	 */
	public final DefaultValueDTO getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Sets the value of defaultValue.    
	 *
	 * @param defaultValue the defaultValue to set
	 */
	public final void setDefaultValue(final DefaultValueDTO defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the value of maximumOverrides.
	 *
	 * @return the value of maximumOverrides.
	 */
	public final int getMaximumOverrides() {
		return maximumOverrides;
	}

	/**
	 * Sets the value of maximumOverrides.    
	 *
	 * @param maximumOverrides the maximumOverrides to set
	 */
	public final void setMaximumOverrides(final int maximumOverrides) {
		this.maximumOverrides = maximumOverrides;
	}

	/**
	 * Gets the value of definedValues.
	 *
	 * @return the value of definedValues.
	 */
	public final List<DefinedValueDTO> getDefinedValues() {
		if (definedValues == null) {
			definedValues = Collections.emptyList();
		}
		return definedValues;
	}

	/**
	 * Sets the value of definedValues.    
	 *
	 * @param definedValues the definedValues to set
	 */
	public final void setDefinedValues(final List<DefinedValueDTO> definedValues) {
		this.definedValues = definedValues;
	}

	/**
	 * Gets the value of metadataValues.
	 *
	 * @return the value of metadataValues.
	 */
	public final List<MetadataDTO> getMetadataValues() {
		if (metadataValues == null) {
			metadataValues = Collections.emptyList();
		}
		return metadataValues;
	}

	/**
	 * Sets the value of metadataValues.    
	 *
	 * @param metadataValues the metadataValues to set
	 */
	public final void setMetadataValues(final List<MetadataDTO> metadataValues) {
		this.metadataValues = metadataValues;
	}
}
