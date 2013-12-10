package com.elasticpath.importexport.common.dto.catalogs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of CatalogAttribute object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlRootElement(name = AttributeDTO.ROOT_ELEMENT)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { })
public class AttributeDTO implements Dto {

	/** XML Root element name. */
	public static final String ROOT_ELEMENT = "attribute";

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "key", required = true)
	private String key;

	@XmlElement(name = "name", required = true)
	private String name;

	@XmlElement(name = "usage", required = true)
	private AttributeUsageType usage;

	@XmlElement(name = "type", required = true)
	private AttributeTypeType type;

	@XmlElement(name = "multilanguage", required = true)
	private Boolean multiLanguage;

	@XmlElement(name = "required", required = true)
	private Boolean required;

	@XmlElement(name = "multivalue", required = true)
	private Boolean multivalue;

	@XmlElement(name = "global", required = true)
	private Boolean global;

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key.
	 *
	 * @param key the key to set
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the usage.
	 *
	 * @return the usage
	 */
	public AttributeUsageType getUsage() {
		return usage;
	}

	/**
	 * Sets the usage.
	 *
	 * @param usage the usage to set
	 */
	public void setUsage(final AttributeUsageType usage) {
		this.usage = usage;
	}

	/**
	 * Gets the product type.
	 *
	 * @return the AttributeTypeType
	 */
	public AttributeTypeType getType() {
		return type;
	}

	/**
	 * Sets the product type.
	 *
	 * @param type the type to set
	 */
	public void setType(final AttributeTypeType type) {
		this.type = type;
	}

	/**
	 * Gets the multiLanguage.
	 *
	 * @return true if attribute is multiLanguage, false otherwise
	 */
	public Boolean getMultiLanguage() {
		return multiLanguage;
	}

	/**
	 * Sets the multiLanguage.
	 *
	 * @param multiLanguage true if attribute is multiLanguage, false otherwise
	 */
	public void setMultiLanguage(final Boolean multiLanguage) {
		this.multiLanguage = multiLanguage;
	}

	/**
	 * Gets the required.
	 *
	 * @return true if attribute is required, false otherwise
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 *
	 * @param required true if attribute is required, false otherwise
	 */
	public void setRequired(final Boolean required) {
		this.required = required;
	}

	/**
	 * Gets the multivalue.
	 *
	 * @return true if attribute is multivalue, false otherwise
	 */
	public Boolean getMultivalue() {
		return multivalue;
	}

	/**
	 * Sets the multivalue.
	 *
	 * @param multivalue true if attribute is multivalue, false otherwise
	 */
	public void setMultivalue(final Boolean multivalue) {
		this.multivalue = multivalue;
	}

	/**
	 * Gets the global.
	 *
	 * @return true if attribute is global, false otherwise
	 */
	public Boolean getGlobal() {
		return global;
	}

	/**
	 * Sets the global.
	 *
	 * @param global true if attribute is global, false otherwise
	 */
	public void setGlobal(final Boolean global) {
		this.global = global;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("key", getKey())
			.append("name", getName())
			.append("usage", getUsage())
			.append("type", getType())
			.append("multiLanguage", getMultiLanguage())
			.append("required", getRequired())
			.append("multivalue", getMultivalue())
			.append("global", getGlobal())
			.toString();
	}
}
