package com.elasticpath.importexport.common.dto.products;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of attribute group object.
 * <p>
 * This implementation designed for JAXB to working with xml representation of data
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AttributeGroupDTO implements Dto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name = "attribute")
	private List<AttributeValuesDTO> attributeValues;

	/**
	 * Gets the product attributes dto list.
	 * 
	 * @return the productAttributes
	 */
	public List<AttributeValuesDTO> getAttributeValuess() {
		if (attributeValues == null) {
			return Collections.emptyList();
		}
		return attributeValues;
	}

	/**
	 * Sets the attribute values dto list.
	 * 
	 * @param attributeValues the attribute values to set
	 */
	public void setAttributeValues(final List<AttributeValuesDTO> attributeValues) {
		this.attributeValues = attributeValues;
	}

}
