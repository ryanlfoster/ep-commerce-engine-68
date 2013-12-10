package com.elasticpath.common.dto.customer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.common.dto.Dto;

/**
 * Similar to a PropertyDTO, but also contains the type associated with the key-value.
 */
@XmlRootElement(name = AttributeValueDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class AttributeValueDTO implements Dto {

	/** XML root element name. */
	public static final String ROOT_ELEMENT = "attribute_value";

	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String key;

	@XmlAttribute(required = true)
	private String type;

	@XmlValue
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ObjectUtils.hashCode(key); 
		result = prime * result + ObjectUtils.hashCode(type);  
		result = prime * result + ObjectUtils.hashCode(value); 
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		AttributeValueDTO other = (AttributeValueDTO) obj;
		
		return ObjectUtils.equals(key, other.key) 
			&& ObjectUtils.equals(type, other.type) 
			&& ObjectUtils.equals(value, other.value);
	}

}
