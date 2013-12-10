package com.elasticpath.common.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * A simple key-value DTO where key is an attribute and value is an XmlValue.
 */
@XmlRootElement(name = "property")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class PropertyDTO {

	@XmlAttribute(name = "key", required = true)
	private String propertyKey;

	@XmlValue
	private String value;

	/** Constructor available for JAXB. */
	public PropertyDTO() {
		// Constructor available for JAXB.
	}

	/**
	 * Create a simple property (key-value) DTO.
	 * 
	 * @param propertyKey name of the key
	 * @param value string value associated with the key.
	 */
	public PropertyDTO(final String propertyKey, final String value) {
		this.propertyKey = propertyKey;
		this.value = value;
	}

	public String getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(final String key) {
		this.propertyKey = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
