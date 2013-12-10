package com.elasticpath.importexport.common.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Option elements contain settings used for optional export and import job execution.
 * Every instance of this class represents option as a [key, value] pair.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigurationOption {

	@XmlAttribute(name = "key", required = true)
	private String key;

	@XmlAttribute(name = "value", required = true)
	private String value;
	
	/**
	 * Default constructor does nothing.
	 */
	public ConfigurationOption() {
		// empty constructor for JAXB
	}
	
	/**
	 * Constructs pair {key, value} from the given arguments.
	 * 
	 * @param key string representation
	 * @param value string representation
	 */
	public ConfigurationOption(final String key, final String value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * Gets key.
	 * 
	 * @return key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Sets key.
	 * 
	 * @param key key
	 */
	public void setKey(final String key) {
		this.key = key;
	}
	
	/**
	 * Gets value.
	 * 
	 * @return value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets value.
	 * 
	 * @param value value
	 */
	public void setValue(final String value) {
		this.value = value;
	}
}
