package com.elasticpath.importexport.common.dto.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface for setting metadata value.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "metadata")
public class MetadataDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "key", required = true)
	private String key;

	@XmlValue
	private String value;

	/**
	 * @return the Setting Metadata Key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @param key the the Setting Metadata Key
	 */
	public final void setKey(final String key) {
		this.key = key;
	}

	/**
	 * @return the Setting Metadata Value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @param value the Setting Metadata Value
	 */
	public final void setValue(final String value) {
		this.value = value;
	}
}
