package com.elasticpath.importexport.common.dto.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface for default setting value.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "defaultValue")
public class DefaultValueDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "type", required = true)
	private String type;

	@XmlValue
	private String value;

	/**
	 * @return Default Value Type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type Default Value Type
	 */
	public final void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the Default Value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @param value the Default Value
	 */
	public final void setValue(final String value) {
		this.value = value;
	}

}
