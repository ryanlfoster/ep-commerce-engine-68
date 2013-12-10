/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.importexport.common.dto.products.bundles;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>
 * Java class for productBundleConstituentCodeDTO complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="productBundleConstituentCodeDTO">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="type">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="product"/>
 *             &lt;enumeration value="sku"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "productBundleConstituentCodeDTO", propOrder = { "value" })
public class ProductBundleConstituentCodeDTO {

	@XmlValue
	private String value;

	@XmlAttribute
	private String type;

	/**
	 * Gets the value of the value property (element value).
	 * 
	 * @return possible object is {@link String }
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property (element value).
	 * 
	 * @param value allowed object is {@link String }
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value allowed object is {@link String }
	 */
	public void setType(final String value) {
		this.type = value;
	}

}
