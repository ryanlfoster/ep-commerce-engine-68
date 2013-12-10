package com.elasticpath.importexport.common.dto.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface for defined setting value.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "definedValue")
public class DefinedValueDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "context")
	private String context;

	@XmlValue
	private String value;

	/**
	 * @return the Setting Context
	 */
	public final String getContext() {
		return context;
	}

	/**
	 * @param context the Setting Context
	 */
	public final void setContext(final String context) {
		this.context = context;
	}

	/**
	 * @return the Setting Value
	 */
	public final String getValue() {
		return value;
	}

	/**
	 * @param value the Setting Value
	 */
	public final void setValue(final String value) {
		this.value = value;
	}

}
