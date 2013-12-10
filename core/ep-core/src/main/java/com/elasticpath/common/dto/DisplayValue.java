package com.elasticpath.common.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * This class is holder for different multi language attributes.
 * <p>
 * It designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class DisplayValue implements Dto {
	private static final long serialVersionUID = -8657441790922007120L;

	@XmlAttribute(name = "language")
	private String language;

	@XmlValue
	private String value;

	/**
	 * Constructs empty object.
	 */
	public DisplayValue() {
		// no-arg constructor
	}

	/**
	 * Constructs object with given parameters.
	 *
	 * @param language the language
	 * @param value the value
	 */
	public DisplayValue(final String language, final String value) {
		this.language = language;
		this.value = value;
	}

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language.
	 *
	 * @param language the language to set
	 */
	public void setLanguage(final String language) {
		this.language = language;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("language", getLanguage())
			.append("value", getValue())
			.toString();
	}
}
