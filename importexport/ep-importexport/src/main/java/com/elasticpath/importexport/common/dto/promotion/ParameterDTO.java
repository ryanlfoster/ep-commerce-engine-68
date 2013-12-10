package com.elasticpath.importexport.common.dto.promotion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.elasticpath.common.dto.Dto;

/**
 * Contains XML mapping for <code>RuleParameter</code> of Promotion Rule Element object. Designed for JAXB
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "parameter")
public class ParameterDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "key", required = true)
	private String key;

	@XmlElement(name = "value", required = true)
	private String value;

	/**
	 * Gets the key of rule element parameter.
	 * 
	 * @return rule element parameter key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of rule element parameter.
	 * 
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Gets the value of rule element parameter.
	 * 
	 * @return the value of rule element parameter
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of rule element parameter.
	 * 
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}
}
